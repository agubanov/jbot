package example.jbot.slack.command;

import example.jbot.slack.ReviewAction;
import example.jbot.slack.ReviewParticipantAction;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * "123 add -r user1 -o user2 start",
 "123 add -r user1 -o user2",
 "123 add -r user1",
 "123 add -o user2 -r user1",
 "123 add -o user2 -o user3",
 "123 add -o user2",
 "123 start",
 "123 remove -r user1"
 */
public class ReviewCommandTest {
	
	@Test
	public void reviewCommandTest() {
		ReviewCommand reviewCommand1 = new ReviewCommand("123 add -r user1 -o user2 start");
		assertEquals(123, reviewCommand1.getReviewId());
		assertEquals(ReviewAction.START, reviewCommand1.getReviewAction());
		assertEquals(ReviewParticipantAction.ADD, reviewCommand1.getParticipantAction());
		assertNotNull(reviewCommand1.getReviewers());
		assertEquals("user1", reviewCommand1.getReviewersString());
		assertNotNull(reviewCommand1.getObservers());
		assertEquals("user2", reviewCommand1.getObserversString());
//
//		ReviewCommand reviewCommand2 = new ReviewCommand("123 add -r user1 -o user2");
//		assertEquals(123, reviewCommand2.getReviewId());
//		assertEquals(ReviewParticipantAction.ADD, reviewCommand2.getParticipantAction());
//		assertNotNull(reviewCommand2.getParams());
//		assertTrue(reviewCommand2.getParams().containsEntry("-r", "user1"));
//		assertTrue(reviewCommand2.getParams().containsEntry("-o", "user2"));
//
//		ReviewCommand reviewCommand3 = new ReviewCommand("123 add -r user1");
//		assertEquals(123, reviewCommand3.getReviewId());
//		assertEquals(ReviewParticipantAction.ADD, reviewCommand3.getParticipantAction());
//		assertNotNull(reviewCommand3.getParams());
//		assertTrue(reviewCommand3.getParams().containsEntry("-r", "user1"));
//
//		ReviewCommand reviewCommand4 = new ReviewCommand("123 add -o user1 -r user2");
//		assertEquals(123, reviewCommand4.getReviewId());
//		assertEquals(ReviewParticipantAction.ADD, reviewCommand4.getParticipantAction());
//		assertNotNull(reviewCommand4.getParams());
//		assertTrue(reviewCommand4.getParams().containsEntry("-r", "user2"));
//		assertTrue(reviewCommand4.getParams().containsEntry("-o", "user1"));
//
//		ReviewCommand reviewCommand5 = new ReviewCommand("123 add -o user1");
//		assertEquals(123, reviewCommand5.getReviewId());
//		assertEquals(ReviewParticipantAction.ADD, reviewCommand5.getParticipantAction());
//		assertNotNull(reviewCommand5.getParams());
//		assertTrue(reviewCommand5.getParams().containsEntry("-o", "user1"));
//
//		ReviewCommand reviewCommand6 = new ReviewCommand("123 start");
//		assertEquals(123, reviewCommand6.getReviewId());
//		assertEquals(ReviewAction.START, reviewCommand6.getReviewAction());
//
//		ReviewCommand reviewCommand7 = new ReviewCommand("123 remove -r user1");
//		assertEquals(123, reviewCommand7.getReviewId());
//		assertEquals(ReviewParticipantAction.REMOVE, reviewCommand7.getParticipantAction());
//		assertNotNull(reviewCommand7.getParams());
//		assertTrue(reviewCommand7.getParams().containsEntry("-r", "user1"));
	
	}
}