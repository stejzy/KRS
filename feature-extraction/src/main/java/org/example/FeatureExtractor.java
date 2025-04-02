package org.example;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.example.extractors.CurrencyExtractor;
import org.example.extractors.NumericExtractor;
import org.example.stopWords.DocumentCleaner;
import org.example.stopWords.StopWordLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FeatureExtractor {
    private static final Set<String> stopWords = StopWordLoader.loadStopWordsFromFile("stopWords.json");

    public static ExtractedFeatures extractFeatures(Document doc) {
        String place = doc.getPlace();
        String body = doc.getBody();

        //Tworzymy obiekt przechowywujący cechy
        ExtractedFeatures features = new ExtractedFeatures(place);

        // NOTE_FOR_ME: Ekstrakcja cech z dokumentu

        //Tekst poddany analizie bez usuwania stop words
        //Wykorzystywany do ekstrakcji cech numerycznych
        StanfordCoreNLP pipeline = Pipeline.getPipeline();
        CoreDocument coreBody = new CoreDocument(body);
        pipeline.annotate(coreBody);

        //Liczba zdań
        int count = NumericExtractor.countSentences(coreBody);
        features.addFeature("Sentences", count);

        //Liczba słów
        count = NumericExtractor.countWords(coreBody);
        features.addFeature("Words", count);

        //Liczba cyfr
        count = NumericExtractor.countNumbers(coreBody);
        features.addFeature("Numbers", count);

        //Średnia długość słowa
        double avg = NumericExtractor.calculateAverageWordLength(coreBody);
        features.addFeature("Average word length", avg);


        //Oczyszczanie tekstu z niepotrzebnych słów (stop words)
        List<String> filteredBody = new ArrayList<>();
        String cleanedText = "";

        for (CoreLabel token : coreBody.tokens()) {
            String word = token.word();
            if (!stopWords.contains(word) && word.matches("[a-zA-Z]+")) {
                filteredBody.add(token.word());
            }
            cleanedText = String.join(" ", filteredBody);
            cleanedText = DocumentCleaner.wrapText(cleanedText, 100);
        }

//        System.out.println(cleanedText);

        //Waluta
        String currency = CurrencyExtractor.extractCurrency(cleanedText);
        features.addFeature("Currency", currency);

        return features;
    }
}
