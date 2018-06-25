package example.jbot.slack;

import example.jbot.JBotApplication;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

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

    /**
     * Invoked when bot receives an event of type message with text satisfying
     * the pattern {@code ([a-z ]{2})(\d+)([a-z ]{2})}. For example,
     * messages like "ab12xy" or "ab2bc" etc will invoke this method.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.MESSAGE, pattern = "^([a-z ]{2})(\\d+)([a-z ]{2})$")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        reply(session, event, "First group: " + matcher.group(0) + "\n" +
                "Second group: " + matcher.group(1) + "\n" +
                "Third group: " + matcher.group(2) + "\n" +
                "Fourth group: " + matcher.group(3));
    }

    /**
     * Invoked when an item is pinned in the channel.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.PIN_ADDED)
    public void onPinAdded(WebSocketSession session, Event event) {
        reply(session, event, "Thanks for the pin! You can find all pinned items under channel details.");
    }

    /**
     * Invoked when bot receives an event of type file shared.
     * NOTE: You can't reply to this event as slack doesn't send
     * a channel id for this event type. You can learn more about
     * <a href="https://api.slack.com/events/file_shared">file_shared</a>
     * event from Slack's Api documentation.
     *
     * @param session
     * @param event
     */
    @Controller(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
        logger.info("File shared: {}", event);
    }

    private UserProfile getUserProfile(String userId) {
        UserProfileUtil.userProfileMap.putIfAbsent(userId, new UserProfile());
        return UserProfileUtil.userProfileMap.get(userId);
    }


    @Controller(pattern = "(config profile)", next = "askCollabCredentials", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void configureProfile(WebSocketSession session, Event event) {
        UserProfile userProfile = getUserProfile(event.getUserId());
        userProfile.setBotChannel(event.getChannelId());
        startConversation(event, "askCollabCredentials");   // start conversation
        reply(session, event, "Let's the party begin.\nEnter collab login and password (use whitespace as delimeter)");
    }


    @Controller(next = "askGithubCredentials", events = {EventType.DIRECT_MENTION, EventType.DIRECT_MESSAGE})
    public void askCollabCredentials(WebSocketSession session, Event event) {
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
        String[] creds = event.getText().split(" ");
        if (creds.length == 2) {
            UserProfile userProfile = getUserProfile(event.getUserId());
            userProfile.setGitHubRepo(creds[0]);
            userProfile.setGitHubToken(creds[1]);
            reply(session, event, "You are done! Good job!");
            stopConversation(event);    // stop conversation only if user says no
        } else {
            reply(session, event,  "You entered wrong number of parameters! \nStart from the beginning");
            stopConversation(event);
        }

    }
}