import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// Model code
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

class ElectionModel {
    private final List<Candidate> candidates = new ArrayList<>();
    private final List<Voter> voters = new ArrayList<>();
    private final List<Vote> votes = new ArrayList<>();

    public void addCandidate(String name, String partyName) {
        Candidate candidate = new Candidate(name, partyName);
        candidates.add(candidate);
        appendCandidateToCSV(name, partyName);
    }

    public void addVoter(String name) {
        String voterID = generateVoterID();
        Voter voter = new Voter(voterID, name);
        voters.add(voter);
        appendVoterToCSV(voterID, name);
    }

    public boolean isVoterVerified(String voterID, String voterName) {
        String lowerCaseVoterID = toLowerCase(voterID);
        String lowerCaseVoterName = toLowerCase(voterName);

        for (Voter voter : voters) {
            if (toLowerCase(voter.getId()).equals(lowerCaseVoterID) && toLowerCase(voter.getName()).equals(lowerCaseVoterName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDoubleVoting(String voterID) {
        for (Vote vote : votes) {
            if (vote.getVoterID().equals(voterID)) {
                return true;
            }
        }
        return false;
    }

    public void castVote(String voterID, String voterName, int choice) {
        long timestamp = System.currentTimeMillis();
        String timestampStr = String.valueOf(timestamp);
        Vote vote = new Vote(timestampStr, voterID, voterName, choice);
        votes.add(vote);
        writeVoteToFile(vote);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public List<Voter> getVoters() {
        return voters;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    private void appendCandidateToCSV(String name, String partyName) {
        try (BufferedWriter candidatesFile = new BufferedWriter(new FileWriter("src/candidates.csv", true))) {
            candidatesFile.write(name + "," + partyName + "\n");
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

    private void writeVoteToFile(Vote vote) {
        try (BufferedWriter voteFile = new BufferedWriter(new FileWriter("src/votes.csv", true))) {
            voteFile.write(vote.getTimestamp() + "," + vote.getVoterID() + "," + vote.getVoterName() + "," + vote.getChoice() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateVoterID() {
        return "V" + (voters.size() + 1);
    }

    private String toLowerCase(String str) {
        return str.toLowerCase();
    }
}

// View code
class ElectionView {
    public void displayCandidateList(List<Candidate> candidates) {
        System.out.println("\n\n #### Please Choose Your Candidate ####\n\n");
        for (int i = 0; i < candidates.size(); i++) {
            System.out.println((i + 1) + ". " + candidates.get(i).getName());
        }
        System.out.println((candidates.size() + 1) + ". " + "None of These");
    }

    public void displayVoterInfo(Voter voter) {
        System.out.println("\n New Voter " + voter.getName() + " with Voter ID " + voter.getId() + " added successfully!");
    }

    public void displayVotingResults() {
        System.out.println("\n Thanks for voting!");
    }

    public void displayVoterList(List<Voter> voters) {
        System.out.println("\n\n #### List of Registered Voters ####\n\n");
        for (Voter voter : voters) {
            System.out.println("Voter ID: " + voter.getId() + ", Name: " + voter.getName());
        }
    }

    public void displayVotingStats(int[] voteCounts, List<Candidate> candidates) {
        System.out.println("\n\n #### Voting Statistics ####");
        for (int i = 1; i <= candidates.size(); i++) {
            System.out.println(candidates.get(i - 1).getName() + " of " + candidates.get(i - 1).getPartyName() + " - " + voteCounts[i]);
        }
    }

    public void displayLeadingCandidate(List<String> leadingCandidates) {
        System.out.println("\n\n  #### Leading Candidate(s) ####\n\n");
        if (!leadingCandidates.isEmpty()) {
            if (leadingCandidates.size() == 1) {
                System.out.println("[" + leadingCandidates.get(0) + "]");
            } else {
                System.out.println("Tie between the following candidates:");
                leadingCandidates.forEach(candidate -> System.out.println("[" + candidate + "]"));
            }
        } else {
            System.out.println("----- Warning !!! No-win situation ----");
        }
    }

    public void displayTurnoutStats(int totalVoters, int totalVotes, double turnoutFraction, double turnoutPercentage) {
        System.out.println("\n\n #### Turnout Statistics ####");
        System.out.println("Total Voters: " + totalVoters);
        System.out.println("Total Votes Cast: " + totalVotes);
        System.out.println("Turnout: " + new DecimalFormat("#.##").format(turnoutFraction) + " (" + new DecimalFormat("#.##").format(turnoutPercentage) + "%)");
    }

    public void displayResetMessage(boolean isHardReset) {
        if (isHardReset) {
            System.out.println("Election system hard reset successful (all details: candidates, votes & voters cleared).");
        } else {
            System.out.println("Election system reset successful (election details: candidates & votes cleared).");
        }
    }
}

// Controller code
class ElectionController {
    private final ElectionModel model;
    private final ElectionView view;

    public ElectionController(ElectionModel model, ElectionView view) {
        this.model = model;
        this.view = view;
    }

    public void handleVoterRegistration(String name) {
        model.addVoter(name);
        Voter voter = model.getVoters().get(model.getVoters().size() - 1);
        view.displayVoterInfo(voter);
    }

    public void handleCandidateNomination(String name, String partyName) {
        model.addCandidate(name, partyName);
        view.displayCandidateList(model.getCandidates());
    }

    public void handleVoting(String voterID, String voterName, int choice) {
        if (!model.isVoterVerified(voterID, voterName)) {
            System.out.println("\n Voter ID and Name combination not found in the records. Please retry.\n");
            return;
        }

        if (model.isDoubleVoting(voterID)) {
            System.out.println("\n You have already cast a vote in this election. Double voting is not allowed.\n");
            return;
        }

        model.castVote(voterID, voterName, choice);
        view.displayVotingResults();
    }

    public void displayVoterList() {
        view.displayVoterList(model.getVoters());
    }

    public void displayVotingStats() {
        int[] voteCounts = new int[model.getCandidates().size() + 1];
        for (Vote vote : model.getVotes()) {
            int choice = vote.getChoice();
            voteCounts[choice]++;
        }
        view.displayVotingStats(voteCounts, model.getCandidates());
    }

    public void getLeadingCandidate() {
        int[] voteCounts = new int[model.getCandidates().size() + 1];
        for (Vote vote : model.getVotes()) {
            int choice = vote.getChoice();
            voteCounts[choice]++;
        }

        int maxVotes = Arrays.stream(voteCounts, 1, voteCounts.length).max().orElse(0);

        List<String> leadingCandidates = new ArrayList<>();
        for (int i = 1; i <= model.getCandidates().size(); i++) {
            if (voteCounts[i] == maxVotes) {
                leadingCandidates.add(model.getCandidates().get(i - 1).getName());
            }
        }

        view.displayLeadingCandidate(leadingCandidates);
    }

    public void checkTurnout() {
        int totalVoters = model.getVoters().size();
        int totalVotes = model.getVotes().size();

        double turnoutFraction = (double) totalVotes / totalVoters;
        double turnoutPercentage = turnoutFraction * 100;

        view.displayTurnoutStats(totalVoters, totalVotes, turnoutFraction, turnoutPercentage);
    }

    public void resetElection(boolean isHardReset) {
        if (isHardReset) {
            model.getVoters().clear();
            model.getCandidates().clear();
            model.getVotes().clear();
            deleteDataFiles();
        } else {
            model.getCandidates().clear();
            model.getVotes().clear();
            deleteVotesAndCandidatesFiles();
        }
        view.displayResetMessage(isHardReset);
    }

    private void deleteDataFiles() {
        File candidateFile = new File("src/candidates.csv");
        File voteFile = new File("src/votes.csv");
        File voterFile = new File("src/voters.csv");
        candidateFile.delete();
        voteFile.delete();
        voterFile.delete();
    }

    private void deleteVotesAndCandidatesFiles() {
        File candidateFile = new File("src/candidates.csv");
        File voteFile = new File("src/votes.csv");
        candidateFile.delete();
        voteFile.delete();
    }

    public void displayCandidateList() {
        view.displayCandidateList(model.getCandidates());
    }
}

//public class Main {
//    public static void main(String[] args) {
//        ElectionModel model = new ElectionModel();
//        ElectionView view = new ElectionView();
//        ElectionController controller = new ElectionController(model, view);
//
//        controller.handleCandidateNomination("John Doe", "Independent");
//        controller.handleCandidateNomination("Jane Doe", "Republican");
//        controller.handleCandidateNomination("Alice Smith", "Democratic");
//
//        controller.handleVoterRegistration("Alice");
//        controller.handleVoterRegistration("Bob");
//        controller.handleVoterRegistration("Charlie");
//
//        controller.handleVoting("V1", "Alice", 1);
//        controller.handleVoting("V2", "Bob", 2);
//        controller.handleVoting("V3", "Charlie", 3);
//
//        controller.displayVoterList();
//        controller.displayVotingStats();
//        controller.getLeadingCandidate();
//        controller.checkTurnout();
//        controller.resetElection(true);
//    }
//}


public class Main {

    public static void main(String[] args) {
        ElectionModel model = new ElectionModel();
        ElectionView view = new ElectionView();
        ElectionController controller = new ElectionController(model, view);

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
                            controller.handleCandidateNomination(candidateName, partyName);
                            break;
                        case 2:
                            System.out.println("\n\n #### Enter the Name of the New Voter ####\n\n");
                            scanner.nextLine();
                            String name = scanner.nextLine();
                            controller.handleVoterRegistration(name);
//                            System.out.println("\n New Voter " + name + " with Voter ID " + voterID + " added successfully!");
                            break;
                        case 3:
                            controller.displayVoterList();
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
                            controller.displayCandidateList();
                            scanner = new Scanner(System.in);
                            System.out.println("\n\n #### Enter the choice of vote ####\n\n");
                            int candidateChoice = scanner.nextInt();
                            System.out.println("\n\n #### Enter the ID of the Voter ####\n\n");
                            scanner.nextLine();
                            String voterID = scanner.nextLine();
                            System.out.println("\n\n #### Enter the Name of the Voter ####\n\n");
                            scanner.nextLine();
                            String voterName = scanner.nextLine();
                            controller.handleVoting(voterID, voterName, candidateChoice); //todo fix
                            break;
                        case 2:
                            controller.displayVotingStats();
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
                            controller.getLeadingCandidate();
                            break;
                        case 2:
                            controller.displayVotingStats();
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
                            controller.resetElection(false);
                            break;
                        case 2:
                            controller.resetElection(true);
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


