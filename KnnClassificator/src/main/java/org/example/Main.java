package org.example;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.List;
import java.util.*;

//metryki

public class Main {

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

        KNNClassifier knn = new KNNClassifier(trainSet, 20);

        // Klasyfikacja testowego zestawu
        int correct = 0;
        for (ExtractedFeatures testInstance : testSet) {
            String predictedLabel = knn.classify(testInstance, metric);
            //System.out.println("Prawdziwa etykieta: " + testInstance.getPlace() + ", Przewidziana: " + predictedLabel);
            if (predictedLabel.equals(testInstance.getPlace())) {
                correct++;
            }
        }

        // Obliczenie i wyświetlenie dokładności
        double accuracy = (correct / (double) testSet.size()) * 100;
        System.out.println("Dokładność klasyfikacji: " + accuracy + "%");



        // Wypisanie rozmiarów zestawów
//        System.out.println("Rozmiar zestawu treningowego: " + trainSet.size());
//        System.out.println("Rozmiar zestawu testowego: " + testSet.size());

//        Map<String, Integer> countryCount = new HashMap<>();
//// Iteracja po zestawie treningowym
//        for (ExtractedFeatures feature : trainSet) {
//            String place = feature.getPlace();
//            countryCount.put(place, countryCount.getOrDefault(place, 0) + 1);
//        }
//
//// Iteracja po zestawie testowym
//        for (ExtractedFeatures feature : testSet) {
//            String place = feature.getPlace();
//            countryCount.put(place, countryCount.getOrDefault(place, 0) + 1);
//        }
//
//// Wypisanie liczby wystąpień każdego kraju w całej grupie
//        System.out.println("Liczba artykułów w całej grupie (treningowej i testowej) dla każdego kraju:");
//        for (Map.Entry<String, Integer> entry : countryCount.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }

    }
}