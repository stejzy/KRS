package org.example;

import java.io.*;
import java.util.*;

public class SplitData {

    public static Map<String, List<ExtractedFeatures>> stratifiedSplit(List<ExtractedFeatures> loadedFeatures, int splitPercentage) throws IOException {
        String fileName = splitPercentage + "splitData.ser";

        File file = new File(fileName);
        if (file.exists()) {
            return readSplitDataFromFile(fileName);
        } else {
            Map<String, List<ExtractedFeatures>> splitData = performStratifiedSplit(loadedFeatures, splitPercentage);
            saveSplitDataToFile(splitData, fileName);
            return splitData;
        }
    }

    private static Map<String, List<ExtractedFeatures>> performStratifiedSplit(List<ExtractedFeatures> loadedFeatures, int splitPercentage) {
        Map<String, List<ExtractedFeatures>> groupedArticles = new HashMap<>();

        for (ExtractedFeatures feature : loadedFeatures) {
            String label = feature.getPlace();
            groupedArticles.putIfAbsent(label, new ArrayList<>());
            groupedArticles.get(label).add(feature);
        }


        List<ExtractedFeatures> trainSet = new ArrayList<>();
        List<ExtractedFeatures> testSet = new ArrayList<>();

        for (String label : groupedArticles.keySet()) {
            List<ExtractedFeatures> articlesInClass = groupedArticles.get(label);

            //System.out.println(label + " " + articlesInClass.size());

            int trainingSize = (int) (articlesInClass.size() * (splitPercentage / 100.0));
            int testSize = articlesInClass.size() - trainingSize;

            if (articlesInClass.size() < 5) {
                trainingSize = articlesInClass.size();
                testSize = 0;
            }

            //Collections.shuffle(articlesInClass);

            trainSet.addAll(articlesInClass.subList(0, trainingSize));

            if (testSize > 0) {
                testSet.addAll(articlesInClass.subList(trainingSize, articlesInClass.size()));
            }
        }

        Map<String, List<ExtractedFeatures>> result = new HashMap<>();
        result.put("trainSet", trainSet);
        result.put("testSet", testSet);

        return result;
    }

    private static void saveSplitDataToFile(Map<String, List<ExtractedFeatures>> splitData, String fileName) throws IOException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            objectOutputStream.writeObject(splitData);
        }
    }

    private static Map<String, List<ExtractedFeatures>> readSplitDataFromFile(String fileName) throws IOException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Map<String, List<ExtractedFeatures>>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error reading split data from file", e);
        }
    }
}
