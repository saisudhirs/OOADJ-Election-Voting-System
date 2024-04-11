import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class Voter {
    private final String id;
    private final String name;

    public Voter(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class VoterManager {
    private List<Voter> voters = new ArrayList<>();
    private Connection conn;
    public VoterManager(Connection connection) {
        this.conn = connection;
        voters=readVotersFromDatabase();
    }

    public String addVoter(String name) {
        voters.clear();
        voters=readVotersFromDatabase();
        String voterID = generateVoterID();
        voters.add(new Voter(voterID, name));
        appendVoterToDatabase(voterID, name);
        return voterID;
    }

    public void displayVoters() {
        voters.clear();
        voters=readVotersFromDatabase();
        System.out.println("\n\n #### List of Registered Voters ####\n\n");
        for (Voter voter : voters) {
            System.out.println("Voter ID: " + voter.getId() + ", Name: " + voter.getName());
        }
    }



    private List<Voter> readVotersFromDatabase() {
        List<Voter> voters = new ArrayList<>();
        String sql = "SELECT id, name FROM voter";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String voterID = rs.getString("id");
                String name = rs.getString("name");
                voters.add(new Voter(voterID, name));
            }
        } catch (SQLException e) {
            System.err.println("Error reading voters from the database: " + e.getMessage());
        }
        return voters;
    }

    private void appendVoterToDatabase(String voterID, String name) {
        String sql = "INSERT INTO voter (id, name) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, voterID);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Voter added to the database.");
        } catch (SQLException e) {
            System.err.println("Error appending voter to the database: " + e.getMessage());
        }
    }

    private List<String> splitString(String input, char delimiter) {
        return Arrays.asList(input.split(String.valueOf(delimiter)));
    }

    private String generateVoterID() {
        return "V" + (voters.size() + 1);
    }
}

class Candidate {
    private final String name;
    private final String partyName;

    public Candidate(String name, String partyName) {
        this.name = name;
        this.partyName = partyName;
    }

    public String getName() {
        return name;
    }

    public String getPartyName() {
        return partyName;
    }
}

class CandidateManager {
    public static void addCandidate(Connection conn, String name, String partyName) {
        if (conn != null) {
            try {
                insertCandidate(conn, name, partyName);
                System.out.println(name + " added successfully for " + partyName);
            } catch (SQLException e) {
                System.err.println("Error adding candidate: " + e.getMessage());
            }
        } else {
            System.err.println("Connection to database is null.");
        }
    }

    private static void insertCandidate(Connection conn, String name, String partyName) throws SQLException {
        String sql = "INSERT INTO candidates (name, party) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, partyName);
            pstmt.executeUpdate();
            displayCandidates(conn);

        }
    }
    private static void displayCandidates(Connection conn) throws SQLException {
        String sql = "SELECT * FROM candidates";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\nCandidates Table:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Party: " + rs.getString("party"));
            }
        }
    }
}

class Vote {
    private final String timestamp;
    private final String voterID;
    private final String voterName;
    private final int choice;

    public Vote(String timestamp, String voterID, String voterName, int choice) {
        this.timestamp = timestamp;
        this.voterID = voterID;
        this.voterName = voterName;
        this.choice = choice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVoterID() {
        return voterID;
    }

    public String getVoterName() {
        return voterName;
    }

    public int getChoice() {
        return choice;
    }
}

class ElectionSystem {
    private final List<Voter> voters = new ArrayList<>();
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Vote> votes = new ArrayList<>();
    private final Connection connection;

    public ElectionSystem(Connection connection) {
        this.connection = connection;
        refreshVotersFromDatabase();
        refreshCandidatesFromDatabase();
        refreshVotesFromDatabase();
    }

    // private List<String> splitString(String input, char delimiter) {
    //     List<String> tokens = new ArrayList<>();
    //     StringTokenizer tokenizer = new StringTokenizer(input, String.valueOf(delimiter));
    //     while (tokenizer.hasMoreTokens()) {
    //         tokens.add(tokenizer.nextToken());
    //     }
    //     return tokens;
    // }

    private List<String> splitString(String input, char delimiter) {
        return Arrays.asList(input.split(String.valueOf(delimiter)));
    }

    private void refreshVotersFromDatabase() {
        voters.clear();
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM voter");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                voters.add(new Voter(rs.getString("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCandidatesFromDatabase() {
        candidates.clear();
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM candidates");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                candidates.add(new Candidate(rs.getString("name"), rs.getString("party")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshVotesFromDatabase() {
        votes.clear();
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM votes");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                votes.add(new Vote(rs.getString("timestamp"), rs.getString("voterid"), rs.getString("votername"), rs.getInt("candidateid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeVoteToDatabase(Vote vote) {
        String sql = "INSERT INTO votes (timestamp, voterid, votername, candidateid) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vote.getTimestamp());
            pstmt.setString(2, vote.getVoterID());
            pstmt.setString(3, vote.getVoterName());
            pstmt.setInt(4, vote.getChoice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isVoterVerified(String voterID, String voterName) {
        String lowerCaseVoterID = toLowerCase(voterID);
        String lowerCaseVoterName = toLowerCase(voterName);

        for (Voter voter : voters) {
            System.out.println(voter.getId()+":"+lowerCaseVoterID+"-"+voter.getName()+":"+lowerCaseVoterName);
            if (toLowerCase(voter.getId()).equals(lowerCaseVoterID) && toLowerCase(voter.getName()).equals(lowerCaseVoterName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDoubleVoting(String voterID) {
        for (Vote vote : votes) {
            if (vote.getVoterID().equals(voterID)) {
                return true;
            }
        }
        return false;
    }

    private void printCandidateList() {
        System.out.println("\n\n #### Please Choose Your Candidate ####\n\n");
        for (int i = 0; i < candidates.size(); i++) {
            System.out.println((i + 1) + ". " + candidates.get(i).getName());
        }
        System.out.println((candidates.size() + 1) + ". " + "None of These");
    }

    public void startVoting() {
        int choice;
        String voterName;
        String voterID;

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\n #### Please Enter Your Voter ID and Name ####\n\n");
        System.out.print("Voter ID: ");
        voterID = scanner.next();
        System.out.print("Voter Name: ");
        scanner.nextLine(); // Ignore any newline left in the input buffer
        voterName = scanner.nextLine();

        if (!isVoterVerified(voterID, voterName)) {
            System.out.println("\n Voter ID and Name combination not found in the records. Please retry.\n");
            return;
        }

        if (isDoubleVoting(voterID)) {
            System.out.println("\n You have already cast a vote in this election. Double voting is not allowed.\n");
            return;
        }

        printCandidateList();

        System.out.println("\n\n Input Your Choice (1 - " + (candidates.size() + 1) + ") : ");
        choice = scanner.nextInt();

        // Voting timestamp
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp);

        Vote vote = new Vote(timestampStr, voterID, voterName, choice);
        votes.add(vote);
        writeVoteToDatabase(vote);

        System.out.println("\n Thanks for voting, " + voterName + "!!!");
    }

    public void votesCount() {
        int[] voteCounts = new int[candidates.size() + 1];

        for (Vote vote : votes) {
            int choice = vote.getChoice();
            voteCounts[choice]++;
        }

        System.out.println("\n\n #### Voting Statistics ####");
        for (int i = 1; i <= candidates.size(); i++) {
            System.out.println(candidates.get(i - 1).getName() + " of " + candidates.get(i - 1).getPartyName() + " - " + voteCounts[i]);
        }
    }

    public void getLeadingCandidate() {
        int[] voteCounts = new int[candidates.size() + 1];

        for (Vote vote : votes) {
            int choice = vote.getChoice();
            voteCounts[choice]++;
        }

        int maxVotes = Arrays.stream(voteCounts, 1, voteCounts.length).max().orElse(0);

        List<String> leadingCandidates = new ArrayList<>();
        for (int i = 1; i <= candidates.size(); i++) {
            if (voteCounts[i] == maxVotes) {
                leadingCandidates.add(candidates.get(i - 1).getName());
            }
        }

        System.out.println("\n\n  #### Leading Candidate(s) ####\n\n");
        if (maxVotes > 0) {
            if (!leadingCandidates.isEmpty()) {
                if (leadingCandidates.size() == 1) {
                    System.out.println("[" + leadingCandidates.getFirst() + "]");
                } else {
                    System.out.println("Tie between the following candidates:");
                    leadingCandidates.forEach(candidate -> System.out.println("[" + candidate + "]"));
                }
            }
        } else {
            System.out.println("----- Warning !!! No-win situation ----");
        }
    }

    public void checkTurnout() {
        int totalVoters = voters.size();
        int totalVotes = votes.size();

        double turnoutFraction = (double) totalVotes / totalVoters;
        double turnoutPercentage = turnoutFraction * 100;

        DecimalFormat df = new DecimalFormat("#.##");

        System.out.println("\n\n #### Turnout Statistics ####");
        System.out.println("Total Voters: " + totalVoters);
        System.out.println("Total Votes Cast: " + totalVotes);
        System.out.println("Turnout: " + df.format(turnoutFraction) + " (" + df.format(turnoutPercentage) + "%)");
    }

    private String toLowerCase(String str) {
        return str.toLowerCase();
    }

    public void resetElection(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Clear candidates and votes tables
            stmt.executeUpdate("DELETE FROM candidates");
            stmt.executeUpdate("DELETE FROM votes");
            System.out.println("Election system reset successful (election details: candidates & votes cleared).");
        } catch (SQLException e) {
            System.err.println("Error resetting election: " + e.getMessage());
        }
    }


    public void hardReset(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Clear candidates, votes, and voters tables
            stmt.executeUpdate("DELETE FROM candidates");
            stmt.executeUpdate("DELETE FROM votes");
            stmt.executeUpdate("DELETE FROM voter");
            System.out.println("Election system hard reset successful (all details: candidates, votes & voters cleared).");
        } catch (SQLException e) {
            System.err.println("Error performing hard reset: " + e.getMessage());
        }
    }
}


public class Main {

    public static void main(String[] args) {
        // Establish database connection
        Connection conn = connectToDatabase("election.db");
        if (conn == null) {
            System.out.println("Failed to connect to the database.");
            return;
        }

        VoterManager voterManager = new VoterManager(conn);
        ElectionSystem election = new ElectionSystem(conn);

        int mode, choice;


        System.out.println("\n\n ##### Welcome to Election System #####");
        System.out.println("\n\nSelect mode of operation:");
        System.out.println(" 1. Registrations (Candidate nominations & Voter addition)");
        System.out.println(" 2. Election ");
        System.out.println(" 3. Results");
        System.out.println(" 4. Reset");

        System.out.println("\n\n Please enter your choice: ");
        Scanner scanner = new Scanner(System.in);
        mode = scanner.nextInt();
        switch (mode) {
            case 1:
                do {
                    System.out.println("\n\nRegistrations Mode\n");
                    System.out.println(" 1. Nominate a Candidate");
                    System.out.println(" 2. Add a Voter");
                    System.out.println(" 3. Query Registered Voters");
                    System.out.println("\n 0. Exit");

                    System.out.println("\n\n Please enter your choice: ");
                    scanner = new Scanner(System.in);
                    choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            System.out.println("\n\n #### Enter the Name of the New Candidate ####\n\n");
                            scanner.nextLine();
                            String candidateName = scanner.nextLine();
                            System.out.println("\n\n #### Enter the Name of the Candidate's Party ####\n\n");
                            String partyName = scanner.nextLine();
                            CandidateManager.addCandidate(conn,candidateName, partyName);
                            break;
                        case 2:
                            System.out.println("\n\n #### Enter the Name of the New Voter ####\n\n");
                            scanner.nextLine();
                            String name = scanner.nextLine();
                            String voterID = voterManager.addVoter(name);
                            System.out.println("\n New Voter " + name + " with Voter ID " + voterID + " added successfully!");
                            break;
                        case 3:
                            voterManager.displayVoters();
                            break;
                        case 0:
                            System.out.println("\n Logging off...\n");
                            break;
                    }
                }
                while (choice != 0);
                break;

            case 2:
                do {
                    System.out.println("\n\nElection Mode\n");
                    System.out.println(" 1. Cast Vote");
                    System.out.println(" 2. Check Turnout");
                    System.out.println("\n 0. Exit");

                    System.out.println("\n\n Please enter your choice: ");
                    scanner = new Scanner(System.in);
                    choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            election.startVoting();
                            break;
                        case 2:
                            election.checkTurnout();
                            break;
                        case 0:
                            System.out.println("\n Logging off...\n");
                            break;
                    }
                }
                while (choice != 0);
                break;

            case 3:
                do {
                    System.out.println("\n\nResult Mode\n");
                    System.out.println(" 1. Find Leading Candidate");
                    System.out.println(" 2. Get Detailed Vote Count");
                    System.out.println("\n 0. Exit");

                    System.out.println("\n\n Please enter your choice: ");
                    scanner = new Scanner(System.in);
                    choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            election.getLeadingCandidate();
                            break;
                        case 2:
                            election.votesCount();
                            break;
                        case 0:
                            System.out.println("\n Logging off...\n");
                            break;
                    }
                }
                while (choice != 0);
                break;

            case 4:
                do {
                    System.out.println("\n\nReset Mode\n");
                    System.out.println(" 1. Reset Election");
                    System.out.println(" 2. Hard Reset (clear all details)");
                    System.out.println("\n 0. Exit");

                    System.out.println("\n\n Please enter your choice: ");
                    scanner = new Scanner(System.in);
                    choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            election.resetElection(conn);
                            break;
                        case 2:
                            election.hardReset(conn);
                            break;
                        case 0:
                            System.out.println("\n Logging off...\n");
                            break;
                    }
                }
                while (choice != 0);
                break;

        }
    }
    private static Connection connectToDatabase(String databaseName) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
            System.out.println("Connected to the database.");
            createTables(conn);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }
    private static void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Create voter table
            stmt.execute("CREATE TABLE IF NOT EXISTS voter (id TEXT PRIMARY KEY, name TEXT)");

            // Create candidates table
            stmt.execute("CREATE TABLE IF NOT EXISTS candidates (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, party TEXT)");

            // Create votes table
            stmt.execute("CREATE TABLE IF NOT EXISTS votes (timestamp INTEGER PRIMARY KEY, voterid TEXT, votername TEXT, candidateid TEXT)");

            // Create reset_history table
            stmt.execute("CREATE TABLE IF NOT EXISTS reset_history (id INTEGER PRIMARY KEY AUTOINCREMENT, time_date TIME)");

            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}
