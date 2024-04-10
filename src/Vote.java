public class Vote {
    private voter voter;
    private Candidate candidate;

    // Constructor
    public Vote(voter voter, Candidate candidate) {
        this.voter = voter;
        this.candidate = candidate;
    }

    // Getter methods
    public voter getVoter() {
        return voter;
    }

    public Candidate getCandidate() {
        return candidate;
    }
}
