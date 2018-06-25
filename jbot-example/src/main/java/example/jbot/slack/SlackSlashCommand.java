package example.jbot.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.jbot.github.GitHubService;
import me.ramswaroop.jbot.core.slack.models.Attachment;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;


/**
 * Sample Slash Command Handler.
 *
 * @author ramswaroop
 * @version 1.0.0, 20/06/2016
 */
@RestController
@Profile("slack")
public class SlackSlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(SlackSlashCommand.class);

    /**
     * The token you get while creating a new Slash Command. You
     * should paste the token in application.properties file.
     */
    @Value("${slashCommandToken}")
    private String slackToken;

    @Value("${slackIncomingWebhookUrl}")
    private String slackIncomingWebhookUrl;

    @Value("${gitHubToken}")
    private String gitHubToken;

    @RequestMapping(value = "/slackwebhook",
            method = RequestMethod.POST)
    public void onReceiveWebhook(@RequestBody Map<String, Object> payload) {

        String eventType = payload.getOrDefault("eventType", "").toString();
        WebhookEventType webhookEventType = WebhookEventType.valueOf(eventType);

        if (webhookEventType == null) {
            return;
        }

        switch(webhookEventType) {
            case COMMENT_ADDED:
                commentAddedWebhook(payload);
                break;
            case REVIEW_PHASE_CHANGED:
                reviewPhaseWebhook(payload);
                break;

            case ADDED_FILES:
                addedFilesWebhook(payload);
                break;

        }
    }

    private void addedFilesWebhook(@RequestBody Map<String, Object> payload) {
        List<String> participants = (List<String>) payload.getOrDefault("reviewParticipants", Collections.EMPTY_LIST);
        if (participants.isEmpty()) { return; }

        String reviewId = payload.getOrDefault("reviewId", "").toString();

        for (String login : participants) {
            RestTemplate restTemplate = new RestTemplate();
            RichMessage richMessage = new RichMessage();
            UserProfile userProfile = UserProfileUtil.getUserProfileByCollabLogin(login);

            if (userProfile != null) {
                richMessage.setChannel(userProfile.getBotChannel());
            }

            List<String> filePaths = (List<String>) payload.getOrDefault("filePaths", Collections.EMPTY_LIST);
            String linkReview = payload.getOrDefault("linkReview", "").toString();
            String userAddedFile = payload.getOrDefault("userAddedFile", "").toString();

            StringBuilder textBuilder = new StringBuilder();
            textBuilder.append("New files were added in review #");
            textBuilder.append(reviewId);
            textBuilder.append(" by ");
            textBuilder.append(userAddedFile);

            richMessage.setText(textBuilder.toString());

            Attachment[] attachments = new Attachment[filePaths.size() + 1];
            for (int i = 0; i < filePaths.size(); i++) {
                attachments[i] = new Attachment();
                attachments[i].setText(filePaths.get(i));
            }
            attachments[attachments.length - 1] = new Attachment();
            attachments[attachments.length - 1].setText(linkReview);
            richMessage.setAttachments(attachments);

            // Always remember to send the encoded message to Slack
            try {
                restTemplate.postForEntity(slackIncomingWebhookUrl, richMessage.encodedMessage(), String.class);
            } catch (RestClientException e) {
                logger.error("Error posting to Slack Incoming Webhook: ", e);
            }
        }
    }

    private void reviewPhaseWebhook(@RequestBody Map<String, Object> payload) {
        List<String> participants = (List<String>) payload.getOrDefault("reviewParticipants", Collections.EMPTY_LIST);
        if (participants.isEmpty()) { return; }

        String reviewId = payload.getOrDefault("reviewId", "").toString();

        for (String login : participants) {
            RestTemplate restTemplate = new RestTemplate();
            RichMessage richMessage = new RichMessage();
            UserProfile userProfile = UserProfileUtil.getUserProfileByCollabLogin(login);

            if (userProfile != null) {
                richMessage.setChannel(userProfile.getBotChannel());
            }

            String newPhase = payload.getOrDefault("newPhase", "").toString();
            String linkReview = payload.getOrDefault("linkReview", "").toString();
            String userChangedPhase = payload.getOrDefault("userChangedPhase", "").toString();

            StringBuilder textBuilder = new StringBuilder();
            textBuilder.append("Phase was changed to ");
            textBuilder.append(newPhase);
            textBuilder.append(" in review #");
            textBuilder.append(reviewId);
            textBuilder.append(" by ");
            textBuilder.append(userChangedPhase);

            richMessage.setText(textBuilder.toString());

            Attachment[] attachments = new Attachment[1];
            attachments[0] = new Attachment();
            attachments[0].setText(linkReview);
            richMessage.setAttachments(attachments);

            // Always remember to send the encoded message to Slack
            try {
                restTemplate.postForEntity(slackIncomingWebhookUrl, richMessage.encodedMessage(), String.class);
            } catch (RestClientException e) {
                logger.error("Error posting to Slack Incoming Webhook: ", e);
            }
        }
    }

    private void commentAddedWebhook(@RequestBody Map<String, Object> payload) {
        List<String> participants = (List<String>) payload.getOrDefault("reviewParticipants", Collections.EMPTY_LIST);
        if (participants.isEmpty()) { return; }

        String reviewId = payload.getOrDefault("reviewId", "").toString();

        for (String login : participants) {
            RestTemplate restTemplate = new RestTemplate();
            RichMessage richMessage = new RichMessage();
            UserProfile userProfile = UserProfileUtil.getUserProfileByCollabLogin(login);

            if (userProfile != null) {
                richMessage.setChannel(userProfile.getBotChannel());
            }

            String path = payload.getOrDefault("path", "").toString();
            String linkReview = payload.getOrDefault("linkReview", "").toString();
            String commentText = payload.getOrDefault("commentText", "").toString();
            String commentLine = payload.getOrDefault("commentLine", "").toString();
            Boolean isOverall = Boolean.valueOf(payload.getOrDefault("isOverallComment", "").toString());
            StringBuilder textBuilder = new StringBuilder();
            textBuilder.append("New comment was added in review #");
            textBuilder.append(reviewId);
            if (!isOverall) {
                textBuilder.append(" to file ");
                textBuilder.append(path);
                textBuilder.append(" on line ");
                textBuilder.append(commentLine);
            } else {
                textBuilder.append(" to Overall chat");
            }
            richMessage.setText(textBuilder.toString());

            Attachment[] attachments = new Attachment[2];
            attachments[0] = new Attachment();
            attachments[0].setText(commentText);
            attachments[1] = new Attachment();
            attachments[1].setText(linkReview);
            richMessage.setAttachments(attachments);

            // Always remember to send the encoded message to Slack
            try {
                restTemplate.postForEntity(slackIncomingWebhookUrl, richMessage.encodedMessage(), String.class);
            } catch (RestClientException e) {
                logger.error("Error posting to Slack Incoming Webhook: ", e);
            }
        }
    }


    /**
     * Slash Command handler. When a user types for example "/app help"
     * then slack sends a POST request to this endpoint. So, this endpoint
     * should match the url you set while creating the Slack Slash Command.
     *
     * @param token
     * @param teamId
     * @param teamDomain
     * @param channelId
     * @param channelName
     * @param userId
     * @param userName
     * @param command
     * @param text
     * @param responseUrl
     * @return
     */
    @RequestMapping(value = "/slash-command",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommand(@RequestParam("token") String token,
                                             @RequestParam("team_id") String teamId,
                                             @RequestParam("team_domain") String teamDomain,
                                             @RequestParam("channel_id") String channelId,
                                             @RequestParam("channel_name") String channelName,
                                             @RequestParam("user_id") String userId,
                                             @RequestParam("user_name") String userName,
                                             @RequestParam("command") String command,
                                             @RequestParam("text") String text,
                                             @RequestParam("response_url") String responseUrl) {
        // validate token
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }

        /** build response */
        RichMessage richMessage = new RichMessage("The is Slash Commander!");
        richMessage.setResponseType("in_channel");
        // set attachments
        Attachment[] attachments = new Attachment[1];
        attachments[0] = new Attachment();
        attachments[0].setText("I will perform all tasks for you.");
        richMessage.setAttachments(attachments);
        
        // For debugging purpose only
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }
        
        return richMessage.encodedMessage(); // don't forget to send the encoded message to Slack
    }


    /**
     * Slash Command handler. When a user types for example "/app help"
     * then slack sends a POST request to this endpoint. So, this endpoint
     * should match the url you set while creating the Slack Slash Command.
     *
     * @param token
     * @param teamId
     * @param teamDomain
     * @param channelId
     * @param channelName
     * @param userId
     * @param userName
     * @param command
     * @param text
     * @param responseUrl
     * @return
     */
    @RequestMapping(value = "*",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RichMessage onReceiveSlashCommandAll(@RequestParam("token") String token,
                                             @RequestParam("team_id") String teamId,
                                             @RequestParam("team_domain") String teamDomain,
                                             @RequestParam("channel_id") String channelId,
                                             @RequestParam("channel_name") String channelName,
                                             @RequestParam("user_id") String userId,
                                             @RequestParam("user_name") String userName,
                                             @RequestParam("command") String command,
                                             @RequestParam("text") String text,
                                             @RequestParam("response_url") String responseUrl) {
        // validate token
/*
        if (!token.equals(slackToken)) {
            return new RichMessage("Sorry! You're not lucky enough to use our slack command.");
        }
*/

        /** build response */
        RichMessage richMessage = new RichMessage("The is Collaborator Slack Commander!");
        richMessage.setResponseType("in_channel");
        Attachment[] attachments = new Attachment[1];

        final String CREATE_PR = "/create-pr";
        final String REVIEW = "/review";

        switch(command){

            case CREATE_PR :
                attachments[0] = new Attachment();
                attachments[0].setText("Create PR command was run");
                //todo ADD SOME ACTIVITIES FOR CREATE PR COMMAND
                if(text == null || text.isEmpty()){
                    richMessage.setText("Parameters are required. Please use this format of command: /create-pr {from-branch} {to-branch}");
                    attachments[0] = null;
                }
                else {
                    String[] params = text.split(" ");
                    if (params.length != 2 && params.length != 3){
                        if(params.length == 1 && "close-pr".equals(params[0])){
                            GitHubService.closePullRequest("https://github.com", "agubanov/jbot", gitHubToken, "test-branch");
                            attachments[0].setText("Pull request was successfully closed.");
                        }
                        else {
                            richMessage.setText("Parameters are required. Please use this format of command: /create-pr {from-branch} {to-branch} {title}");
                            attachments[0] = null;
                        }
                    }
                    else{
                        String message = (params.length == 3)? params[2]:"Pull request from " + params[0] + " to " + params[1];
                        String host = "https://github.com";
                        String repo = "zapelin/collab-github-v2";
                        String gitHubtoken = "c6bc1343435c9747099c23d4557946717f2f273b";

                        String title = "zapelin-patch-102";
                        String head = "zapelin-patch-102";
                        String base = "master";
                        String reviewLink = "";
                        final String response = responseUrl;
                        new Thread(() -> {
                            try {

                                String reviewL = GitHubService.createGitHubPullRequestAndGetReviewLink("https://github.com", "agubanov/jbot", gitHubToken, "pr from bot", "test-branch", "master");
                                RichMessage rMessage = new RichMessage("We are done! Your review is created " + reviewL);
                                RestTemplate restTemplate = new RestTemplate();
                                restTemplate.postForEntity(response, rMessage.encodedMessage(), String.class);

                            }catch(IOException ex){
                                RichMessage rMessage = new RichMessage("Exception during review creation." + ex.getMessage());
                                RestTemplate restTemplate = new RestTemplate();
                                restTemplate.postForEntity(response, rMessage.encodedMessage(), String.class);
                            }

                        }).start();

                        attachments[0].setText("Pull request and review are creating now...");

                    }
                }
                break;

            case REVIEW:
                attachments[0] = new Attachment();
                attachments[0].setText("/review command was run");
                //todo ADD SOME ACTIVITIES FOR REVIEW COMMAND


                break;

            default:

                StringBuilder results = new StringBuilder();
                results.append("token = " + token + " ");
                results.append("team_id = " + teamId + " ");
                results.append("team_domain = " + teamDomain + " ");
                results.append("channel_id = " + channelId + " ");
                results.append("channel_name = " + channelName + " ");
                results.append("user_id = " + userId + " ");
                results.append("user_name = " + userName + " ");
                results.append("command = " + command + " ");
                results.append("text = " + text + " ");
                results.append("response_url = " + responseUrl + " ");
                attachments[0] = new Attachment();
                attachments[0].setText("Review will be created. http://collab.aus.smartbear.com/ui#review:id=13382" + results.toString());
                attachments[0].setTitleLink("http://collab.aus.smartbear.com/ui#review:id=13385");
        }

        // set attachments
        richMessage.setAttachments(attachments);

        //create-pull-request
        //start-review
        //

        //richMessage.setText("Review was successfully created : http://collab.aus.smartbear.com/ui#review:id=13387");
        // For debugging purpose only
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Reply (RichMessage): {}", new ObjectMapper().writeValueAsString(richMessage));
            } catch (JsonProcessingException e) {
                logger.debug("Error parsing RichMessage: ", e);
            }
        }

        return richMessage.encodedMessage(); // don't forget to send the encoded message to Slack
    }
}
