package example.jbot.github;

import java.io.IOException;

public class GitHubApiException extends IOException {

	public static final String BRANCH_NOT_FOUND = "Branch not found";
	public static final String REQUIRED_STATUS_CHECKS_NOT_ENABLED = "Required status checks not enabled";
	public static final String BRANCH_NOT_PROTECTED = "Branch not protected";

	private int responseCode;
	private GitHubErrorResponse gitHubError;

	public GitHubApiException(String message, Integer responseCode, GitHubErrorResponse gitHubError) {
		super(message);
		this.responseCode = responseCode;
		this.gitHubError = gitHubError;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public GitHubErrorResponse getGitHubError() {
		return gitHubError;
	}

}
