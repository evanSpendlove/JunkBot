package scrabbleGame.gameEngine;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import scrabbleGame.exceptions.TileNotFound;
import scrabbleGame.gameModel.*;
import scrabbleGame.UI.components.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/*
    This is the main Scrabble Engine Controller

    This is where all the game control should go.

    TODO - Remove bloat, consolidate methods, integrate JavaFX into existing objects (wrapper methods).
    TODO - Comment + remove prints
 */

/**
 * <h1>ScrabbleEngineController Class</h1>
 * This class is the Main Scrabble Engine Controller.
 * This class contains all of the game controls and runs the game. </br>
 * We utilised a multi controller backend to properly integrate our existing java classes into JavaFX.</br>
 * Team: JunkBotMembers: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Cal Nolan, Reuben Mulligan, Evan Spendlove
 * @version 1.0.0
 * @since 18-03-2020
 */

public class ScrabbleEngineController
{

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
    private TextArea switchPlayerPrompt;

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
    private Pool pool;

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
     * Holds the turn counter
     */
    private int turnCounter = 0;

    /**
     * This method initializes our backend variables and loads the FXML files.
     * It then prints a welcome message to the JavaFX console
     * @throws TileNotFound
     * @author Evan Spendlove
     */

    @FXML
    void initialize() throws TileNotFound
    {
        // Load components into this FXML file

        initialiseBackEnd();
        loadFXMLFiles();

        /*

        // How to dynamically change a stylesheet

        URL url = getClass().getResource("/styling/OGScrabbleTheme.css");

        if(url != null)
        {
            consoleDisplay.getStylesheets().add(url.toString());
        }
        else
        {
            System.out.println("Issue finding stylesheet");
            //throw new NullPointerException("File not found or added as a stylesheet.");
        }
        */
        consoleController.addLineToConsole("Welcome to our Scrabble game! \n To start the game use command Start." +
                " Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit");
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
     * This initialises our backend variables used to store the game
     * @author Evan Spendlove
     */
    private void initialiseBackEnd()
    {


        // Create Game objects

        Pool pool = new Pool();

        Frame p1Frame = new Frame(pool);
        Frame p2Frame = new Frame(pool);

        Player p1 = new Player("player1", 0, p1Frame);
        Player p2 = new Player("player2", 0, p2Frame);

        Board board = new Board();
        board.resetBoard();

        // Store in this instance
        setBoard(board);
        setCurrentFrame(p1Frame);
        setPlayer1(p1);
        setPlayer2(p2);
        setPool(pool);

        currentPlayerNum = 0;
    }


    /**
     * This method implements a delay between player switches as to avoid cheating and stop players from seeing their opponents racks
     * @uses Timer Uses timer to add a delay between turns. Adds a countdown on screen and switches the players after a set time
     * @author Evan Spendlove
     */
    @FXML
    public void switchPlayerDelay()
    {
        //Hide the previous players frame
        currentFrameController.getFramePanes().setVisible(false);

        //Make the switch player prompt visible
        switchPlayerPrompt.setVisible(true);

        //Switch the current player number
        incrementCurrentPlayerNum();

        //Initialise the prompt message
        String message = "PLEASE SWITCH TO PLAYER " + getCurrentPlayerNum() + "\n\n";

        //Use Timer.run to activate the count down and switch the player
        Timer.run(this,5, switchPlayerPrompt, message);
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
                break;
            // If currently 2, transitioning to 1
            case 2:
                setCurrentFrame(getPlayer2().getFrame()); // Update current Frame
                break;
        }

        //If the turn counter is less than 2 then refill the users frame
        //We delay the refilling of frames to accommodate for challenges
        if(turnCounter < 2){
            currentFrameController.refillFrame(getPool());
        }
        //Update the frame controller to contain the new players frame
        currentFrameController.updateFrame(getCurrentFrame()); // Update frame controller
        consoleController.addLineToConsole("------- PLAYER " + getCurrentPlayerNum() + "'S TURN -------"); // Notify player

        //Increase the turn counter
        incrementTurnCounter();
    }

    private int findAdditionalWords(Move m){
        int count;
        Placement plays = m.getPlays().get(0);
        int xCoord = plays.getX();//Set X,Y co-ords to first tile played in move
        int yCoord = plays.getY();
        Square sq = getBoard().getBoard()[yCoord][xCoord];
        ArrayList<Placement> AdditionalWord = new ArrayList<>();
        int scores=0;
        String word="";

        if(m.getDirection()==1){
            for(count=0;count<m.getPlays().size();count++){
                if(getBoard().getBoard()[plays.getY()+1][plays.getX()].isOccupied() || getBoard().getBoard()[plays.getY()-1][plays.getX()].isOccupied()){
                    while(sq.isOccupied()){
                        yCoord++;
                        sq = getBoard().getBoard()[yCoord][xCoord];
                    }
                    while(sq.isOccupied()){
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        word += Character.toString(sq.getTile().character());
                        yCoord--;
                    }
                    scores+=scoring(new Move(AdditionalWord, word, 0));
                    word="";
                    AdditionalWord.clear();
                }
            }
        }
        else{
            for(count=0;count<m.getPlays().size();count++){
                if(getBoard().getBoard()[plays.getY()][plays.getX()+1].isOccupied() || getBoard().getBoard()[plays.getY()][plays.getX()+1].isOccupied()){
                    while(sq.isOccupied()){
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        xCoord++;
                    }
                    xCoord = plays.getX();
                    while(sq.isOccupied()){
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        xCoord--;
                    }
                    scores+=scoring(new Move(AdditionalWord, word, 0));
                    word="";
                    AdditionalWord.clear();
                }
            }
        }
        return scores;
    }

    /**
     * Method to find the score of a played word
     * @param m, the move to be scored
     * @return the total score of the move
     */
    public int scoring(Move m) {
        int letter;//represents the score of each individual tile
        int score = 0;//represents the score of an entire word
        int multi = 1;//represents word multipliers
        Placement tile = m.getPlays().get(0);
        int x = tile.getX();
        int y = tile.getY();
        Square sq = getBoard().getBoard()[y][x];
        checkPreviousSquares(m);
        while (sq.isOccupied()) {
            letter = sq.getTile().value();//for each letter in the word, get it's value, and any special tiles
            switch (sq.getType()) {//Apply letter multipliers to 'letter', and word multipliers to 'multi'
                case DB_LETTER:
                    letter *= 2;
                    break;
                case TR_LETTER:
                    letter *= 3;
                    break;
                case DB_WORD:
                case STAR:
                    multi *= 2;
                    break;
                case TR_WORD:
                    multi *= 3;
                    break;
                default:
                    break;
            }
            score += letter;//add the value of each tile to total word score
            sq.setType(Square.squareType.REGULAR);//Set type of each square to Regular
            if(m.getDirection()==0){//if the word is horizontal, increment x-axis, else increment y
                x++;
            }
            else{
                y++;
            }
            sq = getBoard().getBoard()[y][x];//check the next square on the board
        }
        score *= multi;
        return score;//multiply the total score by the any word multipliers
    }

    /**
     * Method which checks if a placed word is being appended to the end of another word
     * @param m
     */
    private void checkPreviousSquares(Move m){
        int x=m.getPlays().get(0).getX();
        int y=m.getPlays().get(0).getY();

        if(m.getDirection()==0){
            x--;
            Square sq = getBoard().getBoard()[y][x];
            if(sq.isOccupied()) {
                while (sq.isOccupied()) {
                    m.getPlays().add(new Placement(x, y, sq.getTile().character()));
                    x--;
                    sq = getBoard().getBoard()[y][x];
                }
            }
        }
        else{
            y--;
            Square sq = getBoard().getBoard()[y][x];
            if(sq.isOccupied()) {
                while (sq.isOccupied()) {
                    m.getPlays().add(new Placement(x, y, sq.getTile().character()));
                    y--;
                    sq = getBoard().getBoard()[y][x];
                }
            }
        }
    }

    /**
     * method to remove value of tiles in a frame at the end of a game
     * @param p
     * @return total score to be deducted
     */
    public int finalScore(Player p){
        int total=0;
        while(!p.getFrame().isEmpty()){//goes through the frame, adding the scores of each letter to total
            total+=p.getFrame().getTiles().get(0).value();
            p.getFrame().discardTile(p.getFrame().getTiles().get(0));//remove each tile from the frame after getting score
        }
        return total;
    }


    // Public Getters and Private Setters

    /**
     * @return currentPlayerNum
     */
    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }
    /**
     * @return currentFrame
     */
    public Frame getCurrentFrame() {
        return currentFrame;
    }
    /**
     * @param currentFrame
     */
    private void setCurrentFrame(Frame currentFrame) {
        this.currentFrame = currentFrame;
    }
    /**
     * @return board
     */
    public Board getBoard() {
        return board;
    }
    /**
     * @param board
     */
    private void setBoard(Board board) {
        this.board = board;
    }

    /**
     * @return pool
     */
    public Pool getPool() {
        return pool;
    }

    /**
     * @param pool
     */
    private void setPool(Pool pool) {
        this.pool = pool;
    }

    /**
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
     * @return player1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * @param player1
     */
    private void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    /**
     * @return player2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
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
        System.out.println(currentPlayerNum);
    }

    /**
     * Increases the turn counter
     */
    public void incrementTurnCounter(){
        turnCounter++;
    }

    /**
     * @return turnCounter
     */
    public int getTurnCounter() {
        return turnCounter;
    }


}

