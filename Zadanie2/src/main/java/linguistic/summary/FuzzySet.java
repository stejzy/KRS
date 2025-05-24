package linguistic.summary;

import linguistic.summary.membershipfunctions.MembershipFunction;

import java.util.Arrays;

public class FuzzySet {
    private final MembershipFunction function;
    private final double[] universe;

    public FuzzySet(MembershipFunction function, double[] universe) {
        this.function = function;
        this.universe = universe;
    }

    public double getMembership(double x) {
        return function.calculateMembership(x);
    }

    public FuzzySet complement() {
        return new FuzzySet(x -> 1.0 - this.getMembership(x), universe);
    }

    public FuzzySet union(FuzzySet other) {
        return new FuzzySet(x -> Math.max(this.getMembership(x), other.getMembership(x)), universe);
    }

    public FuzzySet intersection(FuzzySet other) {
        return new FuzzySet(x -> Math.min(this.getMembership(x), other.getMembership(x)), universe);
    }

    public double height() {
        double max = 0;
        for (double x : universe) {
            double mu = getMembership(x);
            if (mu > max) max = mu;
        }
        return max;
    }

    public double[] support() {
        return Arrays.stream(universe)
                .filter(x -> getMembership(x) > 0)
                .toArray();
    }

    public double[] alphaCut(double alpha) {
        return Arrays.stream(universe)
                .filter(x -> getMembership(x) >= alpha)
                .toArray();
    }

    public boolean isEmpty() {
        return Arrays.stream(universe).allMatch(x -> getMembership(x) == 0);
    }

    public boolean isNormal() {
        return Arrays.stream(universe).anyMatch(x -> getMembership(x) == 1.0);
    }

    public boolean isConvex() {
        for (int i = 0; i < universe.length; i++) {
            for (int j = 0; j < universe.length; j++) {
                double x = universe[i];
                double y = universe[j];
                for (double lambda = 0; lambda <= 1; lambda += 0.1) {
                    double z = lambda * x + (1 - lambda) * y;
                    double muZ = getMembership(z);
                    double minMuXY = Math.min(getMembership(x), getMembership(y));
                    if (muZ < minMuXY) return false;
                }
            }
        }
        return true;
    }
}

