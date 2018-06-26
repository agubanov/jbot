package example.jbot.collaborator.dto;

public class StartReviewDto {
	
	private String command = "ReviewService.finishReviewPhase";
	private StartReviewArgs args;
	
	public StartReviewDto(StartReviewArgs args) {
		this.args = args;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public StartReviewArgs getArgs() {
		return args;
	}
	
	public void setArgs(StartReviewArgs args) {
		this.args = args;
	}
}
