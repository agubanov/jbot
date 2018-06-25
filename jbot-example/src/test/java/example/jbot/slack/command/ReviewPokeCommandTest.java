package example.jbot.slack.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReviewPokeCommandTest {
	
	@Test
	public void constructorTest() {
		ReviewPokeCommand command = new ReviewPokeCommand("123 poke -all");
		assertEquals(123, command.getReviewId());
		assertTrue(command.isPokeAll());
		assertFalse(command.isPokeUncompleted());
		assertTrue(command.getUsers().isEmpty());
		
		ReviewPokeCommand command1 = new ReviewPokeCommand("123 poke -uncompleted");
		assertEquals(123, command1.getReviewId());
		assertFalse(command1.isPokeAll());
		assertTrue(command1.isPokeUncompleted());
		assertTrue(command1.getUsers().isEmpty());
		
		ReviewPokeCommand command2 = new ReviewPokeCommand("123 poke -p user1 user2, user3");
		assertEquals(123, command2.getReviewId());
		assertFalse(command2.isPokeAll());
		assertFalse(command2.isPokeUncompleted());
		assertFalse(command2.getUsers().isEmpty());
		assertEquals(3, command2.getUsers().size());
		assertTrue(command2.getUsers().contains("user1"));
		assertTrue(command2.getUsers().contains("user2"));
		assertTrue(command2.getUsers().contains("user3"));
	}
}