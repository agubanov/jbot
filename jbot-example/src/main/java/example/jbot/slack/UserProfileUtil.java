package example.jbot.slack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserProfileUtil {
    public static Map<String, UserProfile> userProfileMap = new HashMap<>();

    public static UserProfile getUserProfileByCollabLogin(String login) {
        if (login == null) {
            return null;
        }
        Optional<UserProfile> optional = userProfileMap.values().stream().
                filter(userProfile -> login.equals(userProfile.getCollabLogin())).
                findFirst();

        return optional.orElse(null);

    }

    public static UserProfile getUserProfileByUserId(String userId) {
        return userProfileMap.get(userId);
    }
}
