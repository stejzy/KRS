package linguistic.summary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.TrapezoidalFunction;
import linguistic.summary.membershipfunctions.TriangularFunction;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantifierRegistry {
    private static final Map<String, Quantifier> quantifiers = new HashMap<>();

    private static final String JSON_PATH = "src/main/resources/quantifiers.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
//        INFO: Kwantyfiaktory wględne
//        FuzzySet veryFewSet = FuzzySet.createWithDenseUniverse(
//                new GaussianFunction(0, 0.05), 0.0, 1.0, 0.001);
//        quantifiers.put("Bardzo niewiele", new Quantifier("Bardzo niewiele", veryFewSet, true));
//
//        FuzzySet fewSet = FuzzySet.createWithDenseUniverse(
//                new TriangularFunction(0.00, 0.125, 0.25), 0.0, 1.0, 0.001);
//        quantifiers.put("Niewiele", new Quantifier("Niewiele", fewSet, true));
//
//        FuzzySet severalSet = FuzzySet.createWithDenseUniverse(
//                new TrapezoidalFunction(0.15, 0.25, 0.35, 0.45), 0.0, 1.0, 0.001);
//        quantifiers.put("Kilka", new Quantifier("Kilka", severalSet, true));
//
//        FuzzySet aboutHalfSet = FuzzySet.createWithDenseUniverse(
//                new GaussianFunction(0.50, 0.08), 0.0, 1.0, 0.001);
//        quantifiers.put("Około połowy", new Quantifier("Około połowy", aboutHalfSet, true));
//
//        FuzzySet manySet = FuzzySet.createWithDenseUniverse(
//                new TrapezoidalFunction(0.60, 0.65, 0.75, 0.80), 0.0, 1.0, 0.001);
//        quantifiers.put("Wiele", new Quantifier("Wiele", manySet, true));
//
//        FuzzySet majoritySet = FuzzySet.createWithDenseUniverse(
//                new TriangularFunction(0.73, 0.825, 0.93), 0.0, 1.0, 0.001);
//        quantifiers.put("Większość", new Quantifier("Większość", majoritySet, true));
//
//        FuzzySet almostAllSet = FuzzySet.createWithDenseUniverse(x -> {
//            if (x == 1.0) return 0.0;
//            return new TrapezoidalFunction(0.85, 0.925, 1.0, 1.0).calculateMembership(x);
//        }, 0.0, 1.0, 0.001);
//        quantifiers.put("Prawie wszystkie", new Quantifier("Prawie wszystkie", almostAllSet, true));

//        INFO: Kwantyfiaktory bezwględne

//        FuzzySet lessthan1000 = FuzzySet.createWithDenseUniverse(
//                new TrapezoidalFunction(0, 0, 1000, 1000), 0.0, 10000.0, 0.1);
//        quantifiers.put("Mniej niż 1000", new Quantifier("Mniej niz 1000", lessthan1000, false));
//
//        FuzzySet around2000 = FuzzySet.createWithDenseUniverse(
//                new GaussianFunction(2000, 600), 0.0, 10000.0, 0.1);
//        quantifiers.put("Około 2000", new Quantifier("Około 2000", around2000, false));
//
//        FuzzySet between2500and3000 = FuzzySet.createWithDenseUniverse(
//                new TrapezoidalFunction(2500, 2500, 3000, 3000), 0.0, 10000.0, 0.1);
//        quantifiers.put("Między 2500 a 3000", new Quantifier("Między 2500 a 3000", between2500and3000, false));
//
//        FuzzySet around4000 = FuzzySet.createWithDenseUniverse(
//                new GaussianFunction(4000, 600), 0.0, 10000.0, 0.1);
//        quantifiers.put("Około 4000", new Quantifier("Około 4000", around4000, false));
//
//        FuzzySet around5000 = FuzzySet.createWithDenseUniverse(
//                new GaussianFunction(5000, 600), 0.0, 10000.0, 0.1);
//        quantifiers.put("Około 5000", new Quantifier("Około 5000", around5000, false));
//
//        FuzzySet around6500 = FuzzySet.createWithDenseUniverse(
//                new GaussianFunction(6500, 600), 0.0, 10000.0, 0.1);
//        quantifiers.put("Około 6500", new Quantifier("Około 6500", around6500, false));
//
//        FuzzySet between7500and8000 = FuzzySet.createWithDenseUniverse(
//                new TrapezoidalFunction(7500, 7500, 8000, 8000), 0.0, 10000.0, 0.1);
//        quantifiers.put("Między 7500 a 8000", new Quantifier("Między 7500 a 8000", between7500and8000, false));
//
//        FuzzySet morethan8500 = FuzzySet.createWithDenseUniverse(
//                new TrapezoidalFunction(8500, 8500, 10000, 10000), 0.0, 10000.0, 0.1);
//        quantifiers.put("Więcej niż 8500", new Quantifier("Więcej niż 8500", morethan8500, false));


        loadQuantifiers();

        //NOTE_FOR_ME: DO ZASTANOWIENIA SIE
//        FuzzySet allSet = new FuzzySet(x -> x == 1.0 ? 1.0 : 0.0, createUniverse(1.0, 1.0, 0.001));
//        quantifiers.put("wszystkie", new Quantifier("wszystkie", allSet, true));

    }

    // Dodaj wewnątrz klasy QuantifierRegistry (na końcu pliku, przed ostatnim nawiasem klamrowym)
    private static class QuantifierJson {
        public String name;
        public String type;
        public double[] params;
        public boolean relative;

        public static QuantifierJson fromQuantifier(Quantifier q) {
            QuantifierJson qj = new QuantifierJson();
            qj.name = q.getName();
            qj.relative = q.isRelative();
            var mf = q.getFuzzySet().getFunction();
            if (mf instanceof TriangularFunction t) {
                qj.type = "Triangular";
                qj.params = new double[]{t.getA(), t.getB(), t.getC()};
            } else if (mf instanceof TrapezoidalFunction t) {
                qj.type = "Trapezoidal";
                qj.params = new double[]{t.getA(), t.getB(), t.getC(), t.getD()};
            } else if (mf instanceof GaussianFunction g) {
                qj.type = "Gaussian";
                qj.params = new double[]{g.getC(), g.getSigma()};
            } else {
                throw new IllegalArgumentException("Nieobsługiwany typ funkcji przy serializacji");
            }
            return qj;
        }
    }

    private static void loadQuantifiers() {
        try {
            File file = new File(JSON_PATH);
            List<QuantifierJson> list;
            if (file.exists()) {
                list = mapper.readValue(file, new TypeReference<>() {});
            } else {
                // fallback do zasobów (np. przy pierwszym uruchomieniu)
                InputStream is = QuantifierRegistry.class.getClassLoader().getResourceAsStream("quantifiers.json");
                list = mapper.readValue(is, new TypeReference<>() {});
            }
            quantifiers.clear();
            for (QuantifierJson qj : list) {
                FuzzySet set = switch (qj.type) {
                    case "Triangular" -> FuzzySet.createWithDenseUniverse(
                            new TriangularFunction(qj.params[0], qj.params[1], qj.params[2]),
                            qj.relative ? 0.0 : 0.0,
                            qj.relative ? 1.0 : 10000.0,
                            qj.relative ? 0.001 : 0.1
                    );
                    case "Trapezoidal" -> FuzzySet.createWithDenseUniverse(
                            new TrapezoidalFunction(qj.params[0], qj.params[1], qj.params[2], qj.params[3]),
                            qj.relative ? 0.0 : 0.0,
                            qj.relative ? 1.0 : 10000.0,
                            qj.relative ? 0.001 : 0.1
                    );
                    case "Gaussian" -> FuzzySet.createWithDenseUniverse(
                            new GaussianFunction(qj.params[0], qj.params[1]),
                            qj.relative ? 0.0 : 0.0,
                            qj.relative ? 1.0 : 10000.0,
                            qj.relative ? 0.001 : 0.1
                    );
                    default -> throw new IllegalArgumentException("Nieznany typ funkcji: " + qj.type);
                };
                quantifiers.put(qj.name, new Quantifier(qj.name, set, qj.relative));
            }
        } catch (Exception e) {
            throw new RuntimeException("Błąd ładowania kwantyfikatorów z JSON: " + e.getMessage(), e);
        }
    }

    public static Quantifier get(String name) {
        return quantifiers.get(name);
    }

    public static Map<String, Quantifier> getAll() {
        return quantifiers;
    }

    public static void addQuantifier(String name, Quantifier quantifier) {
        quantifiers.put(name, quantifier);
        saveQuantifiers();
    }

    private static void saveQuantifiers() {
        try {
            List<QuantifierJson> list = new ArrayList<>();
            for (Quantifier q : quantifiers.values()) {
                QuantifierJson qj = QuantifierJson.fromQuantifier(q);
                list.add(qj);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(JSON_PATH), list);
        } catch (Exception e) {
            throw new RuntimeException("Błąd zapisu kwantyfikatorów do JSON: " + e.getMessage(), e);
        }
    }
}
