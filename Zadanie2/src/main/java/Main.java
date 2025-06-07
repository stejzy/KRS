import linguistic.summary.*;
import utils.DataRow;
import utils.PostgresToDataRowLoader;

import java.util.*;


public class Main {
    public static void main(String[] args) {

        // Załaduj dane z bazy danych PostgreSQL
        List<DataRow> dataRows = PostgresToDataRowLoader.loadDataRows();

//        for (int i = 0; i < dataRows.size(); i++) {
//            System.out.println("Row " + (i + 1) + ": " + dataRows.get(i));
//        }


        Scanner scanner = new Scanner(System.in);
        List<Summarizer> summarizers = new ArrayList<>();
        List<Quantifier> quantifiers = new ArrayList<>();

        // === KROK 1: WYBÓR LICZNOSCI PODMIOTOW PODSUMOWANIA ===
        System.out.print("Podsumowanie jednopodmiotowe (1) czy wielopodmiotowe (2)? ");
        int summaryType = scanner.nextInt();

        // === KROK 2: WYBÓR FORMY PODSUMOWANIA ===
        int maxForms = (summaryType == 1) ? 2 : 4;
        System.out.print("Wybierz formę podsumowania (1-" + maxForms + "): ");
        int formNumber = scanner.nextInt();

        if (formNumber < 1 || formNumber > maxForms) {
            System.out.println("Nieprawidłowa forma. Zakończono program.");
            return;
        }

        // === KROK 2: TWORZENIE SUMARYZATORÓW ===
        while (true) {
            Map<String, LinguisticVariable> variables = LinguisticVariableRegistry.getAllLinguisticVariables();
            List<String> variableNames = new ArrayList<>(variables.keySet());

            System.out.println("\nAvailable Linguistic Variables:");
            for (int i = 0; i < variableNames.size(); i++) {
                System.out.println((i + 1) + ". " + variableNames.get(i));
            }
            System.out.print("Enter the number of the variable (or type 'end' to finish summarizers): ");

            String input = scanner.next();
            if (input.equalsIgnoreCase("end") || input.equalsIgnoreCase("zakończ")) break;

            int variableChoice;
            try {
                variableChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (variableChoice < 1 || variableChoice > variableNames.size()) {
                System.out.println("Invalid variable choice.");
                continue;
            }

            String selectedVarName = variableNames.get(variableChoice - 1);
            LinguisticVariable selectedVar = variables.get(selectedVarName);

            List<String> labels = new ArrayList<>(selectedVar.getLabels());
            System.out.println("Labels for variable \"" + selectedVarName + "\":");
            for (int i = 0; i < labels.size(); i++) {
                System.out.println((i + 1) + ". " + labels.get(i));
            }

            System.out.print("Enter the number of the label to create a summarizer: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid label input.");
                scanner.next(); // discard invalid input
                continue;
            }

            int labelChoice = scanner.nextInt();
            if (labelChoice < 1 || labelChoice > labels.size()) {
                System.out.println("Invalid label choice.");
                continue;
            }

            String selectedLabel = labels.get(labelChoice - 1);
            Summarizer summarizer = new Summarizer(selectedLabel, selectedVar);
            summarizers.add(summarizer);

            System.out.println("Summarizer created:");
            System.out.println(" - Variable: " + summarizer.getVariable());
            System.out.println(" - Label: " + summarizer.getLabel());
        }



        // === KROK 3: WYBÓR KWANTYFIKATORÓW Z REJESTRU ===
        System.out.println("\nAvailable quantifiers (type 'end' to finish):");

        Map<String, Quantifier> availableQuantifiers = QuantifierRegistry.getAll();

        // Podziel kwantyfikatory na względne i bezwzględne
        List<String> relativeQuantifiers = new ArrayList<>();
        List<String> absoluteQuantifiers = new ArrayList<>();

        for (Map.Entry<String, Quantifier> entry : availableQuantifiers.entrySet()) {
            if (entry.getValue().isRelative()) {
                relativeQuantifiers.add(entry.getKey());
            } else {
                absoluteQuantifiers.add(entry.getKey());
            }
        }

// Połącz listy — względne najpierw, potem bezwzględne
        List<String> quantifierNames = new ArrayList<>();
        quantifierNames.addAll(relativeQuantifiers);
        quantifierNames.addAll(absoluteQuantifiers);

        while (true) {
            System.out.println("\nQuantifier options:");

            if (!relativeQuantifiers.isEmpty()) {
                System.out.println("Relative quantifiers:");
                for (int i = 0; i < relativeQuantifiers.size(); i++) {
                    System.out.println((i + 1) + ". " + relativeQuantifiers.get(i));
                }
            }

            if (formNumber == 1 && !absoluteQuantifiers.isEmpty()) {
                System.out.println("Absolute quantifiers:");
                for (int i = 0; i < absoluteQuantifiers.size(); i++) {
                    // Indeks kontynuujemy po względnych
                    System.out.println((relativeQuantifiers.size() + i + 1) + ". " + absoluteQuantifiers.get(i));
                }
            }

            System.out.print("Enter the number of the quantifier (or 'end' to finish): ");
            String input = scanner.next();

            if (input.equalsIgnoreCase("end") || input.equalsIgnoreCase("zakończ")) break;

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            if (choice < 1 || choice > quantifierNames.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            String selectedName = quantifierNames.get(choice - 1);
            Quantifier selectedQuantifier = availableQuantifiers.get(selectedName);
            quantifiers.add(selectedQuantifier);

            System.out.println("Quantifier added: " + selectedQuantifier.getName());
        }




        // === KROK 4: GENEROWANIE PODSUMOWAN ===
        List<? extends LinguisticSummaryBase> summaries;

        if (summaryType == 1) {
            boolean useSecondForm = (formNumber == 2);
            summaries = SingleEntitySummaryGenerator.generateAllSummaries(quantifiers, summarizers, useSecondForm);
        } else {
            List<String> uniqueGenders = dataRows.stream()
                    .map(row -> row.getStringValue("Gender"))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            summaries = MultipleEntitySummaryGenerator.generateAllSummaries(quantifiers, summarizers, formNumber, uniqueGenders);
        }


        // === KROK 5: OBLICZANIE MIAR JAKOŚCI DLA KAŻDEGO PODSUMOWANIA ===
        for (LinguisticSummaryBase summary : summaries) {
            if (summaryType == 1 && summary instanceof LinguisticSummary singleSummary) {
                double t1 = SingleEntityQualityMeasureCalculator.calculateT1(singleSummary, dataRows);
                double t2 = SingleEntityQualityMeasureCalculator.calculateT2(singleSummary, dataRows);
                double t3 = SingleEntityQualityMeasureCalculator.calculateT3(singleSummary, dataRows);
                double t4 = SingleEntityQualityMeasureCalculator.calculateT4(singleSummary, dataRows);
                double t5 = SingleEntityQualityMeasureCalculator.calculateT5(singleSummary, dataRows);
                double t6 = SingleEntityQualityMeasureCalculator.calculateT6(singleSummary, dataRows);
                double t7 = SingleEntityQualityMeasureCalculator.calculateT7(singleSummary, dataRows);
                double t8 = SingleEntityQualityMeasureCalculator.calculateT8(singleSummary, dataRows);
                double t9 = SingleEntityQualityMeasureCalculator.calculateT9(singleSummary, dataRows);
                double t10 = SingleEntityQualityMeasureCalculator.calculateT10(singleSummary, dataRows);

                singleSummary.setQualityMeasure("T1", t1);
                singleSummary.setQualityMeasure("T2", t2);
                singleSummary.setQualityMeasure("T3", t3);
                singleSummary.setQualityMeasure("T4", t4);
                singleSummary.setQualityMeasure("T5", t5);
                singleSummary.setQualityMeasure("T6", t6);
                singleSummary.setQualityMeasure("T7", t7);
                singleSummary.setQualityMeasure("T8", t8);
                singleSummary.setQualityMeasure("T9", t9);
                singleSummary.setQualityMeasure("T10", t10);
            } else if (summaryType == 2 && summary instanceof MultipleEntityLinguisticSummary multiSummary) {
                double qualityMeasureT = MultipleEntityQualityMeasureCalculator.calculateT(multiSummary, dataRows);

                multiSummary.setQualityMeasure("T", qualityMeasureT);
            }
        }

        // === KROK 6: WYPISYWANIE PODSUMOWAŃ ===
        System.out.println("\nGenerated linguistic summaries:");
        for (LinguisticSummaryBase summary : summaries) {
            System.out.println(summary);
        }

        scanner.close();



    }
}
