package org.example;

import org.example.ExtractedFeatures;
import org.example.metrics.Chebyshev;
import org.example.metrics.Euklides;
import org.example.metrics.Manhattan;

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

        Map<String, Integer> labelCount = new HashMap<>();
        List<Map.Entry<ExtractedFeatures, Double>> topNeighbors = new ArrayList<>();

        for (int i = 0; i < k && !nearestNeighbors.isEmpty(); i++) {
            Map.Entry<ExtractedFeatures, Double> neighborEntry = nearestNeighbors.poll();
            topNeighbors.add(neighborEntry);
            String label = neighborEntry.getKey().getPlace();
            labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        }

        // Znajdź maksymalną liczbę głosów
        int maxVotes = Collections.max(labelCount.values());

        // Zbierz wszystkie etykiety, które mają maksymalną liczbę głosów (remis)
        List<String> tiedLabels = labelCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();

        // Jeśli tylko jedna etykieta - zwróć ją
        if (tiedLabels.size() == 1) {
            return tiedLabels.get(0);
        }

        // Jeśli jest remis - wybierz najbliższego sąsiada spośród remisujących
        String closestLabel = null;
        double closestDistance = Double.MAX_VALUE;

        for (Map.Entry<ExtractedFeatures, Double> neighbor : topNeighbors) {
            String label = neighbor.getKey().getPlace();
            if (tiedLabels.contains(label) && neighbor.getValue() < closestDistance) {
                closestDistance = neighbor.getValue();
                closestLabel = label;
            }
        }

        return closestLabel;
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