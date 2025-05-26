package linguistic.summary;

public class CrispSet implements ISet {
    private final java.util.Set<Double> elements;
    private final double[] universe;

    public CrispSet(java.util.Set<Double> elements, double[] universe) {
        this.elements = elements;
        this.universe = universe;
    }

    @Override
    public double getMembership(double x) {
        return elements.contains(x) ? 1.0 : 0.0;
    }

    @Override
    public double[] getUniverse() {
        return universe;
    }

    public java.util.Set<Double> getElements() {
        return elements;
    }
}

