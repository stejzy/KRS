import linguistic.summary.*;
import utils.DataRow;
import utils.PostgresToDataRowLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


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

        // === KROK 1: WYBÓR FORMY===
        System.out.print("Use second form? (true/false): ");
        boolean useSecondForm = scanner.nextBoolean();

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

            if (!useSecondForm && !absoluteQuantifiers.isEmpty()) {
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
        List<LinguisticSummary> summaries = SingleEntitySummaryGenerator.generateAllSummaries(quantifiers, summarizers, useSecondForm);



        // === KROK 5: OBLICZANIE MIAR JAKOŚCI DLA KAŻDEGO PODSUMOWANIA ===
        for (LinguisticSummary summary : summaries) {
            double t1 = SingleEntityQualityMeasureCalculator.calculateT1(summary, dataRows);
            double t2 = SingleEntityQualityMeasureCalculator.calculateT2(summary, dataRows);
            double t3 = SingleEntityQualityMeasureCalculator.calculateT3(summary, dataRows);
            double t4 = SingleEntityQualityMeasureCalculator.calculateT4(summary, dataRows);
            double t5 = SingleEntityQualityMeasureCalculator.calculateT5(summary, dataRows);
            double t6 = SingleEntityQualityMeasureCalculator.calculateT6(summary, dataRows);
//            double t7 = SingleEntityQualityMeasureCalculator.calculateT7(summary, dataRows);
//            double t8 = SingleEntityQualityMeasureCalculator.calculateT8(summary, dataRows);
//            double t9 = SingleEntityQualityMeasureCalculator.calculateT9(summary, dataRows);
//            double t10 = SingleEntityQualityMeasureCalculator.calculateT10(summary, dataRows);

            summary.setQualityMeasure("T1", t1);
//            summary.setQualityMeasure("T2", t2);
//            summary.setQualityMeasure("T3", t3);
//            summary.setQualityMeasure("T4", t4);
//            summary.setQualityMeasure("T5", t5);
            summary.setQualityMeasure("T6", t6);
//            summary.setQualityMeasure("T7", t7);
//            summary.setQualityMeasure("T8", t8);
//            summary.setQualityMeasure("T9", t9);
//            summary.setQualityMeasure("T10", t10);
        }

        // === KROK 4: WYPISYWANIE PODSUMOWAŃ ===
        System.out.println("\nGenerated linguistic summaries:");
        for (LinguisticSummary summary : summaries) {
            System.out.println(summary);
        }

        scanner.close();



    }
}
