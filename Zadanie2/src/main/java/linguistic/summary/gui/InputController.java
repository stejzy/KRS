package linguistic.summary.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
        List<String> qualityMeasures = List.of("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10");

        for (String measure : qualityMeasures) {
            CheckBox cb = new CheckBox(measure);
            cb.setUserData(measure);

            cb.setOnAction(e -> updateSelectedQualityMeasures());
            qualityMeasuresContainer.getChildren().add(cb);
        }

        if (!qualityMeasuresContainer.getChildren().isEmpty()) {
            Node first = qualityMeasuresContainer.getChildren().get(0);
            VBox.setMargin(first, new Insets(10, 0, 0, 0));
        }
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
                selectedQualityMeasures.add((String) cb.getUserData());
            }
        }

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
                double value = switch (measure) {
                    case "T1" -> SingleEntityQualityMeasureCalculator.calculateT1(summary, dataRows);
                    case "T2" -> SingleEntityQualityMeasureCalculator.calculateT2(summary, dataRows);
                    case "T3" -> SingleEntityQualityMeasureCalculator.calculateT3(summary, dataRows);
                    case "T4" -> SingleEntityQualityMeasureCalculator.calculateT4(summary, dataRows);
                    case "T5" -> SingleEntityQualityMeasureCalculator.calculateT5(summary, dataRows);
                    case "T6" -> SingleEntityQualityMeasureCalculator.calculateT6(summary, dataRows);
                    case "T7" -> SingleEntityQualityMeasureCalculator.calculateT7(summary, dataRows);
                    case "T8" -> SingleEntityQualityMeasureCalculator.calculateT8(summary, dataRows);
                    case "T9" -> SingleEntityQualityMeasureCalculator.calculateT9(summary, dataRows);
                    case "T10" -> SingleEntityQualityMeasureCalculator.calculateT10(summary, dataRows);
                    default -> throw new IllegalArgumentException("Nieznana miara jakości: " + measure);
                };
                summary.setQualityMeasure(measure, value);
            }
        }

        System.out.println("\nWygenerowane podsumowania:");
        for (LinguisticSummary summary : summaries) {
            System.out.println(summary);
        }
    }
}