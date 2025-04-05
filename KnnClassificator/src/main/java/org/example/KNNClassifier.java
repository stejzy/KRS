package org.example;

import java.util.*;

public class KNNClassifier {
    private final List<ExtractedFeatures> trainSet;
    private final int k;

    public KNNClassifier(List<ExtractedFeatures> trainSet, int k) {
        this.trainSet = trainSet;
        this.k = k;
    }

    public String classify(ExtractedFeatures testInstance, String metric) {
        PriorityQueue<Map.Entry<ExtractedFeatures, Double>> nearestNeighbors =
                new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        for (ExtractedFeatures trainInstance : trainSet) {
            double distance = calculateDistance(testInstance, trainInstance, metric);
            nearestNeighbors.add(Map.entry(trainInstance, distance));
        }

        // Pobierz K najbliższych sąsiadów
        Map<String, Integer> labelCount = new HashMap<>();
        for (int i = 0; i < k && !nearestNeighbors.isEmpty(); i++) {
            ExtractedFeatures neighbor = nearestNeighbors.poll().getKey();
            String label = neighbor.getPlace();
            labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        }

        // Znajdź najczęściej występującą etykietę
        return labelCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    private double calculateDistance(ExtractedFeatures f1, ExtractedFeatures f2, String metric) {
        return switch (metric.toLowerCase()) {
            case "chebyshev" -> Chebyshev.calculateDistance(f1, f2);
            case "manhattan" -> Manhattan.calculateDistance(f1, f2);
            case "euclidean" -> Euklides.calculateDistance(f1, f2);
            default -> throw new IllegalArgumentException("Nieznana metryka: " + metric);
        };
    }
}