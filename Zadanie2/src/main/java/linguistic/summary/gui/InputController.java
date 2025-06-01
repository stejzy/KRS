package linguistic.summary.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import linguistic.summary.*;
import utils.DataRow;
import utils.PostgresToDataRowLoader;

import java.util.*;
import java.util.stream.Collectors;

public class InputController {

    List<DataRow> dataRows = PostgresToDataRowLoader.loadDataRows();

    private String selectedForm;
    private final List<Summarizer> summarizers = new ArrayList<>();
    private final List<Quantifier> quantifiers = new ArrayList<>();

    private final List<String> selectedQualityMeasures = new ArrayList<>();

    private Map<String, LinguisticVariable> variableMap;
    private Map<String, Quantifier> quantifierMap;

    private final Map<String, ToggleGroup> toggleGroups = new HashMap<>();

    @FXML
    private Label formLabel;

    @FXML
    private RadioButton form1;

    @FXML
    private RadioButton form2;

    @FXML
    private ToggleGroup chooseForm;

    @FXML
    private VBox radioGroupsContainer;

    @FXML
    private VBox quantifierContainer;

    @FXML
    private TitledPane summarizerPane;

    @FXML
    private TitledPane quantifierPane;

    @FXML
    private TitledPane qualityMeasuresPane;

    @FXML
    private VBox qualityMeasuresContainer;

    @FXML
    private Button generateButton;

    @FXML
    private ComboBox<String> qualitySortComboBox;

    @FXML
    private VBox weightsContainer;

    @FXML
    private TextField weight1;

    @FXML
    private TextField weight2;

    @FXML
    private TextField weight3;

    @FXML
    private TextField weight4;

    @FXML
    private TextField weight5;

    @FXML
    private TableView<Map<String, String>> summaryTable;
    @FXML
    private TableColumn<Map<String, String>, String> summaryCol;

    @FXML
    private void toggleWeightFields() {
        for (Node node : qualityMeasuresContainer.getChildren()) {
            if (node instanceof CheckBox cb && "Optimal Summary".equals(cb.getText())) {
                boolean isSelected = cb.isSelected();
                weightsContainer.setVisible(isSelected);
                weightsContainer.setManaged(isSelected);
                if (!isSelected) {
                    weight1.clear();
                    weight2.clear();
                    weight3.clear();
                    weight4.clear();
                    weight5.clear();
                }
                break;
            }
        }
    }

    @FXML
    public void initialize() {
        initializeFormSelection();
        initializeLinguisticVariables();
        initializeQuantifiers();
        initializeQualityMeasures();

        summarizerPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                quantifierPane.setExpanded(false);
                qualityMeasuresPane.setExpanded(false);
            }
        });

        quantifierPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                summarizerPane.setExpanded(false);
                qualityMeasuresPane.setExpanded(false);
            }
        });

        qualityMeasuresPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                summarizerPane.setExpanded(false);
                quantifierPane.setExpanded(false);
            }
        });

        generateButton.setOnAction(event -> generateSummaries());
        summaryCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get("summary")));
    }

    private void initializeFormSelection() {
        form1.setSelected(true);
        selectedForm = form1.getText();

        chooseForm.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                selectedForm = selected.getText();

                if ("Forma 2".equals(selectedForm)) {
                    summarizerPane.setText("Wybierz sumaryzatory/kwalifikatory");
                    disableAbsoluteQuantifiers(true); // Wyłącz kwantyfikatory bezwzględne
                } else {
                    summarizerPane.setText("Wybierz sumaryzatory");
                    disableAbsoluteQuantifiers(false); // Włącz kwantyfikatory bezwzględne
                }
            }
        });
    }

    private void disableAbsoluteQuantifiers(boolean disable) {
        for (Node titledPaneNode : quantifierContainer.getChildren()) {
            if (titledPaneNode instanceof TitledPane titledPane && "Bezwzględne kwantyfikatory".equals(titledPane.getText())) {
                VBox vbox = (VBox) titledPane.getContent();

                for (Node node : vbox.getChildren()) {
                    if (node instanceof CheckBox cb) {
                        cb.setDisable(disable);
                        if (disable && cb.isSelected()) {
                            cb.setSelected(false);
                        }
                    }
                }
            }
        }

        updateSelectedQuantifiers();
    }

    private void initializeLinguisticVariables() {
        variableMap = LinguisticVariableRegistry.getAllLinguisticVariables();

        for (Map.Entry<String, LinguisticVariable> entry : variableMap.entrySet()) {
            String variableName = entry.getKey();
            LinguisticVariable variable = entry.getValue();

            VBox content = new VBox(5);
            ToggleGroup group = new ToggleGroup();
            toggleGroups.put(variableName, group);

            for (String label : variable.getLabels()) {
                RadioButton rb = new RadioButton(label);
                rb.setToggleGroup(group);
                rb.setUserData(label);
                content.getChildren().add(rb);

                rb.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (rb.equals(group.getSelectedToggle())) {
                        group.selectToggle(null);
                        event.consume();
                        updateSelectedSummarizers();
                    }
                });

                rb.setOnAction(e -> updateSelectedSummarizers());
            }

            TitledPane titledPane = new TitledPane(variableName, content);
            titledPane.setExpanded(false);

            radioGroupsContainer.getChildren().add(titledPane);
        }

        if (!radioGroupsContainer.getChildren().isEmpty()) {
            Node first = radioGroupsContainer.getChildren().get(0);
            VBox.setMargin(first, new Insets(10, 0, 0, 0));
        }
    }

   private void initializeQualityMeasures() {
       List<String> qualityMeasures = new ArrayList<>(List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10"));
       qualityMeasures.add("Optimal Summary");

       for (String measure : qualityMeasures) {
           CheckBox cb = new CheckBox(measure);
           cb.setUserData(measure);

           if ("Optimal Summary".equals(measure)) {
               cb.setOnAction(e -> {
                   toggleWeightFields();
                   updateSelectedQualityMeasures();
               });
           } else {
               cb.setOnAction(e -> updateSelectedQualityMeasures());
           }
           qualityMeasuresContainer.getChildren().add(cb);
       }

       if (!qualityMeasuresContainer.getChildren().isEmpty()) {
           Node first = qualityMeasuresContainer.getChildren().get(0);
           VBox.setMargin(first, new Insets(10, 0, 0, 0));
       }

       updateSelectedQualityMeasures();
   }


    private void initializeQuantifiers() {
        quantifierMap = QuantifierRegistry.getAll();

        Map<Boolean, List<Quantifier>> partitioned = quantifierMap.values().stream()
                .collect(Collectors.partitioningBy(Quantifier::isRelative));

        createQuantifierPane("Względne kwantyfikatory", partitioned.get(true));
        createQuantifierPane("Bezwzględne kwantyfikatory", partitioned.get(false));

        if (!quantifierContainer.getChildren().isEmpty()) {
            Node first = quantifierContainer.getChildren().getFirst();
            VBox.setMargin(first, new Insets(10, 0, 0, 0));
        }
    }

    private void createQuantifierPane(String title, List<Quantifier> quantifiers) {
        if (quantifiers == null || quantifiers.isEmpty()) return;

        VBox box = new VBox(5);
        box.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-border-color: darkgray; -fx-border-width: 1;");

        for (Quantifier q : quantifiers) {
            CheckBox cb = new CheckBox(q.getName());
            cb.setUserData(q);

            cb.setOnAction(e -> updateSelectedQuantifiers());
            box.getChildren().add(cb);
        }

        TitledPane pane = new TitledPane(title, box);
        pane.setExpanded(false);
        quantifierContainer.getChildren().add(pane);
    }

    private void updateSelectedSummarizers() {
        summarizers.clear();
        for (Map.Entry<String, ToggleGroup> entry : toggleGroups.entrySet()) {
            Toggle selectedToggle = entry.getValue().getSelectedToggle();
            if (selectedToggle != null) {
                String label = (String) selectedToggle.getUserData();
                LinguisticVariable variable = variableMap.get(entry.getKey());
                if (variable != null) {
                    summarizers.add(new Summarizer(label, variable));
                }
            }
        }
        System.out.println("Aktualne summarizery:");
        summarizers.forEach(s -> System.out.println("  - " + s));
    }

    private void updateSelectedQuantifiers() {
        quantifiers.clear();

        for (Node titledPaneNode : quantifierContainer.getChildren()) {
            if (titledPaneNode instanceof TitledPane titledPane) {
                VBox vbox = (VBox) titledPane.getContent();

                for (Node node : vbox.getChildren()) {
                    if (node instanceof CheckBox cb && cb.isSelected()) {
                        Quantifier q = (Quantifier) cb.getUserData();
                        quantifiers.add(q);
                    }
                }
            }
        }

        System.out.println("Wybrane kwantyfikatory:");
        quantifiers.forEach(q -> System.out.println("  - " + q.getName()));
    }

    private void updateSelectedQualityMeasures() {
        selectedQualityMeasures.clear();

        for (Node node : qualityMeasuresContainer.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                String measure = (String) cb.getUserData();
                if (measure != null) {
                    selectedQualityMeasures.add(measure);
                }
            }
        }

        qualitySortComboBox.getItems().clear();
        qualitySortComboBox.getItems().addAll(selectedQualityMeasures);

        System.out.println("Wybrane miary jakości:");
        selectedQualityMeasures.forEach(System.out::println);
    }

private void generateSummaries() {
    if (summarizers.isEmpty() || quantifiers.isEmpty()) {
        System.out.println("Brak wybranych sumaryzatorów lub kwantyfikatorów.");
        return;
    }

    if (selectedQualityMeasures.isEmpty()) {
        System.out.println("Musisz wybrać co najmniej jedną miarę jakości.");
        return;
    }

    boolean useSecondForm = "Forma 2".equals(selectedForm);

    if (useSecondForm && summarizers.size() < 2) {
        System.out.println("Dla drugiej formy musisz wybrać co najmniej dwa sumaryzatory.");
        return;
    }

    List<LinguisticSummary> summaries = SingleEntitySummaryGenerator.generateAllSummaries(quantifiers, summarizers, useSecondForm);

    for (LinguisticSummary summary : summaries) {
        for (String measure : selectedQualityMeasures) {
            double value;
            switch (measure) {
                case "T1" -> value = SingleEntityQualityMeasureCalculator.calculateT1(summary, dataRows);
                case "T2" -> value = SingleEntityQualityMeasureCalculator.calculateT2(summary, dataRows);
                case "T3" -> value = SingleEntityQualityMeasureCalculator.calculateT3(summary, dataRows);
                case "T4" -> value = SingleEntityQualityMeasureCalculator.calculateT4(summary, dataRows);
                case "T5" -> value = SingleEntityQualityMeasureCalculator.calculateT5(summary, dataRows);
                case "T6" -> value = SingleEntityQualityMeasureCalculator.calculateT6(summary, dataRows);
                case "T7" -> value = SingleEntityQualityMeasureCalculator.calculateT7(summary, dataRows);
                case "T8" -> value = SingleEntityQualityMeasureCalculator.calculateT8(summary, dataRows);
                case "T9" -> value = SingleEntityQualityMeasureCalculator.calculateT9(summary, dataRows);
                case "T10" -> value = SingleEntityQualityMeasureCalculator.calculateT10(summary, dataRows);
                case "Optimal Summary" -> {
                    try {
                        double w1 = Double.parseDouble(weight1.getText());
                        double w2 = Double.parseDouble(weight2.getText());
                        double w3 = Double.parseDouble(weight3.getText());
                        double w4 = Double.parseDouble(weight4.getText());
                        double w5 = Double.parseDouble(weight5.getText());
                        value = SingleEntityQualityMeasureCalculator.optimalSummary(summary, dataRows, w1, w2, w3, w4, w5);
                    } catch (NumberFormatException e) {
                        System.out.println("Wprowadź poprawne wartości wag dla Optimal Summary.");
                        return;
                    }
                }
                default -> throw new IllegalArgumentException("Nieznana miara jakości: " + measure);
            }
            summary.setQualityMeasure(measure, value);
        }
    }

    String selectedSortMeasure = qualitySortComboBox.getValue();
    if (selectedSortMeasure != null && !selectedSortMeasure.isEmpty()) {
        summaries.sort(Comparator.comparingDouble((LinguisticSummary s) -> s.getQualityMeasure(selectedSortMeasure)).reversed());
        System.out.println("\nPodsumowania posortowane malejąco według " + selectedSortMeasure + ":");
    } else {
        System.out.println("\nPodsumowania bez sortowania:");
    }

    // --- Ustawienia kolumny summaryCol ---
    summaryCol.setPrefWidth(600);
    summaryCol.setMinWidth(600);
    summaryCol.setMaxWidth(600);
    summaryCol.setCellFactory(col -> new TableCell<>() {
        private final Label label = new Label();
        {
            label.setWrapText(true);
            label.setPrefWidth(600);
            label.setMinHeight(Region.USE_PREF_SIZE);
            setGraphic(label);
        }
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                label.setText(null);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            } else {
                label.setText(item);
                label.setPrefWidth(600);
                label.setMinHeight(Region.USE_PREF_SIZE);
                label.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    setPrefHeight(newHeight.doubleValue() + 10);
                });
            }
        }
    });

    // --- Wyświetlanie w tabeli ---
    summaryTable.getColumns().removeIf(col -> col != summaryCol);

    for (String measure : selectedQualityMeasures) {
        if (measure.equals("summary")) continue;
        TableColumn<Map<String, String>, String> col = new TableColumn<>(measure);
        col.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(measure)));
        summaryTable.getColumns().add(col);
    }

    summaryTable.getItems().clear();
    for (LinguisticSummary summary : summaries) {
        Map<String, String> row = new HashMap<>();
        row.put("summary", summary.toString());
        for (String measure : selectedQualityMeasures) {
            row.put(measure, String.format("%.3f", summary.getQualityMeasure(measure)));
        }
        summaryTable.getItems().add(row);
    }

    // --- Wypisywanie w konsoli ---
    for (LinguisticSummary summary : summaries) {
        System.out.println(summary);
    }
}
}