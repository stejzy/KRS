package linguistic.summary.membershipfunctions;

public interface MembershipFunction {
    double calculateMembership(double value);

    // numerator of clm
    default double clm(){
        return 0;
    };
}
