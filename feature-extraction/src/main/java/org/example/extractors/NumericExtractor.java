package org.example.extractors;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import org.example.Pipeline;

public class NumericExtractor {
    private static StanfordCoreNLP pipeline = Pipeline.getPipeline();

    public static int countSentences(CoreDocument text) {
        List<CoreSentence> sentences = text.sentences();
        return sentences.size();
    }

    public static int countWords(CoreDocument text) {
        List<CoreLabel> tokens = text.tokens();

        int words = 0;
        for (CoreLabel token : tokens) {
            String word = token.word();

            if (word.matches("[a-zA-Z]+") &&  !word.matches("(?i)M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})"))  {
//                System.out.println(word);
                words++;
            }
        }

        return words;
    }

    public static int countNumbers(CoreDocument text) {
        List<CoreLabel> tokens = text.tokens();

        int numbers = 0;
        List<String> numberWords = List.of("five", "one", "two", "three", "four", "six", "seven", "eight", "nine", "ten");

        for (CoreLabel token : tokens) {
            if (!numberWords.contains(token.word().toLowerCase())) {
                String number = token.get(PartOfSpeechAnnotation.class);

                if (number.equals("CD"))  {
//                    System.out.println(token.word());
                    numbers++;
                }
            }
        }

        return numbers;
    }

    public static double calculateAverageWordLength(CoreDocument text) {
        List<CoreLabel> tokens = text.tokens();

        int totalLength = 0;
        int wordCount = 0;

        for (CoreLabel token : tokens) {
            String word = token.word();


            if (word.matches("[a-zA-Z]+"))  {
                totalLength += word.length();
                wordCount++;
            }
        }

        if (wordCount > 0) {
            return (double) totalLength / wordCount;
        } else {
            return 0.0;
        }
    }


}
