package linguistic.summary;

import utils.DataRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleEntityQualityMeasureCalculator {

    //INFO: Raczej w porządku
    public static double calculateT1(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        if (form.equals("Form 1")) {
            double totalMembership = 0;

            for (DataRow row : dataRows) {
                double membership;

                if (summarizers.size() == 1) {
                    membership = summarizers.getFirst().getMembership(
                            row.getNumericValue(summarizers.getFirst().getVariable().getName())
                    );
                } else {
                    membership = 1.0;
                    for (Summarizer s : summarizers) {
                        double value = row.getNumericValue(s.getVariable().getName());
                        double m = s.getMembership(value);
                        membership = Math.min(membership, m);
                    }
                }

                totalMembership += membership;
            }

            double averageMembership;
            if(quantifier.isRelative()){
                averageMembership = totalMembership / dataRows.size();

            } else{
                averageMembership = totalMembership;
            }

            System.out.println("Average membership for summarizers: " + averageMembership);
            return quantifier.getMembership(averageMembership);
        }

        else if (form.equals("Form 2")) {
            List<Summarizer> qualifiers = summary.getQualifiers();

            double numerator = 0;
            double denominator = 0;

            for (DataRow row : dataRows) {
                double qualifierMembership;
                if (qualifiers.size() == 1) {
                    qualifierMembership = qualifiers.getFirst().getMembership(
                            row.getNumericValue(qualifiers.getFirst().getVariable().getName())
                    );
                } else {
                    qualifierMembership = 1.0;
                    for (Summarizer q : qualifiers) {
                        double m = q.getMembership(row.getNumericValue(q.getVariable().getName()));
                        qualifierMembership = Math.min(qualifierMembership, m);
                    }
                }

                double summarizerMembership;
                if (summarizers.size() == 1) {
                    summarizerMembership = summarizers.getFirst().getMembership(
                            row.getNumericValue(summarizers.getFirst().getVariable().getName())
                    );
                } else {
                    summarizerMembership = 1.0;
                    for (Summarizer s : summarizers) {
                        double m = s.getMembership(row.getNumericValue(s.getVariable().getName()));
                        summarizerMembership = Math.min(summarizerMembership, m);
                    }
                }

                numerator += Math.min(qualifierMembership, summarizerMembership);
                denominator += qualifierMembership;
            }
//            System.out.println(numerator / denominator);
            if (denominator == 0) return 0.0;
            return quantifier.getMembership(numerator / denominator);
        }

        else {
            throw new IllegalArgumentException("Unsupported summary form: " + form);
        }
    }

    //INFO: Tylko 1 forma
    public static double calculateT2(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        double geometricMean = 1.0;

        for(Summarizer sum : summarizers){
            double count = 0.0;

            for (DataRow row : dataRows) {
                double value = row.getNumericValue(sum.getVariable().getName());
                if (sum.getMembership(value) > 0) {
                    count++;
                }
            }

            double proportion = (double) count / dataRows.size();
            geometricMean *= proportion;

//            String labelName = sum.getLabel();
//            FuzzySet set = sum.getVariable().getLabel(labelName);
//            geometricMean *= FuzzySetOperations.fuzziness(set);
        }

        System.out.println("Mean 2:");
        System.out.println(geometricMean);

        return 1 - Math.pow(geometricMean, 0.5);
    }

    //INFO: Raczej w porządku
    public static double calculateT3(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        List<Summarizer> qualifiers = summary.getQualifiers();
        if (qualifiers == null) qualifiers = Collections.emptyList();

        double numerator = 0;
        double denominator = 0;

        for (DataRow row : dataRows) {
            boolean summarizerActive = summarizers.stream()
                    .allMatch(sum -> sum.getMembership(row.getNumericValue(sum.getVariable().getName())) > 0);

            boolean qualifierActive = qualifiers.stream()
                    .allMatch(qual -> qual.getMembership(row.getNumericValue(qual.getVariable().getName())) > 0);

            if (form.equals("Form 2")) {
                if (summarizerActive && qualifierActive) numerator++;
                if (qualifierActive) denominator++;
            } else {
                if (summarizerActive) numerator++;
            }
        }

        System.out.println("D");
        System.out.println(denominator);
        System.out.println("N");
        System.out.println(numerator);

        return form.equals("Form 2")
                ? (denominator > 0 ? numerator / denominator : 0.0)
                : numerator / dataRows.size();
    }

    //INFO: Tylko 1 forma
    public static double calculateT4(LinguisticSummary summary, List<DataRow> dataRows) {
        List<Summarizer> summarizers = summary.getSummarizers();
        List<Summarizer> qualifiers = summary.getQualifiers();
        if (qualifiers == null) { qualifiers = Collections.emptyList(); }

        double T4 = 1.0;

        for (Summarizer summarizer : summarizers) {
            int count = 0;
            System.out.println(dataRows.size());

            for (DataRow row : dataRows) {
                double value = row.getNumericValue(summarizer.getVariable().getName());
                if (summarizer.getMembership(value) > 0) {
                    count++;
                }
            }
            System.out.println("Count");
            System.out.println(count);
            double proportion = (double) count / dataRows.size();
            System.out.println("Prop");
            System.out.println(proportion);
            T4 *= proportion;
        }
        System.out.println("T4:");
        System.out.println(T4);


        double T3 = calculateT3(summary, dataRows);

        System.out.println("T3:");
        System.out.println(T3);


        return Math.abs(T4 - T3);
    }


    public static double calculateT5(LinguisticSummary summary, List<DataRow> dataRows) {
        List<Summarizer> summarizers = summary.getSummarizers();
        List<Summarizer> qualifiers = summary.getQualifiers();
        if (qualifiers == null) { qualifiers = Collections.emptyList(); }

        return 2 * Math.pow(0.5, summarizers.size());
    }

    public static double calculateT11(LinguisticSummary summary, List<DataRow> dataRows) {
        List<Summarizer> qualifiers = summary.getQualifiers();
        if (qualifiers == null) { qualifiers = Collections.emptyList(); }

        return 2 * Math.pow(0.5, qualifiers.size());
    }

    public static double optimalSummary(
            LinguisticSummary summary,
            List<DataRow> dataRows,
            double w1, double w2, double w3, double w4, double w5
    ) {
        double sumWeights = w1 + w2 + w3 + w4 + w5;
        if (Math.abs(sumWeights - 1.0) > 1e-6) {
            throw new IllegalArgumentException("Suma wag musi wynosić dokładnie 1.0");
        }

        double t1 = calculateT1(summary, dataRows);
        double t2 = calculateT2(summary, dataRows);
        double t3 = calculateT3(summary, dataRows);
        double t4 = calculateT4(summary, dataRows);
        double t5 = calculateT5(summary, dataRows);

        return w1 * t1 + w2 * t2 + w3 * t3 + w4 * t4 + w5 * t5;
    }

   public static double extendedOptimalSummary(
           LinguisticSummary summary,
           List<DataRow> dataRows,
           double w1, double w2, double w3, double w4, double w5, double w6, double w7, double w8, double w9, double w10, double w11
   ) {
       String form = summary.getForm();
       double sumWeights = w1 + w2 + w3 + w4 + w5 + w6 + w7 + w8 + (form.equals("Form 2") ? (w9 + w10 + w11) : 0);
       if (Math.abs(sumWeights - 1.0) > 1e-6) {
           throw new IllegalArgumentException("Suma wag musi wynosić dokładnie 1.0");
       }

       double t1 = calculateT1(summary, dataRows);
       double t2 = calculateT2(summary, dataRows);
       double t3 = calculateT3(summary, dataRows);
       double t4 = calculateT4(summary, dataRows);
       double t5 = calculateT5(summary, dataRows);
       double t6 = calculateT6(summary, dataRows);
       double t7 = calculateT7(summary, dataRows);
       double t8 = calculateT8(summary, dataRows);

       if (form.equals("Form 2")) {
           double t9 = calculateT9(summary, dataRows);
           double t10 = calculateT10(summary, dataRows);
           double t11 = calculateT11(summary, dataRows);
           return w1 * t1 + w2 * t2 + w3 * t3 + w4 * t4 + w5 * t5 + w6 * t6 + w7 * t7 + w8 * t8 + w9 * t9 + w10 * t10 + w11 * t11;
       } else {
           return w1 * t1 + w2 * t2 + w3 * t3 + w4 * t4 + w5 * t5 + w6 * t6 + w7 * t7 + w8 * t8;
       }
   }

    //INFO: Raczej dobrze
    public static double calculateT6(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        FuzzySet set = quantifier.getFuzzySet();

//        System.out.println(quantifier.getFuzzySet().getUniverse().length);

        CrispSet set2 = FuzzySetOperations.support(set);

//        System.out.println(set2.getElements().size());

        return 1 - FuzzySetOperations.fuzziness(set);
    }

    //INFO: Raczej dobrze
    public static double calculateT7(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        FuzzySet set = quantifier.getFuzzySet();

//        System.out.println(quantifier.getFuzzySet().getFunction().clm());

        if(quantifier.isRelative()){
            return 1 - quantifier.getFuzzySet().getFunction().clm();
        } else {
            return 1 - quantifier.getFuzzySet().getFunction().clm() / dataRows.size();
        }

    }

    //INFO
    public static double calculateT8(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        double geometricMean = 1.0;

        for(Summarizer sum : summarizers){
            double count = 0.0;

            for (DataRow row : dataRows) {
                double value = row.getNumericValue(sum.getVariable().getName());
                if (sum.getMembership(value) > 0) {
                    count += sum.getMembership(value);
                }
            }

            double proportion = (double) count / dataRows.size();
            geometricMean *= proportion;

//            String labelName = sum.getLabel();
//            FuzzySet set = sum.getVariable().getLabel(labelName);
//            geometricMean *= (set.getFunction().clm() / sum.getVariable().getUniverse().length);

        }


//        System.out.println("Mean 8:");
//        System.out.println(geometricMean);

        return 1 - Math.pow(geometricMean, 0.5);
    }

    //INFO: Tylko druga forma
    public static double calculateT9(LinguisticSummary summary, List<DataRow> dataRows) {
        String form = summary.getForm();

        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        List<Summarizer> qualifiers = summary.getQualifiers();
        if (qualifiers == null) { qualifiers = Collections.emptyList(); }

        double geometricMean = 1.0;

        for(Summarizer qual : qualifiers){
            double count = 0.0;

            for (DataRow row : dataRows) {
                double value = row.getNumericValue(qual.getVariable().getName());
                if (qual.getMembership(value) > 0) {
                    count++;
                }
            }

            double proportion = (double) count / dataRows.size();
            geometricMean *= proportion;

//            String labelName = sum.getLabel();
//            FuzzySet set = sum.getVariable().getLabel(labelName);
//            geometricMean *= FuzzySetOperations.fuzziness(set);
        }

        //INFO: Stara wersja
//        for(Summarizer qual : qualifiers){
//            String labelName = qual.getLabel();
//            FuzzySet set = qual.getVariable().getLabel(labelName);
//            geometricMean *= FuzzySetOperations.fuzziness(set);
//        }

//        System.out.println("Mean 9:");
//        System.out.println(geometricMean);

        return 1 - Math.pow(geometricMean, 0.5);
    }

    //INFO: Tylko druga forma
    public static double calculateT10(LinguisticSummary summary, List<DataRow> dataRows) {
        List<Summarizer> qualifiers = summary.getQualifiers();
        if (qualifiers == null) { qualifiers = Collections.emptyList(); }

        double geometricMean = 1.0;

        for(Summarizer qual : qualifiers) {
            double count = 0.0;

            for (DataRow row : dataRows) {
                double value = row.getNumericValue(qual.getVariable().getName());
                if (qual.getMembership(value) > 0) {
                    count += qual.getMembership(value);
                }
            }

            double proportion = (double) count / dataRows.size();
            geometricMean *= proportion;
        }

//        for(Summarizer qual : qualifiers){
//            String labelName = qual.getLabel();
//            FuzzySet set = qual.getVariable().getLabel(labelName);
//
//            geometricMean *= (set.getFunction().clm() / qual.getVariable().getUniverse().length);
//        }

//        System.out.println("Mean 10:");
//        System.out.println(geometricMean);

        return 1 - Math.pow(geometricMean, 0.5);
    }
}
