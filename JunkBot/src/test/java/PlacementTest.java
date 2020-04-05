
import org.junit.jupiter.api.Test;
import scrabbleGame.gameModel.Placement;
import static org.junit.jupiter.api.Assertions.*;

/*
    PlacementTest JUnit Test Class

    Purpose: Unit Testing the Placement class of the scrabbleGame package
    Summary: This class attempts to thoroughly test the Placement Class and its methods to ensure they
             are robust and handle invalid input appropriately.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 22-02-2020
 */

public class PlacementTest
{

    /*
        Goal: To test that the constructor for the Placement class works as expected.
        Testing Method: Instantiate an instance of a placement with valid inputs and check no exception thrown.
     */
    @Test
    public void testConstructor()
    {
        try
        {
            Placement p = new Placement(2, 3, 'A');

            assertEquals(2, p.getX());
            assertEquals(3, p.getY());
            assertEquals('A', p.getLetter());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when a Placement is constructed using valid inputs.");
        }

        try
        {
            Placement p = new Placement(-1, -2, '.');

            fail("");
        }
        catch(Exception ex)
        {
            assertEquals("X must lie within the range 0 - 14 inclusive.", ex.getMessage());
        }

    }

    /*
        Goal: To test that the getter for X coordinate works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    public void testGetX()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            assertEquals(3, p.getX());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the x coordinate.");
        }
    }

    /*
        Goal: To test that the getter for Y coordinate works as expected.
        Testing Method: Instantiate an object and test the getter
     */
    @Test
    public void testGetY()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            assertEquals(5, p.getY());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the x coordinate.");
        }
    }

    /*
        Goal: To test that the setter for the X coordinate works when the value passed is valid.
        Testing Method: Call setter for X coordinate passing a valid value.
     */
    @Test
    public void testSetXValid()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            p.setX(9);

            assertEquals(9, p.getX());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the x coordinate.");
        }
    }

    /*
        Goal: To test that the setter for the X coordinate does not allow negative values to be set.
        Testing Method: Call the setter passing a negative value
     */
    @Test
    public void testSetX_Negative()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            p.setX(-9);

            fail("X coordinate of a placement should not be set to a negative value.");
        }
        catch(Exception ex)
        {
            assertEquals("X must lie within the range 0 - 14 inclusive.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the setter for the X coordinate does not allow out-of-bounds (i.e. off the board) values to be set.
        Testing Method: Call the setter passing an out-of-bounds value.
     */
    @Test
    public void testSetX_OutOfBounds()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            p.setX(20);

            fail("X coordinate of a placement should be limited to within the inclusive range 0 - 14");
        }
        catch(Exception ex)
        {
            assertEquals("X must lie within the range 0 - 14 inclusive.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the setter for the Y coordinate works when the value passed is valid.
        Testing Method: Call setter for Y coordinate passing a valid value.
     */
    @Test
    public void testSetY_Valid()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            p.setY(1);

            assertEquals(1, p.getY());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the Y coordinate.");
        }
    }

    /*
        Goal: To test that the setter for the Y coordinate does not allow negative values to be set.
        Testing Method: Call the setter passing a negative value
     */
    @Test
    public void testSetY_Negative()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            p.setY(-2);

            fail("Y coordinate of a placement should not be set to a negative value.");
        }
        catch(Exception ex)
        {
            assertEquals("Y must lie within the range 0 - 14 inclusive.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the setter for the y coordinate does not allow out-of-bounds (i.e. off the board) values to be set.
        Testing Method: Call the setter passing an out-of-bounds value.
     */
    @Test
    public void testSetY_OutOfBounds()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            p.setY(16);

            fail("Y coordinate of a placement should be limited to within the inclusive range 0 - 14");
        }
        catch(Exception ex)
        {
            assertEquals("Y must lie within the range 0 - 14 inclusive.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the getter for letter works as expected.
        Testing Method: Instantiate an object and test the getter.
     */
    @Test
    public void testGetLetter()
    {
        try
        {
            Placement p = new Placement(3, 5, 'c');

            assertEquals('C', p.getLetter());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting the letter.");
        }
    }

    /*
        Goal: To test that the setter for the letter works for uppercase letters.
        Testing Method: Call the setter passing an uppercase letter.
     */
    @Test
    public void testSetLetter_Uppercase()
    {
        try
        {
            Placement p = new Placement(2, 2, 'c');

            p.setLetter('Q');

            assertEquals('Q', p.getLetter());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when setting a valid uppercase letter.");
        }
    }

    /*
        Goal: To test that the setter for the letter works for lowercase letters.
        Testing Method: Call the setter passing an lowercase letter.
     */
    @Test
    public void testSetLetter_Lowercase()
    {
        try
        {
            Placement p = new Placement(2, 2, 'c');

            p.setLetter('q');

            assertEquals('Q', p.getLetter());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when setting a valid lowercase letter.");
        }

    }

    /*
        Goal: To test that the setter for the letter throws a valid exception when passing an invalid character.
        Testing Method: Call the setter passing an invalid character
     */
    @Test
    public void testSetLetter_Invalid()
    {
        try
        {
            Placement p = new Placement(2, 2, 'c');

            p.setLetter('!');

            fail("No exception should be thrown when setting a valid uppercase letter.");
        }
        catch(Exception ex)
        {
            assertEquals("The letter must be a valid letter within the range A-Z.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the toString() method works as expected.
        Testing Method: Call toString() and validate the string returned. Test after calling all setter methods.
     */
    @Test
    public void testToString()
    {
        try
        {
            Placement p = new Placement(4, 6, 'H');
            assertEquals("(4, 6 : H)", p.toString());

            p.setX(2);
            assertEquals("(2, 6 : H)", p.toString());

            p.setY(9);
            assertEquals("(2, 9 : H)", p.toString());

            p.setLetter('V');
            assertEquals("(2, 9 : V)", p.toString());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when calling the toString() method.");
        }
    }

}
