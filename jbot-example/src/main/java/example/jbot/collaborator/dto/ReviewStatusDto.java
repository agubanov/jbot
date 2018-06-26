package example.jbot.collaborator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewStatusDto {
    ReviewPhase reviewMovingOn;
    List<ReviewParticipant> reviewParticipants;

    public ReviewPhase getReviewMovingOn() {
        return reviewMovingOn;
    }

    public void setReviewMovingOn(ReviewPhase reviewMovingOn) {
        this.reviewMovingOn = reviewMovingOn;
    }

    public List<ReviewParticipant> getReviewParticipants() {
        return reviewParticipants;
    }

    public void setReviewParticipants(List<ReviewParticipant> reviewParticipants) {
        this.reviewParticipants = reviewParticipants;
    }

    public String getShortInfo() {
        String answer = " \nReview phase : " + reviewMovingOn.getPhase() + " \n";
        return answer;
    }

    public String getInfo(){
        String answer = " \nReview phase : " + reviewMovingOn.getPhase() + " \n";
        if(reviewParticipants != null && reviewParticipants.size() > 0){
            answer += "Review participants: \n";
            for(ReviewParticipant participant : reviewParticipants){
                answer += participant.getUser().getFullName() + " role: "+participant.getRole().getSystemName()+"; status: " + participant.getAssignmentState().name + " \n";
            }
        }
        return answer;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ReviewPhase{
    String phase;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ReviewParticipant{
    ReviewerStatus assignmentState;
    ReviewRole role;
    ReviewUser user;

    public ReviewerStatus getAssignmentState() {
        return assignmentState;
    }

    public void setAssignmentState(ReviewerStatus assignmentState) {
        this.assignmentState = assignmentState;
    }

    public ReviewRole getRole() {
        return role;
    }

    public void setRole(ReviewRole role) {
        this.role = role;
    }

    public ReviewUser getUser() {
        return user;
    }

    public void setUser(ReviewUser user) {
        this.user = user;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ReviewRole{
    String systemName;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class ReviewUser{
    String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

enum ReviewerStatus {
    NEW("Newly Assigned"),
    ACTIVE("Active Participant"),
    WAITING_FOR_ANY("Waiting for any activity"),
    WAITING_FOR_AUTHOR(""),
    WAITING_FOR_FILES("Waiting for file activity"),
    WAITING_FOR_POKE("Waiting to be poked"),
    FINISHED_UNTIL_ANY("Finished unless any activity occurs"),
    FINISHED_UNTIL_AUTHOR(""),
    FINISHED_UNTIL_FILES("Approved"),
    FINISHED_UNTIL_POKE("Approved"),
    REWORKED_UNTIL_ANY("Sent to rework unless any activity occurs"),
    REWORKED_UNTIL_AUTHOR(""),
    REWORKED_UNTIL_FILES("Sent to rework unless file activity occurs"),
    REWORKED_UNTIL_POKE("Sent to rework unless poked"),
    COMPLETED( "Completed");

    public final String name;
    ReviewerStatus(String name){
        this.name = name;
    }
}