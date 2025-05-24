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
            sb.append("którzy są ");
            sb.append(stringify(qualifiers));
            sb.append(", ");
        }
        sb.append("jest ");
        sb.append(stringify(summarizers));
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
