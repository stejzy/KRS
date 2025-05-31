package linguistic.summary;

import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.TrapezoidalFunction;
import linguistic.summary.membershipfunctions.TriangularFunction;

import java.util.HashMap;
import java.util.Map;

public class QuantifierRegistry {
    private static final Map<String, Quantifier> quantifiers = new HashMap<>();

    static {
//        INFO: Kwantyfiaktory wględne
        FuzzySet veryFewSet = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(0, 0.05), 0.0, 1.0, 0.001);
        quantifiers.put("bardzo niewiele", new Quantifier("bardzo niewiele", veryFewSet, true));

        FuzzySet fewSet = FuzzySet.createWithDenseUniverse(
                new TriangularFunction(0.00, 0.125, 0.25), 0.0, 1.0, 0.001);
        quantifiers.put("niewiele", new Quantifier("niewiele", fewSet, true));

        FuzzySet severalSet = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(0.15, 0.25, 0.35, 0.45), 0.0, 1.0, 0.001);
        quantifiers.put("kilka", new Quantifier("kilka", severalSet, true));

        FuzzySet aboutHalfSet = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(0.50, 0.08), 0.0, 1.0, 0.001);
        quantifiers.put("około połowy", new Quantifier("około połowy", aboutHalfSet, true));

        FuzzySet manySet = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(0.60, 0.65, 0.75, 0.80), 0.0, 1.0, 0.001);
        quantifiers.put("wiele", new Quantifier("wiele", manySet, true));

        FuzzySet majoritySet = FuzzySet.createWithDenseUniverse(
                new TriangularFunction(0.73, 0.825, 0.93), 0.0, 1.0, 0.001);
        quantifiers.put("większość", new Quantifier("większość", majoritySet, true));

        FuzzySet almostAllSet = FuzzySet.createWithDenseUniverse(x -> {
            if (x == 1.0) return 0.0;
            return new TrapezoidalFunction(0.85, 0.925, 1.0, 1.0).calculateMembership(x);
        }, 0.0, 1.0, 0.001);
        quantifiers.put("prawie wszystkie", new Quantifier("prawie wszystkie", almostAllSet, true));

//        INFO: Kwantyfiaktory bezwględne

        FuzzySet lessthan1000 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(0, 0, 1000, 1000), 0.0, 10000.0, 0.1);
        quantifiers.put("mniej niż 1000", new Quantifier("mniej niz 1000", lessthan1000, true));

        FuzzySet around2000 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(2000, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("około 2000", new Quantifier("około 2000", around2000, true));

        FuzzySet between2500and3000 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(2500, 2500, 3000, 3000), 0.0, 10000.0, 0.1);
        quantifiers.put("między 2500 a 3000", new Quantifier("między 2500 a 3000", between2500and3000, true));

        FuzzySet around4000 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(4000, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("około 4000", new Quantifier("około 4000", around4000, true));

        FuzzySet around5000 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(5000, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("około 5000", new Quantifier("około 5000", around5000, true));

        FuzzySet around6500 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(6500, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("około 6500", new Quantifier("około 6500", around6500, true));

        FuzzySet between7500and8000 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(7500, 7500, 8000, 8000), 0.0, 10000.0, 0.1);
        quantifiers.put("między 7500 a 8000", new Quantifier("między 7500 a 8000", between7500and8000, true));

        FuzzySet morethan8500 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(8500, 8500, 10000, 10000), 0.0, 10000.0, 0.1);
        quantifiers.put("więcej niż 8500", new Quantifier("więcej niż 8500", morethan8500, true));




        //NOTE_FOR_ME: DO ZASTANOWIENIA SIE
//        FuzzySet allSet = new FuzzySet(x -> x == 1.0 ? 1.0 : 0.0, createUniverse(1.0, 1.0, 0.001));
//        quantifiers.put("wszystkie", new Quantifier("wszystkie", allSet, true));

    }

    public static Quantifier get(String name) {
        return quantifiers.get(name);
    }

    public static Map<String, Quantifier> getAll() {
        return quantifiers;
    }
}
