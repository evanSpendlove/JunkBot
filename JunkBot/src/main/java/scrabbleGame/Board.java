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
     * Enum for storing the current status of the game.
     * WIN_X shows which player won.
     */
    private enum gameStatus {READY, IN_PROGRESS, WIN_P1, WIN_P2}

    // Instance variables
    private Square[][] board;
    gameStatus status;
    ArrayList<String> wordsPlayed;

    // Getters and Setters

    /**
     * This method initialises the board and reads in the layout from a txt file.
     * This allows the user to define a board layout of their choice.
     * @throws FileNotFoundException Thrown if the board layout txt file cannot be found.
     * @author Reuben Mulligan
     */
    private void setBoard() throws FileNotFoundException
    {
        File boardFile = this.getFileFromResources("/text/scrabbleBoard.txt"); // Get file
        Scanner scan = new Scanner(boardFile).useDelimiter(" |\n"); // New Scanner object
        String x; // String for reading in

        // Loop over each line and each square in each line
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                x = scan.next();

                // Check to see square type which it matches and set the appropriate type

                if(x.compareTo("3W")==0){
                    this.board[i][j].setType(Square.squareType.TR_WORD);
                }
                else if(x.compareTo("3L") == 0){
                    this.board[i][j].setType(Square.squareType.TR_LETTER);
                }
                else if(x.compareTo("2W") == 0){
                    this.board[i][j].setType(Square.squareType.DB_WORD);
                }
                else if(x.compareTo("2L") == 0){
                    this.board[i][j].setType(Square.squareType.DB_LETTER);
                }
                else if(x.compareTo("s") == 0){
                    this.board[i][j].setType(Square.squareType.STAR);
                }
            }
        }
        System.out.println("Board initialised \n"); // TODO: REMOVE
    }

    /**
     * Getter for the board object.
     * @return square[][] Returns the board which is a 2D array of squares.
     * @author Reuben Mulligan
     */
    public Square[][] getBoard() {
        return this.board;
    }

    /**
     * Getter for the current game status.
     * @return gameStatus Returns the current game status (as an enum).
     * @author Reuben Mulligan
     */
    public gameStatus getStatus() {
        return status;
    }

    /**
     * Setter for updating the current game status.
     * @param status Pass the new game status.
     * @author Reuben Mulligan
     */
    public void setStatus(gameStatus status) {
        this.status = status;
    }

    /**
     * Getter for the array list of words player so far.
     * @return ArrayList Returns the ArrayList of strings (i.e. words) already played on the board.
     * @author Reuben Mulligan
     */
    public ArrayList<String> getWordsPlayed() {
        return wordsPlayed;
    }

    /**
     * Adds a word played to the wordPlayed ArrayList.
     * @param m Pass the word that you want to add.
     * @author Reuben Mulligan
     */
    public void addWordPlayed(Move m)
    {
        this.getWordsPlayed().add(m.getWord());
    }

    // Constructors

    /**
     * Constructor for the Board object.
     * This initialises the board object and then calls setBoard() to read in the board format.
     * @author Reuben Mulligan
     */
    public Board ()
    {
        try
        {
            this.board = new Square[15][15];

            for(int i = 0; i < 15; i++){
                for(int j = 0; j< 15; j++){
                    this.board[i][j] = new Square();
                }
            }
            this.setBoard();
            this.wordsPlayed = new ArrayList<>(); // Initialise wordsPlayed
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.out.println("ERROR: FILE NOT FOUND FOR BOARD INITIALISING.");
        }
    }

    // Core Methods

    /**
     * This method resets the board to its default state.
     * It clears the words played and tiles from the board.
     * @author Reuben Mulligan
     */
    public void resetBoard()
    {
        try
        {
            this.setBoard(); // Clear the board
            this.getWordsPlayed().clear(); // Clear all words played
            this.setStatus(gameStatus.READY); // Reset the game status
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.out.println("ERROR: FILE NOT FOUND FOR BOARD INITIALISING.");
        }
    }
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

                if(board[play.getY()][play.getX()].isOccupied()) // Check the chosen tile is not currently occupied
            }
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

    // Display board (ASCII)
    /**
     * Displays the board in ASCII format to the command line.
     * @author Reuben Mulligan
     */
    public void printBoard()
    {
        for(int i = 0; i < 15; i++)
        {
            for(int j = 0; j < 15; j++)
            {
                System.out.print(board[i][j].toString() + " ");

                if(j == 14)
                {
                    System.out.println();
                }
            }
        }
    }

    // Utility Methods

    /**
     * Method used for accessing files from the Resources directory.
     * Main use: reading in the board layout source text file.
     * @param fileName Pass the file name that you want to get.
     * @return File Returns the File object for the associated file name.
     * @author Reuben Mulligan
     */
    private File getFileFromResources(String fileName){

        URL resource = this.getClass().getResource(fileName);

        if(resource == null)
        {
            throw new IllegalArgumentException("file does not exist");
        }

        else
        {
            return new File(resource.getFile());
        }
    }

    public static void main(String [] args){

    }
}
