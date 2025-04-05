package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.*;

//metryki

public class Main {

    public static void normalizeFeatures(List<ExtractedFeatures> features) {
        if (features.isEmpty()) return;

        // Mapowanie: nazwa cechy -> [min, max]
        Map<String, Double> minValues = new HashMap<>();
        Map<String, Double> maxValues = new HashMap<>();

        // Szukanie min i max dla każdej cechy
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

        // Normalizacja cech
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
                        feats.put(key, 0.0); // stała wartość, ustaw na 0
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
        // Wczytanie danych z pliku .ser
        List<ExtractedFeatures> loadedFeatures = loadFromFile("ExtractedFeatures.ser");
        if (loadedFeatures == null) {
            System.out.println("Błąd wczytywania danych.");
            return;
        }
        // Wyświetlenie liczby wczytanych danych
        //System.out.println("Liczba wczytanych danych: " + loadedFeatures.size());

        // Wprowadzenie przez użytkownika procentowego podziału
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj procent danych do zestawu treningowego (0-100%): ");
        int splitPercentage = scanner.nextInt();

        // Wywołanie metody stratifiedSplit, aby podzielić dane lub wczytać je z pliku
        Map<String, List<ExtractedFeatures>> splitData = SplitData.stratifiedSplit(loadedFeatures, splitPercentage);

        // Pobieranie wyników podziału
        List<ExtractedFeatures> trainSet = splitData.get("trainSet");
        List<ExtractedFeatures> testSet = splitData.get("testSet");

        // Wybór metryki
        System.out.println("Wybierz metrykę dystansu:");
        System.out.println("1 - Euklidesowa");
        System.out.println("2 - Manhattan");
        System.out.println("3 - Chebyshev");

        int metricChoice;
        String metric = "";
        do {
            System.out.print("Podaj numer (1-3): ");
            metricChoice = scanner.nextInt();
            switch (metricChoice) {
                case 1 -> metric = "euclidean";
                case 2 -> metric = "manhattan";
                case 3 -> metric = "chebyshev";
                default -> System.out.println("Niepoprawny wybór. Spróbuj ponownie.");
            }
        } while (metric.isEmpty());

        System.out.print("Podaj ilość somsiadów: ");
        int k = scanner.nextInt();
        KNNClassifier knn = new KNNClassifier(trainSet, k);

        // Klasyfikacja testowego zestawu
        int correct = 0;
        for (ExtractedFeatures testInstance : testSet) {
            String predictedLabel = knn.classify(testInstance, metric);
            System.out.println("Prawdziwa etykieta: " + testInstance.getPlace() + ", Przewidziana: " + predictedLabel);
            if (predictedLabel.equals(testInstance.getPlace())) {
                correct++;
            }
        }

        // Obliczenie i wyświetlenie dokładności
        double accuracy = (correct / (double) testSet.size()) * 100;
        System.out.println("Dokładność klasyfikacji: " + (double)Math.round(accuracy * 100d) / 100d + "%");



        // Wypisanie rozmiarów zestawów
        System.out.println("Rozmiar zestawu treningowego: " + trainSet.size());
        System.out.println("Rozmiar zestawu testowego: " + testSet.size());

        Map<String, Integer> countryCount = new HashMap<>();
// Iteracja po zestawie treningowym
        for (ExtractedFeatures feature : trainSet) {
            String place = feature.getPlace();
            countryCount.put(place, countryCount.getOrDefault(place, 0) + 1);
        }

// Iteracja po zestawie testowym
        for (ExtractedFeatures feature : testSet) {
            String place = feature.getPlace();
            countryCount.put(place, countryCount.getOrDefault(place, 0) + 1);
        }

// Wypisanie liczby wystąpień każdego kraju w całej grupie
        System.out.println("Liczba artykułów w całej grupie (treningowej i testowej) dla każdego kraju:");
        for (Map.Entry<String, Integer> entry : countryCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }
}