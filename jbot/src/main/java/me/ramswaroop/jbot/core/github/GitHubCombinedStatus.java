package me.ramswaroop.jbot.core.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.List;

public class GitHubCombinedStatus {

	private String state;
	private List<Status> statuses;

	@JsonProperty("total_count")
	private int totalCount;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Status> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<Status> statuses) {
		this.statuses = statuses;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isSuccessful() {
		return state.equals("success");
	}

	public boolean hasUnfinishedBuilds() {
		return Iterables.any(statuses, new Predicate<Status>() {
			@Override
			public boolean apply(Status input) {
				return !input.getContext().equals("Collaborator") && !input.isSuccessful();
			}
		});
	}
	
	public int getReviewId() {
		for (Status status : statuses) {
			if (status.getContext().equals("Collaborator")) {
				String targetUrl = status.getTargetUrl();
				return Integer.parseInt(
						targetUrl.substring(
								targetUrl.indexOf("review:id=")));
			}
		}
		return 0;
	}
	
	public String getReviewUrl() {
		for (Status status : statuses) {
			if (status.getContext().equals("Collaborator")) {
				return status.getTargetUrl();
			}
		}
		return "";
	}

	public static class Status {

		private String state;
		private String context;
		
		@JsonProperty("target_url")
		private String targetUrl;

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public boolean isSuccessful() {
			return state.equals("success");
		}
		
		public String getTargetUrl() {
			return targetUrl;
		}
		
		public void setTargetUrl(String targetUrl) {
			this.targetUrl = targetUrl;
		}
	}
}
