package example.jbot.collaborator.dto;

public class LoginRequestDto {
    //"command" : "SessionService.getLoginTicket"
    public String command = "SessionService.getLoginTicket";
    public LoginRequestArgs args;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public LoginRequestArgs getArgs() {
        return args;
    }

    public void setArgs(LoginRequestArgs args) {
        this.args = args;
    }
}
