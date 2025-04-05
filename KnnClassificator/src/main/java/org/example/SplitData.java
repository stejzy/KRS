package org.example;

import java.io.*;
import java.util.*;

public class SplitData {

    // Metoda do podziału danych na zestawy treningowy i testowy
    public static Map<String, List<ExtractedFeatures>> stratifiedSplit(List<ExtractedFeatures> loadedFeatures, int splitPercentage) throws IOException {
        String fileName = splitPercentage + "splitData.ser";  // Dynamiczna nazwa pliku

        // Sprawdzenie, czy plik już istnieje
        File file = new File(fileName);
        if (file.exists()) {
            // Jeśli plik istnieje, odczytaj dane
            return readSplitDataFromFile(fileName);
        } else {
            // Jeśli plik nie istnieje, wykonaj podział danych i zapisz je
            Map<String, List<ExtractedFeatures>> splitData = performStratifiedSplit(loadedFeatures, splitPercentage);
            saveSplitDataToFile(splitData, fileName);
            return splitData;
        }
    }

    // Metoda do wykonania stratified split z dynamiczną proporcją
    private static Map<String, List<ExtractedFeatures>> performStratifiedSplit(List<ExtractedFeatures> loadedFeatures, int splitPercentage) {
        // Mapa do grupowania artykułów według etykiety (place)
        Map<String, List<ExtractedFeatures>> groupedArticles = new HashMap<>();

        // Grupowanie artykułów według etykiety (place)
        for (ExtractedFeatures feature : loadedFeatures) {
            String label = feature.getPlace();  // Etykieta artykułu to 'place'
            groupedArticles.putIfAbsent(label, new ArrayList<>());
            groupedArticles.get(label).add(feature);
        }

//        for (Map.Entry<String, List<ExtractedFeatures>> elo : groupedArticles.entrySet()) {
//            System.out.println("Key: " + elo.getKey());
//            System.out.println("Value: " + elo.getValue());
//        }


        // Zbiory dla danych treningowych i testowych
        List<ExtractedFeatures> trainSet = new ArrayList<>();
        List<ExtractedFeatures> testSet = new ArrayList<>();

        // Ustal liczbę próbek do wyboru z każdej klasy
        for (String label : groupedArticles.keySet()) {
            List<ExtractedFeatures> articlesInClass = groupedArticles.get(label);

            System.out.println(label + " " + articlesInClass.size());

            // Oblicz liczbę próbek, które mają trafić do zestawu treningowego (np. 20% z tej klasy)
            int trainingSize = (int) (articlesInClass.size() * (splitPercentage / 100.0));  // SplitPercentage to procent danych do treningu
            int testSize = articlesInClass.size() - trainingSize;                            // Pozostałe do testu

            //5 to 75% najmniejszego zbioru

            // Jeśli liczba prób w danej klasie jest mniejsza niż wymagany procent (np. 20%), używamy wszystkich danych
            if (articlesInClass.size() < 5) {  // Może być zmieniona ta liczba w zależności od potrzeby
                trainingSize = articlesInClass.size();  // Użyj wszystkich prób w przypadku zbyt małej liczby
                testSize = 0;
            }

            // Losowe mieszanie artykułów w danej klasie
            Collections.shuffle(articlesInClass);

            // Wybieramy 'splitPercentage'% próbek z danej klasy do zestawu treningowego
            trainSet.addAll(articlesInClass.subList(0, trainingSize));

            // Pozostałe próbki trafiają do zestawu testowego
            if (testSize > 0) {
                testSet.addAll(articlesInClass.subList(trainingSize, articlesInClass.size()));
            }
        }

        // Tworzymy mapę z wynikami
        Map<String, List<ExtractedFeatures>> result = new HashMap<>();
        result.put("trainSet", trainSet);
        result.put("testSet", testSet);

        return result;
    }

    // Metoda do zapisywania zestawów danych do pliku
    private static void saveSplitDataToFile(Map<String, List<ExtractedFeatures>> splitData, String fileName) throws IOException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            objectOutputStream.writeObject(splitData);
        }
    }

    // Metoda do odczytywania zestawów danych z pliku
    private static Map<String, List<ExtractedFeatures>> readSplitDataFromFile(String fileName) throws IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Map<String, List<ExtractedFeatures>>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error reading split data from file", e);
        }
    }
}
