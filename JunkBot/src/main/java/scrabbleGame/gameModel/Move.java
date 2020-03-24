package scrabbleGame.gameModel;

import java.util.List;

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
public class Move implements java.io.Serializable
{
    // Fields

    // Set in constructor
    private String word = "";
    List<Placement> plays;
    private int direction;

    // Set afterwards or privately
    private boolean bingo = false;
    private int score;

    // Constructor

    /**
     * Constructor which sets the score to 0 by default and sets bingo based on the number of tiles to be played.
     * @param plays List of Tile placements.
     * @param word Word to be played on the board (including hook letter)
     * @param direction Direction: 0 = horizontal, 1 = vertical
     */
        public Move(List<Placement> plays, String word, int direction)
    {
        setWord(word);
        setPlays(plays);
        setDirection(direction);

        this.score = 0; // Initialise score to a default value
    }

    // Getters and Setters

    /**
     * Getter for the word
     * @return String Returns the word to be played on the board (including hook letter) as a String.
     */
    public String getWord() {
        return word;
    }

    /**
     * Private Setter for the word
     * @param word Pass the word you wish to set for this move.
     */
    private void setWord(String word)
    {
        if(!word.isEmpty() && !word.isBlank())
        {
            this.word = word;
        }
        else
        {
            throw new IllegalArgumentException("Cannot play a blank word.");
        }
    }

    /**
     * Getter for the direction.
     * 0 = horizontal, 1 = vertical.
     * @return int Returns an integer indicating the direction in which the word is placed.
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Private Setter for the direction.
     * Pass a 0 if you wish to play the word horizontally.
     * Pass a 1 if you wish to play the word vertically.
     * @param direction Pass the direction as a binary integer.
     */
    private void setDirection(int direction)
    {
        if(direction == 0 || direction == 1)
        {
            this.direction = direction;
        }
        else
        {
            throw new IllegalArgumentException("Direction can only be 0 or 1 indicating horizontal or vertical word placement.");
        }
    }

    /**
     * Getter for the List of Tile Placements.
     * @return List Returns a list of Tile Placements.
     */
    public List<Placement> getPlays()
    {
        return plays;
    }

    /**
     * Private Setter for the plays variable.
     * @param plays Pass the List of Tile placements to be set.
     */
    private void setPlays(List<Placement> plays)
    {
        if(plays.size() > 0 && plays.size() < 8) // Validate plays size is within acceptable limits
        {
            this.plays = plays; // Initialise plays
            setBingo();
        }
        else
        {
            throw new IllegalArgumentException("Cannot play a word with 0 or more than 7 letter placements.");
        }
    }

    /**
     * Getter for checking if a move is to be awarded the bingo bonus.
     * @return boolean Returns a boolean indicating if the move is a bingo-move.
     */
    public boolean isBingo()
    {
        return bingo;
    }

    /**
     * Private Setter called when constructing a move.
     * Sets the bingo value based on the size of the list of plays.
     */
    private void setBingo()
    {
        if(plays.size() == 7)
        {
            this.bingo = true;
        }
        else
        {
            this.bingo = false;
        }
    }

    /**
     * Getter for accessing the score associated with this move.
     * @return int Returns the score associated with the move as an int.
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Setter for the score associated with this move.
     * @param score Pass the score (non-zero) that you wish to set.
     */
    public void setScore(int score)
    {
        if(score > 0)
        {
            this.score = score;
        }
        else
        {
            throw new IllegalArgumentException("The score of a move cannot be set to zero or a negative value.");
        }
    }

    /**
     * Method used for accessing a user-friendly String representation of this object.
     * @return String Returns this object in a user-friendly string format.
     */
    public String toString()
    {
        return getPlays().toString();
    }
}
