import com.opencsv.CSVReader;
import java.io.FileReader;
import java.sql.*;

public class CsvToPostgresLoader {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/fitness_db";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "1234";

    private static final String CSV_FILE = "workout_fitness_tracker_data.csv";

    public static void main(String[] args) {
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                CSVReader reader = new CSVReader(new FileReader(CSV_FILE))
        ) {
            System.out.println("Połączono z bazą danych.");

            createTableIfNotExists(conn);

            String[] headers = reader.readNext(); // Pomijamy nagłówki
            String[] line;
            while ((line = reader.readNext()) != null) {
                insertRecord(conn, line);
            }

            System.out.println("Import danych zakończony pomyślnie.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS fitness_data (
                user_id INT,
                age INT,
                gender VARCHAR(10),
                height_cm INT,
                weight_kg INT,
                workout_type VARCHAR(50),
                workout_duration_mins INT,
                calories_burned INT,
                heart_rate_bpm INT,
                steps_taken INT,
                distance_km FLOAT,
                workout_intensity VARCHAR(20),
                sleep_hours FLOAT,
                water_intake_liters FLOAT,
                daily_calories_intake INT,
                resting_heart_rate_bpm INT,
                vo2_max FLOAT,
                body_fat_percent FLOAT,
                mood_before_workout VARCHAR(20),
                mood_after_workout VARCHAR(20)
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void insertRecord(Connection conn, String[] data) throws SQLException {
        String sql = """
        INSERT INTO fitness_data VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        );
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parseIntSafe(data[0]));
            ps.setInt(2, parseIntSafe(data[1]));
            ps.setString(3, data[2]);
            ps.setInt(4, parseIntSafe(data[3]));
            ps.setInt(5, parseIntSafe(data[4]));
            ps.setString(6, data[5]);
            ps.setInt(7, parseIntSafe(data[6]));
            ps.setInt(8, parseIntSafe(data[7]));
            ps.setInt(9, parseIntSafe(data[8]));
            ps.setInt(10, parseIntSafe(data[9]));
            ps.setFloat(11, parseFloatSafe(data[10]));
            ps.setString(12, data[11]);
            ps.setFloat(13, parseFloatSafe(data[12]));
            ps.setFloat(14, parseFloatSafe(data[13]));
            ps.setInt(15, parseIntSafe(data[14]));
            ps.setInt(16, parseIntSafe(data[15]));
            ps.setFloat(17, parseFloatSafe(data[16]));
            ps.setFloat(18, parseFloatSafe(data[17]));
            ps.setString(19, data[18]);
            ps.setString(20, data[19]);
            ps.executeUpdate();
        }
    }

    private static int parseIntSafe(String s) {
        return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim());
    }

    private static float parseFloatSafe(String s) {
        return (s == null || s.isBlank()) ? 0.0f : Float.parseFloat(s.trim());
    }

}
