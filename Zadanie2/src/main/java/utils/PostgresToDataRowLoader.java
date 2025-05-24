package utils;

import java.sql.*;
import java.util.*;

public class PostgresToDataRowLoader {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/fitness_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static final Map<String, String> COLUMN_NAME_MAPPING = createColumnNameMapping();

    public static List<DataRow> loadDataRows() {
        List<DataRow> dataRows = new ArrayList<>();

        List<String> dbColumns = new ArrayList<>(COLUMN_NAME_MAPPING.keySet());

        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for (int i = 0; i < dbColumns.size(); i++) {
            queryBuilder.append("\"").append(dbColumns.get(i)).append("\"");
            if (i < dbColumns.size() - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(" FROM fitness_data");

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(queryBuilder.toString())) {

            while (resultSet.next()) {
                Map<String, Double> attributes = new HashMap<>();
                for (String dbColumn : dbColumns) {
                    double value = resultSet.getDouble(dbColumn);
                    if (resultSet.wasNull()) {
                        value = 0.0;
                    }
                    String label = COLUMN_NAME_MAPPING.get(dbColumn);
                    attributes.put(label, value);
                }
                dataRows.add(new DataRow(attributes));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataRows;
    }

    private static Map<String, String> createColumnNameMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("age", "Age");
        map.put("height_cm", "Height (cm)");
        map.put("weight_kg", "Weight (kg)");
        map.put("workout_duration_mins", "Workout Duration (mins)");
        map.put("calories_burned", "Calories Burned");
        map.put("heart_rate_bpm", "Heart Rate (bpm)");
        map.put("steps_taken", "Steps Taken");
        map.put("distance_km", "Distance (km)");
        map.put("sleep_hours", "Sleep Hours");
        map.put("daily_calories_intake", "Daily Calories Intake");
        map.put("resting_heart_rate_bpm", "Resting Heart Rate (bpm)");
        return Collections.unmodifiableMap(map);
    }
}
