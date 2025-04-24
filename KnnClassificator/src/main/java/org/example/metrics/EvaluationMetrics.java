package org.example.metrics;

import org.example.ExtractedFeatures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationMetrics {

    public static void evaluate(List<ExtractedFeatures> testSet, List<String> predictions) {
        int correct = 0;
        Map<String, Integer> tp = new HashMap<>();
        Map<String, Integer> fp = new HashMap<>();
        Map<String, Integer> fn = new HashMap<>();
        Map<String, Integer> labelCounts = new HashMap<>();

        for (int i = 0; i < testSet.size(); i++) {
            String actual = testSet.get(i).getPlace();
            String predicted = predictions.get(i);

            if (actual.equals(predicted)) {
                correct++;
                tp.put(actual, tp.getOrDefault(actual, 0) + 1);
            } else {
                fp.put(predicted, fp.getOrDefault(predicted, 0) + 1);
                fn.put(actual, fn.getOrDefault(actual, 0) + 1);
            }

            labelCounts.put(actual, labelCounts.getOrDefault(actual, 0) + 1);
        }

        double accuracy = (correct / (double) testSet.size()) * 100;
        System.out.println("Dokładność (Accuracy): " + Math.round(accuracy * 100.0) / 100.0 + "%");

        System.out.println("\nPrecyzja, Czułość (Recall) i F1-score dla każdej klasy:");
        for (String label : labelCounts.keySet()) {
            int truePos = tp.getOrDefault(label, 0);
            int falsePos = fp.getOrDefault(label, 0);
            int falseNeg = fn.getOrDefault(label, 0);

            double precision = truePos + falsePos == 0 ? 0 : (double) truePos / (truePos + falsePos);
            double recall = truePos + falseNeg == 0 ? 0 : (double) truePos / (truePos + falseNeg);
            double f1 = precision + recall == 0 ? 0 : 2 * precision * recall / (precision + recall);

            System.out.printf("Klasa: %s\n", label);
            System.out.printf("  Precision: %.2f\n", precision);
            System.out.printf("  Recall: %.2f\n", recall);
            System.out.printf("  F1-score: %.2f\n\n", f1);
        }
    }
}
