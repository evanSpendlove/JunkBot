package scrabbleGame;

/**
 * <h1>Placement Class</h1>
 *  This class represents a single placement of a tile.
 *  It uses x and y coordinates to locate its placement on the board.
 *  It also stores the letter to be placed as a character.
 *
 *  Team: JunkBot
 *  Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 *
 *  @author Evan Spendlove
 *  @version 1.0.0
 *  @since 22-02-2020
 */
public class Placement implements java.io.Serializable
{
    // Instance variables
    private int x;
    private int y;
    private char letter;

    /**
     * Constructor for the Placement class.
     * Calls setters for all variables for proper input-verification.
     * @param x Pass the X coordinate you wish to set.
     * @param y Pass the Y coordinate you wish to set.
     * @param c Pass the letter to be placed.
     */
    public Placement(int x, int y, char c)
    {
        setX(x);
        setY(y);
        setLetter(c);
    }

    // Getters and Setters

    /**
     * Getter for X coordinate.
     * @return int Returns the X coordinate as an int.
     */
    public int getX() {
        return x;
    }

    /**
     * Setter for the X coordinate.
     * @param x Pass the X coordinate you wish to set.
     */
    public void setX(int x) {
        if(x >= 0 && x < 15) // If x lies on the board
        {
            this.x = x;
        }
        else
        {
            throw new IllegalArgumentException("X must lie within the range 0 - 14 inclusive.");
        }
    }

    /**
     * Getter for the Y coordinate.
     * @return int Returns the Y coordinate as an int.
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for the Y coordinate.
     * @param y Pass the Y coordinate you wish to set.
     */
    public void setY(int y)
    {
        if(y >= 0 && y < 15) // If x lies on the board
        {
            this.y = y;
        }
        else
        {
            throw new IllegalArgumentException("Y must lie within the range 0 - 14 inclusive.");
        }
    }

    /**
     * Getter for the letter.
     * @return char Returns the letter to be placed as a character.
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Setter for the letter.
     * @param letter Pass the letter to be placed.
     */
    public void setLetter(char letter) {
        if(letter >= 'A' && letter <= 'Z') // If uppercase
        {
            this.letter = letter; // Set letter
        }
        else // Else, check if lowercase
        {
            if(letter >= 'a' && letter <= 'z')
            {
                this.letter = (char) (letter + ('A' - 'a') );
            }
            else // Otherwise, not a valid character
            {
                throw new IllegalArgumentException("The letter must be a valid letter within the range A-Z.");
            }
        }
    }

    /**
     * This returns the desired String format that represents this object.
     * This form is (X, Y, Letter).
     * @return String Returns the desired String format of the object.
     */
    @Override
    public String toString()
    {
        return "(" + getX() + ", " + getY() + " : " + getLetter() + ")";
    }

}
