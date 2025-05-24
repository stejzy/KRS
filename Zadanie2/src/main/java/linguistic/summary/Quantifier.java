package linguistic.summary;

public class Quantifier {
    private final String name;
    private final FuzzySet fuzzySet;
    private final boolean isRelative;

    public Quantifier(String name, FuzzySet fuzzySet, boolean isRelative) {
        this.name = name;
        this.fuzzySet = fuzzySet;
        this.isRelative = isRelative;
    }

    public String getName() {
        return name;
    }

    public FuzzySet getFuzzySet() {
        return fuzzySet;
    }

    public boolean isRelative() {
        return isRelative;
    }

    public double getMembership(double value) {
        return fuzzySet.getMembership(value);
    }
}

