package linguistic.summary.membershipfunctions;

public class TrapezoidalFunction implements MembershipFunction{
    private final double a, b, c, d;

    public TrapezoidalFunction(double a, double b, double c, double d) {
        if (!(a <= b && b <= c && c <= d)) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double calculateMembership(double value) {
        if (value <= a || value >= d) {
            return 0.0;
        } else if (value >= b && value <= c) {
            return 1.0;
        } else if (value > a && value < b) {
            return (value - a) / (b - a);
        } else {
            return (d - value) / (d - c);
        }
    }
}
