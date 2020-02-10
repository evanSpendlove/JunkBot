import org.junit.jupiter.api.Test;
import scrabbleGame.Frame;
import scrabbleGame.Player;
import scrabbleGame.Pool;

import static org.junit.jupiter.api.Assertions.*;

/*
    PlayerTest JUnit Test Class

    Purpose: Unit-Testing the Player Class of the scrabbleGame package.
    Summary: This class attempts to thoroughly test the Player Class and its methods to ensure they
             are robust and handle invalid input appropriately.

    Author: Reuben Mulligan
    Version: 1.0.0
    Since: 07-02-2020
 */

public class PlayerTest
{

    /*
        Goal: To test that the full constructor works
        Testing Method: Construct new instance using certain valid values. Check for exception thrown. Check values properly assigned.
     */
    @Test
    public void testFullConstructor() {
        try{
            // Create Player field values
            String uname = "Bob";
            int score = 5;
            Frame frame = new Frame();

            // Construct a new instance of Player using these values
            Player test = new Player(uname, score, frame);

            // Assert that they are the same
            assertEquals(uname, test.getUsername());
            assertEquals(score, test.getScore());
            assertEquals(frame, test.getFrame());
        }
        catch(Exception ex) {
            fail("Full constructor should not through an exception when passed correct values.");
        }
    }

    /*
        Goal: To test that the partial constructor with only username and score works.
        Testing Method: Construct new instance using certain valid values. Check for exception thrown. Check values properly assigned.
     */
    @Test
    public void testPartialConstructor_uname_score()
    {
        try{
            // Create Player field values
            String uname = "Bob";
            int score = 5;

            // Construct a new instance of Player using these values
            Player test = new Player(uname, score);

            // Assert that they are the same
            assertEquals(uname, test.getUsername());
            assertEquals(score, test.getScore());
            assertNotNull(test.getFrame()); // Frame should be initialised
        }
        catch(Exception ex) {
            fail("Partial constructor (username, score) should not through an exception when passed correct values.");
        }
    }

    /*
        Goal: To test that the partial constructor with only username and frame works.
        Testing Method: Construct new instance using certain valid values. Check for exception thrown. Check values properly assigned.
     */
    @Test
    public void testPPartialConstructor_uname_frame()
    {
        try{
            // Create Player field values
            String uname = "Bob";
            Frame frame = new Frame();

            // Construct a new instance of Player using these values
            Player test = new Player(uname, frame);

            // Assert that they are the same
            assertEquals(uname, test.getUsername());
            assertEquals(0, test.getScore()); // Score should be set to 0 by default
            assertEquals(frame, test.getFrame());
        }
        catch(Exception ex) {
            fail("Partial constructor (username, frame) should not through an exception when passed correct values.");
        }
    }

    /*
        Goal: To test that the partial constructor with only username works.
        Testing Method: Construct new instance using certain valid values. Check for exception thrown. Check values properly assigned.
     */
    @Test
    public void testPartialConstructor_uname()
    {
        try{
            // Create Player field values
            String uname = "Bob";

            // Construct a new instance of Player using these values
            Player test = new Player(uname);

            // Assert that they are the same
            assertEquals(uname, test.getUsername());
            assertEquals(0, test.getScore()); // Should be set to 0 by default
            assertNotNull(test.getFrame()); // Frame should be initialised
        }
        catch(Exception ex) {
            fail("Partial Constructor (username) should not through an exception when passed correct values.");
        }
    }

    /*
        Goal: To test that the username cannot be set to a blank string.
        Testing Method: Instantiate a blank string and pass it to a constructor. Verify that the correct exception is thrown.
     */
    @Test
    public void testUsernameNotBlank()
    {
        // Not black-box testing here, as we know that all constructors call the setUsername() method for input verification.
        // Thus, we only need to test one constructor here.
        try
        {
            String uname = "";

            Player test1 = new Player(uname);
            fail("Username should not be able to be set to a blank string");
        }
        catch(Exception ex)
        {
            assertEquals("Username cannot be empty or filled with spaces.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the username cannot be set to a string consisting of all spaces.
        Testing Method: Instantiate a string consisting of all spaces and pass it to a constructor. Verify that the correct exception is thrown.
     */
    @Test
    public void testUsernameNotAllSpaces()
    {
        try
        {
            String uname = "   ";

            Player test1 = new Player(uname);
            fail("Username should not be able to be set to a string of spaces");
        }
        catch(Exception ex)
        {
            assertEquals("Username cannot be empty or filled with spaces.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the username cannot be set to a null string.
        Testing Method: Instantiate a null string and pass it to a constructor. Verify that the correct exception is thrown.
     */
    @Test
    public void testUsernameNotNull()
    {
        try
        {
            String uname = null;
            Player test = new Player(uname);
            fail("Username should not be able to be set to a null string");
        }
        catch(Exception ex)
        {
            assertEquals(NullPointerException.class, ex.getClass());
        }
    }

    /*
        Goal: To test that the username cannot contain a newline character.
        Testing Method: Instantiate a string containing a newline character and pass it to a constructor. Verify that this is handled without an exception being thrown.
     */
    @Test
    public void testUsernameContainsNewline()
    {
        try
        {
            String username = "bob\n";
            Player test = new Player(username);
            assertEquals("bob", test.getUsername());
        }
        catch(Exception ex)
        {
            fail("Usernames with newline characters should not throw an exception, but should be handled with trimming.");
        }
    }

    /*
        Goal: To test that the username cannot contain a tab character.
        Testing Method: Instantiate a string containing a tab character and pass it to a constructor. Verify that this is handled without an exception being thrown.
     */
    @Test
    public void testUsernameContainsTab()
    {
        try
        {
            String username = "bob\t";
            Player test = new Player(username);
            assertEquals("bob", test.getUsername());
        }
        catch(Exception ex)
        {
            fail("Usernames with tabs should not throw an exception, but should be handled with trimming.");
        }
    }

    /*
        Goal: To test that the username cannot contain additional whitespace.
        Testing Method: Instantiate a string containing additional whitespace and pass it to a constructor. Verify that this is handled without an exception being thrown.
     */
    @Test
    public void testUsernameContainsExtraWhitespace()
    {
        try
        {
            String username = "    bob              ";
            Player test = new Player(username);
            assertEquals("bob", test.getUsername());
        }
        catch(Exception ex)
        {
            fail("Usernames with extra whitespace should not throw an exception, but should be handled with trimming.");
        }
    }

    /*
        Goal: To test that the setScore() method works with positive integers.
        Testing Method: Call setScore() on a Player object passing a positive integer.
     */
    @Test
    public void testSetPositiveScore()
    {
        try
        {
            Player test = new Player("Bob");
            test.setScore(5);

            assertEquals(5, test.getScore());
        }
        catch(Exception ex)
        {
            fail("Score should be able to be set to a positive integer.");
        }
    }

    /*
        Goal: To test that the setScore() method does not work with negative integers.
        Testing Method: Call setScore() on a Player object passing a negative integer.
     */
    @Test
    public void testSetNegativeScore()
    {
        try
        {
            Player test = new Player("bob");
            test.setScore(-5);
            fail("Score should not be able to be set to a negative integer.");
        }
        catch(Exception ex)
        {
            assertEquals("Score cannot be set to a negative value.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the setScore() method works with passing 0.
        Testing Method: Call setScore() on a Player object passing 0 as the argument.
     */
    @Test
    public void testSetScoreToZero()
    {
        try
        {
            Player test = new Player("Bob");
            test.setScore(0);

            assertEquals(0, test.getScore());
        }
        catch(Exception ex)
        {
            fail("Score should be able to be set to zero.");
        }
    }

    // Frame Methods Tested

    /*
        Goal: To test that the getter method for the Frame field of the Player works correctly.
        Testing Method: Call the getFrame() method and check that no exception is thrown.
     */
    @Test
    public void testFrameGetter()
    {
        try
        {
            Frame frame = new Frame();
            frame.refillFrame(new Pool());
            Player test = new Player("Bob", 5, frame);
            test.getFrame();
            assertEquals(frame, test.getFrame());
        }
        catch(Exception ex)
        {
            fail("Frame getter should work without throwing an exception.");
        }
    }

    /*
        Goal: To test that the setter for the Frame of the Player works correctly.
        Testing Method: Call the setFrame() method passing a valid argument and check that no exception is thrown.
     */
    @Test
    public void testFrameSetter()
    {
        try
        {
            Frame frame = new Frame();
            frame.refillFrame(new Pool());
            Player test = new Player("Bob", 5);
            test.setFrame(frame);
            assertEquals(frame, test.getFrame());
        }
        catch(Exception ex)
        {
            fail("Frame setter should work without throwing an exception.");
        }
    }

    // Other Methods Tested

    /*
        Goal: To test that the resetPlayer() method actually resets all fields to pre-defined default values
        Testing Method: Instantiate an instance of Player with certain values. Call resetPlayer(). Check the new field values match the expected defaults.
     */
    @Test
    public void testResetPlayer()
    {
        try
        {
            Frame testFrame = new Frame();
            testFrame.refillFrame(new Pool());
            Player test = new Player("Bob", 5, testFrame);
            test.resetPlayer();

            assertEquals("", test.getUsername()); // Check that the name is now the default
            assertEquals(0, test.getScore()); // Check that the score is now the default
            assertNotNull(test.getFrame()); // Check that the Frame object is not null
            assertNotEquals(testFrame, test.getFrame()); // Check that it is not the same Frame object
        }
        catch(Exception ex)
        {
            fail("resetPlayer() should work without throwing an exception.");
        }
    }

    /*
        Goal: To test if the toString() method for Player works as expected.
        Testing Method: Instantiate multiple test cases for the various constructors. Call the method on each and compare the return value with the expected return.
     */
    @Test
    public void testPlayerToString()
    {
        Frame testFrame = new Frame();
        testFrame.refillFrame(new Pool());

        Player test1 = new Player("bob", 5, testFrame); // Test with full constructor
        assertEquals("Player {Username: 'bob', Score: 5}", test1.toString());

        Player test2 = new Player("Billy", 3); // Test with partial (username, score) constructor
        assertEquals("Player {Username: 'Billy', Score: 3}", test2.toString());

        Player test3 = new Player("Bobby"); // Test with partial (username) constructor
        assertEquals("Player {Username: 'Bobby', Score: 0}", test3.toString());

        Player test4 = new Player("Brad", testFrame);
        assertEquals("Player {Username: 'Brad', Score: 0}", test4.toString());

    }

    /*
        Goal: To test if the dumpPlayerInfo() method outputs the Player's field data correctly.
        Testing Method: Instantiate multiple test objects using the various constructors. Call the method on each and compare the return value with the expected return.
     */
    @Test
    public void testPlayerDumpInfo()
    {
        Frame testFrame = new Frame();
        testFrame.refillFrame(new Pool());
        String frameInfo = testFrame.toString();

        Player test1 = new Player("bob", 5, testFrame); // Test with full constructor
        assertEquals("Player {Username: 'bob', Score: 5, " + frameInfo + "}", test1.dumpPlayerInfo());

        Player test2 = new Player("Billy", 3);  // Test with partial (username, score) constructor
        assertEquals("Player {Username: 'Billy', Score: 3, {}}", test2.dumpPlayerInfo());

        Player test3 = new Player("Bobby"); // Test with partial (username) constructor
        assertEquals("Player {Username: 'Bobby', Score: 0, {}}", test3.dumpPlayerInfo());

        Player test4 = new Player("Brad", testFrame); // Test with partial (username, frame) constructor
        assertEquals("Player {Username: 'Brad', Score: 0, " + frameInfo + "}", test4.dumpPlayerInfo());
    }
}