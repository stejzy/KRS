package org.example;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import org.example.stopWords.DocumentCleaner;
import org.example.stopWords.StopWordLoader;

import javax.print.Doc;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
//        System.out.println("Podaj zakres dokumentów do wczytania (0-21): ");
//        Scanner scanner = new Scanner(System.in);
        int start = 0;
        int end = 0;
//        do {
//            System.out.println("Początek (0-21): ");
//            while(!scanner.hasNextInt()) {
//                System.out.println("Podaj liczbę!");
//                scanner.next();
//            }
//            start = scanner.nextInt();
//        } while (start < 0 || start > 21);
//        do {
//            System.out.println("Koniec  (" + start + "-21): ");
//            while(!scanner.hasNextInt()) {
//                System.out.println("Podaj liczbę!");
//                scanner.next();
//            }
//            end = scanner.nextInt();
//        } while (end < start || end > 21);

        //Wczytanie dokumentów z plików SGM
        List<Document> extractedDocuments = SGMReader.readFiles(start, end);
        System.out.println(extractedDocuments.size() + " dokumentów wczytano");

        //NOTE_FOR_ME: Zapisanie dokumnentów do pliku .txt, w przystępniejszej formie do analizy
        FileSaver.saveDocumentsToFile(extractedDocuments, "basicDoc/doc.txt");

        //NOTE_FOR_ME: Oczyszczanie dokumentów z niepotrzebnych słów i ich zapis
        Set<String> stopWords = StopWordLoader.loadStopWordsFromFile("stopWords.json");
        List<Document> cleanedDocuments = DocumentCleaner.cleanDocuments(extractedDocuments, stopWords);
        FileSaver.saveDocumentsToFile(cleanedDocuments, "cleanDoc/doc.txt");


        //Ekstrakcja cech z pojedynczego dokumentu
        ExtractedFeatures features = FeatureExtractor.extractFeatures(extractedDocuments.get(186));
        System.out.println(features);

//        List<Document> brokenDocuments = new ArrayList<>();
//        int i = 0;
//        for (Document doc : cleanedDocuments) {
//            ExtractedFeatures features = FeatureExtractor.extractFeatures(doc);
//
//            Object currencyFeature = features.getFeature("Currency");
//            System.out.println(i++);
//
//            if (currencyFeature != null && currencyFeature.equals("none")) {
//                brokenDocuments.add(doc);
//            }
//        }
//        System.out.println(i);
//        FileSaver.saveDocumentsToFile(brokenDocuments, "brokeDoc/doc.txt");
    }
}