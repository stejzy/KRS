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

    public double clm() {
        return sigma * Math.sqrt(2 * Math.PI);
    }

    public double getC() { return c; }
    public double getSigma() { return sigma; }

    @Override
    public double calculateMembership(double value) {
        return Math.exp(-Math.pow(value - c, 2) / (2 * sigma * sigma));
    }
}
