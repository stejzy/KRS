package linguistic.summary;

import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.MembershipFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FuzzySet implements ISet {
    private final MembershipFunction function;
    private final double[] universe;

    public FuzzySet(MembershipFunction function, double[] universe) {
        this.function = function;
        this.universe = universe;
    }

    @Override
    public double getMembership(double x) {
        double membership = function.calculateMembership(x);

        if (function instanceof GaussianFunction && membership < 0.1) {
            return 0.0;
        }

        return membership;
    }

    @Override
    public double[] getUniverse() {
        return universe;
    }

    public static FuzzySet createWithDiscreteUniverse(MembershipFunction function, double[] values) {
        return new FuzzySet(function, values);
    }

    public static FuzzySet createWithDenseUniverse(MembershipFunction function, double start, double end, double step) {
        return new FuzzySet(function, generateDenseUniverse(start, end, step));
    }

    private static double[] generateDenseUniverse(double start, double end, double step) {
        List<Double> points = new ArrayList<>();
        for (double x = start; x <= end; x += step) {
            points.add(x);
        }
        return points.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public MembershipFunction getFunction() {
        return function;
    }
}