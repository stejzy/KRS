package org.example;

import java.util.Set;

public class Euklides {
    public static double calculateDistance(ExtractedFeatures f1, ExtractedFeatures f2) {
        double sum = 0;
        for (String key : f1.getFeatures().keySet()) {
            Object val1 = f1.getFeature(key);
            Object val2 = f2.getFeature(key);

            if (val1 instanceof Number && val2 instanceof Number) {
                double diff = ((Number) val1).doubleValue() - ((Number) val2).doubleValue();
                sum += diff * diff;
            } else if (val1 instanceof String && val2 instanceof String) {
                double diff = GenNGramDist.calculateDistance((String) val1, (String) val2);
                sum += diff * diff;
            }
        }
        return Math.sqrt(sum);
    }
}