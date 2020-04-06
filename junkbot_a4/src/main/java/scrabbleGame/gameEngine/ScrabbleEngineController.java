package scrabbleGame.gameEngine;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import scrabbleGame.UI.components.BoardController;
import scrabbleGame.UI.components.ConsoleController;
import scrabbleGame.UI.components.FrameController;
import scrabbleGame.UI.components.Timer;
import scrabbleGame.UI.utilityPanes.ImageViewPane;
import scrabbleGame.UI.utilityPanes.SquarePane;
import scrabbleGame.gameModel.*;

import java.net.URL;
import java.util.ArrayList;

/**
 * <h1>ScrabbleEngineController Class</h1>
 * This class is the Main Scrabble Engine Controller.
 * This class contains all of the game controls and runs the game. </br>
 * We utilised a multi controller backend to properly integrate our existing java classes into JavaFX.</br>
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Cal Nolan, Reuben Mulligan, Evan Spendlove
 * @version 1.0.0
 * @since 18-03-2020
 */

public class ScrabbleEngineController
{
    // Constants
    public static final boolean USING_THEMED_BOARD = false;

    // Controllers

    /**
     * Holds the Frame controller
     */
    @FXML
    public FrameController currentFrameController;

    /**
     * Holds the console controller
     */
    @FXML
    public ConsoleController consoleController;

    /**
     * Holds the Board controller
     */
    @FXML
    public BoardController boardController;

    // Components

    @FXML
    private GridPane boardYLabelLeft;

    @FXML
    private GridPane boardYLabelRight;

    @FXML
    private GridPane boardXLabelTop;

    @FXML
    private GridPane boardXLabelBottom;

    @FXML
    private TextArea ScoreTextArea;

    @FXML
    private Pane boardBackgroundPane;

    @FXML
    private Pane boardBackgroundImage;

    /**
     * Holds the frame border pane
     */
    @FXML
    private BorderPane frameBorder;

    /**
     * Holds the Board border pane
     */
    @FXML
    private BorderPane boardBorder;

    /**
     * Holds the Console border pane
     */
    @FXML
    private BorderPane consoleBorder;

    /**
     * Holds the switchPlayerPrompt Text area
     */
    @FXML
    public TextArea switchPlayerPrompt;

    // Back-End Objects

    /**
     * Holds the current frame object
     */
    private Frame currentFrame;

    /**
     * Holds the current board object
     */
    private Board board;

    /**
     * Holds the current pool object
     */
    private Pool pool = new Pool();

    /**
     * Holds the player1 Player object
     */
    private Player player1;

    /**
     * Holds the player2 Player object
     */
    private Player player2;

    /**
     * Holds the current player number
     */
    private int currentPlayerNum;

    /**
     * Holds the dictionary
     */
    private Lexicon dictionary;

    /**
     * Holds the turn counter
     */
    private int turnCounter = 0;

    /**
     * This method initializes our backend variables and loads the FXML files.
     * It then prints a welcome message to the JavaFX console
     * @author Evan Spendlove
     */
    @FXML
    void initialize()
    {
        // Load components into this FXML file

        initialiseBackEnd();
        loadFXMLFiles();

        // Identify which player goes first

        int turnChoice = order();

        if(turnChoice == 2)
        {
            incrementCurrentPlayerNum();
        }

        consoleController.addLineToConsole("Welcome to our Scrabble game!\nTo start the game use command Start." +
                " Before you do this, please enter usernames for each player using the format: Username <name> <playerNumber>\n" +
                "To quit, use command <quit>");
    }

    /**
     * This loads the FXML files in for all the controllers.
     * @author Evan Spendlove
     */
    private void loadFXMLFiles()
    {
        try
        {
            // Load the Frame FXML

            FXMLLoader frameLoader = new FXMLLoader(getClass().getResource("/view/frame.fxml"));

            frameBorder.setCenter(frameLoader.load());

            currentFrameController = frameLoader.getController();

            currentFrameController.setScrabbleEngineController(this);

            // Load the Board FXML

            FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("/view/board.fxml"));

            boardBorder.setCenter(boardLoader.load());

            boardController = boardLoader.getController();

            // Load board outline

            loadBoardOutline();

            if(this.USING_THEMED_BOARD)
            {
                // Load image to imageView

                URL image = this.getClass().getResource("/assets/boardTest.png");

                ImageViewPane ivPane = new ImageViewPane();
                ivPane.setPrefHeight(600);
                ivPane.setPrefWidth(600);
                ivPane.setImageView(new ImageView(new Image(image.toExternalForm())));

                boardBackgroundPane.getChildren().set(0, ivPane);
                // Object temp = boardBackgroundPane.getChildren().get(0);


                //boardBackgroundImage.setImage(new Image(image.toExternalForm()));
            }

            boardController.updateBoard(this.board);

            // Load the Console FXML

            FXMLLoader consoleLoader = new FXMLLoader(getClass().getResource("/view/console.fxml"));

            consoleBorder.setCenter(consoleLoader.load());

            consoleController = consoleLoader.getController();

            consoleController.setScrabbleEngineController(this);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     *  Method to load the outline of the board (1,..., 15 and A,...).
     */
    private void loadBoardOutline()
    {
        char c = 'a';
        // For the 15 squares, add the letters A - 0.
        for(int i = 0; i < 15; i++)
        {
            SquarePane sp = new SquarePane();
            sp.updateSquare(new Square(), Character.toString(c).toUpperCase());
            c++;
            boardXLabelTop.add(sp, i, 0);
        }

        c = 'a';

        // For the 15 squares, add the letters A - 0.
        for(int i = 0; i < 15; i++)
        {
            SquarePane sp = new SquarePane();
            sp.updateSquare(new Square(), Character.toString(c).toUpperCase());
            c++;
            boardXLabelBottom.add(sp, i, 0);
        }

        // For the 15 squares, add the number 1 - 15.
        for(int i = 0; i < 15; i++)
        {
            SquarePane sp = new SquarePane();
            sp.updateSquare(new Square(), Integer.toString(i+1));
            boardYLabelLeft.add(sp, 0, i);
        }

        // For the 15 squares, add the number 1 - 15.
        for(int i = 0; i < 15; i++)
        {
            SquarePane sp = new SquarePane();
            sp.updateSquare(new Square(), Integer.toString(i+1));
            boardYLabelRight.add(sp, 0, i);
        }
    }

    /**
     * Method to decide which player goes first.
     * @return 1 or 2, depending on who goes first.
     */
    private int order() {
        Tile tile1 = getPool().draw(); // Draws 2 random tiles from the pool
        Tile tile2 = getPool().draw(); // Draws 2 random tiles from the pool

        while(tile1.character() == tile2.character()){
            //if the two tiles have the same letter, draw 2 new tiles
            tile1 = getPool().draw();
            tile2 = getPool().draw();
        }

        getPool().reset();//reset the pool after all tiles have been drawn

        if (tile1.character() < tile2.character())
        {
            // If tile 1 is blank space, or closer to 'A' than tile 2, return 1 - player 1 goes first
            return 1;
        }
        // If tile 2 is blank space, or closer to 'A' than tile 1, return 2 - player 2 goes first
        return 2;
    }

    /**
     * This initialises our backend variables used to store the game
     * @author Evan Spendlove
     */
    private void initialiseBackEnd()
    {
        // Create Game objects

        Pool pool = new Pool();

        Frame p1Frame = new Frame(pool);
        Frame p2Frame = new Frame(pool);

        Player p1 = new Player("Player 1", 0, p1Frame);
        Player p2 = new Player("Player 2", 0, p2Frame);

        Board board = new Board();
        board.resetBoard();

        Lexicon dict = new Lexicon();

        // Store in this instance
        setBoard(board);
        setCurrentFrame(p1Frame);
        setPlayer1(p1);
        setPlayer2(p2);
        setPool(pool);
        setDictionary(dict);
        currentPlayerNum = 2;
        updateScore();
    }

    /**
     * This method switches the players, it is a nested method of switchPlayerDelay
     * It switches the instance variables around, refills the player frame if its not the start of the game.
     * It then updates the frame on the board and prints a message
     * @author Evan Spendlove
     */
    public void switchPlayer()
    {
        switch(getCurrentPlayerNum())
        {
            // If currently 1, transitioning to 2
            case 1:
                setCurrentFrame(getPlayer1().getFrame()); // Update current Frame
                currentFrameController.updateFrame(getPlayer1().getFrame());
                break;
            // If currently 2, transitioning to 1
            case 2:
                setCurrentFrame(getPlayer2().getFrame()); // Update current Frame
                currentFrameController.updateFrame(getPlayer2().getFrame());
                break;
        }

        // If the turn counter is less than 2 then refill the users frame
        // We delay the refilling of frames to accommodate for challenges
        if(turnCounter != 0)
        {
            currentFrameController.refillFrame(getPool());
        }

        // Update the frame controller to contain the new players frame
        currentFrameController.updateFrame(getCurrentFrame()); // Update frame controller
        consoleController.addLineToConsole("------- PLAYER " + getCurrentPlayerNum() + "'S TURN -------"); // Notify player

        // Increase the turn counter
        incrementTurnCounter();
    }

    /**
     * This method implements a delay between player switches as to avoid cheating and stop players from seeing their opponents racks.
     * @uses Timer Uses timer to add a delay between turns. Adds a countdown on screen and switches the players after a set time.
     * @author Evan Spendlove
     */
    public void switchPlayerDelay()
    {
        // Hide the current player's frame and show the prompt
        currentFrameController.getFramePanes().setVisible(false);
        switchPlayerPrompt.setVisible(true);
        incrementCurrentPlayerNum();

        // Need to prompt the user to switch

        String username = getPlayer1().getUsername().toUpperCase();

        if(getCurrentPlayerNum() == 2)
        {
            username = getPlayer2().getUsername().toUpperCase();
        }

        String message = "\n\n\n\nPLEASE SWITCH TO " + username + "\n\n";
        Timer.run(this,3, switchPlayerPrompt, message);
    }

    /**
     * Method to update the username of a Player both in the Player object and on the board.
     * @param player Pass the player number to be updated.
     * @param username Pass the desired username to be set.
     * @author Evan Spendlove
     */
    public void updateUsername(int player, String username)
    {
        // Update the username based on the argument
        switch(player)
        {
            case 1:
                getPlayer1().setUsername(username);
                break;
            case 2:
                getPlayer2().setUsername(username);
                break;
        }

        updateScore();
    }

    /**
     * Method to update the graphical representation of the score displayed on the game window.
     * @author Evan Spendlove
     */
    public void updateScore()
    {
        ScoreTextArea.clear();
        ScoreTextArea.setText(getPlayer1().getUsername() + "\t| " + getPlayer1().getScore() +  " \t|||\t" + getPlayer2().getUsername() + "\t| " + getPlayer2().getScore());
    }

    /**
     * Method to find additional words that have been altered by a move, in order to score them.
     * @param m Pass the move for which additional words are to be found.
     * @return Total score of all such words.
     */
    private int findAdditionalWords(Move m)
    {
        ArrayList<Placement> AdditionalWord = new ArrayList<>();
        int scores=0;

        if(m.getDirection() == 0) // If horizontal
        {
            for(int i = 0; i < m.getPlays().size(); i++) // For each play
            {
                int xCoord = m.getPlays().get(i).getX();
                int yCoordUp = m.getPlays().get(i).getY()-1;
                int yCoordDown = m.getPlays().get(i).getY()+1;

                Square sq = getBoard().getBoard()[yCoordUp][xCoord];

                // Search for letters surrounding it
                while(sq.isOccupied()){
                    AdditionalWord.add(new Placement(xCoord,yCoordUp,sq.getTile().character())); // Add to ArrayList
                    yCoordUp--; // Decrement counter to move up
                    sq = getBoard().getBoard()[yCoordUp][xCoord]; // Update sq
                }

                AdditionalWord.add(m.getPlays().get(i)); // Add current play
                sq = getBoard().getBoard()[yCoordDown][xCoord]; // Update sq

                // Search for letters surrounding it
                while(sq.isOccupied()){
                    AdditionalWord.add(new Placement(xCoord,yCoordDown,sq.getTile().character())); // Add to ArrayList
                    yCoordDown++; // Increment counter moving down
                    sq = getBoard().getBoard()[yCoordDown][xCoord]; // Update sq
                }

                if(AdditionalWord.size() != 1) // If not a single letter, i.e. just that letter placement as part of the move
                {
                    addWordToLastWordsPlayed(AdditionalWord); // Update LastWordsPlayed

                    // Score word
                    for(int j = 0; j<AdditionalWord.size(); j++){
                        int newY = AdditionalWord.get(j).getY();
                        int newX = AdditionalWord.get(j).getX();
                        scores += getBoard().getBoard()[newY][newX].getTile().value();
                    }
                }

                AdditionalWord.clear();
            }
        }
        else {
            for(int i = 0; i < m.getPlays().size(); i++)
            {
                int xCoordLeft = m.getPlays().get(i).getX() - 1;
                int xCoordRight = m.getPlays().get(i).getX() + 1;
                int yCoord = m.getPlays().get(i).getY();
                Square sq = getBoard().getBoard()[yCoord][xCoordLeft];
                while(sq.isOccupied()){
                    AdditionalWord.add(new Placement(xCoordLeft, yCoord, sq.getTile().character()));
                    xCoordLeft--;
                    sq = getBoard().getBoard()[yCoord][xCoordLeft];
                }
                AdditionalWord.add(m.getPlays().get(i));
                sq = getBoard().getBoard()[yCoord][xCoordRight];
                while(sq.isOccupied()){
                    AdditionalWord.add(new Placement(xCoordRight, yCoord, sq.getTile().character()));
                    xCoordRight++;
                    sq = getBoard().getBoard()[yCoord][xCoordRight];
                }

                if(AdditionalWord.size() != 1)
                {

                    addWordToLastWordsPlayed(AdditionalWord);

                    for(int j = 0; j<AdditionalWord.size(); j++){
                        int newY = AdditionalWord.get(j).getY();
                        int newX = AdditionalWord.get(j).getX();
                        scores += getBoard().getBoard()[newY][newX].getTile().value();
                    }
                }

                AdditionalWord.clear();
            }
        }
        return scores;
    }

    /**
     * Method to call other scoring methods.
     * @param m Pass the move to be scored.
     * @return total score of played move.
     */
    public int scoring(Move m)
    {
        Move original = m; // Original move

        int check = findAdditionalWords(m);
        check += calculateScoring(original);

        if(m.isBingo()) // If bingo, add 50 points to check
        {
            check += 50;
        }

        consoleController.setLastMoveScore(check); // Update lastMoveScore

        return check;
    }

    /**
     * Method to find the score of a played word
     * @param m, the move to be scored
     * @return the total score of the move
     */
    private int calculateScoring(Move m)
    {
        int playPtr = 0;
        Boolean flag = true;
        int letter = 0;//represents the score of each individual tile
        int score = 0;//represents the score of an entire word
        int multi = 1;//represents word multipliers
        Placement tile = m.getPlays().get(playPtr);
        int x = tile.getX();
        int y = tile.getY();
        Square sq = this.board.getBoard()[y][x];
        checkSurroundingSquares(m);

        while (flag)
        {
            letter = sq.getTile().value();//for each letter in the word, get it's value, and any special tiles

            switch (sq.getType()) {//Apply letter multipliers to 'letter', and word multipliers to 'multi'
                case DB_LETTER:
                    letter *= 2;
                    break;
                case TR_LETTER:
                    letter *= 3;
                    break;
                case DB_WORD:
                    multi = 2;
                    break;
                case STAR:
                    multi = 2;
                    break;
                case TR_WORD:
                    multi = 3;
                    break;
            }

            score += letter;//add the value of each tile to total word score
            sq.setType(Square.squareType.REGULAR);//Set type of each square to Regular
            if(playPtr < m.getPlays().size()-1){
                playPtr++;
            }
            else{
                flag = false;
            }
            tile = m.getPlays().get(playPtr);
            x = tile.getX();
            y= tile.getY();
            sq = getBoard().getBoard()[y][x];//check the next square on the board
        }

        score *= multi;

        return score;//multiply the total score by the any word multipliers
    }

    /**
     * Method which checks if a placed word is being appended to the end or start of another word
     * @param m Pass the move to be checked
     */
    private void checkSurroundingSquares(Move m){
        int xCoord=m.getPlays().get(0).getX();
        int yCoord=m.getPlays().get(0).getY();
        int xCoord2 = xCoord;
        int yCoord2 = yCoord;
        Boolean flag = false;

        if(m.getDirection()==0){ // If word is horizontal
            xCoord2--; // Check square directly before first tile in move
            Square sq = getBoard().getBoard()[yCoord][xCoord2];
            if(sq.isOccupied()) {//if it's occupied
                while(sq.isOccupied()){//go back through the tiles until a blank is found, adding each tile to move
                    m.getPlays().add(new Placement(xCoord2, yCoord, sq.getTile().character()));
                    xCoord2--;
                    sq = getBoard().getBoard()[yCoord][xCoord2];
                }
            }
            xCoord++;
            sq = getBoard().getBoard()[yCoord][xCoord];
            while(sq.isOccupied()){ // If occupied, go though tiles until an empty square is found - add all tiles to move
                Placement temp = new Placement(xCoord, yCoord, sq.getTile().character());

                for(int i = 0; i < m.getPlays().size(); i++)
                {
                    if(m.getPlays().get(i).getX() == temp.getX() && m.getPlays().get(i).getY() == temp.getY() && m.getPlays().get(i).getLetter() == temp.getLetter()){
                       flag = true;
                    }
                }

                if(!flag)
                {
                    m.getPlays().add(temp);
                }
                else
                {
                    flag = false;
                }

                xCoord++;
                sq = getBoard().getBoard()[yCoord][xCoord];
            }
        }
        else
        {
            //Same as previously, but for a vertical word
            yCoord2--;
            Square sq = getBoard().getBoard()[yCoord2][xCoord];

            if(sq.isOccupied()) {
                while (sq.isOccupied()) {
                    m.getPlays().add(new Placement(xCoord, yCoord2, sq.getTile().character()));
                    yCoord2--;
                    sq = getBoard().getBoard()[yCoord2][xCoord];
                }
            }
            yCoord++;
            sq = getBoard().getBoard()[yCoord][xCoord];

            while(sq.isOccupied()){
                Placement temp = new Placement(xCoord, yCoord, sq.getTile().character());
                for(int i = 0; i < m.getPlays().size(); i++){
                    if(m.getPlays().get(i).getX() == temp.getX() && m.getPlays().get(i).getY() == temp.getY() && m.getPlays().get(i).getLetter() == temp.getLetter()){
                        flag = true;
                    }
                }
                if(!flag){
                    m.getPlays().add(temp);
                }else{
                    flag = false;
                }
                yCoord++;
                sq = getBoard().getBoard()[yCoord][xCoord];
            }
        }
    }

    /**
     * Method to calculate value of tiles in a frame at the end of a game.
     * @param f Pass the frame for which the final deduction is to be calculated.
     * @return Total score to be deducted.
     */
    public int finalScore(Frame f){
        int total=0;
        while(!f.isEmpty()){//goes through the frame, adding the scores of each letter to total
            total+=f.getTiles().get(0).value();
            f.discardTile(f.getTiles().get(0));//remove each tile from the frame after getting score
        }
        return total;
    }

    /**
     * Method to add a word to the lastWordsPlayed ArrayList when a move is complete.
     * @param plays Pass the ArrayList of placements that make up the word
     */
    private void addWordToLastWordsPlayed(ArrayList<Placement> plays)
    {
        String word = "";

        for(int i = 0; i < plays.size(); i++) // Creates the word from the move
        {
            word += plays.get(i).getLetter();
        }

        consoleController.updateLastWordsPlayed(word); // Updates LastWordsPlayed
    }

    // Public Getters and Private Setters

    /**
     * Getter for current player number.
     * @return currentPlayerNum
     */
    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }
    /**
     * Getter for current Frame
     * @return currentFrame
     */
    public Frame getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Setter for current Frame
     * @param currentFrame
     */
    private void setCurrentFrame(Frame currentFrame) {
        this.currentFrame = currentFrame;
    }

    /**
     * Getter for board object
     * @return board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Setter for board object.
     * @param board
     */
    private void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Getter for Pool object.
     * @return pool
     */
    public Pool getPool() {
        return pool;
    }

    /**
     * Setter for Pool object.
     * @param pool
     */
    private void setPool(Pool pool) {
        this.pool = pool;
    }

    /**
     * Get player by their number (1 or 2).
     * @param playerNum
     * @return player
     */
    public Player getPlayer(int playerNum){
        if(playerNum == 1){
            return player1;
        }else{
            return player2;
        }
    }

    /**
     * Getter for Player 1
     * @return player1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Setter for Player 1
     * @param player1
     */
    private void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    /**
     * Getter for Player 1
     * @return player2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Setter for Player 2
     * @param player2
     */
    private void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    /**
     * Switches the player number
     * @author Evan Spendlove
     */
    public void incrementCurrentPlayerNum()
    {
        if(currentPlayerNum == 1)
        {
            currentPlayerNum++;
        }
        else
        {
            currentPlayerNum = 1;
        }
    }

    /**
     * Increases the turn counter
     */
    public void incrementTurnCounter(){
        turnCounter++;
    }

    /**
     * Getter for turn counter.
     * @return turnCounter
     */
    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Getter for Dictionary
     * @return Lexicon Returns the dictionary.
     */
    public Lexicon getDictionary() {
        return dictionary;
    }

    /**
     * Setter for the dictionary.
     * @param dictionary Pass the dictionary to be set.
     */
    public void setDictionary(Lexicon dictionary) {
        this.dictionary = dictionary;
    }
}

