package scrabbleGame;

/**
 * <h1>Tile Class</h1>
 * This represents a tile in Scrabble, which has an associated character and value.
 * <p></p>
 * Team: JunkBot
 *  * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 07-02-2020
 */
public enum Tile implements java.io.Serializable
{
    // Enum values for all tiles including blanks
    BLANK('#', 0), A('A', 1), B('B', 3), C('C', 3), D('D', 2), E('E',1), F('F', 4), G('G', 2), H('H',4),
    I('I', 1), J('J', 8), K('K', 5), L('L', 1), M('M', 3), N('N', 1), O('O', 1), P('P', 3), Q('Q', 10), R('R', 1),
    S('S', 1), T('T', 1), U('U', 1), V('V', 4), W('W', 4), X('X', 8), Y('Y', 4), Z('Z', 10);

    // Private fields
    /**
     * Character Field
     * Stores the (uppercase) character of the Tile
     */
    private final char character;

    /**
     * Value Field
     * Stores the value of the tile
     */
    private final int value;

    // Constructor

    /**
     * Default constructor for an enum class.
     * @param c takes a character as input for the constructor
     * @param value takes a value as input for the constructor
     */
    Tile(char c, int value){
        this.character = c;
        this.value = value;
    }

    /**
     * This accessor allows access to the character field of the current Tile instance.
     * @return char this returns the character field of this current Tile instance
     */
    public char character() {return character;}

    /**
     * This accessor allows access to the value field of the current Tile instance.
     * @return int this returns the value field of this current Tile instance
     */
    public int value()
    {
        return value;
    }

    /**
     * This method returns and instance of the Tile class for the character passed
     * @param c the letter for which you want an instance of Tile
     * @return Tile This returns an instance of the Tile class for the character passed.
     */
    public static Tile getInstance(char c) {
        if(c < 'A' || c > 'Z' ){ // If not an uppercase letter.
            if(c >= 'a' && c <= 'z'){ // If a lower case letter
                return Tile.valueOf(Character.toString(c).toUpperCase()); // Pass it uppercase
            }
            else if(c == '#'){ // if a blank tile
                return Tile.BLANK; // Return a blank tile
            }
            else { // Throw an exception, as this is not a valid letter for a Tile value check.
                throw new IllegalArgumentException("Cannot get an instance of a character that is not a blank or valid Scrabble letter.");
            }
        }

        return Tile.valueOf(Character.toString(c)); // Otherwise, this is an uppercase letter so check normally.
    }

    /**
     * This method returns the value associated with the character passed as per the enum.
     * @param c pass a character for which you want the associated value.
     * @return int Returns the value associated with the character passed.
     */
    public static int getValue(char c){
        if(c < 'A' || c > 'Z' ){ // If not an uppercase letter.
            if(c >= 'a' && c <= 'z'){ // If a lower case letter
                return Tile.valueOf(Character.toString(c).toUpperCase()).value(); // Get value of uppercase letter
            }
            else if(c == '#'){ // if a blank tile
                return Tile.valueOf("BLANK").value();
            }
            else { // Throw an exception, as this is not a valid letter for a Tile value check.
                throw new IllegalArgumentException("Cannot get the value of a character that is not a blank or valid Scrabble letter.");
            }
        }

        return Tile.valueOf(Character.toString(c)).value(); // Otherwise, this is an uppercase letter so get value normally
    }

    /**
     * This method returns the value associated with the String passed as per the enum.
     * @param letter pass a String for which you want the associated value.
     * @return int Returns the value associated with the String passed.
     */
    public static int getValue(String letter) {
        if(letter.equals("#")){ // If a blank
            return Tile.valueOf("BLANK").value(); // Return the value of the blank
        }
        else
        {
            char l = letter.toUpperCase().trim().replaceAll("\\s", "").charAt(0); // Remove whitespace, make uppercase and get first character

            if(l >= 'A' && l <= 'Z') // Check if valid uppercase letter
            {
                return Tile.valueOf(Character.toString(l)).value; // Return value
            }
            else
            {
                throw new IllegalArgumentException("Cannot get the value of a character (in String format) that is not a blank or valid Scrabble letter.");
            }

        }
    }

    /**
     * This method overrides the toString() method of object for a custom String return value.
     * @return String Returns only the character of the current Tile instance
     */
    @Override
    public String toString() {
        return Character.toString(character()); // String containing just the character (no need for associated value)
    }
}