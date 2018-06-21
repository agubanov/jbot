package example.jbot.github;

import java.io.IOException;

public class GitHubService {
	
	public static String createGitHubPullRequestAndGetReviewLink(String host, String repo, String token,
														   String title, String from, String to) throws IOException {
		GitHubAPI api = GitHubAPI.build(host, repo, token);
		GitHubPullRequest pullRequest = api.createPullRequest(title, from, to);
		GitHubCombinedStatus status = api.getStatus(pullRequest);
		return status.getReviewUrl();
	}
	
	public static boolean closePullRequest(String host, String repo, String token, String from) {
		GitHubAPI api = GitHubAPI.build(host, repo, token);
		try {
			GitHubPullRequest pullRequest = api.getPullRequests(from).get(0);
			api.closePullRequest(pullRequest.getId());
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean closePullRequest(String host, String repo, String token, int pullrequestId) {
		GitHubAPI api = GitHubAPI.build(host, repo, token);
		try {
			api.closePullRequest(pullrequestId);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
