package scrabbleGame.gameModel;

/**
 * <h1>Square Class</h1>
 *  This class represents a single square on the Scrabble board.
 *  A square can have different multipliers, represented using an enum.
 *  There are the relevant getters/setters for accessing the squares fields.
 *  Team: JunkBot
 *  Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 *  @author Evan Spendlove
 *  @version 1.0.0
 *  @since 21-02-2020
 */
public class Square implements java.io.Serializable
{
    /**
     * Enum for storing the type of square.
     * There is only one star at the centre of the board.
     * The other types are multipliers for letters or words.
     */
    public enum squareType {STAR, REGULAR, DB_LETTER, DB_WORD, TR_LETTER, TR_WORD}

    // Fields
    squareType type;
    boolean occupied;
    Tile tile;


    /**
     * Getter for the square type
     * @return squareType Returns the enum value for the square.
     */
    public squareType getType() {
        return type;
    }

    /**
     * Setter for the square type
     * @param t Pass the type that you want to set for the square.
     */
    public void setType(squareType t)
    {
        if(isOccupied())
        {
            if(t == squareType.REGULAR)
            {
                this.type = t;
            }
            else
            {
                throw new IllegalStateException("A Square cannot have a tile on it and have a special type.");
            }
        }
        else
        {
            this.type = t;
        }

    }

    /**
     * Getter for checking if the square currently has a tile on it.
     * @return boolean Returns true if there is a currently a tile on this square.
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Setter for the occupied status of the square.
     * This is called automatically when a tile is placed on the square
     * @param occupied Pass the boolean value that you want to update the square with.
     */
    private void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /**
     * Getter for the tile currently on the square.
     * @return Tile Returns the tile currently on the square, or null if no tile on the square.
     */
    public Tile getTile()
    {
        // Check if there is currently a tile on the square
        if(isOccupied())
        {
            return tile;     // If so, return the tile
        }
        else
        {
            // System.out.println("Error: Square is currently empty.");
            return null; // Return null for error checking.
        }
    }

    /**
     * Setter for the Tile object.
     * @param tile Pass the tile which you wish to place on the square.
     * @throws IllegalStateException Throws an exception if there is already a tile on the square.
     */
    public void setTile(Tile tile) throws IllegalStateException
    {
        if(isOccupied())
        {
            throw new IllegalStateException("Cannot add a tile to a square with a tile on it.");
        }
        else
        {
            this.tile = tile;
            setOccupied(true);
        }
    }

    /**
     * Clears the tile from the square.
     * @throws IllegalStateException Cannot remove a tile from a square without a tile.
     */
    public void clearTile() throws IllegalStateException
    {
        if(!isOccupied())
        {
            throw new IllegalStateException("Cannot clear a tile from a square that is not occupied.");
        }
        else
        {
            this.tile = null;
            setOccupied(false);
        }
    }

    /**
     * Constructor for the Square object.
     * The occupied field is false by default, and it is a regular square (no multiplier, not a star) by default.
     */
    public Square()
    {
        occupied = false; // Always false by default
        type = squareType.REGULAR;
    }

    @Override
    public String toString()
    {
        if(isOccupied())
        {
            return getTile().toString();
        }
        else
        {
            switch (getType())
            {
                case TR_WORD:
                    return "3W";

                case TR_LETTER:
                    return "3L";

                case DB_WORD:
                    return "2W";

                case DB_LETTER:
                    return "2L";

                case STAR:
                    return "S";

                default:
                    return "_";
            }
        }
    }

}
