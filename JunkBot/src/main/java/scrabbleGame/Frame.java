package scrabbleGame;

import java.util.ArrayList;

/**
 * <h1>Frame Class</h1>
 * This class represents the Frame (Rack) in Scrabble.
 * The frame contains up to 7 tiles.
 * It has methods for removing and adding tiles.
 * Team: JunkBot
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 07-02-2020
 */
public class Frame implements java.io.Serializable
{
    // Instance variables
    private ArrayList<Tile> tiles;

    // Getters and Setters

    /**
     * Getter for tiles ArrayList.
     * @return ArrayList Returns the ArrayList of tiles stored in the Frame.
     */
    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    // Constructors

    /**
     * Constructor which takes an ArrayList of tiles as an argument for initialising tiles field.
     * @param startingTiles Pass this argument to initialise the frame with up to 7 tiles.
     */
    public Frame(ArrayList<Tile> startingTiles)
    {
        if(startingTiles.size() == 7)
        {
            this.tiles = startingTiles;
        }
        else
        {
            throw new IllegalArgumentException("Frame should hold exactly 7 tiles.");
        }
    }

    /**
     * Constructor which takes an instance of Pool as an argument and initialises tiles field using refillFrame().
     * @param p Pass this argument to initialise the frame with tiles from the Pool.
     */
    public Frame(Pool p)
    {
        this.tiles = new ArrayList<>(7); // Initialise tiles with initial capacity of 7
        refillFrame(p); // Call refillFrame() using the Pool object passed
    }

    /**
     * Empty constructor which initialises the tiles ArrayList to a new ArrayList with capacity for 7 tiles.
     */
    public Frame() // Empty constructor
    {
        this.tiles = new ArrayList<>(7); // Initialise tiles with initial capacity of 7
    }

    // Tile access methods

    /**
     * Private method used for adding tiles to the Frame.
     * @param letter Pass the tile that you want to add to the Frame.
     */
    private void addTile(Tile letter)
    {
        if(this.tiles.size() < 7)
        {
            this.tiles.add(letter); // Add the letter
        }
        else
        {
            throw new IllegalStateException("Frame cannot hold more than 7 tiles.");
        }
    }

    /**
     * Private method used for removing tiles from the Frame.
     * @param letter Pass the Frame that you want to remove.
     */
    private void removeTile(Tile letter)
    {
        if(this.tiles.size() > 0)
        {
            if(this.containsTile(letter)){
                this.tiles.remove(letter);
            }else{
                throw new IllegalArgumentException("Cannot remove a letter not on the frame");
            }
        }
        else
        {
            throw new IllegalStateException("Cannot remove a letter from an empty Frame.");
        }
    }

    /**
     * Method used for checking if the rack contains a given tile.
     * @param letter Pass the tile that you want to check.
     * @return boolean Returns true if the rack contains the tile, else false.
     */
    public boolean containsTile(Tile letter)
    {
        return tiles.contains(letter);
    }

    /**
     * Method used for checking if the rack contains a given tile.
     * @param letter Pass the letter that you want to check (Tile conversion done by method).
     * @return boolean Returns true if the rack contains the tile, else false.
     */
    public boolean containsTile(char letter)
    {
        return tiles.contains(Tile.getInstance(letter));
    }

    /**
     * Method used for getting a tile from the rack
     * @param letter Pass the letter that you want to get.
     * @return Tile Returns the Tile for the given letter on the rack.
     */
    public Tile getTile(char letter)
    {
        if(containsTile(letter))
        {
            // Get the index of the tile
            int index = tiles.indexOf(Tile.getInstance(letter));

            return tiles.get(index); // Return the tile
        }
        else
        {
            return null; // No tile found
        }
    }

    /**
     * Method used for playing a tile on the board.
     * @param letter Pass the tile that you want to play.
     * @return Tile Returns the tile after removing it from the rack.
     */
    public Tile playTile(Tile letter)
    {
        removeTile(letter); // Remove letter
        return letter; // Return the played letter
    }

    /**
     * Method used for discarding a tile that you do not want.
     * @param letter Pass the tile that you do not want to keep.
     * @return Tile Returns the tile that you are discarding.
     */
    public Tile discardTile(Tile letter)
    {
        removeTile(letter); // Remove letter
        return letter; // Return the removed letter
    }


    // Utility methods

    /**
     * Method used for checking if the rack is currently empty.
     * @return boolean Returns true if the rack is empty, else false.
     */
    public boolean isEmpty()
    {
        return tiles.isEmpty();
    }

    /**
     * Method used for refilling the frame to the full 7 tiles from a pool.
     * @param pool Pass the pool from which the letters will be drawn.
     */
    public void refillFrame(Pool pool)
    {
        while(this.getTiles().size() < 7) // While there are less than 7 tiles in the rack
        {
            Tile newTile = pool.draw(); // Pick a new tile
            addTile(newTile); // Add it to the rack
        }
    }

    /**
     * Method overriding toString() from Object to allow custom String for printing
     * @return String Returns a custom String representation of this Class
     */
    @Override
    public String toString()
    {
        String frameString = "{";

        if(!isEmpty()) // Check that there are tiles in the Frame
        {
            for(int i = 0; i < this.tiles.size()-1; i++) // For each tile except the last one
            {
                frameString += this.tiles.get(i).toString() + ", "; // Add it followed by a comma and space to the String
            }

            frameString +=  this.tiles.get(this.tiles.size()-1).toString(); // Add the final tile to the String
        }

        return frameString + "}";
    }

}
