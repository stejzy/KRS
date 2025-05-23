package linguistic.summary.membershipfunctions;

public class TriangularFunction implements MembershipFunction{
    private final double a, b, c;

    public TriangularFunction(double a, double b, double c) {
        if (!(a <= b && b <= c)) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double calculateMembership(double value) {
        if (value <= a || value >= c) {
            return 0.0;
        } else if (value == b) {
            return 1.0;
        } else if (value < b) {
            return (value - a) / (b - a);
        } else {
            return (c - value) / (c - b);
        }
    }
}
