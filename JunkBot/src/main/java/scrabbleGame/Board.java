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

    /**
     * Method to check if the first move being made is valid.
     * Checks if it's a valid word, whether the player has the necessary tiles, and whether the word contains the central tile.
     * @param m Pass the move that you want to check.
     * @param p Pass the player who made the move.
     * @return Returns a boolean indicating if its a valid move or not.
     * @author Cal Nolan
     */
    private boolean checkFirstMove(Move m, Player p)
    {
        boolean containsStar = false;
        Placement q;

        if(!wordsPlayed.isEmpty())  { return false; }

        for(int x = 0; x < m.plays.size() && containsStar == false; x++) // For each play
        {
            q = m.plays.get(x);

            if(q.getX() == 7 && q.getY() == 7) // Checks whether any of the tiles in the word are in the middle co-ordinate, {7, 7}
            {
                containsStar = true; // Update boolean
            }
        }

        return containsStar && containsRequiredLetters(m, p.getFrame()) && isValidPosition(m);
    }

    /**
     * Method to ensure that a move is valid.
     * Checks that the player has the necessary tiles, and that a word can go in the relevant spaces
     * @param m Pass the move that you want to check.
     * @param p Pass the player who made the move.
     * @return boolean Returns a boolean which indicates if the move is valid.
     * @author Cal Nolan
     */
    private boolean checkValidMove(Move m, Player p)
    {
        return containsRequiredLetters(m, p.getFrame()) && checkValidPlacement(m);
    }

    /**
     * Checks that all of the letters in a word are in the relevant players Frame.
     * @param m Pass the move that you want to check.
     * @param f Pass the frame of the player who made the move.
     * @return boolean Returns a boolean indicating if the frame contains the required letters for the move.
     * @author Cal Nolan
     */
    private boolean containsRequiredLetters(Move m, Frame f)
    {
        for(int x=0;x<m.plays.size();x++)
        {
            //Checks whether each of the tiles in the word exist in the frame
            if(!f.containsTile(m.plays.get(x).getLetter()))
            {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks that all of the tiles are placed along one axis, consistent with the direction indicated.
     * @param m Pass the move that you wish to verify.
     * @return boolean Returns true if the word is inline with the direction stated, else false.
     * @author Cal Nolan
     */
    private boolean inLine(Move m)
    {
        if(m.getDirection() == 0) // Horizontal, so Y is constant
        {
            for(int i = 0; i < m.getPlays().size(); i++) // For each play
            {
                if(m.getPlays().get(i).getY() != m.getPlays().get(0).getY()) // Check the Y is inline
                {
                    return false;
                }
            }
        }
        else // Vertical, so x is constant
        {
            for(int i = 0; i < m.getPlays().size(); i++) // For each play
            {
                if(m.getPlays().get(i).getX() != m.getPlays().get(0).getX()) // Check the X is inline
                {
                    return false;
                }
            }
        }

        return isConnected(m);
    }


    /**
     * Checks that each tile to be placed is connected (i.e. not more than one unit in x or y away from the previous) to the previous tile.
     * Also handles hooks by bypassing that letter.
     * @param m Pass the move that you wish to verify.
     * @return boolean Returns true if the word is connected, else false.
     * @author Cal Nolan
     */
    private boolean isConnected(Move m)
    {
        List<Placement> hook = getHook(m);

        // If hooked
        if(hook != null)
        {
            int targetValue = 0;
            int actualValue = 0;
            int hookOffset = 0;

            // If horizontal
            if(m.getDirection() == 0)
            {
                for(int i = 0; i < m.getPlays().size(); i++)
                {
                    actualValue = m.getPlays().get(i).getX();
                    targetValue = m.getPlays().get(0).getX() + i;

                    // First, check if we are looking at the hook
                    while(targetValue == hook.get(hookOffset).getX())
                    {
                        hookOffset += 1; // Bypass this check
                    }

                    targetValue += hookOffset;

                    hookOffset=0;

                    if(actualValue != targetValue)
                    {
                        return false;
                    }
                }
            }
            else // Vertical
            {
                for(int i = 0; i < m.getPlays().size(); i++)
                {
                    actualValue = m.getPlays().get(i).getY();
                    targetValue = m.getPlays().get(0).getY() + i;

                    // First, check if we are looking at the hook
                    while(targetValue == hook.get(hookOffset).getY())
                    {
                        hookOffset += 1; // Bypass this check
                    }

                    targetValue += hookOffset;

                    hookOffset=0;

                    if(actualValue != targetValue)
                    {
                        return false;
                    }
                }
            }
        }
        else
        {
            if(m.getDirection() == 0) // If horizontal
            {
                for(int i = 0; i < m.getPlays().size(); i++)
                {
                    if(m.getPlays().get(i).getX() != m.getPlays().get(0).getX() + i)
                    {
                        return false;
                    }
                }
            }
            else // Vertical
            {
                for(int i = 0; i < m.getPlays().size(); i++)
                {
                    if(m.getPlays().get(i).getY() != m.getPlays().get(0).getY() + i)
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Method to check that a placed word is connected to another word, if it isn't the first word played.
     * Checks the direction of the word, and if there are any tiles beside, after , or before it.
     * @param m Pass the move for which you want to find the hook.
     * @return Placement Returns null if an error occurred, else returns the Placement of the hook (coordinates and letter).
     * @author Cal Nolan
     */
    private List<Placement> getHook(Move m) {
        // Variables for storing coordinates and value of hook
        int x = -1, y = -1, count = 0;
        char c;
        List<Placement> q = new ArrayList<>();

        for (int i = 0; i < m.getPlays().size() - 1; i++) {
            int difX = m.getPlays().get(i + 1).getX() - m.getPlays().get(i).getX(); // Check X
            int difY = m.getPlays().get(i + 1).getY() - m.getPlays().get(i).getY(); // Check Y

            if (difX > 1) {
                for (count = 0; count < difX; count++) {
                    x = m.getPlays().get(i).getX() + 1;
                    y = m.getPlays().get(i).getY();

                    c = board[y][x].tile.character();
                    q.add(new Placement(x, y, c));
                }
            } else if (difY > 1) {
                for (count = 0; count < difX; count++) {
                    x = m.getPlays().get(i).getX();
                    y = m.getPlays().get(i).getY() + 1;

                    c = board[y][x].tile.character();
                    q.add(new Placement(x, y, c));
                }
            }

            if (x == -1 && y == -1) {
                return null; // No hook
            }

            for (count = 0; count < q.size(); count++) {

                if (!board[q.get(count).getY()][q.get(count).getX()].isOccupied()) {
                    return null;
                }
            }
        }
        return q;
    }


    /**
    * Method to handle the first word placed in a game.
    * Calls checkFirstMove to check if the word is valid, and then adds the word to the board.
    * @param m Pass the move that you wish to place on the board.
    * @param p Pass the player who made the move so their score can be updated.
    * @return int Returns 2 if the move is successfully placed (also valid), and -1 if not placed.
    * @author Cal Nolan
    */
    public int placeFirstWord(Move m, Player p){
        // If checkFirstMove returns true, the word can be played.
        if(checkFirstMove(m, p))
        {
            addWordToBoard(m, p);
            return 2;
        }
        else {
            return -1; // Return error code since this isn't a valid placement
        }
    }


    /**
     * Method to add a word to the board
     * Calculates score, and updates the squares to have tiles placed on them
     * @param m Pass the move that you wish to place on the board.
     * @param p Pass the player who made the move so their score can be updated.
     * @author Cal Nolan
     */
    private void addWordToBoard(Move m, Player p){
        calculateScore(m)

        // Set each tile played on the relevant square, and set each tile to REGULAR type

        for(int x = 0; x < m.plays.size(); x++)
        {
            Placement q = m.plays.get(x);
            board[q.getY()][q.getX()].setTile(Tile.getInstance(q.getLetter()));
            board[q.getY()][q.getX()].setType(Square.squareType.REGULAR);
        }

        wordsPlayed.add(m.getWord());
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
        System.out.println("ree");
    }
}
