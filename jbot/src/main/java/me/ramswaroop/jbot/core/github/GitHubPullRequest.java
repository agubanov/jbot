package me.ramswaroop.jbot.core.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubPullRequest {

	@JsonProperty("number")
	private int id;
	private GitHubCommitPointer head;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public GitHubCommitPointer getHead() {
		return head;
	}
	
	public void setHead(GitHubCommitPointer head) {
		this.head = head;
	}
	
	public String getHeadHash() {
		return getHead().getSha();
	}
}
