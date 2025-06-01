package linguistic.summary;

import java.util.*;

public class LinguisticSummary {
    private final Quantifier quantifier;
    private final List<Summarizer> summarizers;
    private final List<Summarizer> qualifiers; // null if Form 1
    private final String form;

    private final Map<String, Double> qualityMeasures = new HashMap<>();

    public LinguisticSummary(Quantifier quantifier, List<Summarizer> summarizers, List<Summarizer> qualifiers, String form) {
        this.quantifier = quantifier;
        this.summarizers = summarizers;
        this.qualifiers = qualifiers;
        this.form = form;
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public List<Summarizer> getSummarizers() {
        return summarizers;
    }

    public List<Summarizer> getQualifiers() {
        return qualifiers;
    }

    public String getForm() {
        return form;
    }

    public void setQualityMeasure(String name, double value) {
        qualityMeasures.put(name, value);
    }

    public Double getQualityMeasure(String name) {
        return qualityMeasures.get(name);
    }

    public Map<String, Double> getAllQualityMeasures() {
        return qualityMeasures;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(quantifier.getName()).append(" użytkowników ");
        if (qualifiers != null && !qualifiers.isEmpty()) {

            List<String> qualifierPhrases = new ArrayList<>();

            for (Summarizer summarizer1 : qualifiers) {
                String label = summarizer1.getLabel();
                String variable = summarizer1.getVariable().getName();
                String phrase = "";

                if (variable.equals("Age")) {
                    phrase = "są " + label;
                } else if (variable.equals("Height (cm)")) {
                    phrase = "są " + label + " wzrostu";
                } else if (variable.equals("Weight (kg)")) {
                    phrase = "mają " + label + " wagę";
                } else if (variable.equals("Distance (km)")) {
                    phrase = "pokonali " + label + " dystans";
                } else if (variable.equals("Calories Burned")) {
                    phrase = "spalili " + label + " ilość kalorii";
                } else if (variable.equals("Workout Duration (mins)")) {
                    phrase = "trenowali przez " + label + " czas";
                } else if (variable.equals("Sleep Hours")) {
                    phrase = "spali " + label + " godzin";
                } else if (variable.equals("Heart Rate (bpm)")) {
                    phrase = "mają " + label + " tętno podczas wysiłku";
                } else if (variable.equals("Daily Calories Intake")) {
                    phrase = "mają " + label + " dzienne przyjmowanie kalorii";
                } else if (variable.equals("Resting Heart Rate (bpm)")) {
                    phrase = "mają " + label + " tętno podczas spoczynku";
                } else if (variable.equals("Steps Taken")) {
                    phrase = "przeszli " + label + " kroków";
                } else {
                    phrase = "są " + label;
                }

                qualifierPhrases.add(phrase);
            }

            sb.append("którzy ")
                    .append(String.join(", ", qualifierPhrases));
        }

        if (summarizers != null && !summarizers.isEmpty()) {

            List<String> summarizerPhrases = new ArrayList<>();

            for (Summarizer summarizer2 : summarizers) {
                String label = summarizer2.getLabel();
                String variable = summarizer2.getVariable().getName();
                String phrase = "";

                if (variable.equals("Age")) {
                    phrase = "są " + label;
                } else if (variable.equals("Height (cm)")) {
                    phrase = "są " + label + " wzrostu";
                } else if (variable.equals("Weight (kg)")) {
                    phrase = "mają " + label + " wagę";
                } else if (variable.equals("Distance (km)")) {
                    phrase = "pokonali " + label + " dystans";
                } else if (variable.equals("Calories Burned")) {
                    phrase = "spalili " + label + " ilość kalorii";
                } else if (variable.equals("Workout Duration (mins)")) {
                    phrase = "trenowali przez " + label + " czas";
                } else if (variable.equals("Sleep Hours")) {
                    phrase = "spali " + label + " godzin";
                } else if (variable.equals("Heart Rate (bpm)")) {
                    phrase = "mają " + label + " tętno podczas wysiłku";
                } else if (variable.equals("Daily Calories Intake")) {
                    phrase = "mają " + label + " dzienne przyjmowanie kalorii";
                } else if (variable.equals("Resting Heart Rate (bpm)")) {
                    phrase = "mają " + label + " tętno podczas spoczynku";
                } else if (variable.equals("Steps Taken")) {
                    phrase = "przeszli " + label + " kroków";
                } else {
                    phrase = "są " + label;
                }

                summarizerPhrases.add(phrase);
            }

            sb.append(" -> ").append(String.join(", ", summarizerPhrases)).append(".");
        }

            sb.append(" (").append(form).append(")");

            if (!qualityMeasures.isEmpty()) {
                sb.append(" — Miary: ");
                qualityMeasures.forEach((k, v) -> sb.append(k).append(": ").append(String.format("%.3f", v)).append("  "));
            }

            return sb.toString();
    }

    private String stringify(List<Summarizer> summarizers) {
        return String.join(" i ", summarizers.stream()
                .map(Summarizer::getLabel)
                .toList());
    }
}
