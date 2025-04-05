package org.example.extractors;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import org.example.Pipeline;

import java.util.Map;

public class NameExtractor {
    public static String extractName(CoreDocument coreDocument) {
//        Pipeline.setPropertiesName("tokenize, ssplit, pos, lemma, ner");
//        CoreDocument coreDocument = new CoreDocument(text);
//        Pipeline.getPipeline().annotate(coreDocument);

        Map<String, Integer> nameMap = new java.util.HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (CoreSentence sentence : coreDocument.sentences()) {
            for (CoreLabel token : sentence.tokens()) {
                String word = token.word();
                String ner = token.ner();

                if (ner != null && ner.equals("PERSON")) {
                    if (!sb.isEmpty()) {
                        sb.append(" ");
                    }
                    sb.append(word);
                } else {
                    if (!sb.isEmpty()) {
                        String city = sb.toString();
                        nameMap.put(city, nameMap.getOrDefault(city, 0) + 1);
                        sb.setLength(0);
                    }
                }
            }
            if (!sb.isEmpty()) {
                String city = sb.toString();
                nameMap.put(city, nameMap.getOrDefault(city, 0) + 1);
                sb.setLength(0);
            }
        }

        return nameMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    //System.out.println(entry.getKey() + " " + entry.getValue());
                    return entry.getKey();
                })
                .orElse("none");
    }
}
