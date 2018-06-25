package example.jbot.collaborator;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.jbot.collaborator.dto.LoginRequestDto;
import example.jbot.github.GitHubApiClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.net.URL;

/**
 * General class for communication with collaborator
 */
public class CollaboratorService {

    private static final String JSON_API_ENDPOINT = "/services/json/v1";
    private HttpClient client = new HttpClient();
    private HttpMethod method;
    private GitHubApiClient.Method methodType;
    public enum Method { GET, PUT, POST, DELETE }

    public String login(String login, String password, String collabHost) {
        try {

            LoginRequestDto request = new LoginRequestDto();
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(request);

            URL serverURL = new URL("http://collab.aus.smartbear.com");
            this.method = new PostMethod();
            PostMethod method = new PostMethod("http://collab.aus.smartbear.com"+JSON_API_ENDPOINT);
            //((PostMethod) method).getParams().setParameters();
            StringRequestEntity requestEntity = new StringRequestEntity(
                    " [ {\"command\" : \"SessionService.getLoginTicket\", \"args\":{\"login\": \"aivashchenko\", \"password\": \"<my-password>\"}} ]",
                    "application/json",
                    "UTF-8");
            method.setRequestEntity(requestEntity);
            int statusCode = client.executeMethod(method);
            if(statusCode == HttpStatus.SC_OK){
                String resStr = new String(method.getResponseBody(), "UTF-8");
                return resStr;
            }

        }catch (IOException ex){
            return "Exception " + ex.getMessage();
        }
        return "";
    }
}
