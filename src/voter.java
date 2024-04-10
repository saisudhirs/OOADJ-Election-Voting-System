public class voter {
    private String id;
    private String name;
    private boolean hasVoted;

    // Constructor
    public voter(String id, String name) {
        this.id = id;
        this.name = name;
        this.hasVoted = false; // Initially, the voter hasn't voted
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    // Method to mark the voter as voted
    public void markAsVoted() {
        this.hasVoted = true;
    }
}
