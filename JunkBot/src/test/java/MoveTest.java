
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrabbleGame.gameModel.Placement;
import scrabbleGame.gameModel.Move;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/*
    MoveTest JUnit Test Class

    Purpose: Unit Testing the MoveTest of the scrabbleGame package
    Summary: This class attempts to thoroughly test the MoveTest and its methods to ensure they
             are robust and handle invalid input appropriately.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 23-02-2020
 */
public class MoveTest
{

    /*
        Goal: To test that the constructor for the Move class works as expected.
        Testing Method: Instantiate an instance of a Move with valid inputs and check no exception thrown.
    */
    @Test
    public void testConstructor()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertEquals("DUE", m.getWord());
            assertEquals(1, m.getDirection());
            assertEquals(0, m.getScore());
            assertFalse(m.isBingo());
            assertEquals(ply, m.getPlays());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when constructing a Move with valid inputs.");
        }
    }

    /*
        Goal: To test that the constructor for the Move class works as expected.
        Testing Method: Instantiate an instance of a Move with invalid inputs and check an appropriate exception thrown.
    */
    @Test
    public void testConstructor_ZeroPlays()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Move zeroPlays = new Move(ply, "EMPTY", 0);

            fail("A Move should not be constructed with an empty list of placements.");
        }
        catch(Exception ex)
        {
            assertEquals("Cannot play a word with 0 or more than 7 letter placements.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the constructor for the Move class works as expected.
        Testing Method: Instantiate an instance of a Move with invalid inputs and check an appropriate exception thrown.
    */
    @Test
    public void testConstructor_EightPlays()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            while(ply.size() < 10)
            {
                ply.add(new Placement(3, 3, 'E'));
            }

            Move manyPlays = new Move(ply, "MANYPLAYS", 0);

            fail("A Move should not be constructed with more than 7 placements.");
        }
        catch(Exception ex)
        {
            assertEquals("Cannot play a word with 0 or more than 7 letter placements.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the constructor will not accept invalid direction values.
        Testing Method: Pass an invalid direction to the constructor. Validate the exception thrown.
    */
    @Test
    public void testConstructInvalidDirection()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            Move invalidDir = new Move(ply, "INVALIDDIR", 3);

            fail("A Move should only be constructed with a direction value of 1 or 0.");
        }
        catch(Exception ex)
        {
            assertEquals("Direction can only be 0 or 1 indicating horizontal or vertical word placement.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the constructor will not take a blank word as input.
        Testing Method: Pass a blank word to the constructor and validate the exception thrown.
     */
    @Test
    public void testConstructBlankWord()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            Move blankWord = new Move(ply, "", 1);

            fail("A Move cannot be constructed with a blank word.");
        }
        catch(Exception ex)
        {
            assertEquals("Cannot play a blank word.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the getter for word works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    void testGetWord()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertEquals("DUE", m.getWord());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the word field.");
        }
    }

    /*
        Goal: To test that the getter for direction works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    void testGetDirection()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertEquals(1, m.getDirection());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the direction field.");
        }
    }

    /*
        Goal: To test that the getter for List of Placements works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    void testGetPlays()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertEquals(ply, m.getPlays());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the List of Placements.");
        }
    }

    /*
        Goal: To test that the getter for score works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    void testGetScore()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertEquals(0, m.getScore());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the score field.");
        }
    }

    /*
        Goal: To test that the getter for bingo boolean works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    void testIsBingo()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertFalse(m.isBingo());

            for(int i = 0; i < 5; i++) // Add 5 new placements to the list
            {
                ply.add(new Placement(3, 4,'E'));
            }

            Move bingoMove = new Move(ply, "DUEEEEEE", 1); // This is a 7-Tile move

            assertTrue(bingoMove.isBingo());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the bingo field.");
        }
    }

    /*
        Goal: To test that the setter for score with a valid value works as expected.
        Testing Method: Instantiate an object and test the setter with a valid, non-zero value.
     */
    @Test
    void testSetScore_Valid()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            m = new Move(ply, "DUE", 1);

            m.setScore(12);

            assertEquals(12, m.getScore());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when setting a valid, non-zero score.");
        }
    }

    /*
        Goal: To test that you cannot set the score of the Move to zero.
        Testing Method: Instantiate an object and try setting the score to 0.
     */
    @Test
    void testSetScore_Zero()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            m = new Move(ply, "DUE", 1);

            m.setScore(0);

            fail("The score of a move should not be set to zero.");
        }
        catch(Exception ex)
        {
            assertEquals("The score of a move cannot be set to zero or a negative value.", ex.getMessage());
        }
    }

    /*
        Goal: To test that you cannot set the score of the Move to a negative value.
        Testing Method: Instantiate an object and try setting the score to -1.
     */
    @Test
    void testSetScore_Negative()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            m = new Move(ply, "DUE", 1);

            m.setScore(-1);

            fail("The score of a move should not be set to a negative value.");
        }
        catch(Exception ex)
        {
            assertEquals("The score of a move cannot be set to zero or a negative value.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the toString() method works as expected.
        Testing Method: Call toString() and validate the string returned. Test after calling all setter methods.
     */
    @Test
    void testToString()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            ply.clear();

            ply.add(new Placement(3, 3, 'A'));
            ply.add(new Placement(4, 4, 'B'));

            Move m2 = new Move(ply, "AB", 1);
            assertEquals("[(3, 3 : A), (4, 4 : B)]", m2.toString());

            m.setScore(2);
            assertEquals("[(3, 3 : A), (4, 4 : B)]", m2.toString());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when toString() is called on an instance of Move.");
        }
    }

    /*
        Goal: To test that the includesBlank() method works as expected.
        Testing Method: Test when expecting both true and false and check the return values.
     */
    @Test
    public void testIncludesBlank()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertFalse(m.includesBlank());

            m.addBlankLetter('S');

            assertTrue(m.includesBlank());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when checking if the move contains a blank letter.");
        }
    }

    /*
        Goal: To test that the addBlank() method correctly adds a blank.
        Testing Method: Test adding multiple blanks and check the Move is updated correctly.
     */
    @Test
    public void testAddBlank()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertFalse(m.includesBlank());

            m.addBlankLetter('S');

            assertTrue(m.includesBlank());

            m.addBlankLetter('E');

            assertTrue(m.includesBlank());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when adding a blank letter.");
        }
    }

    /*
        Goal: To test that the getter for the blank letters works as expected.
        Testing Method: Test the getter when there are no blanks and when there have been some added.
     */
    @Test
    public void testGetBlankLetters()
    {
        try
        {
            ArrayList<Placement> ply;
            Move m;

            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);

            assertEquals(0, m.getBlankLetter().size());

            ArrayList<Character> blanks = new ArrayList<>();

            m.addBlankLetter('S');
            m.addBlankLetter('E');

            blanks.add('S');
            blanks.add('E');

            assertEquals(blanks, m.getBlankLetter());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the blank letters from a move.");
        }
    }
}
