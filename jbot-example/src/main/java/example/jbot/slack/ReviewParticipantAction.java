package example.jbot.slack;

public enum ReviewParticipantAction {
	ADD ("added"),
	REMOVE ("removed");
	
	private String label;
	
	ReviewParticipantAction(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
