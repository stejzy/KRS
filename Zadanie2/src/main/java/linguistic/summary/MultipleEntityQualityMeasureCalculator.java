package linguistic.summary;

import utils.DataRow;

import java.util.*;

public class MultipleEntityQualityMeasureCalculator {

    public static double calculateT(MultipleEntityLinguisticSummary summary, List<DataRow> dataRows) {
        Map<String, List<DataRow>> groupedData = splitDataByGroups(dataRows, summary.getComparedGroups());
        return switch (summary.getForm()) {
            case 1 -> calculateTForm1(summary, groupedData);
            case 2 -> calculateTForm2(summary, groupedData);
            case 3 -> calculateTForm3(summary, groupedData);
            case 4 -> calculateTForm4(summary, dataRows);
            default -> throw new IllegalArgumentException("Nieobsługiwana forma: " + summary.getForm());
        };
    }

    private static double calculateTForm1(MultipleEntityLinguisticSummary summary, Map<String, List<DataRow>> groupedData) {
        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();

        List<String> keys = new ArrayList<>(groupedData.keySet());
        String entity1 = keys.get(0);
        String entity2 = keys.get(1);

        double sigmaCount1 = sumMemberships(groupedData.get(entity1), summarizers, null);
        double sigmaCount2 = sumMemberships(groupedData.get(entity2), summarizers, null);

        System.out.println(sigmaCount1);
        System.out.println(sigmaCount2);

        int M1 = groupedData.get(entity1).size();
        int M2 = groupedData.get(entity2).size();

        double numerator = sigmaCount1 / M1;
        double denominator = (sigmaCount1 / M1) + (sigmaCount2 / M2);

        return quantifier.getMembership(numerator / denominator);
    }

    private static double calculateTForm2(MultipleEntityLinguisticSummary summary, Map<String, List<DataRow>> groupedData) {
        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();
        List<Summarizer> qualifiers = summary.getQualifiers();

        List<String> keys = new ArrayList<>(groupedData.keySet());
        String entity1 = keys.get(0);
        String entity2 = keys.get(1);

        double sigmaCount1 = sumMemberships(groupedData.get(entity1), summarizers, null);
        double sigmaCount2 = sumMemberships(groupedData.get(entity2), summarizers, qualifiers);

        System.out.println(sigmaCount1);
        System.out.println(sigmaCount2);

        int M1 = groupedData.get(entity1).size();
        int M2 = groupedData.get(entity2).size();

        double numerator = sigmaCount1 / M1;
        double denominator = (sigmaCount1 / M1) + (sigmaCount2 / M2);

        return quantifier.getMembership(numerator / denominator);
    }

    private static double calculateTForm3(MultipleEntityLinguisticSummary summary, Map<String, List<DataRow>> groupedData) {
        List<Summarizer> summarizers = summary.getSummarizers();
        Quantifier quantifier = summary.getQuantifier();
        List<Summarizer> qualifiers = summary.getQualifiers();

        List<String> keys = new ArrayList<>(groupedData.keySet());
        String entity1 = keys.get(0);
        String entity2 = keys.get(1);

        double sigmaCount1 = sumMemberships(groupedData.get(entity1), summarizers, qualifiers);
        double sigmaCount2 = sumMemberships(groupedData.get(entity2), summarizers, null);

        System.out.println(sigmaCount1);
        System.out.println(sigmaCount2);

        int M1 = groupedData.get(entity1).size();
        int M2 = groupedData.get(entity2).size();

        double numerator = sigmaCount1 / M1;
        double denominator = (sigmaCount1 / M1) + (sigmaCount2 / M2);

        return quantifier.getMembership(numerator / denominator);
    }

    private static double calculateTForm4(MultipleEntityLinguisticSummary summary, List<DataRow> rows) {

        List<Summarizer> summarizers = summary.getSummarizers();
        double numerator = 0.0;

        for(DataRow row : rows) {
            double temp = 1.0;
            for (Summarizer s : summarizers) {
                double value = row.getNumericValue(s.getVariable().getName());
                double m = s.getMembership(value);
                temp = Math.min(temp, m);
            }

            if(Objects.equals(row.getStringValue("Gender"), summary.getComparedGroups().getFirst())){
//                numerator += temp;
                numerator += (1 - temp);
            } else {
                numerator += temp;
//                numerator += (1 - temp);
            }
        }

        return 1 - (numerator / rows.size());
    }

    private static double sumMemberships(List<DataRow> rows, List<Summarizer> summarizers, List<Summarizer> qualifiers) {
        double sum = 0.0;
        for (DataRow row : rows) {
            double sumMembership = 1.0;
            double qualMembership = 1.0;

            for (Summarizer s : summarizers) {
                double value = row.getNumericValue(s.getVariable().getName());
                double m = s.getMembership(value);
                sumMembership = Math.min(sumMembership, m);
            }

            if(qualifiers != null && !qualifiers.isEmpty()){
                for (Summarizer q : qualifiers) {
                    double value = row.getNumericValue(q.getVariable().getName());
                    double m = q.getMembership(value);
                    qualMembership = Math.min(qualMembership, m);
                }
            }

            sum += Math.min(qualMembership, sumMembership);
        }
        return sum;
    }

    private static Map<String, List<DataRow>> splitDataByGroups(List<DataRow> dataRows, List<String> comparedGroups) {
        Map<String, List<DataRow>> grouped = new HashMap<>();
        for (String group : comparedGroups) {
            grouped.put(group, new ArrayList<>());
        }
        for (DataRow row : dataRows) {
            Object genderObj = row.getValue("Gender");
            if (genderObj != null) {
                String gender = genderObj.toString();
                if (grouped.containsKey(gender)) {
                    grouped.get(gender).add(row);
                }
            }
        }
        return grouped;
    }
}