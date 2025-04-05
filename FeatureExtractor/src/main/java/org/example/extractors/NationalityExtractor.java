package org.example.extractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NationalityExtractor {
    private static final List<String> NATIONALITY_LIST = new ArrayList<>();
    private static final Pattern NATIONALITY_PATTERN;

    static {

        NATIONALITY_LIST.add("american");
        NATIONALITY_LIST.add("british");
        NATIONALITY_LIST.add("french");
        NATIONALITY_LIST.add("canadian");
        NATIONALITY_LIST.add("japanese");
        NATIONALITY_LIST.add("german");

        String pattern = "\\b" + String.join("|", NATIONALITY_LIST) + "\\b";
        NATIONALITY_PATTERN = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public static String extractNationality(String text) {
        Matcher matcher = NATIONALITY_PATTERN.matcher(text);

        Map<String, Integer> nationalityCount = new HashMap<>();

        while (matcher.find()) {
            String nation = matcher.group(0).toLowerCase();
            nationalityCount.put(nation, nationalityCount.getOrDefault(nation, 0) + 1);
        }

        return nationalityCount
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(
                       entry ->  {
//                           System.out.println("Nationality: " + entry.getKey() + " with count: " + entry.getValue());
                           return entry.getKey();
                       }
                )
                .orElse("none");
    }

}
