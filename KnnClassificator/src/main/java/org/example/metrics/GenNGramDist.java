package org.example.metrics;

import java.util.HashSet;
import java.util.Set;

public class GenNGramDist {
    public static double calculateDistance(String s1, String s2) {
        int N1 = s1.length();
        int N2 = s2.length();
        int N = Math.max(N1, N2);

//        double totalSubstrings = (N * (N + 1)) / 2.0;
        int n1 = 2;
        int n2 = 3;
        double totalSubstrings = ((double)((N - n1 + 1) * (N - n1 + 2)) - (double)((N - n2 + 1) * (N - n2))) / 2;
        double commonSubstrings = 0;

        for (int length = n1; length <= n2; length++) {
            Set<String> substrings1 = getSubstrings(s1, length);
            Set<String> substrings2 = getSubstrings(s2, length);

            for (String sub : substrings1) {
                if (substrings2.contains(sub)) {
                    commonSubstrings++;
                }
            }
        }

        return 1 - (commonSubstrings / totalSubstrings);
    }

    private static Set<String> getSubstrings(String str, int length) {
        Set<String> substrings = new HashSet<>();
        for (int i = 0; i <= str.length() - length; i++) {
            substrings.add(str.substring(i, i + length));
        }
        return substrings;
    }
}
