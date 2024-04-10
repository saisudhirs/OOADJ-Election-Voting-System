public class Candidate {
    private String id;
    private String name;
    private int voteCount;

    // Constructor
    public Candidate(String id, String name) {
        this.id = id;
        this.name = name;
        this.voteCount = 0; // Initially, the candidate has no votes
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVoteCount() {
        return voteCount;
    }

    // Method to increment vote count for the candidate
    public void incrementVoteCount() {
        this.voteCount++;
    }
}
