package org.example;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ExtractedFeatures implements Serializable {
    private final String place;
    private final Map<String, Object> extractedFeatures;
   @Serial
    private static final long serialVersionUID = 2L;

    public ExtractedFeatures(String place) {
        extractedFeatures = new HashMap<>();
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void addFeature(String featureName, Object featureValue) {
        if(featureValue instanceof String || featureValue instanceof Number) {
            extractedFeatures.put(featureName, featureValue);
        } else {
            throw new IllegalArgumentException("Feature value must be a String or a Number");
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExtractedFeatures.class.getSimpleName() + "[", "]")
                .add("Place: '" + place + "'")
                .add("extractedFeatures{" + formatFeatures() + "}")
                .toString();
    }

    private String formatFeatures() {
        return extractedFeatures.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue()) // Usuwamy "="
                .collect(Collectors.joining(", "));
    }

    public Object getFeature(String featureName) {
        if (extractedFeatures.containsKey(featureName)) {
            return extractedFeatures.get(featureName);
        }
        return null;
    }

    public Map<String, Object> getFeatures() {
        return extractedFeatures;
    }

}

