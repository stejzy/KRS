package linguistic.summary;

import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.TrapezoidalFunction;
import linguistic.summary.membershipfunctions.TriangularFunction;

import java.util.HashMap;
import java.util.Map;

public class QuantifierRegistry {
    private static final Map<String, Quantifier> quantifiers = new HashMap<>();

    static {
        FuzzySet veryFewSet = new FuzzySet(new GaussianFunction(0.05, 0.03), createUniverse(0.0, 0.18, 0.001));
        quantifiers.put("bardzo niewiele", new Quantifier("bardzo niewiele", veryFewSet, true));

        FuzzySet fewSet = new FuzzySet(new TriangularFunction(0.00, 0.125, 0.25), createUniverse(0.0, 0.25, 0.001));
        quantifiers.put("niewiele", new Quantifier("niewiele", fewSet, true));

        FuzzySet severalSet = new FuzzySet(new TrapezoidalFunction(0.15, 0.20, 0.30, 0.35), createUniverse(0.15, 0.35, 0.001));
        quantifiers.put("kilka", new Quantifier("kilka", severalSet, true));

        FuzzySet around4000Set = new FuzzySet(new GaussianFunction(0.40, 0.011), createUniverse(0.35, 0.45, 0.001));
        quantifiers.put("około 4000", new Quantifier("około 4000", around4000Set, false));

        FuzzySet aboutHalfSet = new FuzzySet(new GaussianFunction(0.50, 0.08), createUniverse(0.20, 0.80, 0.001));
        quantifiers.put("około połowy", new Quantifier("około połowy", aboutHalfSet, true));

        FuzzySet manySet = new FuzzySet(new TrapezoidalFunction(0.60, 0.65, 0.75, 0.80), createUniverse(0.60, 0.80, 0.001));
        quantifiers.put("wiele", new Quantifier("wiele", manySet, true));

        FuzzySet majoritySet = new FuzzySet(new TriangularFunction(0.65, 0.775, 0.90), createUniverse(0.65, 0.90, 0.001));
        quantifiers.put("większość", new Quantifier("większość", majoritySet, true));

        FuzzySet almostAllSet = new FuzzySet(x -> {
            if (x == 1.0) return 0.0;
            return new TrapezoidalFunction(0.85, 0.925, 1.0, 1.0).calculateMembership(x);
        }, createUniverse(0.85, 1.0, 0.001));
        quantifiers.put("prawie wszystkie", new Quantifier("prawie wszystkie", almostAllSet, true));

        //NOTE_FOR_ME: DO ZASTANOWIENIA SIE
//        FuzzySet allSet = new FuzzySet(x -> x == 1.0 ? 1.0 : 0.0, createUniverse(1.0, 1.0, 0.001));
//        quantifiers.put("wszystkie", new Quantifier("wszystkie", allSet, true));

    }

    private static double[] createUniverse(double min, double max, double step) {
        int size = (int) Math.ceil((max - min) / step) + 1;
        double[] universe = new double[size];
        for (int i = 0; i < size; i++) {
            universe[i] = min + i * step;
        }
        return universe;
    }

    public static Quantifier get(String name) {
        return quantifiers.get(name);
    }

    public static Map<String, Quantifier> getAll() {
        return quantifiers;
    }
}
