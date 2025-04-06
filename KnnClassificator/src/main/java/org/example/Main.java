package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.*;

public class Main {
    private static final Set<String> SKIP_KEYS = Set.of();
    public static void removeUnwantedKeys(List<ExtractedFeatures> features) {
        for (ExtractedFeatures ef : features) {
            Map<String, Object> feats = ef.getFeatures();
            feats.keySet().removeIf(key -> SKIP_KEYS.contains(key));
        }
    }
    public static void normalizeFeatures(List<ExtractedFeatures> features) {
        if (features.isEmpty()) return;

        Map<String, Double> minValues = new HashMap<>();
        Map<String, Double> maxValues = new HashMap<>();

        for (ExtractedFeatures ef : features) {
            Map<String, Object> feats = ef.getFeatures();
            for (Map.Entry<String, Object> entry : feats.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Number num) {
                    double val = num.doubleValue();
                    minValues.put(key, Math.min(minValues.getOrDefault(key, Double.MAX_VALUE), val));
                    maxValues.put(key, Math.max(maxValues.getOrDefault(key, -Double.MAX_VALUE), val));
                }
            }
        }

        for (ExtractedFeatures ef : features) {
            Map<String, Object> feats = ef.getFeatures();
            for (Map.Entry<String, Object> entry : feats.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

 if (value instanceof Number num) {
                    double val = num.doubleValue();
                    double min = minValues.get(key);
                    double max = maxValues.get(key);

                    if (max - min != 0) {
                        double normalized = (val - min) / (max - min);
                        feats.put(key, normalized);
                    } else {
                        feats.put(key, 0.0);
                    }
                }
            }
        }
    }


    public static List<ExtractedFeatures> loadFromFile(String file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ExtractedFeatures>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<ExtractedFeatures> loadedFeatures = loadFromFile("ExtractedFeatures.ser");
        if (loadedFeatures == null) {
            System.out.println("Błąd wczytywania danych.");
            return;
        }

        int splitPercentage = 20;
        String metric = "euclidean";
        int k = 5;

        removeUnwantedKeys(loadedFeatures);
        normalizeFeatures(loadedFeatures);

        for (ExtractedFeatures feat :loadedFeatures) {
            System.out.println(feat);
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj procent danych do zestawu treningowego (0-100%): ");
        //int splitPercentage = scanner.nextInt();


        Map<String, List<ExtractedFeatures>> splitData = SplitData.stratifiedSplit(loadedFeatures, splitPercentage);

        List<ExtractedFeatures> trainSet = splitData.get("trainSet");
        List<ExtractedFeatures> testSet = splitData.get("testSet");

        System.out.println("Wybierz metrykę:");
        System.out.println("1 - Euklidesowa");
        System.out.println("2 - Manhattan");
        System.out.println("3 - Chebyshev");

        int metricChoice;
//        String metric = "";
//        do {
//            System.out.print("Podaj numer (1-3): ");
//            metricChoice = scanner.nextInt();
//            switch (metricChoice) {
//                case 1 -> metric = "euclidean";
//                case 2 -> metric = "manhattan";
//                case 3 -> metric = "chebyshev";
//                default -> System.out.println("Niepoprawny wybór. Spróbuj ponownie.");
//            }
//        } while (metric.isEmpty());

//        System.out.print("Podaj ilość sąsiadów: ");
//        int k = scanner.nextInt();
        KNNClassifier knn = new KNNClassifier(trainSet, k);

        int correct = 0;
        for (ExtractedFeatures testInstance : testSet) {
            String predictedLabel = knn.classify(testInstance, metric);
            System.out.println("Prawdziwa etykieta: " + testInstance.getPlace() + ", Przewidziana: " + predictedLabel);
            if (predictedLabel.equals(testInstance.getPlace())) {
                correct++;
            }
        }

        double accuracy = (correct / (double) testSet.size()) * 100;
        System.out.println("Dokładność klasyfikacji: " + (double)Math.round(accuracy * 100d) / 100d + "%");


        //System.out.println("Rozmiar zestawu treningowego: " + trainSet.size());
        //System.out.println("Rozmiar zestawu testowego: " + testSet.size());

//        Map<String, Integer> countryCount = new HashMap<>();
//
//        for (ExtractedFeatures feature : trainSet) {
//            String place = feature.getPlace();
//            countryCount.put(place, countryCount.getOrDefault(place, 0) + 1);
//        }
//
//        for (ExtractedFeatures feature : testSet) {
//            String place = feature.getPlace();
//            countryCount.put(place, countryCount.getOrDefault(place, 0) + 1);
//        }
//
//        System.out.println("Liczba artykułów w całej grupie (treningowej i testowej) dla każdego kraju:");
//        for (Map.Entry<String, Integer> entry : countryCount.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }

    }
}