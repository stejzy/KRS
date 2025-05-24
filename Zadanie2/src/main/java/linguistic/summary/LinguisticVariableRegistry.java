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

        LinguisticVariable height = new LinguisticVariable("Height (cm)", 150, 199, 1);
        height.addLabel("niski", new GaussianFunction(150, 5));
        height.addLabel("średni", new GaussianFunction(170, 8));
        height.addLabel("wysoki", new TrapezoidalFunction(180, 190, 199, 199));
        variables.put("Height (cm)", height);

        LinguisticVariable weight = new LinguisticVariable("Weight (kg)", 50, 119, 1);
        weight.addLabel("chuda", new GaussianFunction(50, 8));
        weight.addLabel("normalna", new GaussianFunction(75, 12));
        weight.addLabel("otyła", new TrapezoidalFunction(90, 100, 119, 119));
        variables.put("Weight (kg)", weight);

        LinguisticVariable workoutDuration = new LinguisticVariable("Workout Duration (mins)", 10, 119, 1);
        workoutDuration.addLabel("krótki", new TriangularFunction(10, 10, 45));
        workoutDuration.addLabel("umiarkowany", new TriangularFunction(30, 60, 90));
        workoutDuration.addLabel("długi", new TrapezoidalFunction(75, 90, 119, 119));
        variables.put("Workout Duration (mins)", workoutDuration);

        LinguisticVariable caloriesBurned = new LinguisticVariable("Calories Burned", 100, 999, 1);
        caloriesBurned.addLabel("niska", new TriangularFunction(100, 100, 450));
        caloriesBurned.addLabel("umiarkowana", new TriangularFunction(250, 550, 900));
        caloriesBurned.addLabel("wysoka", new TrapezoidalFunction(700, 900, 999, 999));
        variables.put("Calories Burned", caloriesBurned);

        LinguisticVariable heartRate = new LinguisticVariable("Heart Rate (bpm)", 80, 179, 1);
        heartRate.addLabel("niskie", new GaussianFunction(80, 10));
        heartRate.addLabel("umiarkowane", new GaussianFunction(130, 15));
        heartRate.addLabel("wysokie", new TrapezoidalFunction(150, 165, 179, 179));
        variables.put("Heart Rate (bpm)", heartRate);

        LinguisticVariable steps = new LinguisticVariable("Steps Taken", 1000, 19998, 1);
        steps.addLabel("mało", new TriangularFunction(1000, 1000, 8000));
        steps.addLabel("średnio", new TriangularFunction(5000, 10000, 15000));
        steps.addLabel("dużo", new TrapezoidalFunction(13000, 16000, 19998, 19998));
        variables.put("Steps Taken", steps);

        LinguisticVariable distance = new LinguisticVariable("Distance (km)", 0.5, 15, 0.1);
        distance.addLabel("krótki", new GaussianFunction(0.5, 2));
        distance.addLabel("średni", new GaussianFunction(7.5, 1.5));
        distance.addLabel("daleki", new TrapezoidalFunction(10, 12, 15, 15));
        variables.put("Distance (km)", distance);

        LinguisticVariable sleep = new LinguisticVariable("Sleep Hours", 4, 10, 0.1);
        sleep.addLabel("mało", new TriangularFunction(4, 4, 6));
        sleep.addLabel("przeciętnie", new GaussianFunction(7, 0.7));
        sleep.addLabel("dużo", new TrapezoidalFunction(8.5, 9, 10, 10));
        variables.put("Sleep Hours", sleep);

        LinguisticVariable dailyCaloriesIntake = new LinguisticVariable("Daily Calories Intake", 1500, 3999, 1);
        dailyCaloriesIntake.addLabel("niskie", new TriangularFunction(1500, 1500, 2200));
        dailyCaloriesIntake.addLabel("średnie", new TriangularFunction(2000, 2700, 3400));
        dailyCaloriesIntake.addLabel("wysokie", new TrapezoidalFunction(3200, 3600, 3999, 3999));
        variables.put("Daily Calories Intake", dailyCaloriesIntake);

        LinguisticVariable restingHeartRate = new LinguisticVariable("Resting Heart Rate (bpm)", 55, 89, 1);
        restingHeartRate.addLabel("niskie", new GaussianFunction(55, 5));
        restingHeartRate.addLabel("średnie", new TriangularFunction(60, 70, 80));
        restingHeartRate.addLabel("wysokie", new TrapezoidalFunction(75, 80, 89, 89));
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
