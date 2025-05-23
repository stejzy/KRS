package linguistic.summary.membershipfunctions;

public class GaussianFunction implements MembershipFunction{
    private final double c;
    private final double sigma;

    public GaussianFunction(double c, double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma must be positive");
        }
        this.c = c;
        this.sigma = sigma;
    }

    @Override
    public double calculateMembership(double value) {
        return Math.exp(-Math.pow(value - c, 2) / (2 * sigma * sigma));
    }
}
