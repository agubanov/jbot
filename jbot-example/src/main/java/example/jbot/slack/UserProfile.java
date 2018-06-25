package example.jbot.slack;

public class UserProfile {
    private String collabUrl;
    private String collabLogin;
    private String collabPassword;
    private String gitHubRepo;
    private String gitHubToken;

    private String botChannel;

    public String getCollabLogin() {
        return collabLogin;
    }

    public void setCollabLogin(String collabLogin) {
        this.collabLogin = collabLogin;
    }

    public String getCollabPassword() {
        return collabPassword;
    }

    public void setCollabPassword(String collabPassword) {
        this.collabPassword = collabPassword;
    }

    public String getGitHubRepo() {
        return gitHubRepo;
    }

    public void setGitHubRepo(String gitHubRepo) {
        this.gitHubRepo = gitHubRepo;
    }

    public String getGitHubToken() {
        return gitHubToken;
    }

    public void setGitHubToken(String gitHubToken) {
        this.gitHubToken = gitHubToken;
    }

    public String getBotChannel() {
        return botChannel;
    }

    public void setBotChannel(String botChannel) {
        this.botChannel = botChannel;
    }

    public String getCollabUrl() {
        return collabUrl;
    }

    public void setCollabUrl(String collabUrl) {
        this.collabUrl = collabUrl;
    }

    public String toString() {
        return "Collab URL: " + collabUrl + "\n" +
                "Collab Login/Password: " + collabLogin + "/****\n" +
                "Github Repo/Token: " + gitHubRepo + "/****\n";
    }
}
