package example.jbot.slack.command;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents command:
 * /review {reviewId} status -all
 */
public class ReviewStatusCommand {
	
	private int reviewId;
	private boolean fullStatus;
	
	public ReviewStatusCommand(String command) {
		List<String> args = new ArrayList<>(Splitter.on(" ").splitToList(command));
		reviewId = Integer.parseInt(args.remove(0));
		args.remove("status");
		if (args.isEmpty()) {
			return;
		}
		if (args.get(0).equals("-all")) {
			fullStatus = true;
		}
	}
	
	public int getReviewId() {
		return reviewId;
	}
	
	public boolean isFullStatus() {
		return fullStatus;
	}
}
