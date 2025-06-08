package linguistic.summary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultipleEntitySummaryGenerator {

    public static List<MultipleEntityLinguisticSummary> generateAllSummaries(
            List<Quantifier> quantifiers,
            List<Summarizer> allSummarizers,
            int formNumber,
            List<String> comparedGroups
    ) {
        List<MultipleEntityLinguisticSummary> summaries = new ArrayList<>();
        List<List<Summarizer>> summarizerCombinations = getAllNonEmptySubsets(allSummarizers);

        List<List<String>> groupPairs = Arrays.asList(
                Arrays.asList(comparedGroups.get(0), comparedGroups.get(1)),
                Arrays.asList(comparedGroups.get(1), comparedGroups.get(0))
        );

        for (List<String> pair : groupPairs) {
            if (formNumber == 4) {
                for (List<Summarizer> summarizerSet : summarizerCombinations) {
                    summaries.add(new MultipleEntityLinguisticSummary(null, summarizerSet, null, formNumber, pair));
                }
            } else {
                for (Quantifier quantifier : quantifiers) {
                    for (List<Summarizer> summarizerSet : summarizerCombinations) {
                        if (formNumber == 1) {
                            summaries.add(new MultipleEntityLinguisticSummary(quantifier, summarizerSet, null, formNumber, pair));
                        } else if (formNumber == 2 || formNumber == 3) {
                            for (List<Summarizer> qualifierSet : summarizerCombinations) {
                                if (!summarizerSet.equals(qualifierSet) && Collections.disjoint(summarizerSet, qualifierSet)) {
                                    summaries.add(new MultipleEntityLinguisticSummary(quantifier, summarizerSet, qualifierSet, formNumber, pair));
                                }
                            }
                        }
                    }
                }
            }
        }

        return summaries;
    }

    private static List<List<Summarizer>> getAllNonEmptySubsets(List<Summarizer> input) {
        List<List<Summarizer>> subsets = new ArrayList<>();
        int n = input.size();

        for (int i = 1; i < (1 << n); i++) {
            List<Summarizer> combination = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    combination.add(input.get(j));
                }
            }
            subsets.add(combination);
        }

        return subsets;
    }
}
