package example.jbot.github;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

public class GitHubApiClient {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final int RESULTS_PER_PAGE = 30;

	static {
		MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private String token;
	private Map<String, Object> data = new HashMap<>();
	private String customBody;

	private HttpClient client = new HttpClient();
	private HttpMethod method;
	private Method methodType;
	public enum Method { GET, PUT, POST, DELETE }

	public GitHubApiClient() {
		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
	}

	public GitHubApiClient authenticate(String token) {
		this.token = token;
		return this;
	}

	public GitHubApiClient method(Method method) {
		switch (method) {
			case GET:
				this.method = new GetMethod();
				break;
			case POST:
				this.method = new PostMethod();
				break;
			case PUT:
				this.method = new PutMethod();
				break;

		}
		this.method.setRequestHeader(AUTHORIZATION, "token " + token);
		this.methodType = method;
		return this;
	}

	/**
	 * Key-Values used to create JSON method body
	 */
	public GitHubApiClient with(String key, Object value) {
		if (StringUtils.isNotEmpty(key)) {
			data.put(key, value);
		}
		return this;
	}

	/**
	 * Value to be used as method body as is (no conversion or JSON creation)
	 */
	public GitHubApiClient withBody(String body) {
		customBody = body;
		return this;
	}

	public GitHubApiClient header(String key, String value) {
		if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
			method.setRequestHeader(key, value);
		}
		return this;
	}

	public <T> T to(String url, Class<T> type) throws IOException {
		String response = makeCall(removeTrailingSlash(url));
		if (type == null) {
			return null;
		}
		return parse(response, type);
	}

	private String removeTrailingSlash(String url) {
		return url.charAt(url.length() - 1) == '/'
				? url.substring(0, url.length() - 1)
				: url;
	}

	public <T> List<T> toList(String url, Class<T> type) throws IOException {
		int page = 1;
		List<T> result = new ArrayList<>();
		String urlClean = removeTrailingSlash(url);

		while (result.isEmpty() || result.size() % RESULTS_PER_PAGE == 0) {
			String next = result.isEmpty()
					? urlClean
					: urlClean + (urlClean.contains("?") ? "&" : "?") + "page=" + ++page;
			String json = makeCall(next);
			if (json.equals("[]")) {
				return result;
			}
			result.addAll(parseList(json, type));
		}
		return result;
	}

	private String makeCall(String url) throws IOException {
		try {
			method.setURI(new URI(url, false, "UTF-8"));
		} catch (URIException e) {
			throw new IllegalArgumentException("Invalid uri '" + url + "': " + e.getMessage());
		}
		setBody();

		int status = client.executeMethod(method);

		// some endpoints as delete branch return no content
		if (status == HttpStatus.SC_NO_CONTENT) {
			return "";
		}
		String response = IOUtils.toString(method.getResponseBodyAsStream(), "UTF-8");
		if (status != HttpStatus.SC_OK
				& status != HttpStatus.SC_CREATED) {
			GitHubErrorResponse error = MAPPER.readValue(response, GitHubErrorResponse.class);
			throw new GitHubApiException(method.getURI() + " " + status + ": " + error.getMessage(), status, error);
		}
		return response;
	}

	private void setBody() throws IOException {
		if (!data.isEmpty()) {
			if (Method.GET.equals(methodType)
					&& method.getQueryString() == null) {
				method.setQueryString(
						"?" + Joiner.on("&").withKeyValueSeparator("=").useForNull("").join(data));
			} else if (Method.POST.equals(methodType) || Method.PUT.equals(methodType)) {
				StringRequestEntity body = new StringRequestEntity(
						MAPPER.writeValueAsString(data),
						"application/json",
						"UTF-8");
				((EntityEnclosingMethod) method).setRequestEntity(body);
			}
		} else if (StringUtils.isNotEmpty(customBody)
				&& (Method.POST.equals(methodType) || Method.PUT.equals(methodType) || Method.DELETE.equals(methodType))) {
			StringRequestEntity body = new StringRequestEntity(
					customBody,
					"application/json",
					"UTF-8");
			((EntityEnclosingMethod) method).setRequestEntity(body);
		}
	}

	private <T> T parse(String data, Class<T> type) throws IOException {
		return MAPPER.readValue(data, type);
	}

	private <T> List<T> parseList(String data, Class<T> type) throws IOException {
		return MAPPER.readValue(data, MAPPER.getTypeFactory().constructCollectionType(List.class, type));
	}
}
