package org.example.extractors;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyExtractor {
    private static final Map<String, String> CURRENCY_MAP = new HashMap<>();
    private static final Pattern CURRENCY_PATTERN;

    static {
        // US
        CURRENCY_MAP.put("dlr", "dlr");
        CURRENCY_MAP.put("dlrs", "dlr");
        CURRENCY_MAP.put("dollar", "dlr");
        CURRENCY_MAP.put("dollars", "dlr");

        // France
        CURRENCY_MAP.put("franc", "franc");
        CURRENCY_MAP.put("francs", "franc");

        // Japan
        CURRENCY_MAP.put("yen", "yen");

        // West-Germany
        CURRENCY_MAP.put("mark", "mark");
        CURRENCY_MAP.put("marks", "mark");

        // UK
        CURRENCY_MAP.put("stg", "stg");

        String patternString = "\\b(" + String.join("|", CURRENCY_MAP.keySet()) + ")\\b";
        CURRENCY_PATTERN = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
    }

    public static String extractCurrency(String text) {
        Matcher matcher = CURRENCY_PATTERN.matcher(text);

        Map<String, Integer> currencyCount = new HashMap<>();

        while (matcher.find()) {
            String currency = matcher.group(0).toLowerCase();
            String normalizedCurrency = CURRENCY_MAP.get(currency);

            if (normalizedCurrency != null) {
                currencyCount.put(normalizedCurrency, currencyCount.getOrDefault(normalizedCurrency, 0) + 1);
            }
        }

        return currencyCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
//                    System.out.println("Most frequent currency: " + entry.getKey() + " with count: " + entry.getValue());
                    return entry.getKey();
                })
                .orElse("none");
    }
}
