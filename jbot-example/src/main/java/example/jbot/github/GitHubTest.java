package example.jbot.github;

import java.io.IOException;

public class GitHubTest {
	
	public static void main(String[] args) throws IOException {
		
		String host = "https://github.com";
		String repo = "zapelin/collab-github-v2";
		String token = "c6bc1343435c9747099c23d4557946717f2f273b";
		
		String title = "zapelin-patch-102";
		String head = "zapelin-patch-102";
		String base = "master";
		
		String reviewLink = createGitHubPullRequestAndGetReviewLink(host, repo, token, title, head, base);
		System.out.println("reviewLink = " + reviewLink);
	}
	
	public static String createGitHubPullRequestAndGetReviewLink(String host, String repo, String token,
														   String title, String head, String base) throws IOException {
		GitHubAPI api = GitHubAPI.build(host, repo, token);
		GitHubPullRequest pullRequest = api.createPullRequest(title, head, base);
		GitHubCombinedStatus status = api.getStatus(pullRequest);
		return status.getReviewUrl();
	}
}
