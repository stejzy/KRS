package linguistic.summary;

import utils.DataRow;

import java.util.List;

public class SingleEntityQualityMeasureCalculator {
    public static double calculateT1(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        if (form.equals("Form 1")) {
            double totalMembership = 0;

            for (DataRow row : dataRows) {
                double membership;

                if (summarizers.size() == 1) {
                    membership = summarizers.get(0).getMembership(
                            row.getValue(summarizers.get(0).getVariable().getName())
                    );
                } else {
                    membership = 1.0;
                    for (Summarizer s : summarizers) {
                        double value = row.getValue(s.getVariable().getName());
                        double m = s.getMembership(value);
                        membership = Math.min(membership, m);
                    }
                }

                totalMembership += membership;
            }

            double averageMembership = totalMembership / dataRows.size();
            return quantifier.getMembership(averageMembership);
        }

        // FORM 2: With qualifier
        else if (form.equals("Form 2")) {
            List<Summarizer> qualifiers = summary.getQualifiers();

            double numerator = 0;
            double denominator = 0;

            for (DataRow row : dataRows) {
                // oblicz przynależność kwalifikatora (AND dla wielu summarizerów)
                double qualifierMembership;
                if (qualifiers.size() == 1) {
                    qualifierMembership = qualifiers.get(0).getMembership(
                            row.getValue(qualifiers.get(0).getVariable().getName())
                    );
                } else {
                    qualifierMembership = 1.0;
                    for (Summarizer q : qualifiers) {
                        double m = q.getMembership(row.getValue(q.getVariable().getName()));
                        qualifierMembership = Math.min(qualifierMembership, m);
                    }
                }

                // oblicz przynależność sumaryzatora (również AND jeśli złożony)
                double summarizerMembership;
                if (summarizers.size() == 1) {
                    summarizerMembership = summarizers.get(0).getMembership(
                            row.getValue(summarizers.get(0).getVariable().getName())
                    );
                } else {
                    summarizerMembership = 1.0;
                    for (Summarizer s : summarizers) {
                        double m = s.getMembership(row.getValue(s.getVariable().getName()));
                        summarizerMembership = Math.min(summarizerMembership, m);
                    }
                }

                numerator += Math.min(qualifierMembership, summarizerMembership);
                denominator += qualifierMembership;
            }

            if (denominator == 0) return 0.0;
            return quantifier.getMembership(numerator / denominator);
        }

        else {
            throw new IllegalArgumentException("Unsupported summary form: " + form);
        }
    }

    public static double calculateT2(LinguisticSummary summary, List<DataRow> dataRows) {
        // T2 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T2 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT3(LinguisticSummary summary, List<DataRow> dataRows) {
        // T3 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T3 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT4(LinguisticSummary summary, List<DataRow> dataRows) {
        // T4 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T4 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT5(LinguisticSummary summary, List<DataRow> dataRows) {
        // T5 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T5 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT6(LinguisticSummary summary, List<DataRow> dataRows) {
        // T6 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T6 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT7(LinguisticSummary summary, List<DataRow> dataRows) {
        // T7 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T7 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT8(LinguisticSummary summary, List<DataRow> dataRows) {
        // T8 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T8 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT9(LinguisticSummary summary, List<DataRow> dataRows) {
        // T9 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T9 calculation logic here if needed.
        return 0.0;
    }

    public static double calculateT10(LinguisticSummary summary, List<DataRow> dataRows) {
        // T10 is not defined in the original code, so we return 0.0 as a placeholder.
        // Implement T10 calculation logic here if needed.
        return 0.0;
    }
}
