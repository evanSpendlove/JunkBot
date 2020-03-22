package java;

import org.junit.jupiter.api.Test;
import scrabbleGame.gameModel.Square;
import scrabbleGame.gameModel.Tile;
import static org.junit.jupiter.api.Assertions.*;

/*
    SquareTest JUnit Test Class

    Purpose: Unit Testing the Square class of the scrabbleGame package
    Summary: This class attempts to thoroughly test the Square Class and its methods to ensure they
             are robust and handle invalid input appropriately.

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 08-02-2020
 */


public class SquareTest
{

    /*
        Goal: To test that the constructor for the Square class works as expected.
        Testing Method: Instantiate an instance of the square and test the default values.
     */
    @Test
    public void testConstructor()
    {
        try
        {
            Square s = new Square();

            // Test the properties of s
            assertEquals(Square.squareType.REGULAR, s.getType());
            assertFalse(s.isOccupied());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when a square is constructed normally.");
        }
    }

    /*
        Goal: To test that the getTile() method works as expected.
        Testing Method: Check it returns null when no tile on the square.
                        Check returns the right tile when there is one placed.
     */
    @Test
    public void testGetTile()
    {
        try
        {
            Square s = new Square();

            assertNull(s.getTile()); // Should return null as no tile placed yet.

            s.setTile(Tile.E); // Add a tile

            assertEquals(Tile.E, s.getTile()); // Check the tile is now on the square
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when getting a Tile from the square.");
        }
    }

    /*
        Goal: To test that the setTile() method works correctly.
        Testing Method: Try setting a tile. Check it is correctly set. Check the status is updated.
     */
    @Test
    public void testSetTile()
    {
        try
        {
            Square s = new Square();

            s.setTile(Tile.E); // Add a tile

            assertEquals(Tile.E, s.getTile()); // Check the tile is now on the square
            assertTrue(s.isOccupied()); // Check the status is now true
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when setting a Tile onto the square.");
        }
    }

    /*
        Goal: To test that a tile cannot be placed on top of another tile.
        Testing Method: Place a tile on a square. Now, place another tile on the same square. Check the exception thrown.
     */
    @Test
    public void testSetTileWhenOccupied()
    {
        try
        {
            Square s = new Square();

            s.setTile(Tile.E); // Add a tile

            s.setTile(Tile.B); // Add another tile

            fail("You should not be able to place a tile on a square which is already occupied.");
        }
        catch(Exception ex)
        {
            assertEquals("Cannot add a tile to a square with a tile on it.", ex.getMessage());
            assertEquals(IllegalStateException.class, ex.getClass());
        }
    }

    /*
        Goal: To test that the isOccupied() method works as expected.
        Testing Method: Instantiate an instance of the square. Check isOccupied returns false by default.
                        Add a tile and check it now returns true.
     */
    @Test
    public void testIsOccupied()
    {
        try
        {
            Square s = new Square();

            assertFalse(s.isOccupied()); // Should be false as no tile on it yet

            s.setTile(Tile.A); // Add a tile

            assertTrue(s.isOccupied()); // Should now be true
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when adding a tile to a square normally.");
        }
    }

    /*
        Goal: To test that the getter and setter for the squareType enum work (interdependent methods).
        Testing Method: Test setting and getting each possible enum type.
     */
    @Test
    public void testGetSetType()
    {
        try
        {
            Square s = new Square();

            assertEquals(Square.squareType.REGULAR, s.getType());

            s.setType(Square.squareType.DB_WORD);
            assertEquals(Square.squareType.DB_WORD, s.getType());

            s.setType(Square.squareType.DB_LETTER);
            assertEquals(Square.squareType.DB_LETTER, s.getType());

            s.setType(Square.squareType.STAR);
            assertEquals(Square.squareType.STAR, s.getType());

            s.setType(Square.squareType.TR_WORD);
            assertEquals(Square.squareType.TR_WORD, s.getType());

            s.setType(Square.squareType.TR_LETTER);
            assertEquals(Square.squareType.TR_LETTER, s.getType());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when setting the type of the object");
        }
    }

    /*
        Goal: To test that a square cannot be occupied and have a special type.
        Testing Method: Add a tile to a square. Then, try and change it's type to a multiplier. Check the exception thrown.
     */
    @Test
    public void testSetTypeOccupiedSquare()
    {
        try
        {
            Square s = new Square();

            s.setTile(Tile.T);
            s.setType(Square.squareType.DB_WORD);

            fail("An occupied square should not have a special type.");
        }
        catch(Exception ex)
        {
            assertEquals("A Square cannot have a tile on it and have a special type.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the toString() method works as expected.
        Testing Method: Test the toString() method with all possible types of square.
     */
    @Test
    public void testToString()
    {
        try
        {
            Square s1 = new Square();
            assertEquals("_", s1.toString());

            s1.setType(Square.squareType.DB_LETTER);
            assertEquals("2L", s1.toString());

            s1.setType(Square.squareType.STAR);
            assertEquals("S", s1.toString());

            s1.setType(Square.squareType.DB_WORD);
            assertEquals("2W", s1.toString());

            s1.setType(Square.squareType.TR_LETTER);
            assertEquals("3L", s1.toString());

            s1.setType(Square.squareType.TR_WORD);
            assertEquals("3W", s1.toString());

            s1.setTile(Tile.getInstance('A'));
            assertEquals("A", s1.toString());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when calling toString on Square.");
        }
    }

}
