package example.jbot.slack.command;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import example.jbot.slack.ReviewAction;
import example.jbot.slack.ReviewParticipantAction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents commands:
 * /review {reviewId} add/remove -r user1 -o user2 start
 */
public class ReviewCommand {
	
	private int reviewId;
	private ReviewParticipantAction participantAction;
	private List<String> reviewers = new ArrayList<>();
	private List<String> observers = new ArrayList<>();
	private ReviewAction reviewAction;
	
	public ReviewCommand(String command) {
		List<String> args = new ArrayList<>(Splitter.on(Pattern.compile("[ ,;]")).splitToList(command));
		args.remove("");
		reviewId = Integer.parseInt(args.remove(0));
		
		if (args.contains(ReviewAction.START.name().toLowerCase())) {
			reviewAction = ReviewAction.START;
			args.remove(ReviewAction.START.name().toLowerCase());
		}
		if (args.isEmpty()) {
			return;
		}
		participantAction = ReviewParticipantAction.valueOf(args.remove(0).toUpperCase());
		
		for (int j = 0; j < args.size(); j++) {
			String arg = args.get(j);
			if (arg.contains("-r")) {
				reviewers.add(args.get(j + 1));
			} else if (arg.contains("-o")) {
				observers.add(args.get(j + 1));
			}
		}
	}
	
	public int getReviewId() {
		return reviewId;
	}
	
	public ReviewParticipantAction getParticipantAction() {
		return participantAction;
	}
	
	public List<String> getReviewers() {
		return reviewers;
	}
	
	public String getReviewersString() {
		return Joiner.on(", ").join(reviewers);
	}
	
	public List<String> getObservers() {
		return observers;
	}
	
	public String getObserversString() {
		return Joiner.on(", ").join(observers);
	}
	
	public ReviewAction getReviewAction() {
		return reviewAction;
	}
}
