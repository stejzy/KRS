package linguistic.summary;

import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.MembershipFunction;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FuzzySetOperations {

    public static FuzzySet complement(FuzzySet set) {
        return new FuzzySet(x -> 1.0 - set.getMembership(x), set.getUniverse());
    }

    public static FuzzySet union(FuzzySet a, FuzzySet b) {
        return new FuzzySet(x -> Math.max(a.getMembership(x), b.getMembership(x)), a.getUniverse());
    }

    public static FuzzySet intersection(FuzzySet a, FuzzySet b) {
        return new FuzzySet(x -> Math.min(a.getMembership(x), b.getMembership(x)), a.getUniverse());
    }

    public static double height(FuzzySet set) {
        double max = 0;
        for (double x : set.getUniverse()) {
            double mu = set.getMembership(x);
            if (mu > max) max = mu;
        }
        return max;
    }

    public static CrispSet support(FuzzySet set) {
        MembershipFunction mf = set.getFunction();

        double threshold = (mf instanceof GaussianFunction) ? 0.1 : 0.0;

        Set<Double> elements = Arrays.stream(set.getUniverse())
                .filter(x -> set.getMembership(x) > threshold)
                .boxed()
                .collect(Collectors.toSet());

        return new CrispSet(elements, set.getUniverse());
    }

    public static double fuzziness(FuzzySet set) {
        CrispSet supportSet = support(set);
        int supportSize = supportSet.getElements().size();
        int universeSize = set.getUniverse().length;

        System.out.println("dasdas");
        System.out.println(supportSize);
        System.out.println(universeSize);

        return ((double) supportSize / universeSize);
    }

    public static CrispSet alphaCut(FuzzySet set, double alpha) {
        Set<Double> elements = Arrays.stream(set.getUniverse())
                .filter(x -> set.getMembership(x) >= alpha)
                .boxed()
                .collect(Collectors.toSet());
        return new CrispSet(elements, set.getUniverse());
    }

    public static boolean isEmpty(FuzzySet set) {
        return Arrays.stream(set.getUniverse())
                .allMatch(x -> set.getMembership(x) == 0);
    }

    public static boolean isNormal(FuzzySet set) {
        return Arrays.stream(set.getUniverse())
                .anyMatch(x -> set.getMembership(x) == 1.0);
    }

    public static boolean isConvex(FuzzySet set) {
        double[] universe = set.getUniverse();
        for (int i = 0; i < universe.length - 2; i++) {
            double x = universe[i];
            double z = universe[i + 1];
            double y = universe[i + 2];

            double muX = set.getMembership(x);
            double muY = set.getMembership(y);
            double muZ = set.getMembership(z);

            double minMuXY = Math.min(muX, muY);

            if (muZ < minMuXY) {
                return false;
            }
        }
        return true;
    }
}

