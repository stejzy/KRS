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

    public double clm() {
        double rising = 0.5 * (b - a);
        double top = c - b;
        double falling = 0.5 * (d - c);
        return rising + top + falling;
    }

    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    public double getD() { return d; }

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
