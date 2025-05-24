package linguistic.summary;

import linguistic.summary.membershipfunctions.MembershipFunction;

import java.util.*;

public class LinguisticVariable {
    private final String name;
    private final double min, max;
    private final Map<String, FuzzySet> labels = new HashMap<>();
    private final double[] universe;

    public LinguisticVariable(String name, double min, double max, int resolution) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.universe = createUniverse(min, max, resolution);
    }

    private double[] createUniverse(double min, double max, double step) {
        int size = (int) Math.ceil((max - min) / step) + 1;
        double[] universe = new double[size];
        for (int i = 0; i < size; i++) {
            universe[i] = min + i * step;
        }
        return universe;
    }

    public void addLabel(String labelName, MembershipFunction mf) {
        labels.put(labelName, new FuzzySet(mf, universe));
    }

    public double getMembership(String labelName, double x) {
        FuzzySet set = labels.get(labelName);
        if (set == null) return 0;
        return set.getMembership(x);
    }

    public Set<String> getLabels() {
        return labels.keySet();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LinguisticVariable.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("min=" + min)
                .add("max=" + max)
                .add("labels=" + labels)
                .add("universe=" + Arrays.toString(universe))
                .toString();
    }
}
