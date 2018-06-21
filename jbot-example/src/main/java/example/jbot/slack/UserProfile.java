package example.jbot.slack;

public class UserProfile {
    private String collabLogin;
    private String collabPassword;
    private String gitHubRepo;
    private String gitHubToken;

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
}
