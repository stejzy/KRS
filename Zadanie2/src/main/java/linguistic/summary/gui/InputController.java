package linguistic.summary.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import linguistic.summary.*;
import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.MembershipFunction;
import linguistic.summary.membershipfunctions.TrapezoidalFunction;
import linguistic.summary.membershipfunctions.TriangularFunction;
import utils.DataRow;
import utils.PostgresToDataRowLoader;

import java.io.File;
import java.io.PrintWriter;
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
    private RadioButton singleSummaryRadio;

    @FXML
    private RadioButton multiSummaryRadio;

    @FXML
    private ToggleGroup summaryTypeGroup;

    @FXML
    private RadioButton form1;

    @FXML
    private RadioButton form2;

    @FXML private CheckBox form1Check, form2Check, form3Check, form4Check;

    @FXML
    private HBox formButtonsBox;

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
    private Button saveButton;

    @FXML
    private TextField rowsToSaveField;

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
    private TextField weight6;

    @FXML
    private TextField weight7;

    @FXML
    private TextField weight8;

    @FXML
    private TextField weight9;

    @FXML
    private TextField weight10;

    @FXML
    private TextField weight11;

    @FXML
    private TableView<Map<String, String>> summaryTable;
    @FXML
    private TableColumn<Map<String, String>, String> summaryCol;

    @FXML
    private void toggleWeightFields() {
        boolean isOptimalSelected = false;
        for (Node node : qualityMeasuresContainer.getChildren()) {
            if (node instanceof CheckBox cb && "Optimal Summary".equals(cb.getText())) {
                isOptimalSelected = cb.isSelected();
                weightsContainer.setVisible(isOptimalSelected);
                weightsContainer.setManaged(isOptimalSelected);
                if (!isOptimalSelected) {
                    weight1.clear(); weight2.clear(); weight3.clear(); weight4.clear(); weight5.clear();
                    weight6.clear(); weight7.clear(); weight8.clear(); weight9.clear(); weight10.clear(); weight11.clear();
                }
                break;
            }
        }
        updateWeightFieldsState();
    }

    private void updateWeightFieldsState() {
        boolean isForm2 = "Forma 2".equals(selectedForm);
        // weight1–weight8 zawsze aktywne gdy widoczne
        weight1.setDisable(false); weight2.setDisable(false); weight3.setDisable(false); weight4.setDisable(false);
        weight5.setDisable(false); weight6.setDisable(false); weight7.setDisable(false); weight8.setDisable(false);
        // weight9–weight11 tylko dla Formy 2
        weight9.setDisable(!isForm2);
        weight10.setDisable(!isForm2);
        weight11.setDisable(!isForm2);
        if (!isForm2) {
            weight9.clear();
            weight10.clear();
            weight11.clear();
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
        saveButton.setOnAction(event -> saveSummariesToFile());

        summaryCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get("summary")));
    }

    private void saveSummariesToFile() {
        int rowsToSave = summaryTable.getItems().size();
        String input = rowsToSaveField.getText();
        if (input != null && !input.isBlank()) {
            try {
                int n = Integer.parseInt(input);
                if (n > 0 && n < rowsToSave) {
                    rowsToSave = n;
                }
            } catch (NumberFormatException e) {
                System.out.println("Niepoprawna liczba wierszy do zapisu.");
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz podsumowania");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik tekstowy", "*.txt"));
        File file = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            // Nagłówki
            List<String> headers = summaryTable.getColumns().stream()
                    .map(TableColumn::getText)
                    .collect(Collectors.toList());
            writer.println(String.join("\t", headers));
            writer.println("-------");

            // Wiersze
            for (int i = 0; i < rowsToSave; i++) {
                Map<String, String> row = summaryTable.getItems().get(i);
                List<String> values = new ArrayList<>();
                for (TableColumn<Map<String, String>, ?> col : summaryTable.getColumns()) {
                    String columnHeader = col.getText();
                    if ("#".equals(columnHeader)) {
                        values.add(String.valueOf(i + 1));
                    } else if ("summary".equalsIgnoreCase(columnHeader) || "Podsumowanie".equalsIgnoreCase(columnHeader)) {
                        values.add(row.getOrDefault("summary", ""));
                    } else {
                        values.add(row.getOrDefault(columnHeader, ""));
                    }
                }
                writer.println(String.join("\t", values));
                writer.println("-------");
            }

            System.out.println("Podsumowania zapisane do pliku: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Błąd zapisu: " + e.getMessage());
        }
    }


    private void clearSelectedSummarizers() {
        for (ToggleGroup group : toggleGroups.values()) {
            group.selectToggle(null);
        }
        summarizers.clear();
    }

    private void clearSelectedQuantifiers() {
        for (Node titledPaneNode : quantifierContainer.getChildren()) {
            if (titledPaneNode instanceof TitledPane titledPane) {
                VBox vbox = (VBox) titledPane.getContent();
                for (Node node : vbox.getChildren()) {
                    if (node instanceof CheckBox cb) {
                        cb.setSelected(false);
                    }
                }
            }
        }
        quantifiers.clear();
    }

    private void initializeFormSelection() {
        form1.setSelected(true);
        selectedForm = form1.getText();

        // Ustawienie widoczności form dla jednopodmiotowych podsumowań
        form1.setVisible(true); form1.setManaged(true);
        form2.setVisible(true); form2.setManaged(true);

        summaryTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isMulti = newToggle == multiSummaryRadio;

            // RadioButtony tylko dla jednopodmiotowych
            form1.setVisible(!isMulti); form1.setManaged(!isMulti);
            form2.setVisible(!isMulti); form2.setManaged(!isMulti);

            // CheckBoxy tylko dla wielopodmiotowych
            form1Check.setVisible(isMulti); form1Check.setManaged(isMulti);
            form2Check.setVisible(isMulti); form2Check.setManaged(isMulti);
            form3Check.setVisible(isMulti); form3Check.setManaged(isMulti);
            form4Check.setVisible(isMulti); form4Check.setManaged(isMulti);

            // Reset wyborów
            form1.setSelected(!isMulti);
            form2.setSelected(false);

            form1Check.setSelected(false);
            form2Check.setSelected(false);
            form3Check.setSelected(false);
            form4Check.setSelected(false);

            clearSelectedSummarizers();
            clearSelectedQuantifiers();
            summaryTable.getItems().clear();

            disableAbsoluteQuantifiers(isMulti || "Forma 2".equals(selectedForm));
        });

        chooseForm.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                selectedForm = selected.getText();
                boolean isMulti = multiSummaryRadio.isSelected();
                if ("Forma 2".equals(selectedForm)) {
                    summarizerPane.setText("Wybierz sumaryzatory/kwalifikatory");
                } else {
                    summarizerPane.setText("Wybierz sumaryzatory");
                }

                disableAbsoluteQuantifiers(isMulti || "Forma 2".equals(selectedForm));
                updateWeightFieldsState();
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
        radioGroupsContainer.getChildren().clear();
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

            Button addLabelButton = new Button("Dodaj etykietę");
            addLabelButton.setOnAction(e -> showAddLabelDialog(variable));
            content.getChildren().add(addLabelButton);

            TitledPane titledPane = new TitledPane(variableName, content);
            titledPane.setExpanded(false);

            radioGroupsContainer.getChildren().add(titledPane);
        }

        if (!radioGroupsContainer.getChildren().isEmpty()) {
            Node first = radioGroupsContainer.getChildren().get(0);
            VBox.setMargin(first, new Insets(10, 0, 0, 0));
        }
    }

    private void showAddLabelDialog(LinguisticVariable variable) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Dodaj etykietę do " + variable.getName());

        Label nameLabel = new Label("Nazwa etykiety:");
        TextField nameField = new TextField();

        Label functionLabel = new Label("Funkcja przynależności:");
        ComboBox<String> functionType = new ComboBox<>();
        functionType.getItems().addAll("Trójkątna", "Trapezowa", "Gaussa");
        functionType.setValue("Trapezowa"); // domyślnie trapezowa

        CheckBox relativeBox = new CheckBox("Względny (0-1)?");

        // Pola dla funkcji
        final TextField[] aField = new TextField[1];
        final TextField[] bField = new TextField[1];
        final TextField[] cField = new TextField[1];
        final TextField[] dField = new TextField[1];
        final TextField[] centerField = new TextField[1];
        final TextField[] sigmaField = new TextField[1];

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, nameLabel, nameField);
        grid.addRow(1, functionLabel, functionType);

        // Domyślnie trapezowa
        aField[0] = new TextField();
        bField[0] = new TextField();
        cField[0] = new TextField();
        dField[0] = new TextField();
        grid.addRow(2, new Label("a:"), aField[0]);
        grid.addRow(3, new Label("b:"), bField[0]);
        grid.addRow(4, new Label("c:"), cField[0]);
        grid.addRow(5, new Label("d:"), dField[0]);
        grid.addRow(6, relativeBox);

        functionType.setOnAction(e -> {
            grid.getChildren().removeIf(node -> {
                Integer row = GridPane.getRowIndex(node);
                return row != null && row >= 2;
            });

            switch (functionType.getValue()) {
                case "Trójkątna" -> {
                    aField[0] = new TextField();
                    bField[0] = new TextField();
                    cField[0] = new TextField();
                    grid.addRow(2, new Label("a:"), aField[0]);
                    grid.addRow(3, new Label("b:"), bField[0]);
                    grid.addRow(4, new Label("c:"), cField[0]);
                    grid.addRow(5, relativeBox);
                }
                case "Trapezowa" -> {
                    aField[0] = new TextField();
                    bField[0] = new TextField();
                    cField[0] = new TextField();
                    dField[0] = new TextField();
                    grid.addRow(2, new Label("a:"), aField[0]);
                    grid.addRow(3, new Label("b:"), bField[0]);
                    grid.addRow(4, new Label("c:"), cField[0]);
                    grid.addRow(5, new Label("d:"), dField[0]);
                    grid.addRow(6, relativeBox);
                }
                case "Gaussa" -> {
                    centerField[0] = new TextField();
                    sigmaField[0] = new TextField();
                    grid.addRow(2, new Label("c (środek):"), centerField[0]);
                    grid.addRow(3, new Label("σ (odchylenie):"), sigmaField[0]);
                    grid.addRow(4, relativeBox);
                }
            }
        });

        VBox wrapper = new VBox(grid);
        wrapper.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(wrapper);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String labelName = nameField.getText();
                MembershipFunction function = null;
                boolean isRelative = relativeBox.isSelected();
                try {
                    switch (functionType.getValue()) {
                        case "Trójkątna" -> function = new TriangularFunction(
                                Double.parseDouble(aField[0].getText()),
                                Double.parseDouble(bField[0].getText()),
                                Double.parseDouble(cField[0].getText()));
                        case "Trapezowa" -> function = new TrapezoidalFunction(
                                Double.parseDouble(aField[0].getText()),
                                Double.parseDouble(bField[0].getText()),
                                Double.parseDouble(cField[0].getText()),
                                Double.parseDouble(dField[0].getText()));
                        case "Gaussa" -> function = new GaussianFunction(
                                Double.parseDouble(centerField[0].getText()),
                                Double.parseDouble(sigmaField[0].getText()));
                    }
                } catch (Exception ex) {
                    return null;
                }
                if (function != null) {
                    // Jeśli chcesz zapisać względność, przekaż isRelative do odpowiedniej metody
                    LinguisticVariableRegistry.addLabelToVariable(variable.getName(), labelName, function);
                    initializeLinguisticVariables();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private final List<String> singleEntityQualityMeasures = new ArrayList<>(List.of(
            "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "Optimal Summary"
    ));

    private final List<String> multiEntityQualityMeasures = new ArrayList<>(List.of("T"));

       private void initializeQualityMeasures() {
           // Listener do przełączania miar w zależności od typu podsumowania
           summaryTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
               if (newToggle == singleSummaryRadio) {
                   loadQualityMeasures(singleEntityQualityMeasures);
               } else if (newToggle == multiSummaryRadio) {
                   loadQualityMeasures(multiEntityQualityMeasures);
               }
           });
           // Domyślnie załaduj miary dla jednopodmiotowych
           loadQualityMeasures(singleEntityQualityMeasures);
       }

     private void loadQualityMeasures(List<String> measures) {
           qualityMeasuresContainer.getChildren().clear();
           boolean hasOptimal = false;
           for (String measure : measures) {
               CheckBox cb = new CheckBox(measure);
               cb.setUserData(measure);

               if ((measures == singleEntityQualityMeasures && "T1".equals(measure)) ||
                   (measures == multiEntityQualityMeasures && "T".equals(measure))) {
                   cb.setSelected(true);
               }

               if ("Optimal Summary".equals(measure)) {
                   hasOptimal = true;
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
           // Ukryj i wyczyść wagi jeśli nie ma Optimal Summary
           if (!hasOptimal) {
               weightsContainer.setVisible(false);
               weightsContainer.setManaged(false);
               weight1.clear(); weight2.clear(); weight3.clear(); weight4.clear(); weight5.clear();
               weight6.clear(); weight7.clear(); weight8.clear(); weight9.clear(); weight10.clear(); weight11.clear();
           }
           updateSelectedQualityMeasures();
       }


   private void initializeQuantifiers() {
        quantifierContainer.getChildren().clear(); // <-- dodaj to!
        quantifierMap = QuantifierRegistry.getAll();

        Map<Boolean, List<Quantifier>> partitioned = quantifierMap.values().stream()
                .collect(Collectors.partitioningBy(Quantifier::isRelative));

        createQuantifierPane("Względne kwantyfikatory", partitioned.get(true));
        createQuantifierPane("Bezwzględne kwantyfikatory", partitioned.get(false));

        if (!quantifierContainer.getChildren().isEmpty()) {
            Node first = quantifierContainer.getChildren().getFirst();
            VBox.setMargin(first, new Insets(10, 0, 0, 0));
        }

        Button addQuantifierButton = new Button("Dodaj kwantyfikator");
        addQuantifierButton.setOnAction(e -> {
            showAddQuantifierDialog();
            System.out.println("Dodawanie nowego kwantyfikatora...");
        });
        quantifierContainer.getChildren().add(addQuantifierButton);
        VBox.setMargin(addQuantifierButton, new Insets(10, 0, 0, 0));
    }

  private void showAddQuantifierDialog() {
      Dialog<Quantifier> dialog = new Dialog<>();
      dialog.setTitle("Dodaj kwantyfikator");

      Label nameLabel = new Label("Nazwa:");
      TextField nameField = new TextField();

      Label functionLabel = new Label("Funkcja przynależności:");
      ComboBox<String> functionType = new ComboBox<>();
      functionType.getItems().addAll("Trójkątna", "Trapezowa", "Gaussa");
      functionType.setValue("Trapezowa");

      CheckBox relativeBox = new CheckBox("Względny (0-1)?");

      // Referencje do aktualnych pól
      final TextField[] currentAField = new TextField[1];
      final TextField[] currentBField = new TextField[1];
      final TextField[] currentCField = new TextField[1];
      final TextField[] currentDField = new TextField[1];
      final TextField[] currentCenterField = new TextField[1];
      final TextField[] currentSigmaField = new TextField[1];

      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(10);
      grid.addRow(0, nameLabel, nameField);
      grid.addRow(1, functionLabel, functionType);

      // Domyślnie trapezowa
      currentAField[0] = new TextField();
      currentBField[0] = new TextField();
      currentCField[0] = new TextField();
      currentDField[0] = new TextField();
      grid.addRow(2, new Label("a:"), currentAField[0]);
      grid.addRow(3, new Label("b:"), currentBField[0]);
      grid.addRow(4, new Label("c:"), currentCField[0]);
      grid.addRow(5, new Label("d:"), currentDField[0]);
      grid.addRow(6, relativeBox);

      functionType.setOnAction(e -> {
          grid.getChildren().clear();
          grid.addRow(0, nameLabel, nameField);
          grid.addRow(1, functionLabel, functionType);
          switch (functionType.getValue()) {
              case "Trójkątna" -> {
                  currentAField[0] = new TextField();
                  currentBField[0] = new TextField();
                  currentCField[0] = new TextField();
                  grid.addRow(2, new Label("a:"), currentAField[0]);
                  grid.addRow(3, new Label("b:"), currentBField[0]);
                  grid.addRow(4, new Label("c:"), currentCField[0]);
                  grid.addRow(5, relativeBox);
              }
              case "Trapezowa" -> {
                  currentAField[0] = new TextField();
                  currentBField[0] = new TextField();
                  currentCField[0] = new TextField();
                  currentDField[0] = new TextField();
                  grid.addRow(2, new Label("a:"), currentAField[0]);
                  grid.addRow(3, new Label("b:"), currentBField[0]);
                  grid.addRow(4, new Label("c:"), currentCField[0]);
                  grid.addRow(5, new Label("d:"), currentDField[0]);
                  grid.addRow(6, relativeBox);
              }
              case "Gaussa" -> {
                  currentCenterField[0] = new TextField();
                  currentSigmaField[0] = new TextField();
                  grid.addRow(2, new Label("c (środek):"), currentCenterField[0]);
                  grid.addRow(3, new Label("σ (odchylenie):"), currentSigmaField[0]);
                  grid.addRow(4, relativeBox);
              }
          }
      });

      dialog.getDialogPane().setContent(grid);
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

      dialog.setResultConverter(btn -> {
          if (btn == ButtonType.OK) {
              String name = nameField.getText();
              boolean isRelative = relativeBox.isSelected();
              FuzzySet set = null;

              switch (functionType.getValue()) {
                  case "Trójkątna" -> {
                      double a = Double.parseDouble(currentAField[0].getText());
                      double b = Double.parseDouble(currentBField[0].getText());
                      double c = Double.parseDouble(currentCField[0].getText());
                      set = FuzzySet.createWithDenseUniverse(
                          new TriangularFunction(a, b, c),
                          isRelative ? 0.0 : 0.0,
                          isRelative ? 1.0 : 10000.0,
                          isRelative ? 0.001 : 0.1
                      );
                  }
                  case "Trapezowa" -> {
                      double a = Double.parseDouble(currentAField[0].getText());
                      double b = Double.parseDouble(currentBField[0].getText());
                      double c = Double.parseDouble(currentCField[0].getText());
                      double d = Double.parseDouble(currentDField[0].getText());
                      set = FuzzySet.createWithDenseUniverse(
                          new TrapezoidalFunction(a, b, c, d),
                          isRelative ? 0.0 : 0.0,
                          isRelative ? 1.0 : 10000.0,
                          isRelative ? 0.001 : 0.1
                      );
                  }
                  case "Gaussa" -> {
                      double center = Double.parseDouble(currentCenterField[0].getText());
                      double sigma = Double.parseDouble(currentSigmaField[0].getText());
                      set = FuzzySet.createWithDenseUniverse(
                          new GaussianFunction(center, sigma),
                          isRelative ? 0.0 : 0.0,
                          isRelative ? 1.0 : 10000.0,
                          isRelative ? 0.001 : 0.1
                      );
                  }
              }
              return new Quantifier(name, set, isRelative);
          }
          return null;
      });

      Optional<Quantifier> result = dialog.showAndWait();
      result.ifPresent(q -> {
          QuantifierRegistry.addQuantifier(q.getName(), q);
          initializeQuantifiers();
      });
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

    private double parseWeightOrZero(TextField field) {
        String text = field.getText();
        if (text == null || text.isBlank()) return 0.0;
        return Double.parseDouble(text);
    }

    private List<Integer> getSelectedMultiForms() {
        List<Integer> forms = new ArrayList<>();
        if (form1Check.isSelected()) forms.add(1);
        if (form2Check.isSelected()) forms.add(2);
        if (form3Check.isSelected()) forms.add(3);
        if (form4Check.isSelected()) forms.add(4);
        return forms;
    }

    private void generateSummaries() {

        boolean isMulti = multiSummaryRadio.isSelected();
        List<Integer> selectedForms = isMulti ? getSelectedMultiForms() : List.of();
        boolean onlyForm4 = isMulti && selectedForms.size() == 1 && selectedForms.getFirst() == 4;

        // Dla jednopodmiotowych: wymagaj kwantyfikatora i sumaryzatora
        if (!isMulti && (summarizers.isEmpty() || quantifiers.isEmpty())) {
            System.out.println("Dla jednopodmiotowych musisz wybrać sumaryzator i kwantyfikator.");
            return;
        }
        // Dla wielopodmiotowych: dotychczasowa logika
        if (isMulti && (summarizers.isEmpty() || (quantifiers.isEmpty() && !onlyForm4 && !selectedForms.isEmpty()))) {
            System.out.println("Brak wybranych sumaryzatorów lub kwantyfikatorów.");
            return;
        }

        if (selectedQualityMeasures.isEmpty()) {
            System.out.println("Musisz wybrać co najmniej jedną miarę jakości.");
            return;
        }

        List<? extends LinguisticSummaryBase> summaries;

        if (!isMulti) {
            boolean useSecondForm = "Forma 2".equals(selectedForm);

            if (useSecondForm && summarizers.size() < 2) {
                System.out.println("Dla drugiej formy musisz wybrać co najmniej dwa sumaryzatory.");
                return;
            }

            // Jednopodmiotowe podsumowania
            summaries = SingleEntitySummaryGenerator.generateAllSummaries(quantifiers, summarizers, useSecondForm);

            for (LinguisticSummaryBase baseSummary : summaries) {
                if (!(baseSummary instanceof LinguisticSummary summary)) {
                    continue;
                }

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
                        case "T11" -> value = SingleEntityQualityMeasureCalculator.calculateT10(summary, dataRows);
                        case "Optimal Summary" -> {
                            try {
                                double w1 = parseWeightOrZero(weight1);
                                double w2 = parseWeightOrZero(weight2);
                                double w3 = parseWeightOrZero(weight3);
                                double w4 = parseWeightOrZero(weight4);
                                double w5 = parseWeightOrZero(weight5);
                                double w6 = parseWeightOrZero(weight6);
                                double w7 = parseWeightOrZero(weight7);
                                double w8 = parseWeightOrZero(weight8);
                                double w9 = parseWeightOrZero(weight9);
                                double w10 = parseWeightOrZero(weight10);
                                double w11 = parseWeightOrZero(weight11);
                                value = SingleEntityQualityMeasureCalculator.extendedOptimalSummary(
                                        summary, dataRows, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11
                                );
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
        } else {
            if (selectedForms.isEmpty()) {
                System.out.println("Wybierz przynajmniej jedną formę dla wielopodmiotowych.");
                return;
            }

            List<LinguisticSummaryBase> allSummaries = new ArrayList<>();
            List<String> uniqueGroups = dataRows.stream()
                    .map(row -> row.getStringValue("Gender"))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            for (int formNumber : selectedForms) {
                boolean needsTwoSummarizers = formNumber == 2 || formNumber == 3;
                if (needsTwoSummarizers && summarizers.size() < 2) {
                    System.out.println("Dla formy " + formNumber + " musisz wybrać co najmniej dwa sumaryzatory.");
                    return;
                }

                summaries = MultipleEntitySummaryGenerator.generateAllSummaries(
                        quantifiers, summarizers, formNumber, uniqueGroups);

                for (LinguisticSummaryBase baseSummary : summaries) {
                    if (baseSummary instanceof MultipleEntityLinguisticSummary summary) {
                        double t = MultipleEntityQualityMeasureCalculator.calculateT(summary, dataRows);
                        summary.setQualityMeasure("T", t);
                    }
                }
                allSummaries.addAll(summaries);
            }
            summaries = allSummaries;
        }

        // Sortowanie
        String selectedSortMeasure = qualitySortComboBox.getValue();
        if (selectedSortMeasure != null && !selectedSortMeasure.isEmpty()) {
            summaries.sort(Comparator.comparingDouble((LinguisticSummaryBase s) -> s.getQualityMeasure(selectedSortMeasure)).reversed());
            System.out.println("\nPodsumowania posortowane malejąco według " + selectedSortMeasure + ":");
        } else {
            System.out.println("\nPodsumowania bez sortowania:");
        }

        // Kolumna numer wiersza
        summaryTable.getColumns().clear();

        TableColumn<Map<String, String>, String> rowNumberCol = new TableColumn<>("#");
        rowNumberCol.setPrefWidth(40);
        rowNumberCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(String.valueOf(summaryTable.getItems().indexOf(cellData.getValue()) + 1))
        );

        summaryTable.getColumns().add(rowNumberCol);

        //Kolumna summary
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
        summaryTable.getColumns().add(summaryCol);

        for (String measure : selectedQualityMeasures) {
            if (measure.equals("summary")) continue;
            TableColumn<Map<String, String>, String> col = new TableColumn<>(measure);
            col.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(measure)));
            summaryTable.getColumns().add(col);
        }

        // Dodanie danych do tabeli
        summaryTable.getItems().clear();
        for (LinguisticSummaryBase baseSummary : summaries) {
            Map<String, String> row = new HashMap<>();
            row.put("summary", baseSummary.toString());
            for (String measure : selectedQualityMeasures) {
                row.put(measure, String.format("%.3f", baseSummary.getQualityMeasure(measure)));
            }
            summaryTable.getItems().add(row);
        }

        // Wypisywanie podsumowań w konsoli
        for (LinguisticSummaryBase baseSummary : summaries) {
            System.out.println(baseSummary);
        }
    }


}
