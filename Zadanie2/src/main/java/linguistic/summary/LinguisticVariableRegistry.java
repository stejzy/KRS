package linguistic.summary;

import linguistic.summary.membershipfunctions.GaussianFunction;
import linguistic.summary.membershipfunctions.TrapezoidalFunction;
import linguistic.summary.membershipfunctions.TriangularFunction;

import java.util.HashMap;
import java.util.Map;

public class LinguisticVariableRegistry {
    private static final Map<String, LinguisticVariable> variables = new HashMap<>();

    static {
        LinguisticVariable age = new LinguisticVariable("Age", 18, 59, 1);

        age.addLabel("młody", new TriangularFunction(18, 18, 29));
        age.addLabel("w średnim wieku", new TriangularFunction(22, 35, 48));
        age.addLabel("stary", new TrapezoidalFunction(40, 45, 59, 59));
        variables.put("Age", age);

        LinguisticVariable height = new LinguisticVariable("Height", 150, 199, 1);
        height.addLabel("niski", new GaussianFunction(150, 5));
        height.addLabel("średni", new GaussianFunction(170, 8));
        height.addLabel("wysoki", new TrapezoidalFunction(180, 190, 199, 199));
        variables.put("Height", height);

        LinguisticVariable weight = new LinguisticVariable("Weight", 50, 119, 1);
        weight.addLabel("chuda", new GaussianFunction(50, 8));
        weight.addLabel("normalna", new GaussianFunction(75, 12));
        weight.addLabel("otyła", new TrapezoidalFunction(90, 100, 119, 119));
        variables.put("Weight", weight);
    }

    public static LinguisticVariable getLinguisticVariable(String name) {
        return variables.get(name);
    }

    public static void registerLinguisticVariable(String name, LinguisticVariable variable) {
        variables.put(name, variable);
    }

    public static boolean containsLinguisticVariable(String name) {
        return variables.containsKey(name);
    }

    public static Map<String, LinguisticVariable> getAllLinguisticVariables() {
        return variables;
    }
}
