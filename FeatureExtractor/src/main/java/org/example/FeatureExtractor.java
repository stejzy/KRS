package org.example;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.example.extractors.*;
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

        //Liczba unikalnych słów
        int uniqueWords = NumericExtractor.countUniqueWords(coreBody);
        features.addFeature("Unique words", uniqueWords);


        //NOTE_FOR_ME Testowanie programu bez usuwania stop words
        //Oczyszczanie tekstu z niepotrzebnych słów (stop words)
//        List<String> filteredBody = new ArrayList<>();
//        String cleanedText = "";

//        for (CoreLabel token : coreBody.tokens()) {
//            String word = token.word();
//            if (!stopWords.contains(word) && word.matches("[a-zA-Z]+")) {
//                filteredBody.add(token.word());
//            }
//            cleanedText = String.join(" ", filteredBody);
//            cleanedText = DocumentCleaner.wrapText(cleanedText, 100);
//        }



        //Waluta
        String currency = CurrencyExtractor.extractCurrency(body);
        features.addFeature("Currency", currency);

        //Nazwa kraju
        String country = CountryExtractor.extractCountry(body);
        features.addFeature("Country", country);

        //Nazwa narodowości
        String nation = NationalityExtractor.extractNationality(body);
        features.addFeature("Nationality", nation);

        Pipeline.setPropertiesName("tokenize, ssplit, pos, lemma, ner");
//        CoreDocument coreDocument = new CoreDocument(body);
        Pipeline.getPipeline().annotate(coreBody);


        //Nazwa miasta
        String city = CityExtractor.extractCity(coreBody);
        features.addFeature("City", city);

        //Nazwa osoby
        String name = NameExtractor.extractName(coreBody);
        features.addFeature("Name", name);

        return features;
    }
}
