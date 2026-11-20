package view;

import adapter.controller.SearchCompoundController;
import adapter.viewmodel.CompoundViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Lets the student search PubChem for a compound and see its structure, molecular weight,
 * melting and boiling points and safety data. What it shows comes from
 * {@link CompoundViewModel}, filled in by the SearchCompound use case.
 */
public class CompoundSearchView extends JPanel implements PropertyChangeListener {
  private static final List<String> QUICK_PICKS =
      List.of("diethyl ether", "sodium hydroxide", "hydrochloric acid", "water");
  private static final Color HINT = new Color(0x666666);
  private static final int VALUE_COLUMNS = 22;
  private static final int STRUCTURE_SIZE = 200;

  private final CompoundViewModel viewModel;
  private final SearchCompoundController searchCompound;
  private final JTextField query = new JTextField(24);
  private final JButton search = Theme.filledButton("Search", Theme.GREEN, Theme.ON_GREEN);
  private final List<JButton> quickPicks = new ArrayList<>();
  private final JLabel message = new JLabel(" ");
  private final JPanel result = new JPanel(new BorderLayout(24, 0));

  /**
   * Builds the search panel.
   *
   * @param viewModel where the search result appears
   * @param searchCompound runs a search
   */
  public CompoundSearchView(CompoundViewModel viewModel,
      SearchCompoundController searchCompound) {
    super(new BorderLayout(0, 16));
    this.viewModel = viewModel;
    this.searchCompound = searchCompound;
    viewModel.addPropertyChangeListener(this);
    setBackground(Color.WHITE);
    setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    // Fixed size so the popup doesn't jump around every time a new result comes in.
    setPreferredSize(new Dimension(720, 620));

    JLabel title = new JLabel("Compound Lookup");
    title.setFont(Theme.bold(Theme.HEADER_SIZE));
    JLabel hint = new JLabel("Search PubChem for a reagent's properties and safety data.");
    hint.setForeground(HINT);

    query.addActionListener(event -> runSearch(query.getText()));
    search.addActionListener(event -> runSearch(query.getText()));
    JPanel searchRow = row();
    searchRow.add(query);
    searchRow.add(search);

    JPanel quickRow = row();
    quickRow.add(new JLabel("Try:"));
    for (String name : QUICK_PICKS) {
      JButton pick = Theme.linkButton(name, Theme.GREEN);
      // Less padding than a normal button, so the links sit close together like a list.
      pick.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
      pick.addActionListener(event -> {
        query.setText(name);
        runSearch(name);
      });
      quickPicks.add(pick);
      quickRow.add(pick);
    }

    JPanel top = new JPanel();
    top.setOpaque(false);
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
    for (JComponent part : new JComponent[] {title, hint, searchRow, quickRow, message}) {
      part.setAlignmentX(Component.LEFT_ALIGNMENT);
      top.add(part);
    }
    add(top, BorderLayout.NORTH);

    result.setOpaque(false);
    JScrollPane scroll = new JScrollPane(result,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setBorder(null);
    scroll.getViewport().setBackground(Color.WHITE);
    scroll.getVerticalScrollBar().setUnitIncrement(16);
    add(scroll, BorderLayout.CENTER);
  }

  /** Redraws the result whenever the SearchCompound presenter updates the view model. */
  @Override
  public void propertyChange(PropertyChangeEvent event) {
    // The presenter runs on the background search thread, but Swing must be touched on its own.
    if (SwingUtilities.isEventDispatchThread()) {
      refresh();
    } else {
      SwingUtilities.invokeLater(this::refresh);
    }
  }

  /**
   * Looks the name up in the background. PubChem can take a few seconds (or never answer if
   * the wifi is off), and doing that on the Swing thread would freeze the whole window.
   */
  private void runSearch(String name) {
    setBusy(true);
    message.setForeground(HINT);
    message.setText("Searching PubChem...");
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        searchCompound.execute(name);
        return null;
      }

      @Override
      protected void done() {
        setBusy(false);
        try {
          get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          message.setForeground(Theme.RED);
          message.setText("Something went wrong with the search. Try again.");
        }
      }
    }.execute();
  }

  private void setBusy(boolean busy) {
    query.setEnabled(!busy);
    search.setEnabled(!busy);
    for (JButton pick : quickPicks) {
      pick.setEnabled(!busy);
    }
  }

  private void refresh() {
    message.setForeground(Theme.RED);
    message.setText(viewModel.getMessage().isEmpty() ? " " : viewModel.getMessage());
    result.removeAll();
    if (viewModel.isFound()) {
      result.add(structure(), BorderLayout.WEST);
      result.add(details(), BorderLayout.CENTER);
    }
    result.revalidate();
    result.repaint();
  }

  private JComponent structure() {
    byte[] image = viewModel.getStructureImage();
    JLabel structure = image == null
        ? new JLabel("No structure picture", SwingConstants.CENTER)
        : new JLabel(new ImageIcon(new ImageIcon(image).getImage()
            .getScaledInstance(STRUCTURE_SIZE, STRUCTURE_SIZE, Image.SCALE_SMOOTH)));
    structure.setVerticalAlignment(SwingConstants.TOP);
    structure.setForeground(HINT);
    return structure;
  }

  private JPanel details() {
    JPanel details = new JPanel(new GridBagLayout());
    details.setOpaque(false);
    GridBagConstraints at = new GridBagConstraints();
    at.anchor = GridBagConstraints.NORTHWEST;
    at.insets = new Insets(0, 0, 10, 16);
    at.gridy = 0;

    JLabel name = new JLabel(viewModel.getName());
    name.setFont(Theme.bold(Theme.HEADER_SIZE));
    at.gridwidth = 2;
    details.add(name, at);
    at.gridwidth = 1;

    addRow(details, at, "Formula", viewModel.getFormula());
    addRow(details, at, "Molecular weight", viewModel.getMolecularWeight());
    addRow(details, at, "Melting point", viewModel.getMeltingPoint());
    addRow(details, at, "Boiling point", viewModel.getBoilingPoint());
    addRow(details, at, "Signal word", viewModel.getSignal());
    StringBuilder hazards = new StringBuilder();
    for (String statement : viewModel.getHazardStatements()) {
      hazards.append(hazards.length() == 0 ? "" : "\n").append("• ").append(statement);
    }
    addRow(details, at, "Hazards", hazards.length() == 0 ? "None listed" : hazards.toString());

    // Soaks up the leftover height so the rows stay packed at the top.
    at.gridy++;
    at.weighty = 1;
    details.add(new JLabel(), at);
    return details;
  }

  private static void addRow(JPanel details, GridBagConstraints at, String label,
      String value) {
    at.gridy++;
    at.gridx = 0;
    at.weightx = 0;
    at.fill = GridBagConstraints.NONE;
    JLabel name = new JLabel(label);
    name.setFont(Theme.bold(Theme.BODY_SIZE));
    details.add(name, at);

    at.gridx = 1;
    at.weightx = 1;
    at.fill = GridBagConstraints.HORIZONTAL;
    details.add(wrapped(value), at);
    at.gridx = 0;
  }

  /** Read-only text that wraps, since PubChem's boiling points can be a whole sentence. */
  private static JTextArea wrapped(String text) {
    JTextArea area = new JTextArea(text);
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setOpaque(false);
    area.setBorder(null);
    area.setFont(Theme.SANS_REGULAR.deriveFont(Theme.BODY_SIZE));
    area.setColumns(VALUE_COLUMNS);
    // A text area only knows how many lines it wraps to once it has a width. Without this it
    // asks for one line of height and the rest of the text gets cut off.
    area.setSize(area.getPreferredSize().width, Short.MAX_VALUE);
    return area;
  }

  private static JPanel row() {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    row.setOpaque(false);
    return row;
  }
}
