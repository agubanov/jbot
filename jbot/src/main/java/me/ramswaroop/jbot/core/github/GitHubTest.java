package me.ramswaroop.jbot.core.github;

import java.io.IOException;

public class GitHubTest {
	
	public static void main(String[] args) throws IOException {
		
		String host = "https://github.com";
		String token = "c6bc1343435c9747099c23d4557946717f2f273b";
		GitHubAPI api = GitHubAPI.build(host, token);
		
		String repo = "zapelin/collab-github-v2";
		String title = "zapelin-patch-102";
		String head = "zapelin-patch-102";
		String base = "master";
		
		GitHubPullRequest pullRequest = api.createPullRequest(repo, title, head, base);
		System.out.println("pullRequest.getId() = " + pullRequest.getId());
		
		GitHubCombinedStatus status = api.getStatus(repo, pullRequest);
		System.out.println("status.getReviewUrl() = " + status.getReviewUrl());
	}
}
