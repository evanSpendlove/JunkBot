package scrabbleGame;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

/**
 * <h1>Board Class</h1>
 *  This class represents the Board in Scrabble.
 *  The board is a 15 x 15 array of squares.
 *  The board has several methods of adding words to the board and initialising it.
 *  Team: JunkBot
 *  Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 *  @author Cal Nolan, Reuben Mulligan, Evan Spendlove
 *  @version 1.0.0
 *  @since 21-02-2020
 */
public class Board
{
    /**
     * Checks if the move passed is a current placement by calling isValidPosition() and isHooked().
     * @param m Pass the move you want to check.
     * @return boolean Returns a boolean indicating if the move is valid or not.
     * @author Evan Spendlove
     */
    private boolean checkValidPlacement(Move m)
    {
        return isValidPosition(m) && (getHook(m) != null);
    }

    /**
     * Checks that the coordinates for each placement lie within the bounds of the board.
     * Calls inLine() as well to verify that the word is placed in a single line of characters.
     * @param m Pass the move that you wish to verify.
     * @return boolean Returns true if the move is valid, else false.
     * @author Evan Spendlove
     */
    private boolean isValidPosition(Move m)
    {
        // check the position lies on the board
        // check these tiles aren't already empty

        boolean validPosition = true;

        for(int i = 0; i < m.getPlays().size() && validPosition; i++) // For each Play
        {
            Placement play = m.getPlays().get(i); // Get the play

            if(play.getX() > 14 || play.getY() > 14 || play.getX() < 0 || play.getY() < 0) // Check the coordinates are on the board
            {
                validPosition = false; //
            }

            if(board[play.getY()][play.getX()].isOccupied()) // Check the chosen tile is not currently occupied
            {
                validPosition = false;
            }
        }

        return validPosition && inLine(m);
    }

    /**
     * Method for placing a word on the board.
     * Calls checkValidMove() to validate a move before it is placed.
     * @param m Pass the move that you wish to place on the board.
     * @param p Pass the player who made the move so their score can be updated.
     * @return int Returns 2 if the move is successfully placed (also valid), and -1 if not placed.
     * @author Evan Spendlove
     */
    public int placeWord(Move m, Player p)
    {
        if(checkValidMove(m, p))
        {
            addWordToBoard(m, p);
            addWordPlayed(m);
            return 2;
        }
        else
        {
            return -1; // Return error code since this isn't a valid placement
        }
    }

    // Calculate score
    // TODO: Complete method
    private int calculateScore(Move m)
    {
        return 0;
    }
}
