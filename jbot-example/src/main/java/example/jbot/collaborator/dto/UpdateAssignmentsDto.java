package example.jbot.collaborator.dto;

public class UpdateAssignmentsDto {
	private String command = "ReviewService.updateAssignments";
	private UpdateAssignmentsArgs args;
	
	public UpdateAssignmentsDto(UpdateAssignmentsArgs args) {
		this.args = args;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public UpdateAssignmentsArgs getArgs() {
		return args;
	}
	
	public void setArgs(UpdateAssignmentsArgs args) {
		this.args = args;
	}
}
