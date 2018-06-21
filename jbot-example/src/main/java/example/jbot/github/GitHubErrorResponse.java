package example.jbot.github;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.List;

public class GitHubErrorResponse {

	private String message;
	private List<Error> errors;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public static class Error {
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}
	public boolean alreadyExists() {
		return errors != null &&
				Iterables.tryFind(errors, new Predicate<Error>() {
					@Override
					public boolean apply(GitHubErrorResponse.Error input) {
						return "already_exists".equals(input.getCode());
					}
				}).isPresent();
	}
}
