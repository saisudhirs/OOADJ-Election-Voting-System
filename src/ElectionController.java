@CustomController
public class ElectionController {

    @CustomGetMapping("/")
    public <Model> String home(@CustomModelAttribute Model model) {
        // Populate model with data if needed
        return "home"; // Return the homepage view
    }

    @CustomPostMapping("/vote")
    public String vote(@CustomModelAttribute("vote") Vote vote) {
        // Logic for casting a vote
        return "redirect:/result";
    }

    @CustomGetMapping("/result")
    public <Model> String result(@CustomModelAttribute Model model) {
        // Logic for displaying real-time vote counts
        return "result";
    }
}
