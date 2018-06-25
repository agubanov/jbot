package example.jbot.collaborator.dto;

import java.util.List;

public class UpdateAssignmentsArgs {
	
	private int reviewId;
	private List<AssignmentDto> assignments;
	
	public UpdateAssignmentsArgs(int reviewId, List<AssignmentDto> assignments) {
		this.reviewId = reviewId;
		this.assignments = assignments;
	}
	
	public int getReviewId() {
		return reviewId;
	}
	
	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}
	
	public List<AssignmentDto> getAssignments() {
		return assignments;
	}
	
	public void setAssignments(List<AssignmentDto> assignments) {
		this.assignments = assignments;
	}
}
