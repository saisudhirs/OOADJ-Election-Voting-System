import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class connect {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Register SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to the SQLite database
            String url = "jdbc:sqlite:D:\\java\\project\\OOADJ-Election-Voting-System\\src\\election.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to the database.");

            // Create tables and insert sample data
            createTables(connection);

            System.out.println("Tables created and sample data inserted.");

        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to the database failed.");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing the connection.");
                e.printStackTrace();
            }
        }
    }


    // Method to create tables and insert sample data
    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create tables
            statement.execute("CREATE TABLE IF NOT EXISTS candidates (id INTEGER PRIMARY KEY, name TEXT, party TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS voters (id INTEGER PRIMARY KEY, name TEXT)");
            statement.execute("CREATE TABLE IF NOT EXISTS votes (id INTEGER PRIMARY KEY, voter_id INTEGER REFERENCES voters(id), candidate_id INTEGER REFERENCES candidates(id))");
            statement.execute("CREATE TABLE IF NOT EXISTS reset_history (id INTEGER PRIMARY KEY, time_date TIMESTAMP)");

            // Insert sample data for candidates table
            statement.execute("INSERT INTO candidates (id, name, party) VALUES (1, 'Candidate 1', 'Party A')");
            statement.execute("INSERT INTO candidates (id, name, party) VALUES (2, 'Candidate 2', 'Party B')");
            statement.execute("INSERT INTO candidates (id, name, party) VALUES (3, 'Candidate 3', 'Party C')");

            // Insert sample data for voters table
            statement.execute("INSERT INTO voters (id, name) VALUES (1, 'Voter 1')");
            statement.execute("INSERT INTO voters (id, name) VALUES (2, 'Voter 2')");
            statement.execute("INSERT INTO voters (id, name) VALUES (3, 'Voter 3')");

            // Insert sample data for votes table
            statement.execute("INSERT INTO votes (id, voter_id, candidate_id) VALUES (1, 1, 1)");
            statement.execute("INSERT INTO votes (id, voter_id, candidate_id) VALUES (2, 2, 2)");
            statement.execute("INSERT INTO votes (id, voter_id, candidate_id) VALUES (3, 3, 3)");

            // Insert sample data for reset_history table
            statement.execute("INSERT INTO reset_history (id, time_date) VALUES (1, '2024-04-10 10:00:00')");
            statement.execute("INSERT INTO reset_history (id, time_date) VALUES (2, '2024-04-09 15:30:00')");

        }


    }

}
