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
        int end = 2;
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

        List<ExtractedFeatures>  allFeatures = new ArrayList<>();
        for (int i = 0; i < extractedDocuments.size(); i ++) {
            ExtractedFeatures features = FeatureExtractor.extractFeatures(extractedDocuments.get(i));
            allFeatures.add(features);
            System.out.println(features);
            System.out.println(i);
        }
        FileSaver.saveExtractedFeatures(allFeatures, "ExtractedFeatures.ser");


    }
}