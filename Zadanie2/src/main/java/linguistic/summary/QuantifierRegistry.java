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
        quantifiers.put("Bardzo niewiele", new Quantifier("Bardzo niewiele", veryFewSet, true));

        FuzzySet fewSet = FuzzySet.createWithDenseUniverse(
                new TriangularFunction(0.00, 0.125, 0.25), 0.0, 1.0, 0.001);
        quantifiers.put("Niewiele", new Quantifier("Niewiele", fewSet, true));

        FuzzySet severalSet = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(0.15, 0.25, 0.35, 0.45), 0.0, 1.0, 0.001);
        quantifiers.put("Kilka", new Quantifier("Kilka", severalSet, true));

        FuzzySet aboutHalfSet = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(0.50, 0.08), 0.0, 1.0, 0.001);
        quantifiers.put("Około połowy", new Quantifier("Około połowy", aboutHalfSet, true));

        FuzzySet manySet = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(0.60, 0.65, 0.75, 0.80), 0.0, 1.0, 0.001);
        quantifiers.put("Wiele", new Quantifier("Wiele", manySet, true));

        FuzzySet majoritySet = FuzzySet.createWithDenseUniverse(
                new TriangularFunction(0.73, 0.825, 0.93), 0.0, 1.0, 0.001);
        quantifiers.put("Większość", new Quantifier("Większość", majoritySet, true));

        FuzzySet almostAllSet = FuzzySet.createWithDenseUniverse(x -> {
            if (x == 1.0) return 0.0;
            return new TrapezoidalFunction(0.85, 0.925, 1.0, 1.0).calculateMembership(x);
        }, 0.0, 1.0, 0.001);
        quantifiers.put("Prawie wszystkie", new Quantifier("Prawie wszystkie", almostAllSet, true));

//        INFO: Kwantyfiaktory bezwględne

        FuzzySet lessthan1000 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(0, 0, 1000, 1000), 0.0, 10000.0, 0.1);
        quantifiers.put("Mniej niż 1000", new Quantifier("Mniej niz 1000", lessthan1000, false));

        FuzzySet around2000 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(2000, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("Około 2000", new Quantifier("Około 2000", around2000, false));

        FuzzySet between2500and3000 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(2500, 2500, 3000, 3000), 0.0, 10000.0, 0.1);
        quantifiers.put("Między 2500 a 3000", new Quantifier("Między 2500 a 3000", between2500and3000, false));

        FuzzySet around4000 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(4000, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("Około 4000", new Quantifier("Około 4000", around4000, false));

        FuzzySet around5000 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(5000, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("Około 5000", new Quantifier("Około 5000", around5000, false));

        FuzzySet around6500 = FuzzySet.createWithDenseUniverse(
                new GaussianFunction(6500, 600), 0.0, 10000.0, 0.1);
        quantifiers.put("Około 6500", new Quantifier("Około 6500", around6500, false));

        FuzzySet between7500and8000 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(7500, 7500, 8000, 8000), 0.0, 10000.0, 0.1);
        quantifiers.put("Między 7500 a 8000", new Quantifier("Między 7500 a 8000", between7500and8000, false));

        FuzzySet morethan8500 = FuzzySet.createWithDenseUniverse(
                new TrapezoidalFunction(8500, 8500, 10000, 10000), 0.0, 10000.0, 0.1);
        quantifiers.put("Więcej niż 8500", new Quantifier("Więcej niż 8500", morethan8500, false));




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
