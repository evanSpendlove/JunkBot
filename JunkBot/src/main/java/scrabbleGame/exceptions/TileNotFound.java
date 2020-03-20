package scrabbleGame.exceptions;

/**
 * <h1>TileNotFound Exception Class</h1>
 *  This exception is used when a method tries to access a tile that is not found where expected.
 *  E.g. accessing a tile from the board using invalid coordinates.
 *  Team: JunkBot
 *  Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 *  @author Evan Spendlove
 *  @version 1.0.0
 *  @since 20-03-2020
 */
public class TileNotFound extends Exception
{
    /**
     * Constructor for TileNotFound
     * @param message Pass the message for the exception to throw
     */
    public TileNotFound(String message)
    {
        super("404: " + message); // Super to Exception class constructor
    }
}
