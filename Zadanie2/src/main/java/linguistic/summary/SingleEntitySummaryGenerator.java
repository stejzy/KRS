package linguistic.summary;

import java.util.*;

public class SingleEntitySummaryGenerator {

    public static List<LinguisticSummary> generateAllSummaries(
            List<Quantifier> quantifiers,
            List<Summarizer> allSummarizers,
            boolean useSecondForm
    ) {
        List<LinguisticSummary> summaries = new ArrayList<>();

        List<List<Summarizer>> summarizerCombinations = getAllNonEmptySubsets(allSummarizers);

        for (Quantifier quantifier : quantifiers) {
            for (List<Summarizer> summarizerSet : summarizerCombinations) {
                if (!useSecondForm) {
                    summaries.add(new LinguisticSummary(quantifier, summarizerSet, null, "Form 1"));
                } else {
                    for (List<Summarizer> qualifierSet : summarizerCombinations) {
                        if (!summarizerSet.equals(qualifierSet) && Collections.disjoint(summarizerSet, qualifierSet)) {
                            summaries.add(new LinguisticSummary(quantifier, summarizerSet, qualifierSet, "Form 2"));
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

