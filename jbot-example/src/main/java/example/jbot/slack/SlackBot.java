package example.jbot.slack;


import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.WebSocketSession;


/**
 * A simple Slack Bot. You can create multiple bots by just
 * extending {@link Bot} class like this one. Though it is
 * recommended to create only bot per jbot instance.
 *
 * @author ramswaroop
 * @version 1.0.0, 05/06/2016
 */
@JBot
@Profile("slack")
public class SlackBot extends Bot {

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);

    /**
     * Slack token from application.properties file. You can get your slack token
     * next <a href="https://my.slack.com/services/new/bot">creating a new bot</a>.
     */
    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }



    private UserProfile getUserProfile(String userId) {
        UserProfileUtil.userProfileMap.putIfAbsent(userId, new UserProfile());
        return UserProfileUtil.userProfileMap.get(userId);
    }

    /*@Controller(pattern = "(show profile)", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void showProfile(WebSocketSession session, Event event) {
        if (!UserProfileUtil.userProfileMap.containsKey(event.getUserId())) {
            reply(session, event, "User profile was not configured yet");
            return;
        }
        UserProfile userProfile = getUserProfile(event.getUserId());
        reply(session, event, userProfile.toString());
    }*/

    /*@Controller(pattern = "(config profile)", next = "askCollabCredentials", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void configureProfile(WebSocketSession session, Event event) {
        UserProfile userProfile = getUserProfile(event.getUserId());
        userProfile.setBotChannel(event.getChannelId());
        startConversation(event, "askCollabCredentials");   // start conversation
        reply(session, event, "Let's the party begin.\nEnter collab login and password (use whitespace as delimeter)");
    }*/


    @Controller(pattern = "(config profile)", next = "askCollabUrl", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void configureProfile(WebSocketSession session, Event event) {
        UserProfile userProfile = getUserProfile(event.getUserId());
        userProfile.setBotChannel(event.getChannelId());
        startConversation(event, "askCollabUrl");   // start conversation
        reply(session, event, "Let's the party begin.\nEnter Collaborator URL");

    }

    @Controller(next = "askCollabCredentials", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void askCollabUrl(WebSocketSession session, Event event) {
        if (!isConversationOn(event)) {
            return;
        }
        UserProfile userProfile = getUserProfile(event.getUserId());
        String collabUrl = event.getText();
        if (collabUrl.startsWith("<")) {
            collabUrl = collabUrl.substring(1);
        }

        if (collabUrl.endsWith(">")) {
            collabUrl = collabUrl.substring(0, collabUrl.length() - 1);
        }

        userProfile.setCollabUrl(collabUrl);
        reply(session, event, "Enter collab login and password (use whitespace as delimeter)");
        nextConversation(event);
    }


    @Controller(next = "askGithubCredentials", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void askCollabCredentials(WebSocketSession session, Event event) {
        if (!isConversationOn(event)) {
            return;
        }
        String[] creds = event.getText().split(" ");
        if (creds.length == 2) {
            UserProfile userProfile = getUserProfile(event.getUserId());
            userProfile.setCollabLogin(creds[0]);
            userProfile.setCollabPassword(creds[1]);
            reply(session, event,  " Enter Github repo and token (use whitespace as delimeter)");
            nextConversation(event);    // jump to next question in conversation
        } else {
            reply(session, event,  "You entered wrong number of parameters! \nStart from the beginning");
            stopConversation(event);
        }

    }

    @Controller(events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void askGithubCredentials(WebSocketSession session, Event event) {
        if (!isConversationOn(event)) {
            return;
        }
        String[] creds = event.getText().split(" ");
        if (creds.length == 2) {
            UserProfile userProfile = getUserProfile(event.getUserId());
            userProfile.setGitHubRepo(creds[0]);
            userProfile.setGitHubToken(creds[1]);
            reply(session, event, "You are done!\n" + userProfile);
            stopConversation(event);    // stop conversation only if user says no
        } else {
            reply(session, event,  "You entered wrong number of parameters! \nStart from the beginning");
            stopConversation(event);
        }

    }
}