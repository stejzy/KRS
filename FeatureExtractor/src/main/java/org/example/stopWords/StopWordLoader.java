package org.example.stopWords;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWordLoader {
    public static Set<String> loadStopWordsFromFile(String filePath){
        ObjectMapper mapper = new ObjectMapper();
        Set<String> stopWords = new HashSet<>();

        try{
            List<String> words = mapper.readValue(new File(filePath), List.class);
            stopWords.addAll(words);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }
}
