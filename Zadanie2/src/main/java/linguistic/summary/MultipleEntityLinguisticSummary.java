package linguistic.summary;

import java.util.ArrayList;
import java.util.List;

public class MultipleEntityLinguisticSummary implements LinguisticSummaryBase{
    private final Quantifier quantifier;
    private final List<Summarizer> summarizers;
    private final List<Summarizer> qualifiers;
    private final int form;
    private final List<String> comparedGroups; // Zawsze tylko 2 podmioty
    private double qualityMeasureT;
//    private final Map<String, List<DataRow>> groupedData;

    public MultipleEntityLinguisticSummary(
            Quantifier quantifier,
            List<Summarizer> summarizers,
            List<Summarizer> qualifiers,
            int form,
            List<String> comparedGroups
//            Map<String, List<DataRow>> groupedData
    ) {
        this.quantifier = quantifier;
        this.summarizers = summarizers;
        this.qualifiers = qualifiers;
        this.form = form;
        this.comparedGroups = comparedGroups;
//        this.groupedData = groupedData;
    }

    public Quantifier getQuantifier() { return quantifier; }
    public List<Summarizer> getSummarizers() { return summarizers; }
    public List<Summarizer> getQualifiers() { return qualifiers; }
    public int getForm() { return form; }
    public List<String> getComparedGroups() { return comparedGroups; }

    @Override
    public double getQualityMeasure(String name) {
        return qualityMeasureT;
    }

    @Override
    public void setQualityMeasure(String name, double value) {
        this.qualityMeasureT = value;
    }

    @Override
    public String toString() {
        if (comparedGroups.size() != 2 || summarizers == null || summarizers.isEmpty()) {
            return "Nieobsługiwana forma podsumowania lub niepoprawna liczba podmiotów.";
        }

        String subject1 = translateGroup(comparedGroups.get(0));
        String subject2 = translateGroup(comparedGroups.get(1));
        String summarizerText = String.join(" i ", summarizers.stream().map(this::describeSummarizer).toList());
        String qualifierText = qualifiers != null && !qualifiers.isEmpty()
                ? String.join(" i ", qualifiers.stream().map(this::describeSummarizer).toList())
                : "";
        String quantifierText = quantifier != null ? quantifier.getName() : "";

        String baseSummary = switch (form) {
            case 1 -> String.format("%s %s w porównaniu do %s %s.", quantifierText, subject1, subject2, summarizerText);
            case 2 -> String.format("%s %s w porównaniu do tych %s, które %s, %s.", quantifierText, subject1, subject2, qualifierText, summarizerText);
            case 3 -> String.format("%s %s, które %s, w porównaniu do %s, %s.", quantifierText, subject1, qualifierText, subject2, summarizerText);
            case 4 -> String.format("Więcej %s niż %s %s.", subject1, subject2, summarizerText);
            default -> "Nieobsługiwana forma podsumowania.";
        };

        // Dodajemy na koniec formę i miarę T (qualityMeasureT)
        return String.format("%s [T=%.4f] (Forma %d)", baseSummary, qualityMeasureT, form);
    }


    private String describeSummarizer(Summarizer summarizer) {
        String label = summarizer.getLabel();
        String variable = summarizer.getVariable().getName();

        return switch (variable) {
            case "Age" -> "są " + label;
            case "Height (cm)" -> "są " + label + " wzrostu";
            case "Weight (kg)" -> "mają " + label + " wagę";
            case "Distance (km)" -> "pokonują " + label + " dystans";
            case "Calories Burned" -> "spalają " + label + " ilość kalorii";
            case "Workout Duration (mins)" -> "trenują przez " + label + " czas";
            case "Sleep Hours" -> "śpią " + label + " godzin";
            case "Heart Rate (bpm)" -> "mają " + label + " tętno podczas wysiłku";
            case "Daily Calories Intake" -> "mają " + label + " dzienne spożycie kalorii";
            case "Resting Heart Rate (bpm)" -> "mają " + label + " tętno spoczynkowe";
            case "Steps Taken" -> "przechodzą " + label + " kroków";
            default -> "są " + label;
        };
    }



    // Pomocnicza metoda tłumacząca
    private String translateGroup(String group) {
        return switch (group.toLowerCase()) {
            case "male" -> "mężczyzn";
            case "female" -> "kobiet";
            default -> group;
        };
    }



//    public Map<String, List<DataRow>> getGroupedData() { return groupedData; }
}
