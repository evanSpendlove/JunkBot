package java;

import org.junit.jupiter.api.Test;
import scrabbleGame.gameModel.Tile;

import static org.junit.jupiter.api.Assertions.*;

/*
    TileTest JUnit Test Class

    Purpose: Unit-Testing the Tile Class of the scrabbleGame package.
    Summary: This class attempts to thoroughly test the Tile Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 07-02-2020
 */
public class TileTest
{

    /*
        Goal: To test that the blank tile instance of Tile works as expected
        Testing Method: Instantiate an instance of the blank tile and test its attributes.
     */
    @Test
    public void testBlankTile()
    {
        Tile blank = Tile.BLANK;

        assertEquals('#', blank.character()); // Correct character
        assertEquals(0, blank.value()); // Correct value
    }

    /*
        Goal: To test that the getValue() method taking a char as the argument works.
        Testing Method: Test getValue() passing a char as the argument for all possible Tile values.
     */
    @Test
    public void testGetValChar()
    {
        Tile test = Tile.A;

        int[] testValues = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10}; // Correct values for Scrabble

        assertEquals(0, test.getValue('#')); // Test blank separately

        for(int i = 0; i < testValues.length; i++) // For each letter
        {
            assertEquals(testValues[i], test.getValue((char) ('a' + i) )); // Test the value
        }

        for(int i = 0; i < testValues.length; i++) // For each letter
        {
            assertEquals(testValues[i], test.getValue((char) ('A' + i) )); // Test the value
        }
    }

    /*
        Goal: To test that the getValue() method taking a String as the argument works.
        Testing Method: Test getValue() passing a String as the argument for all possible Tile values.
     */
    @Test
    public void testGetValString()
    {
        Tile test = Tile.A;

        int[] testValues = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10}; // Correct values for Scrabble

        assertEquals(0, test.getValue("#")); // Test blank separately

        for(int i = 0; i < testValues.length; i++) // For each letter
        {
            assertEquals(testValues[i], test.getValue(Character.toString((char)('a' + i)))); // Test the value
        }

        for(int i = 0; i < testValues.length; i++) // For each letter
        {
            assertEquals(testValues[i], test.getValue(Character.toString((char)('A' + i)))); // Test the value
        }
    }

    /*
        Goal:  To test that the getValue() method throws an appropriate exception when an invalid character is passed.
        Testing Method: Pass an invalid character as the argument to getValue() and check the exception thrown (if any).
     */
    @Test
    public void testGetInvalidValChar()
    {
        try
        {
            int val = Tile.getValue('~');
            fail("There is no value associated with the character \'~\'");
        }
        catch(Exception ex)
        {
            assertEquals(IllegalArgumentException.class, ex.getClass());
            assertEquals("Cannot get the value of a character that is not a blank or valid Scrabble letter.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the getValue() method throws an appropriate exception when an invalid String is passed.
        Testing Method: Pass an invalid String as the argument to getValue() and check the exception thrown (if any).
     */
    @Test
    public void testGetInvalidValString()
    {
        try
        {
            int val = Tile.getValue("~");
            fail("There is no value associated with the String \"~\"");
        }
        catch(Exception ex)
        {
            assertEquals(IllegalArgumentException.class, ex.getClass());
            assertEquals("Cannot get the value of a character (in String format) that is not a blank or valid Scrabble letter.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the getInstance() method throws an appropriate exception when asked for an instance of an invalid char.
        Testing Method: Call getInstance() passing an invalid character as the argument.
     */
    @Test
    public void testGetInvalidInstance()
    {
        try
        {
            Tile t = Tile.getInstance('~');
            fail("There is no valid instance of the character \'~\'. An appropriate exception should be thrown.");
        }
        catch(Exception ex)
        {
            assertEquals(IllegalArgumentException.class, ex.getClass());
            assertEquals("Cannot get an instance of a character that is not a blank or valid Scrabble letter.", ex.getMessage());
        }
    }

    /*
        Goal: To test that the getInstance() method works for all possible letters, both uppercase and lowercase.
        Testing Method: Try calling getInstance() passing every possible letter, both uppercase and lowercase.
     */
    @Test
    public void testAllLetters()
    {
        int[] values = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

        try
        {
            Tile blank = Tile.getInstance('#');

            assertEquals(0, blank.value());
            assertEquals('#', blank.character());

            for(char letter = 'A'; letter <= 'Z'; letter++) // Test for uppercase
            {
                Tile t = Tile.getInstance(letter);

                assertEquals(letter, t.character());
                assertEquals(values[(letter-'A')], t.value());
            }

            for(char letter = 'a'; letter <= 'z'; letter++) // Test for lowercase
            {
                Tile t = Tile.getInstance(letter);

                char upperLetter = (char) (letter + ('A' - 'a')); // Convert to uppercase letter

                assertEquals(upperLetter, t.character());
                assertEquals(values[(upperLetter-'A')], t.value());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("Tile creation should not throw exception for valid letters");
        }
    }

    /*
        Goal: To test that the toString() method works for all tiles.
        Testing Method: Call toString() on all tiles and check that the appropriate String is returned.
     */
    @Test
    public void testToString()
    {
        for(Tile t : Tile.values()) // For each possible Tile
        {
            assertEquals(Character.toString(t.character()), t.toString()); // Check their toString()
        }
    }

}
