package org.example.metrics;

import org.example.ExtractedFeatures;

public class Chebyshev {
    public static double calculateDistance(ExtractedFeatures f1, ExtractedFeatures f2) {
        double maxDiff = 0;
        for (String key : f1.getFeatures().keySet()) {
            Object val1 = f1.getFeature(key);
            Object val2 = f2.getFeature(key);

            double diff = 0;
            if (val1 instanceof Number && val2 instanceof Number) {
                diff = Math.abs(((Number) val1).doubleValue() - ((Number) val2).doubleValue());
            } else if (val1 instanceof String && val2 instanceof String) {
                diff = GenNGramDist.calculateDistance((String) val1, (String) val2);
            }

            maxDiff = Math.max(maxDiff, diff);
        }
        return maxDiff;
    }
}