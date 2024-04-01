import java.io.*;
import java.util.*;

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
    private final List<Voter> voters = new ArrayList<>();

    public VoterManager() {
        refreshVotersFromCSV();
    }

    public String addVoter(String name) {
        String voterID = generateVoterID();
        voters.add(new Voter(voterID, name));
        appendVoterToCSV(voterID, name);
        return voterID;
    }

    public void displayVoters() {
        System.out.println("\n\n #### List of Registered Voters ####\n\n");
        for (Voter voter : voters) {
            System.out.println("Voter ID: " + voter.getId() + ", Name: " + voter.getName());
        }
    }

    public void refreshVotersFromCSV() {
        voters.clear();
        readVotersFromCSV();
    }

    private void readVotersFromCSV() {
        try (BufferedReader voterFile = new BufferedReader(new FileReader("src/voters.csv"))) {
            String line;
            while ((line = voterFile.readLine()) != null) {
                List<String> tokens = splitString(line, ',');
                if (tokens.size() == 2) {
                    voters.add(new Voter(tokens.get(0), tokens.get(1)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendVoterToCSV(String voterID, String name) {
        try (BufferedWriter voterFile = new BufferedWriter(new FileWriter("src/voters.csv", true))) {
            voterFile.write(voterID + "," + name + "\n");
        } catch (IOException e) {
            e.printStackTrace();
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

    public Candidate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public ElectionSystem() {
        refreshVotersFromFile();
        readCandidatesFromFile();
        readVotesFromFile();
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

    private void readVotersFromFile() {
        try (BufferedReader voterFile = new BufferedReader(new FileReader("src/voters.csv"))) {
            String line;
            while ((line = voterFile.readLine()) != null) {
                List<String> tokens = splitString(line, ',');
                if (tokens.size() == 2) {
                    voters.add(new Voter(tokens.get(0), tokens.get(1)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshVotersFromFile() {
        voters.clear();
        readVotersFromFile();
    }

    private void readCandidatesFromFile() {
        try (BufferedReader candidateFile = new BufferedReader(new FileReader("src/candidates.csv"))) {
            String line;
            while ((line = candidateFile.readLine()) != null) {
                List<String> tokens = splitString(line, ',');
                if (tokens.size() == 2) {
                    candidates.add(new Candidate(tokens.getFirst()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readVotesFromFile() {
        try (BufferedReader voteFile = new BufferedReader(new FileReader("src/votes.csv"))) {
            String line;
            while ((line = voteFile.readLine()) != null) {
                List<String> tokens = splitString(line, ',');
                if (tokens.size() >= 4) {
                    votes.add(new Vote(tokens.get(0), tokens.get(1), tokens.get(2), Integer.parseInt(tokens.get(3))));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeVoteToFile(Vote vote) {
        try (BufferedWriter voteFile = new BufferedWriter(new FileWriter("src/votes.csv", true))) {
            voteFile.write(vote.getTimestamp() + "," + vote.getVoterID() + "," + vote.getVoterName() + "," + vote.getChoice() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isVoterVerified(String voterID, String voterName) {
        String lowerCaseVoterID = toLowerCase(voterID);
        String lowerCaseVoterName = toLowerCase(voterName);

        for (Voter voter : voters) {
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
        writeVoteToFile(vote);

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
            System.out.println(candidates.get(i - 1).getName() + " - " + voteCounts[i]);
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
            if (leadingCandidates.size() == 1) {
                System.out.println("[" + leadingCandidates.getFirst() + "]");
            } else {
                System.out.println("Tie between the following candidates:");
                leadingCandidates.forEach(candidate -> System.out.println("[" + candidate + "]"));
            }
        } else {
            System.out.println("----- Warning !!! No-win situation ----");
        }
    }

    private String toLowerCase(String str) {
        return str.toLowerCase();
    }
}

public class Main {
    private static final String[] CANDIDATE_NAMES = {"Candidate A", "Candidate B", "Candidate C", "Candidate D"};
    private static final int CANDIDATE_COUNT = CANDIDATE_NAMES.length;

    public static void main(String[] args) {
        VoterManager voterManager = new VoterManager();
        ElectionSystem election = new ElectionSystem();
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
                            System.out.println("Under development"); // TODO
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
                            System.out.println("Under development"); // TODO
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
    }
}
