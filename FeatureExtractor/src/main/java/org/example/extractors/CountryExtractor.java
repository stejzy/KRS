package org.example.extractors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountryExtractor {
    private static final Map<String, String> COUNTRY_MAP = new HashMap<>();
    private static final Pattern COUNTRY_PATTERN;

    static {
        COUNTRY_MAP.put("usa", "USA");
        COUNTRY_MAP.put("america", "USA");
        COUNTRY_MAP.put("united states", "USA");

        COUNTRY_MAP.put("uk", "UK");
        COUNTRY_MAP.put("united kingdom", "UK");
        COUNTRY_MAP.put("great britain", "UK");
        COUNTRY_MAP.put("britain", "UK");
        COUNTRY_MAP.put("england", "UK");

        COUNTRY_MAP.put("west germany", "West Germany");
        COUNTRY_MAP.put("france", "France");
        COUNTRY_MAP.put("canada", "Canada");
        COUNTRY_MAP.put("japan", "Japan");

        String patternString = "\\b(" + String.join("|", COUNTRY_MAP.keySet()) + ")\\b";
        COUNTRY_PATTERN = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
    }

    public static String extractCountry(String text) {
        Matcher matcher = COUNTRY_PATTERN.matcher(text.toLowerCase());

        Map<String, Integer> countryCount = new HashMap<>();

        while (matcher.find()) {
            String country = matcher.group(0).toLowerCase();
            String normalizedCountry = COUNTRY_MAP.get(country);

            if (normalizedCountry != null) {
                countryCount.put(normalizedCountry, countryCount.getOrDefault(normalizedCountry, 0) + 1);
            }
        }

        return countryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(
                        entry -> {
//                            System.out.println("Most frequent country: " + entry.getKey() + " with count: " + entry.getValue());
                            return entry.getKey();
                        }
                )
                .orElse("none");
    }

}
