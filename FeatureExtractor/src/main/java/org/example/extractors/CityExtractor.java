package org.example.extractors;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import org.example.Pipeline;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CityExtractor {
    public static String extractCity(CoreDocument coreDocument) {
//        Pipeline.setPropertiesName("tokenize, ssplit, pos, lemma, ner");
//        CoreDocument coreDocument = new CoreDocument(text);
//        Pipeline.getPipeline().annotate(coreDocument);

        List<CoreSentence> sentences = coreDocument.sentences();
        Map<String, Integer> cityMap = new java.util.HashMap<>();

        StringBuilder sb = new StringBuilder();
        Pattern romanNumeralPattern = Pattern.compile("(?i)M{0,4}(CM|CD|D?C{0,3})?(XC|XL|L?X{0,3})?(IX|IV|V?I{0,3})");

        for (CoreSentence sentence : sentences) {
            List<CoreLabel> tokens = sentence.tokens();
            for (CoreLabel token : tokens) {
                String word = token.word();
                String ner = token.ner();

//                System.out.println("Word: " + word + ", NER: " + ner);

                if(ner != null && (ner.equals("CITY") || ner.equals("STATE_OR_PROVINCE"))) {
                    if (!sb.isEmpty()) {
                        sb.append(" ");
                    }

                    sb.append(word);

                } else {
                    if (!sb.isEmpty()) {
                        String city = sb.toString();
                        if (!romanNumeralPattern.matcher(city).matches()) {
                            cityMap.put(city, cityMap.getOrDefault(city, 0) + 1);
                        }
                        sb.setLength(0);
                    }
                }
            }
            if (!sb.isEmpty()) {
                String city = sb.toString();
                if (!romanNumeralPattern.matcher(city).matches()) {
                    cityMap.put(city, cityMap.getOrDefault(city, 0) + 1);
                }
                sb.setLength(0);
            }
        }


        return cityMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
//                    System.out.println("Most frequent city: " + entry.getKey() + " with count: " + entry.getValue());
                    return entry.getKey();
                })
                .orElse("none");
    }

}
