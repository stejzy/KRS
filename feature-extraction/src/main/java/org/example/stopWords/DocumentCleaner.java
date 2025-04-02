package org.example.stopWords;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.Sets;
import org.example.Document;
import org.example.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocumentCleaner {
    public static List<Document> cleanDocuments(List<Document> extractedDocuments, Set<String> stopWords) {
        List<Document> cleanedDocuments = new ArrayList<>();
        int i = 0;
        for (Document doc : extractedDocuments) {
            CoreDocument cleanDoc = new CoreDocument(doc.getBody());
            Pipeline.getPipeline().annotate(cleanDoc);

            List<String> filteredTokens = new ArrayList<>();

            for (CoreLabel token : cleanDoc.tokens()) {
                String word = token.word().toLowerCase();

                if (!stopWords.contains(word) && word.matches("[a-zA-Z]+")) {
                    filteredTokens.add(token.word());
                }
            }


            String cleanedText = String.join(" ", filteredTokens);
            cleanedText = wrapText(cleanedText, 60);
            cleanedDocuments.add(new Document(doc.getPlace(), cleanedText));
        }

        return cleanedDocuments;
    }

    public static String wrapText(String text, int wrapLength) {
        StringBuilder sb = new StringBuilder();

        String[] words = text.split(" ");
        int currentLength = 0;

        for (String word : words) {
            if (currentLength + word.length() > wrapLength) {
                sb.append("\n");
                currentLength = 0;
            }

            sb.append(word);
            sb.append(" ");
            currentLength += word.length() + 1;
        }

        return sb.toString().trim();
    }
}
