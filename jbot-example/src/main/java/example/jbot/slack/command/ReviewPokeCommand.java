package example.jbot.slack.command;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents command:
 * /review {reviewId} poke -all/-unconmpleted/-p user1 user2
 */
public class ReviewPokeCommand {
	
	private int reviewId;
	private List<String> users = new ArrayList<>(); // not empty if -p was used
	private boolean pokeAll;
	private boolean pokeUncompleted;
	
	public ReviewPokeCommand(String command) {
		List<String> args = new ArrayList<>(Splitter.on(Pattern.compile("[ ,;]")).splitToList(command));
		args.remove("");
		reviewId = Integer.parseInt(args.remove(0));
		args.remove("poke");
		if (args.isEmpty()) {
			return;
		}
		if (args.get(0).equals("-all")) {
			pokeAll = true;
		} else if (args.get(0).equals("-uncompleted")) {
			pokeUncompleted = true;
		} else if (args.remove("-p")) {
			users.addAll(args);
		}
	}
	
	public int getReviewId() {
		return reviewId;
	}
	
	public List<String> getUsers() {
		return users;
	}
	
	public boolean isPokeAll() {
		return pokeAll;
	}
	
	public boolean isPokeUncompleted() {
		return pokeUncompleted;
	}
}
