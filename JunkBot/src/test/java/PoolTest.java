import org.junit.jupiter.api.Test;
import scrabbleGame.gameModel.Pool;
import scrabbleGame.gameModel.Tile;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/*
    PoolTest JUnit Test Class

    Purpose: Unit Testing the Pool class of the scrabbleGame package
    Summary: This class attempts to thoroughly test the Pool Class and its methods to ensure they
             are robust and handle invalid input appropriately.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Cal Nolan
    Version: 1.0.0
    Since: 08-02-2020
 */

public class PoolTest {

    // Private methods for testing
    private HashMap<Tile, Integer> generateHashPool()
    {
        HashMap<Tile, Integer> poolMap = new HashMap<>();
        int[] numOccur = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};

        // First add blanks
        poolMap.put(Tile.BLANK, 2);

        for(int i = 0; i < numOccur.length; i++)
        {
            poolMap.put(Tile.getInstance((char)('a' + i)), numOccur[i]);
        }

        return poolMap;
    }

    private boolean checkValidPool(Pool p, HashMap<Tile, Integer> hashPool)
    {
        int size = p.size();

        for(int i = 0; i < size; i++)
        {
            Tile t = p.draw();
            hashPool.put(t, hashPool.get(t) - 1);

            if(hashPool.get(t) == 0) // If 0
            {
                hashPool.remove(t); // Remove
            }
        }

        return hashPool.size() == 0;
    }

    private String removeTilesFromString(String s, Tile[] tiles)
    {
        for(int i = 0; i < tiles.length; i++) // For each tile that was drawn
        {
            char c = tiles[i].character(); // Current character to be removed

            int startIndex = s.indexOf(c); // Index of that character
            int comma = -1;

            if(s.charAt(startIndex + 1) == ',') // Is it followed by a comma?
            {
                comma = startIndex + 1; // If so, mark the index of that comma
            }

            if(comma != -1) // If it followed by a comma
            {
                s = s.substring(0, startIndex) + s.substring(comma + 2); // Add the substring up to this space before this letter to one starting after the space after the comma
            }
            else // Otherwise, last letter
            {
                startIndex = startIndex - 2; // Move start index back two as the format is ", X]" so we need to remove the comma and space
                int endIndex = s.length()-1; // End index is the ']' at the end, which is length - 1.

                s = s.substring(0, startIndex) + s.substring(endIndex); // Add substrings
            }
        }

        return s; // Return edited String
    }

    /*
        Goal: To test that constructor properly generates a pool with valid distribution of letters.
        Testing Method: Construct new instance of Pool. Check that it has the right number, and right distribution of letters.
     */
    @Test
    public void testConstructor()
    {
        Pool test = new Pool();

        assertTrue(checkValidPool(test, generateHashPool())); // Check that the pool contains the right distribution of letters
    }

    /*
        Goal: To test that the size() method of the Pool class accurately portrays the size of the Pool.
        Testing Method: Construct a new instance of this class. Check that the size is as expected. Test the number of items contained.
     */
    @Test
    public void testGetSize()
    {
        Pool p = new Pool(); // Should contain 100 items.

        int size = p.size(); // Initial size
        int i = 0; // Counter for counting the size

        while(p.size() > 0) // While p contains Tiles
        {
            p.draw(); // Draw a Tile
            i++; // Increment i
        }

        assertEquals(100, size); // Check the size matches what we expect
        assertEquals(size, i); // Check that i == size
    }

    /*
        Goal: To test that the isEmpty() method works as expected.
        Testing Method: Construct a new instance of Pool. Check when full, check when empty.
     */
    @Test
    public void testIsEmpty()
    {
        Pool p = new Pool();

        assertFalse(p.isEmpty()); // Should not be empty as contains 100 Tiles

        while(p.size() > 0) // While the pool contains Tiles
        {
            p.draw(); // Draw a Tile
        }

        assertEquals(0, p.size()); // Should contain 0 tiles
        assertTrue(p.isEmpty()); // Should be empty
    }

    /*
        Goal: To test the draw() method of Pool.
        Testing Method: Construct a new instance of Pool. Draw 10 tiles. Remove all remaining tiles in Pool from a HashMap.
                        Remove the remaining 10 tiles from the HashMap. Ensure the HashMap is now empty.
                        Thus, the pool had ten valid Tiles drawn. The pool and the drawn tiles make up a complete distribution.
     */
    @Test
    public void testDraw()
    {
        Pool test = new Pool();
        Tile[] tilesDrawn = new Tile[10];

        for(int i = 0; i < tilesDrawn.length; i++) // Draw 10 tiles
        {
            tilesDrawn[i] = test.draw(); // Store them in tilesDrawn array
        }

        HashMap<Tile, Integer> hashPool = generateHashPool(); // Generate the hashPool for testing distribution and count

        if(!checkValidPool(test, hashPool)) // Subtract the pool from the hashPool
        {
            for(int i = 0; i < tilesDrawn.length; i++) // For each Tile in tilesDrawn
            {
                hashPool.put(tilesDrawn[i], hashPool.get(tilesDrawn[i]) - 1); // Decrement count in hashPool

                if(hashPool.get(tilesDrawn[i]) == 0) // If this Tile's count == 0
                {
                    hashPool.remove(tilesDrawn[i]); // Remove
                }
            }
        }
        else // Otherwise, the pool is still full
        {
            fail("No tiles drawn from the pool.");
        }

        assertEquals(0, hashPool.size());
    }

    /*
        Goal: To test that the reset() method works as expected.
        Testing Method: Construct a new instance of Pool. Remove 50 tiles. Reset it.
                        Check that is now contains the right number and distribution of tiles.
     */
    @Test
    public void testReset()
    {
        Pool test = new Pool();

        while(test.size() > 50) // Withdraw 50 tiles
        {
            test.draw();
        }

        test.reset(); // Reset the pool

        assertEquals(test.size(), 100); // Ensure that the total number of tiles returns to 100 after a reset
        assertTrue(checkValidPool(test, generateHashPool())); // Check that the pool contains the right distribution of letters
    }

    /*
        Goal: To test the getValue() method and ensure the values returned are accurate.
        Testing Method: Construct a new instance of Pool. Test the getValue() method against the correct values for all possible Tiles (27).
     */
    @Test
    public void testGetValue()//checks that, for a number of random letters, the corresponding value is correct
    {
        Pool test = new Pool();

        int[] testValues = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10}; // Correct values for Scrabble

        assertEquals(0, test.getValue('#')); // Test blank separately

        for(int i = 0; i < testValues.length; i++) // For each letter
        {
            assertEquals(testValues[i], test.getValue((char) ('a' + i) )); // Test the value
        }
    }

    /*
        Goal: To test that the getValue() method throws an exception for invalid input
        Testing Method: Pass an invalid character to the getValue() method and verify the correct exception is thrown
     */
    @Test
    public void testGetInvalidValue()//ensures that an exception is thrown for invalid input
    {
        try
        {
            Pool test = new Pool();
            test.getValue('~'); // Invalid character
        }
        catch(Exception ex)
        {
            assertEquals(IllegalArgumentException.class, ex.getClass()); // Right class of exception
            assertEquals("Cannot get the value of a character that is not a blank or valid Scrabble letter.", ex.getMessage()); // Right message
        }
    }

    /*
        Goal: To test that the toString() method works for both the full pool and a partial pool.
        Testing Method: Check the toString() against an expected string for both a full and partial pool.
     */
    @Test
    public void testToString()
    {
        Pool p = new Pool();
        String expectedString = "[#, #, A, A, A, A, A, A, A, A, A, B, B, C, C, D, D, D, D, E, E, E, E, E, E, E, E, E, E, E, E, F, F, G, G, G, H, H, I, I, I, I, I, I, I, I, I, J, K, L, L, L, L, M, M, N, N, N, N, N, N, O, O, O, O, O, O, O, O, P, P, Q, R, R, R, R, R, R, S, S, S, S, T, T, T, T, T, T, U, U, U, U, V, V, W, W, X, Y, Y, Z]";

        assertEquals(expectedString, p.toString()); // Check that the toString() for a full pool matches the expected.

        int nTiles = ThreadLocalRandom.current().nextInt(0, 99); // Choose a random number between 0 and 99

        Tile[] tilesDrawn = new Tile[nTiles];

        for(int i = 0; i < nTiles; i++) // Draw nTiles and add them to an array of Tiles
        {
            tilesDrawn[i] = p.draw();
        }

        expectedString = removeTilesFromString(expectedString, tilesDrawn); // Remove these tiles from the expected String

        assertEquals(expectedString, p.toString()); // Assert that the toString() reflects the tile drawn.
    }
}
