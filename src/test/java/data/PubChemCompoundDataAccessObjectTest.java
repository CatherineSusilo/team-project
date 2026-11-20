package data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import entity.Compound;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks the PubChem lookup against replies saved from the real API, so no test goes online.
 */
class PubChemCompoundDataAccessObjectTest {
  private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G'};

  // A tiny local server that plays PubChem. Each page is matched by a piece of its URL;
  // anything not listed gets a 404, the same as PubChem does for missing data.
  private final Map<String, byte[]> pages = new LinkedHashMap<>();
  private final List<String> requested = new ArrayList<>();
  private int forcedStatus;
  // When set, pages whose URL matches get this status instead, e.g. 503 for the details only.
  private Predicate<String> busyPages = uri -> false;
  // When set, runs before answering, e.g. to wait for other requests to arrive.
  private Predicate<String> beforeReply = uri -> true;
  private HttpServer server;
  private PubChemCompoundDataAccessObject pubChem;

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", exchange -> {
      String uri = exchange.getRequestURI().getRawPath() + "?"
          + exchange.getRequestURI().getRawQuery();
      synchronized (requested) {
        requested.add(uri);
      }
      beforeReply.test(uri);
      byte[] body = pages.entrySet().stream().filter(page -> uri.contains(page.getKey()))
          .map(Map.Entry::getValue).findFirst().orElse(null);
      int status = forcedStatus != 0 ? forcedStatus
          : busyPages.test(uri) ? 503 : body == null ? 404 : 200;
      byte[] reply = status == 200 ? body : new byte[0];
      exchange.sendResponseHeaders(status, reply.length == 0 ? -1 : reply.length);
      try (OutputStream out = exchange.getResponseBody()) {
        out.write(reply);
      }
    });
    // Several threads, so requests sent at the same time are also answered at the same time.
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    pubChem = new PubChemCompoundDataAccessObject(
        "http://127.0.0.1:" + server.getAddress().getPort());
  }

  @AfterEach
  void stopServer() {
    server.stop(0);
  }

  @Test
  void findsDiethylEther() throws IOException {
    pages.put("/property/", fixture("diethyl_ether_properties.json").getBytes(
        StandardCharsets.UTF_8));
    pages.put("Melting", fixture("diethyl_ether_mp.json").getBytes(StandardCharsets.UTF_8));
    pages.put("Boiling", fixture("diethyl_ether_bp.json").getBytes(StandardCharsets.UTF_8));
    pages.put("GHS", fixture("diethyl_ether_ghs.json").getBytes(StandardCharsets.UTF_8));
    pages.put("/PNG", PNG);

    Compound ether = pubChem.findByName("diethyl ether").orElseThrow();

    assertEquals("Diethyl Ether", ether.getName());
    assertEquals("C4H10O", ether.getFormula());
    assertEquals(74.12, ether.getMolecularWeight());
    assertTrue(ether.getMeltingPoint().startsWith("-116.3 °C"));
    assertTrue(ether.getBoilingPoint().startsWith("34.6 °C"));
    assertEquals("Danger", ether.getSignal());
    assertEquals("H224: Extremely flammable liquid and vapor",
        ether.getHazardStatements().get(0));
    assertArrayEquals(PNG, ether.getStructureImage());
    assertTrue(requested.get(0).contains("/name/diethyl%20ether/"));
    assertTrue(requested.stream().anyMatch(uri -> uri.contains("/compound/3283/")));
  }

  @Test
  void unknownNameIsEmpty() throws IOException {
    assertEquals(Optional.empty(), pubChem.findByName("unobtainium"));
  }

  @Test
  void busyServerThrows() {
    forcedStatus = 503;
    assertThrows(IOException.class, () -> pubChem.findByName("diethyl ether"));
  }

  @Test
  void garbledReplyThrows() {
    pages.put("/property/", "<html>not json</html>".getBytes(StandardCharsets.UTF_8));
    assertThrows(IOException.class, () -> pubChem.findByName("diethyl ether"));
  }

  @Test
  void missingSectionsAreLeftOut() throws IOException {
    pages.put("/property/", fixture("diethyl_ether_properties.json").getBytes(
        StandardCharsets.UTF_8));

    Compound ether = pubChem.findByName("diethyl ether").orElseThrow();

    assertEquals("Diethyl Ether", ether.getName());
    assertNull(ether.getMeltingPoint());
    assertNull(ether.getBoilingPoint());
    assertNull(ether.getSignal());
    assertTrue(ether.getHazardStatements().isEmpty());
    assertNull(ether.getStructureImage());
  }

  @Test
  void urlEncodesSpaces() {
    assertEquals("diethyl%20ether", PubChemCompoundDataAccessObject.encode("diethyl ether"));
  }

  @Test
  void meltingPointPrefersCelsius() throws IOException {
    // The first report is in °F, so the first °C one should win.
    assertEquals("-116.3 °C (stable crystals); -123.3 °C (metastable crystals)",
        PubChemCompoundDataAccessObject.firstTemperature(fixture("diethyl_ether_mp.json")));
  }

  @Test
  void boilingPointFromNumberAndUnit() throws IOException {
    assertEquals("140 °C",
        PubChemCompoundDataAccessObject.firstTemperature(fixture("aspirin_bp.json")));
  }

  @Test
  void busyDetailsStillFindTheCompound() throws IOException {
    pages.put("/property/", fixture("diethyl_ether_properties.json").getBytes(
        StandardCharsets.UTF_8));
    pages.put("GHS", fixture("diethyl_ether_ghs.json").getBytes(StandardCharsets.UTF_8));
    busyPages = uri -> uri.contains("/pug_view/") || uri.contains("/PNG");

    Compound ether = pubChem.findByName("diethyl ether").orElseThrow();

    assertEquals("Diethyl Ether", ether.getName());
    assertEquals(74.12, ether.getMolecularWeight());
    assertNull(ether.getMeltingPoint());
    assertNull(ether.getSignal());
    assertNull(ether.getStructureImage());
  }

  @Test
  void detailsAreRequestedTogether() throws IOException {
    pages.put("/property/", fixture("diethyl_ether_properties.json").getBytes(
        StandardCharsets.UTF_8));
    pages.put("/PNG", PNG);
    // Each detail request waits here until all four have arrived. If they were sent one
    // after another, the first would give up waiting and the picture would never be sent.
    CountDownLatch allFour = new CountDownLatch(4);
    Set<String> waitedAlone = ConcurrentHashMap.newKeySet();
    beforeReply = uri -> {
      if (uri.contains("/property/")) {
        return true;
      }
      allFour.countDown();
      try {
        if (!allFour.await(2, TimeUnit.SECONDS)) {
          waitedAlone.add(uri);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return true;
    };

    Compound ether = pubChem.findByName("diethyl ether").orElseThrow();

    assertTrue(waitedAlone.isEmpty(), "sent one at a time: " + waitedAlone);
    assertArrayEquals(PNG, ether.getStructureImage());
  }

  @Test
  void parsesGhs() throws IOException {
    String ghs = fixture("diethyl_ether_ghs.json");
    List<String> hazards = PubChemCompoundDataAccessObject.hazardStatements(ghs);
    assertEquals("Danger", PubChemCompoundDataAccessObject.signal(ghs));
    assertEquals(List.of("H224: Extremely flammable liquid and vapor",
        "H302: Harmful if swallowed", "H336: May cause drowsiness or dizziness"),
        hazards.subList(0, 3));
    // Other sources add hazards the first one doesn't list, e.g. NITE-CMC's H305.
    assertTrue(hazards.contains("H305: May be harmful if swallowed and enters airways"));
    assertEquals(hazards.size(), hazards.stream().map(h -> h.split(":")[0]).distinct().count());
  }

  @Test
  void mostSevereSignalWins() throws IOException {
    // ECHA, the first source, says Warning for aspirin; NITE-CMC says Danger.
    String ghs = fixture("aspirin_ghs.json");
    assertEquals("Danger", PubChemCompoundDataAccessObject.signal(ghs));
    List<String> hazards = PubChemCompoundDataAccessObject.hazardStatements(ghs);
    assertEquals("H302: Harmful if swallowed", hazards.get(0));
    assertTrue(hazards.stream().anyMatch(h -> h.startsWith("H334:")));
  }

  @Test
  void keepsEuhStatementsAndDropsEveryKindOfPercent() {
    String ghs = "{\"Record\": {\"Section\": [{\"Information\": ["
        + "{\"Name\": \"Signal\", \"Value\": {\"StringWithMarkup\": [{\"String\": \"Warning\"}]}},"
        + "{\"Name\": \"GHS Hazard Statements\", \"Value\": {\"StringWithMarkup\": ["
        + "{\"String\": \"H314 (> 99.9%): Causes severe skin burns [Danger Skin corrosion]\"},"
        + "{\"String\": \"EUH014 (100%): Reacts violently with water\"}]}},"
        + "{\"Name\": \"GHS Hazard Statements\", \"Value\": {\"StringWithMarkup\": ["
        + "{\"String\": \"H314: Causes severe skin burns and eye damage\"}]}}]}]}}";
    assertEquals("Warning", PubChemCompoundDataAccessObject.signal(ghs));
    assertEquals(List.of("H314: Causes severe skin burns", "EUH014: Reacts violently with water"),
        PubChemCompoundDataAccessObject.hazardStatements(ghs));
  }

  @Test
  void waterIsNotClassified() throws IOException {
    String ghs = fixture("water_ghs.json");
    assertNull(PubChemCompoundDataAccessObject.signal(ghs));
    assertTrue(PubChemCompoundDataAccessObject.hazardStatements(ghs).isEmpty());
  }

  @Test
  void missingSectionIsNull() {
    assertNull(PubChemCompoundDataAccessObject.firstTemperature(null));
    assertNull(PubChemCompoundDataAccessObject.signal(null));
    assertTrue(PubChemCompoundDataAccessObject.hazardStatements(null).isEmpty());
  }

  private static String fixture(String name) throws IOException {
    try (InputStream in = PubChemCompoundDataAccessObjectTest.class
        .getResourceAsStream("/pubchem/" + name)) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
