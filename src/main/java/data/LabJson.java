package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import entity.ItemType;
import entity.LabItem;
import entity.PasteurPipette;
import entity.Scale;
import entity.StockBottle;
import entity.Substance;
import entity.Substances;
import entity.Vessel;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Turns a lab (or any part of it) into JSON text and back. Undo snapshots and the saved lab file
 * both use it. Two things need special handling:
 * <ul>
 *   <li>{@link LabItem} is abstract, so each item is written with its {@code type} and read back
 *       as the subclass registered for that type.</li>
 *   <li>{@link Substance} is a map key in a layer, so it is written as its name and read back
 *       from the constants in {@link Substances}.</li>
 * </ul>
 */
public final class LabJson {
  private static final String TYPE_FIELD = "type";

  private final Gson gson;

  /** Creates a converter that knows every item class on main. */
  // TODO: add IceBath, BuchnerFunnel, etc. here as they land on main.
  public LabJson() {
    this(Map.ofEntries(
        Map.entry(ItemType.CONICAL_TUBE, Vessel.class),
        Map.entry(ItemType.TEST_TUBE, Vessel.class),
        Map.entry(ItemType.GRADUATED_CYLINDER, Vessel.class),
        Map.entry(ItemType.BEAKER, Vessel.class),
        Map.entry(ItemType.WATCH_GLASS, Vessel.class),
        Map.entry(ItemType.VIAL, Vessel.class),
        Map.entry(ItemType.PIPETTE, PasteurPipette.class),
        Map.entry(ItemType.DROPPER, StockBottle.class),
        Map.entry(ItemType.SOLVENT_BOTTLE, StockBottle.class),
        Map.entry(ItemType.SCALE, Scale.class)));
  }

  /**
   * Creates a converter.
   *
   * @param itemClasses the subclass to build for each kind of item
   */
  public LabJson(Map<ItemType, Class<? extends LabItem>> itemClasses) {
    this.gson = new GsonBuilder()
        .enableComplexMapKeySerialization()
        .registerTypeAdapter(Substance.class, new SubstanceAdapter())
        .registerTypeAdapterFactory(new LabItemAdapterFactory(new HashMap<>(itemClasses)))
        .create();
  }

  /**
   * Writes an object as JSON text.
   *
   * @param value the lab, bench or other object to write
   * @return the JSON text
   */
  public String toJson(Object value) {
    return gson.toJson(value);
  }

  /**
   * Reads an object back from JSON text.
   *
   * @param json text made by {@link #toJson(Object)}
   * @param type the class to build
   * @param <T> the class to build
   * @return a new object equal to the one that was written
   * @throws JsonParseException if the text is not valid, or names an unknown substance or item
   */
  public <T> T fromJson(String json, Class<T> type) {
    return gson.fromJson(json, type);
  }

  /** Writes a substance as its name, reads it back from the {@link Substances} constants. */
  private static final class SubstanceAdapter
      implements JsonSerializer<Substance>, JsonDeserializer<Substance> {
    private final Map<String, Substance> byName = new HashMap<>();

    SubstanceAdapter() {
      for (Field field : Substances.class.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers()) && field.getType() == Substance.class) {
          try {
            Substance substance = (Substance) field.get(null);
            byName.put(substance.getName(), substance);
          } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot read " + field.getName(), e);
          }
        }
      }
    }

    @Override
    public JsonElement serialize(Substance src, Type type, JsonSerializationContext context) {
      return new JsonPrimitive(src.getName());
    }

    @Override
    public Substance deserialize(JsonElement json, Type type,
        JsonDeserializationContext context) {
      Substance substance = byName.get(json.getAsString());
      if (substance == null) {
        throw new JsonParseException("Unknown substance: " + json.getAsString());
      }
      return substance;
    }
  }

  /**
   * Handles values typed as plain {@link LabItem}: writes the real subclass, reads the subclass
   * registered for the item's {@code type}.
   */
  private static final class LabItemAdapterFactory implements TypeAdapterFactory {
    private final Map<ItemType, Class<? extends LabItem>> itemClasses;

    LabItemAdapterFactory(Map<ItemType, Class<? extends LabItem>> itemClasses) {
      this.itemClasses = itemClasses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      if (typeToken.getRawType() != LabItem.class) {
        return null;
      }
      return (TypeAdapter<T>) new TypeAdapter<LabItem>() {
        @Override
        public void write(JsonWriter out, LabItem item) throws IOException {
          if (item == null) {
            out.nullValue();
            return;
          }
          TypeAdapter<LabItem> subclass =
              (TypeAdapter<LabItem>) gson.getAdapter(item.getClass());
          subclass.write(out, item);
        }

        @Override
        public LabItem read(JsonReader in) throws IOException {
          JsonElement tree = gson.getAdapter(JsonElement.class).read(in);
          if (tree == null || tree.isJsonNull()) {
            return null;
          }
          JsonObject object = tree.getAsJsonObject();
          if (!object.has(TYPE_FIELD)) {
            throw new JsonParseException("Item has no type.");
          }
          ItemType type = ItemType.valueOf(object.get(TYPE_FIELD).getAsString());
          Class<? extends LabItem> subclass = itemClasses.get(type);
          if (subclass == null) {
            throw new JsonParseException("No item class registered for " + type);
          }
          return gson.getAdapter(subclass).fromJsonTree(object);
        }
      };
    }
  }
}
