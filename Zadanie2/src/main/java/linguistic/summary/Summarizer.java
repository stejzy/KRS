package linguistic.summary;

public class Summarizer {

    private final String label;
    private final LinguisticVariable variable;

    public Summarizer(String label, LinguisticVariable variable) {
        this.label = label;
        this.variable = variable;
    }

    public String getLabel() {
        return label;
    }

    public LinguisticVariable getVariable() {
        return variable;
    }

    public double getMembership(double value) {
        return variable.getMembership(label, value);
    }
}
