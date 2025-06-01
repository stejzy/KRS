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

        age.addLabel("młody", new TriangularFunction(18, 18, 31));
        age.addLabel("w średnim wieku", new TriangularFunction(22, 35, 48));
        age.addLabel("s+tary", new TrapezoidalFunction(40, 45, 59, 59));
        variables.put("Age", age);

        LinguisticVariable height = new LinguisticVariable("Height (cm)", 150, 199, 1);
        height.addLabel("bardzo niski", new GaussianFunction(150, 4));
        height.addLabel("niski", new GaussianFunction(160, 3));
        height.addLabel("średni", new GaussianFunction(168.5, 4));
        height.addLabel("wysoki", new GaussianFunction(178.5, 4));
        height.addLabel("bardzo wysoki", new TrapezoidalFunction(180, 189, 199, 199));
        variables.put("Height (cm)", height);

        LinguisticVariable weight = new LinguisticVariable("Weight (kg)", 47, 119, 1);
        weight.addLabel("bardzo chuda", new GaussianFunction(47, 2.5));
        weight.addLabel("chuda", new GaussianFunction(55, 3));
        weight.addLabel("normalna", new GaussianFunction(66.5, 4.5));
        weight.addLabel("z nadwagą", new GaussianFunction(80, 5));
        weight.addLabel("otyła", new TrapezoidalFunction(80, 100, 119, 119));
        variables.put("Weight (kg)", weight);

        LinguisticVariable workoutDuration = new LinguisticVariable("Workout Duration (mins)", 10, 119, 1);
        workoutDuration.addLabel("krótki", new TriangularFunction(10, 10, 50));
        workoutDuration.addLabel("umiarkowany", new TriangularFunction(25, 60, 90));
        workoutDuration.addLabel("długi", new TrapezoidalFunction(70, 90, 119, 119));
        variables.put("Workout Duration (mins)", workoutDuration);

        LinguisticVariable caloriesBurned = new LinguisticVariable("Calories Burned", 100, 999, 1);
        caloriesBurned.addLabel("niska", new TriangularFunction(100, 100, 450));
        caloriesBurned.addLabel("umiarkowana", new TriangularFunction(250, 550, 850));
        caloriesBurned.addLabel("wysoka", new TrapezoidalFunction(600, 850, 999, 999));
        variables.put("Calories Burned", caloriesBurned);

        LinguisticVariable heartRate = new LinguisticVariable("Heart Rate (bpm)", 80, 179, 1);
        heartRate.addLabel("niskie", new GaussianFunction(80, 10));
        heartRate.addLabel("umiarkowane", new GaussianFunction(115, 12));
        heartRate.addLabel("podwyższone", new GaussianFunction(140, 10));
        heartRate.addLabel("wysokie", new TrapezoidalFunction(150, 165, 179, 179));
        variables.put("Heart Rate (bpm)", heartRate);

        LinguisticVariable steps = new LinguisticVariable("Steps Taken", 1000, 19998, 1);
        steps.addLabel("mało", new TriangularFunction(1000, 1000, 10000));
        steps.addLabel("średnio", new TriangularFunction(5000, 10500, 16000));
        steps.addLabel("dużo", new TrapezoidalFunction(13000, 16000, 19998, 19998));
        variables.put("Steps Taken", steps);

        LinguisticVariable distance = new LinguisticVariable("Distance (km)", 0.5, 15, 0.1);
        distance.addLabel("krótki", new GaussianFunction(0.5, 2.8));
        distance.addLabel("średni", new GaussianFunction(7.5, 2.3));
        distance.addLabel("daleki", new TrapezoidalFunction(10, 12, 15, 15));
        variables.put("Distance (km)", distance);

        LinguisticVariable sleep = new LinguisticVariable("Sleep Hours", 4, 10, 0.1);
        sleep.addLabel("mało", new TriangularFunction(4, 4, 6.5));
        sleep.addLabel("przeciętnie", new GaussianFunction(7, 1.2));
        sleep.addLabel("dużo", new TrapezoidalFunction(8, 8.5, 10, 10));
        variables.put("Sleep Hours", sleep);

        LinguisticVariable dailyCaloriesIntake = new LinguisticVariable("Daily Calories Intake", 1500, 3999, 1);
        dailyCaloriesIntake.addLabel("niskie", new TriangularFunction(1500, 1500, 2500));
        dailyCaloriesIntake.addLabel("średnie", new TriangularFunction(1800, 2750, 3600));
        dailyCaloriesIntake.addLabel("wysokie", new TrapezoidalFunction(3000, 3600, 3999, 3999));
        variables.put("Daily Calories Intake", dailyCaloriesIntake);

        LinguisticVariable restingHeartRate = new LinguisticVariable("Resting Heart Rate (bpm)", 55, 89, 1);
        restingHeartRate.addLabel("niskie", new GaussianFunction(55, 6));
        restingHeartRate.addLabel("średnie", new TriangularFunction(60, 71, 82));
        restingHeartRate.addLabel("wysokie", new TrapezoidalFunction(74, 83, 89, 89));
        variables.put("Resting Heart Rate (bpm)", restingHeartRate);
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
