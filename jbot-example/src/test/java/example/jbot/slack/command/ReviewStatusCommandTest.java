package example.jbot.slack.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReviewStatusCommandTest {
	
	@Test
	public void constructorTest() {
		ReviewStatusCommand command = new ReviewStatusCommand("123 status");
		assertEquals(123, command.getReviewId());
		assertFalse(command.isFullStatus());
		
		ReviewStatusCommand command1 = new ReviewStatusCommand("123 status -all");
		assertEquals(123, command1.getReviewId());
		assertTrue(command1.isFullStatus());
	}
	
}