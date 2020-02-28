package scrabbleGame;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <h1>Pool Class</h1>
 * This class represents the bag of tiles (called the pool) in Scrabble.
 * The pool contains 100 tiles, each a letter in the chosen alphabet.
 * For our implementation, we are using the 26-letter english alphabet, with two additional blank tiles.
 * The pool can be reset and you can draw tiles from the pool.
 * Team: JunkBot
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Cal Nolan
 * @version 1.0
 * @since 07-02-2020
 */
public class Pool implements java.io.Serializable
{
    // Fields
    private ArrayList<Tile> pool; // ArrayList of Tiles for storing current tiles in pool

    // Accessor

    /**
     * Public method for adding a tile to the pool.
     * @param t Takes a tile, t, as input and adds it to the pool.
     */
    public void addTile(Tile t)
    {
        pool.add(t);
    }

    // Constructor
    /**
     * This is the constructor for the Pool class.
     * The constructor initialises the pool field and calls the resetPool() method.
     */
    public Pool()
    {
        // Initialise instance variables
        pool = new ArrayList<>();

        // Initialise the pool
        resetPool();
    }


    // Core methods

    /**
     * resetPool(): This method clears all tiles currently in the pool.
     * It then fills the pool with the required letters according to their
     * frequency of occurrence.
     */
    private void resetPool()
    {
        this.pool.clear(); // First, clear all items currently in the pool.

        // Two arrays used to represent the letters and their frequencies
        char[] letters = {'#','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        int[] count = {2, 9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};

        for(int i = 0; i < letters.length; i++) // For each letter in the alphabet
        {
            for(int j = 0; j < count[i]; j++) // For the number of times it exists in the pool
            {
                addTile(Tile.getInstance(letters[i])); // Add a new Tile of it
            }
        }
    }

    /**
     * This method is used to get the current size of the pool (i.e. the remaining tiles).
     * @return int This returns the size of the pool field.
     */
    public int size()
    {
        return pool.size();
    }

    /**
     * This method is used to test if the pool contains any tiles.
     * @return boolean This returns true if the pool is empty, else it returns false.
     */
    public boolean isEmpty()
    {
        return pool.size()==0;
    }

    /**
     * This method is used to reset the pool to its original state, containing all tiles.
     */
    public void reset()
    {
        resetPool();
    }

    /**
     * This method removes a randomly chosen tile from the pool and returns it.
     * @return Tile Returns the randomly chosen tile.
     */
    public Tile draw()
    {
        int randomInt = ThreadLocalRandom.current().nextInt(0, pool.size()); // Generate random number
        return pool.remove(randomInt); // Return the removed tile
    }

    /**
     * This method allows users to check the value of a tile by passing the character.
     * @param c The character for which you want to get the value (worth) of the tile.
     * @return int This returns the value of the Tile associated with this character.
     */
    public int getValue(char c)
    {
        return Tile.getValue(c);
    }

    /**
     * This method overrides the default String method of the Object class.
     * @return String Returns the String format of the pool ArrayList;
     */
    @Override
    public String toString()
    {
        return this.pool.toString();
    }

}
