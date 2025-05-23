package utils;

import java.sql.*;
import java.util.*;

public class PostgresToMapLoader {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/fitness_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static final List<String> COLUMNS = List.of(
            "age",
            "height_cm",
            "weight_kg",
            "workout_duration_mins",
            "calories_burned",
            "heart_rate_bpm",
            "steps_taken",
            "distance_km",
            "sleep_hours",
            "daily_calories_intake",
            "resting_heart_rate_bpm"
    );

    public static Map<String, List<String>> loadData() {
        Map<String, List<String>> dataMap = new HashMap<>();

        for(String column : COLUMNS) {
            dataMap.put(column, new ArrayList<>());
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for (int i = 0; i < COLUMNS.size(); i++) {
            queryBuilder.append("\"").append(COLUMNS.get(i)).append("\"");
            if (i < COLUMNS.size() - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(" FROM fitness_data");

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(queryBuilder.toString())) {

            while (resultSet.next()) {
                for (String column : COLUMNS) {
                    String value = resultSet.getString(column);
                    dataMap.get(column).add(value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataMap;
    }

}
