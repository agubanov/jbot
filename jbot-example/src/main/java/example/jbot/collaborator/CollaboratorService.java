package example.jbot.collaborator;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.jbot.collaborator.dto.*;
import example.jbot.github.GitHubApiClient;
import example.jbot.slack.ReviewAction;
import example.jbot.slack.command.ReviewCommand;
import example.jbot.slack.command.ReviewPokeCommand;
import example.jbot.slack.command.ReviewStatusCommand;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * General class for communication with collaborator
 */
public class CollaboratorService {
	
	private static final Logger logger = LoggerFactory.getLogger(CollaboratorService.class);
    private static final String JSON_API_ENDPOINT = "/services/json/v1";
    private String serverUrl = "http://collab.aus.smartbear.com";
    private HttpClient client = new HttpClient();
    private HttpMethod method;
    private GitHubApiClient.Method methodType;
    public enum Method { GET, PUT, POST, DELETE }


    /**
     * Get Auth token from collaborator
     * @param login - login
     * @param password - password
     * @param collabHost - Collaborator host
     * @return
     */
    public String login(String login, String password, String collabHost) {

        LoginRequestDto request = new LoginRequestDto();
        request.setArgs(new LoginRequestArgs(login, password));
        StringBuilder debugMessage = new StringBuilder();
        LoginResponseDto response = request(request, LoginResponseDto.class, debugMessage, null, null);
        if(response != null){
            return response.getResult().getLoginTicket();
        }
        else{
            return debugMessage.toString();
        }
    }
	
    public String startReview(ReviewCommand command, String collabLogin, String authTicket){
		String response = "";
  
		List<AssignmentDto> assignments = new ArrayList<>();
		for (String reviewer : command.getReviewers()) {
			assignments.add(new AssignmentDto(reviewer, AssignmentRole.REVIEWER.name()));
		}
		for (String reviewer : command.getObservers()) {
			assignments.add(new AssignmentDto(reviewer, AssignmentRole.OBSERVER.name()));
		}
	
		int reviewId = command.getReviewId();
		if (!assignments.isEmpty()) {
			UpdateAssignmentsDto updateAssignments = new UpdateAssignmentsDto(
					new UpdateAssignmentsArgs(reviewId, assignments)
			);
			request(updateAssignments, null, new StringBuilder(), collabLogin, authTicket);
			response = "participants added to review#" + reviewId;
		}
	
		if (command.getReviewAction() != null && command.getReviewAction().equals(ReviewAction.START)) {
			StartReviewDto startReview = new StartReviewDto(
					new StartReviewArgs(reviewId));
			request(startReview, null, new StringBuilder(), collabLogin, authTicket);
			response = "review #" + reviewId + " was started!";
		}
	
		return response;
    }

    public String pokeReviewParticipants(ReviewPokeCommand command, String authTicket){
        //TODO all logic related to poke review participants

        return "";
    }

    public String getReviewStatus(ReviewStatusCommand command, String authTicket){
        //TODO all logic related to the get review status

        return "";
    }

    /**
     * General method for communication with Collaborator
     * @param requestObject
     * @param responseObjClass
     * @param message
     * @param <S>
     * @param <T>
     * @return
     */

    public <S, T> T request(S requestObject, Class<T> responseObjClass, StringBuilder message, String login, String ticket){
        try {

            ObjectMapper mapper = new ObjectMapper();
	
			this.method = new PostMethod();
			PostMethod method = new PostMethod(serverUrl + JSON_API_ENDPOINT);
			String jsonInString = mapper.writeValueAsString(requestObject);
			if (login != null && !login.isEmpty()) {
				jsonInString =
						"{\"command\" : \"SessionService.authenticate\"," +
								"\"args\":{\"login\":\"" + login + "\",\"ticket\":\"" + ticket + "\"}}, "
								+ jsonInString;
			}
			StringRequestEntity requestEntity = new StringRequestEntity(
                    " [ " + jsonInString + " ]",
                    "application/json",
                    "UTF-8");
			logger.debug("request: " + requestEntity.getContent());
            method.setRequestEntity(requestEntity);
            int statusCode = client.executeMethod(method);

            if(statusCode == HttpStatus.SC_OK){
				String resStr = new String(method.getResponseBody(), "UTF-8");
				logger.debug("response: " + resStr);
				//save message for debug info
				message.append(resStr);
				resStr = resStr.trim();
                resStr = resStr.substring(1, resStr.length() - 1);
				//resStr = resStr.replace('[', ' ');
                //resStr = resStr.replace(']', ' ');
				if (responseObjClass == null) {
					return null;
				}
                T result = mapper.readValue(resStr, responseObjClass);
                return result;
            }

        }catch (IOException ex){
            message.append("Exception " + ex.getMessage());
            return null;
        }

        return null;

    }

    public String getReviewDetails(ReviewStatusCommand command, String login, String ticket){

        String jsonRequest = "{\"command\" : \"SessionService.authenticate\",\n" +
                "\n" +
                "               \"args\":{\"login\":\"" + login + "\",\"ticket\":\""+ticket+"\"}},\n" +
                "\n" +
                "         {\"command\" : \"ReviewService.getReviewSummary\",\n" +
                "         \"args\" : {\"reviewId\":\""+command.getReviewId()+"\",\n" +
                "         \"clientBuild\" : \"11311302\",\n" +
                "         \"clientGuid()\" : \"52b19a07163ee7a14644aed1624f7104\",\n" +
                "         \"updateToken\" : \"\",\n" +
                "         \"active\" : true\n" +
                "         }\n" +
                "         }\n" +
                "\n" +
                "\n";
//        return jsonRequest;
        try{
            this.method = new PostMethod();
            PostMethod method = new PostMethod(serverUrl + JSON_API_ENDPOINT);
            StringRequestEntity requestEntity = new StringRequestEntity(
                    " [ " + jsonRequest + " ]",
                    "application/json",
                    "UTF-8");
            method.setRequestEntity(requestEntity);
            int statusCode = client.executeMethod(method);

            if(statusCode == HttpStatus.SC_OK){
                String resStr = new String(method.getResponseBody(), "UTF-8");
                logger.debug("response: " + resStr);
                resStr = resStr.substring(1, resStr.length() - 1);
                resStr = resStr.replace("{\"result\":{}},", "");
                ObjectMapper mapper = new ObjectMapper();
                StatusResponseDto result = mapper.readValue(resStr, StatusResponseDto.class);
                if(command.isFullStatus()) {
                    return result.getResult().getInfo();
                }
                else{
                    return result.getResult().getShortInfo();
                }
            }

        }catch (IOException ex){
            return "Exception " + ex.getMessage();
        }
        return "";
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
