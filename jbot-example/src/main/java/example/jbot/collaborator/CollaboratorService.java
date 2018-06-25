package example.jbot.collaborator;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.jbot.collaborator.dto.LoginRequestArgs;
import example.jbot.collaborator.dto.LoginRequestDto;
import example.jbot.collaborator.dto.LoginResponseDto;
import example.jbot.github.GitHubApiClient;
import example.jbot.slack.command.ReviewCommand;
import example.jbot.slack.command.ReviewPokeCommand;
import example.jbot.slack.command.ReviewStatusCommand;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;

/**
 * General class for communication with collaborator
 */
public class CollaboratorService {

    private static final String JSON_API_ENDPOINT = "/services/json/v1";
    private static String serverUrl = "http://collab.aus.smartbear.com";
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
        LoginResponseDto response = request(request, LoginResponseDto.class, debugMessage);
        if(response != null){
            return response.getResult().getLoginTicket();
        }
        else{
            return debugMessage.toString();
        }
    }

    public String startReview(ReviewCommand command, String authTicket){
        //TODO all logic related to the start review

        return "review#" + command.getReviewId() + " was started!";
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

    public <S, T> T request(S requestObject, Class<T> responseObjClass, StringBuilder message){
        try {

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(requestObject);

            this.method = new PostMethod();
            PostMethod method = new PostMethod(serverUrl + JSON_API_ENDPOINT);
            StringRequestEntity requestEntity = new StringRequestEntity(
                    " [ " + jsonInString + " ]",
                    "application/json",
                    "UTF-8");
            method.setRequestEntity(requestEntity);
            int statusCode = client.executeMethod(method);

            if(statusCode == HttpStatus.SC_OK){
                String resStr = new String(method.getResponseBody(), "UTF-8");
                //save message for debug info
                message.append(resStr);
                resStr = resStr.replace('[', ' ');
                resStr = resStr.replace(']', ' ');
                T result = mapper.readValue(resStr, responseObjClass);
                return result;
            }

        }catch (IOException ex){
            message.append("Exception " + ex.getMessage());
            return null;
        }

        return null;

    }

    public static String getServerUrl() {
        return serverUrl;
    }

    public static void setServerUrl(String serverUrl) {
        CollaboratorService.serverUrl = serverUrl;
    }
}
