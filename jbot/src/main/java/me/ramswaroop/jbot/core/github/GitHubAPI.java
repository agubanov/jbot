package me.ramswaroop.jbot.core.github;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import static me.ramswaroop.jbot.core.github.GitHubApiClient.Method.GET;
import static me.ramswaroop.jbot.core.github.GitHubApiClient.Method.POST;
import static me.ramswaroop.jbot.core.github.GitHubApiClient.Method.PUT;
import static me.ramswaroop.jbot.core.github.GitHubApiClient.Method.DELETE;


public class GitHubAPI {

	private String host;
	private String token;

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String PROTECTED_BRANCH_API_MEDIATYPE = "application/vnd.github.loki-preview+json";
	private static final String REPOS = "/repos/";
	private static final String PULLS = "/pulls/";
	private static final String COMMITS = "/commits/";
	private static final String BRANCHES = "/branches/";
	private static final String PROTECTION = "/protection/";
	private static final String HOOKS = "/hooks/";
	private static final String COMMENTS = "/comments/";

	static {
		MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private GitHubAPI(String host, String token) {
		this.host = host.startsWith("https://github.com")
				? "https://api.github.com"
				: removeTrailingSlash(host) + "/api/v3";
		this.token = token;
	}

	public static GitHubAPI build(String host, String apiToken) {
		return new GitHubAPI(host, apiToken);
	}

	private GitHubApiClient get() {
		return new GitHubApiClient().authenticate(token).method(GET);
	}

	private GitHubApiClient post() {
		return new GitHubApiClient().authenticate(token).method(POST);
	}

	private GitHubApiClient put() {
		return new GitHubApiClient().authenticate(token).method(PUT);
	}

	private GitHubApiClient delete() {
		return new GitHubApiClient().authenticate(token).method(DELETE);
	}

//	public GitHubRepository getRepository(String repo) throws IOException {
//		return get()
//				.to(host + REPOS + repo, GitHubRepository.class);
//	}
//
//	public List<GitHubRepository> getOrganizationRepositories(String organization) throws IOException {
//		return get()
//				.toList(host + "/orgs/" + organization + REPOS, GitHubRepository.class);
//	}
//
//	public List<GitHubRepository> getUserRepositories(Iterable<String> scopes) throws IOException {
//		String[] defaultScope = {"owner"};
//		String affiliations = Joiner.on(",")
//				.join(Iterables.size(scopes) == 0
//						? Arrays.asList(defaultScope)
//						: scopes)
//				.toLowerCase();
//		return get()
//				.with("affiliation", affiliations)
//				.toList(host + "/user" + REPOS, GitHubRepository.class);
//	}

	public GitHubPullRequest createPullRequest(String repo, String title, String head, String base) throws IOException {
		return post()
				.with("title", title)
				.with("head", head)
				.with("base", base)
				.to(host + REPOS + repo + PULLS, GitHubPullRequest.class);
	}
//
//	public GitHubPullRequest getPullRequest(String repo, int pullRequestNumber) throws IOException {
//		return get()
//				.to(host + REPOS + repo + PULLS + pullRequestNumber, GitHubPullRequest.class);
//	}
//
//	/**
//	 * Get pull requests of repository.
//	 * State is 'open' by default
//	 */
//	public List<GitHubPullRequest> getPullRequests(String repo) throws IOException {
//		return get()
//				.toList(host + REPOS + repo + PULLS, GitHubPullRequest.class);
//	}
//
//	public void closePullRequest(String repo, int pullRequestNumber) throws IOException {
//		post().with("state", "closed")
//				.to(host + REPOS + repo + PULLS + pullRequestNumber, null);
//	}
//
//	public void mergePullRequest(String repo, GitHubPullRequest pullRequest) throws IOException {
//		put().with("sha", pullRequest.getHead().getSha())
//				.with("commit_message", pullRequest.getTitle() + " (Collaborator Auto-Merge)")
//				.to(host + REPOS + repo + PULLS + pullRequest.getId() + "/merge", null);
//	}
//
//	/**
//	 * API returns up to 250 commits in pull request
//	 */
//	public List<GitHubCommit> getPullRequestCommits(String repo, int pullRequestNumber) throws IOException {
//		return get()
//				.toList(host + REPOS + repo + PULLS + pullRequestNumber + COMMITS, GitHubCommit.class);
//	}
//
//	public List<GitHubFile> getPullRequestFiles(String repo, int pullRequestNumber) throws IOException {
//		return get()
//				.toList(host + REPOS + repo + PULLS + pullRequestNumber + "/files", GitHubFile.class);
//	}
//
//	public GitHubCommit getCommit(String repo, String sha) throws IOException {
//		return get()
//				.to(host + REPOS + repo + COMMITS + sha, GitHubCommit.class);
//	}
//
//	/**
//	 * @param path retrieves file history when specified
//	 * @param sha SHA or branch to start listing commits from. Empty: the repository’s default branch (usually master).
//	 */
//	public List<GitHubCommit> getCommits(String repo, String path, String sha) throws IOException {
//		if (StringUtils.isNotEmpty(path)) {
//			path = UrlEscapers.urlFragmentEscaper().escape(path);
//		}
//		return get()
//				.with("path", path)
//				.with("sha", sha)
//				.toList(host + REPOS + repo + COMMITS, GitHubCommit.class);
//	}
//
//	public GitHubFileContent getFileContent(String repo, String path, String ref) throws IOException {
//		return get()
//				.with("ref", ref)
//				.to(host + REPOS + repo + "/contents/" + path, GitHubFileContent.class);
//	}
//
//	/**
//	 * For files 1MB - 100MB size
//	 */
//	public GitHubFileContent getFileBlob(String repo, String fileSha) throws IOException {
//		return get()
//				.to(host + REPOS + repo + "/git/blobs/" + fileSha, GitHubFileContent.class);
//	}
//
//	public GitHubCompare compare(String repo, String base, String head) throws IOException {
//		return get()
//				.to(host + REPOS + repo + "/compare/" + base + "..." + head, GitHubCompare.class);
//	}
//
//	public void createCommitComment(String repo, String sha, String comment, String path, Integer position) throws IOException {
//		post().with("body", comment)
//				.with("path", path)
//				.with("position", position)
//				.to(host + REPOS + repo + COMMITS + sha + COMMENTS, null);
//	}
//
//	public void createPullRequestComment(String repo, int pullRequestNumber, String comment) throws IOException {
//		post().with("body", comment)
//				.to(host + REPOS + repo + "/issues/" + pullRequestNumber + COMMENTS, null);
//	}
//
//	public void deleteBranch(String repo, String ref) throws IOException {
//		delete().to(host + REPOS + repo + "/git/refs/heads/" + ref, null);
//	}
//
//	public GitHubBranch getBranch(String repo, String ref) throws IOException {
//		return get()
//				.header(ACCEPT, PROTECTED_BRANCH_API_MEDIATYPE)
//				.to(host + REPOS + repo + BRANCHES + ref, GitHubBranch.class);
//	}
//
//	public void createStatus(String repo, String sha, String status, String description, String targetUrl, String context) throws IOException {
//		post().with("state", status)
//				.with("description", description)
//				.with("target_url", targetUrl)
//				.with("context", context)
//				.to(host + REPOS + repo + "/statuses/" + sha, null);
//	}

	/**
	 * Get the combined status for a pull request.
	 */
	public GitHubCombinedStatus getStatus(String repo, GitHubPullRequest pullRequest) throws IOException {
		return getStatus(repo, pullRequest.getHeadHash());
	}

	/**
	 * Get the combined status for a specific ref. The most recent status for each context is returned, up to 100
	 */
	public GitHubCombinedStatus getStatus(String repo, String sha) throws IOException {
		return get()
				.to(host + REPOS + repo + COMMITS + sha + "/status", GitHubCombinedStatus.class);
	}

//	public void createWebhook(String repo, Iterable<String> events, boolean active, String url, String secret, boolean sslEnabled) throws IOException {
//		WebhookConfig config = new WebhookConfig();
//		config.setUrl(url);
//		config.setSecret(secret);
//		if (sslEnabled) {
//			config.setInsecureSsl("0");
//		} else {
//			config.setInsecureSsl("1");
//		}
//
//		post().with("name", "web")
//				.with("config", config)
//				.with("events", events)
//				.with("active", active)
//				.to(host + REPOS + repo + HOOKS, null);
//	}
//
//	public List<GitHubWebhook> getWebhooks(String repo) throws IOException {
//		return get()
//				.toList(host + REPOS + repo + HOOKS, GitHubWebhook.class);
//	}
//
//	public void deleteWebhook(String repo, int id) throws IOException {
//		delete().to(host + REPOS + repo + HOOKS + id, null);
//	}

	private String removeTrailingSlash(String url) {
		return url.charAt(url.length() - 1) == '/'
				? url.substring(0, url.length() - 1)
				: url;
	}

//	private class WebhookConfig {
//		private String url;
//		private String secret;
//
//		@JsonProperty("content_type")
//		private String contentType = "json";
//
//		/**
//		 * Supported values include "0" (verification is performed) and "1" (verification is not performed)
//		 */
//		@JsonProperty("insecure_ssl")
//		private String insecureSsl;
//
//		public String getContentType() {
//			return contentType;
//		}
//
//		public void setContentType(String contentType) {
//			this.contentType = contentType;
//		}
//
//		public String getInsecureSsl() {
//			return insecureSsl;
//		}
//
//		public void setInsecureSsl(String insecureSsl) {
//			this.insecureSsl = insecureSsl;
//		}
//
//		public String getSecret() {
//			return secret;
//		}
//
//		public void setSecret(String secret) {
//			this.secret = secret;
//		}
//
//		public String getUrl() {
//			return url;
//		}
//
//		public void setUrl(String url) {
//			this.url = url;
//		}
//	}
//
//	public enum Affiliation {
//		OWNER, COLLABORATOR, ORGANIZATION_MEMBER
//	}
}