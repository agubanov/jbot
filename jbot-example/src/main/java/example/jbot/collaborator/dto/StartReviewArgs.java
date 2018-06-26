package example.jbot.collaborator.dto;

public class StartReviewArgs {
	
	private int reviewId;
	
	public StartReviewArgs(int reviewId) {
		this.reviewId = reviewId;
	}
	
	public int getReviewId() {
		return reviewId;
	}
	
	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}
}
