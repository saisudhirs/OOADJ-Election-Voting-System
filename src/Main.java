import java.text.DecimalFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


class Voter {
    private final int id;
    private final String name;

    public Voter(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}

class VoterManager {
    private final List<Voter> voters = new ArrayList<>();
    private Connection connection;

    public VoterManager(Connection connection) {
        this.connection = connection;
        connectToDatabase();
        refreshVotersFromDatabase();
    }

    public List<Voter> getVoters() {
        return voters;
    }

    public int addVoter(String name) {
        int voterID = generateVoterID();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO voters (id, name) VALUES (?, ?)");
            preparedStatement.setInt(1, voterID);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM voters");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id")+"  "+resultSet.getString("name"));
            }
            voters.add(new Voter(voterID, name));
            System.out.println("\n\n #### Added successfully! ####\n\n");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voterID;
    }

    public void displayVoters() {
        System.out.println("\n\n #### List of Registered Voters ####\n\n");
        for (Voter voter : voters) {
            System.out.println("Voter ID: " + voter.getId() + ", Name: " + voter.getName());
        }
    }


    public void refreshVotersFromDatabase() {
        voters.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM voters");
            while (resultSet.next()) {
                voters.add(new Voter(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//
//    private void appendVoterToDatabase(Connection connection, String voterID, String name) {
//        String query = "INSERT INTO voters (id, name) VALUES (?, ?)";
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setString(1, voterID);
//            statement.setString(2, name);
//            statement.executeUpdate();
//            connection.commit(); // Commit the transaction
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    private void connectToDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:voters.db");
            createVotersTableIfNotExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createVotersTableIfNotExists() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS voters (id TEXT PRIMARY KEY, name TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private List<String> splitString(String input, char delimiter) {
        return Arrays.asList(input.split(String.valueOf(delimiter)));
    }

    private int generateVoterID() {
        return voters.size() + 1;
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
    public static void addCandidate(String name, String partyName) {
        insertCandidateIntoDatabase(name, partyName);
        System.out.println(name + " added successfully for " + partyName);
    }

    private static void insertCandidateIntoDatabase(String name, String partyName) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:candidates.db")) {
            // Create the candidates table if it does not exist
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS candidates (id INTEGER PRIMARY KEY, name TEXT, party TEXT)");
            }

            // Insert data into the candidates table
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO candidates (name, party) VALUES (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, partyName);
            preparedStatement.executeUpdate();
            System.out.println("Candidate added successfully!");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM candidates");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id")+"  "+resultSet.getString("name")+resultSet.getString("party1"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

class Vote {
    private final int id; // ID of the vote in the database
    //private final String timestamp;
    private final int voterID; // ID of the voter in the database
    private final int candidateID; // ID of the candidate in the database

    public Vote(int id, int voterID, int candidateID) {
        this.id = id;
        //this.timestamp = timestamp;
        this.voterID = voterID;
        this.candidateID = candidateID;
    }

    public int getId() {
        return id;
    }

//    public String getTimestamp() {
//        return timestamp;
//    }

    public int getVoterID() {
        return voterID;
    }

    public int getCandidateID() {
        return candidateID;
    }
}


class ElectionSystem {
    private final List<Voter> voters;
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Vote> votes = new ArrayList<>();
    private Connection connection;

    public ElectionSystem(Connection connection, List<Voter> voters) {
        this.connection = connection;
        this.voters = voters;
        connectToDatabase();
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
    private int generateVoteID() {
        return votes.size() + 1;
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:election.db");
            createTablesIfNotExist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<String> splitString(String input, char delimiter) {
        return Arrays.asList(input.split(String.valueOf(delimiter)));
    }

    private void createTablesIfNotExist() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS voters (id INTEGER PRIMARY KEY, name TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS candidates (id INTEGER PRIMARY KEY, name TEXT, party TEXT)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS votes (id INTEGER PRIMARY KEY, timestamp TEXT, voter_id INTEGER, candidate_id INTEGER, FOREIGN KEY (voter_id) REFERENCES voters(id), FOREIGN KEY (candidate_id) REFERENCES candidates(id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void refreshVotersFromDatabase() {
        voters.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM voters");
            while (resultSet.next()) {
                voters.add(new Voter(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCandidatesFromDatabase() {
        candidates.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM candidates");
            while (resultSet.next()) {
                candidates.add(new Candidate(resultSet.getString("name"), resultSet.getString("party"))); //resultSet.getString("id"),
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshVotesFromDatabase() {
        votes.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM votes");
            while (resultSet.next()) {
                votes.add(new Vote(resultSet.getInt("id"), resultSet.getInt("voter_id"), resultSet.getInt("candidate_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void writeVoteToDatabase(Vote vote) {
        try {
            refreshVotesFromDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)");
            preparedStatement.setInt(1, vote.getVoterID());
            preparedStatement.setInt(2, vote.getCandidateID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private boolean isVoterVerified(int voterID, String voterName) {
//        String lowerCaseVoterID = toLowerCase(voterID);
        String lowerCaseVoterName = toLowerCase(voterName);
        System.out.println("Helllo i am here! "+lowerCaseVoterName);
        System.out.println("Total number of voters: " + voters.size());
        refreshVotersFromDatabase();
        refreshCandidatesFromDatabase();
        refreshVotesFromDatabase();
        for (Voter voter : voters) {
            System.out.println("Voter ID: " + voter.getId() + ", Voter Name: " + voter.getName());
            if (voter.getId()==voterID && toLowerCase(voter.getName()).equals(lowerCaseVoterName)) {
                System.out.println("Voter with ID " + voterID + " and Name " + voterName + " is verified.");
                return true;
            }
        }
        return false;
    }

    private boolean isDoubleVoting(int voterID) {
        for (Vote vote : votes) {
            if (vote.getVoterID()==voterID) {
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
        int voterID;

        Scanner scanner = new Scanner(System.in);


        System.out.println("\n\n #### Please Enter Your Voter ID and Name ####\n\n");
        System.out.print("Voter ID: ");
        voterID = Integer.parseInt(scanner.next()); // Parse the input String to an int
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

        Vote vote = new Vote(generateVoteID(), voterID, choice);
        votes.add(vote);
        writeVoteToDatabase(vote);

        System.out.println("\n Thanks for voting, " + voterName + "!!!");
    }

    public void votesCount() {
        int[] voteCounts = new int[candidates.size() + 1];

        for (Vote vote : votes) {
            int choice = vote.getCandidateID();
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
            int choice = vote.getCandidateID();
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

    public void resetElection() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM candidates");
            statement.executeUpdate("DELETE FROM votes");
            statement.executeUpdate("INSERT INTO reset_history (time_date) VALUES (datetime('now'))");
            System.out.println("Election system reset successful (election details: candidates & votes cleared).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void hardReset() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM candidates");
            statement.executeUpdate("DELETE FROM votes");
            statement.executeUpdate("DELETE FROM voters");
            statement.executeUpdate("INSERT INTO reset_history (time_date) VALUES (datetime('now'))");
            System.out.println("Election system hard reset successful (all details: candidates, votes & voters cleared).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

public class Main {
    public static void main(String[] args) {
        // Establishing the connection
        Connection connection = null;
        try {
            // Register SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to the SQLite database
            String url = "jdbc:sqlite:D:\\java\\project\\OOADJ-Election-Voting-System\\src\\election.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to the database.");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS votes (id INTEGER PRIMARY KEY, timestamp TEXT, voter_id INTEGER, candidate_id INTEGER, FOREIGN KEY (voter_id) REFERENCES voters(id), FOREIGN KEY (candidate_id) REFERENCES candidates(id))");

            // Create instances of VoterManager and ElectionSystem with the same connection
            VoterManager voterManager = new VoterManager(connection);
            ElectionSystem election = new ElectionSystem(connection, voterManager.getVoters());

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
                                CandidateManager.addCandidate(candidateName, partyName);
                                break;
                            case 2:
                                System.out.println("\n\n #### Enter the Name of the New Voter ####\n\n");
                                scanner.nextLine();
                                String name = scanner.nextLine();
                                int voterID = voterManager.addVoter(name);
                                System.out.println("\n New Voter " + name + " with Voter ID " + voterID + " added successfully!");
                                break;
                            case 3:
                                voterManager.refreshVotersFromDatabase();
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
                                election.startVoting();  // Pass the connection object
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
                                System.out.println("Under development"); // TODO
                                break;
                            case 2:
                                System.out.println("Under development"); // TODO
                                break;
                            case 0:
                                System.out.println("\n Logging off...\n");
                                break;
                        }
                    }
                    while (choice != 0);
                    break;

            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to the database failed.");
            e.printStackTrace();
        } finally {
            // Close the connection in the finally block
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                System.out.println("Error closing the connection.");
                e.printStackTrace();
            }
        }
    }

}
