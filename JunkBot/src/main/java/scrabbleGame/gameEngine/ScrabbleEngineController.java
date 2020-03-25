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
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
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
     * method to decide which player goes first
     * @return 1 or 2, depending on who goes first
     */
    private int order() {
        Tile tile1 = getPool().draw();//draws 2 random tiles from the pool
        Tile tile2 = getPool().draw();

        while(tile1.character() == tile2.character()){
            //if the two tiles have the same letter, draw 2 new tiles
            tile1 = getPool().draw();
            tile2 = getPool().draw();
        }

        getPool().reset();//reset the pool after all tiles have been drawn

        if (tile1.character() < tile2.character()) {
            //if tile 1 is blank space, or closer to 'A' than tile 2, return 1 - player 1 goes first
            return 1;
        }
        //if tile 2 is blank space, or closer to 'A' than tile 1, return 2 - player 2 goes first
        return 2;
    }

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

    /**
     * method to find additonal words that have been altered by a move, in order to score them
     * @param m
     * @return total score of all such words
     */
    private int findAdditionalWords(Move m){
        int count;
        int xCoord = m.getPlays().get(0).getX();//Set X,Y co-ords to first tile played in move
        int yCoord = m.getPlays().get(0).getY();
        ArrayList<Placement> AdditionalWord = new ArrayList<>();
        int scores=0;
        String word="";
        Square sq = getBoard().getBoard()[yCoord][xCoord];//look at first tile in move

        if(m.getDirection()==0){//check direction of word
            for(count=0;count<m.getPlays().size();count++){//check every tile in move
                if(getBoard().getBoard()[m.getPlays().get(count).getY()+1][m.getPlays().get(count).getX()].isOccupied() || getBoard().getBoard()[m.getPlays().get(count).getY()-1][m.getPlays().get(count).getX()].isOccupied()){
                    //if there is a tile placed directly above or below the tile
                    int yCoord2=yCoord;
                    while(sq.isOccupied()){//go through to the end of the perpendicular word
                        yCoord2--;
                        sq = getBoard().getBoard()[yCoord2][xCoord];
                    }
                    yCoord2--;
                    while(sq.isOccupied()){//when at the end of the word, go through to the other end, adding the tiles to a new move object
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        word += Character.toString(sq.getTile().character());//add letter by letter to new word string
                        yCoord2++;
                        sq = getBoard().getBoard()[yCoord2][xCoord];//increment through word
                    }
                    scores+=calculateScoring(new Move(AdditionalWord, word, 0));//after finding an additional, perpendicular word, have it scored
                    word="";
                    AdditionalWord.clear();
                }
            }
        }
        else{//second half of method is the same as first, for a vertical word
            for(count=0;count<m.getPlays().size();count++){
                if(getBoard().getBoard()[m.getPlays().get(count).getY()][m.getPlays().get(count).getX()+1].isOccupied() || getBoard().getBoard()[m.getPlays().get(count).getY()][m.getPlays().get(count).getX()+1].isOccupied()){
                    int xCoord2=xCoord;
                    while(sq.isOccupied()){
                        xCoord2--;
                        sq = getBoard().getBoard()[yCoord][xCoord2];
                    }
                    xCoord2++;
                    while(xCoord2<xCoord){
                        AdditionalWord.add(new Placement(xCoord2, yCoord, sq.getTile().character()));
                        word += Character.toString(sq.getTile().character());
                        xCoord2++;
                        sq = getBoard().getBoard()[yCoord][xCoord2];
                    }
                    scores+=calculateScoring(new Move(AdditionalWord, word, 0));
                    word="";
                    AdditionalWord.clear();
                }
            }
        }
        return scores;
    }

    /**
     * Method to call other scoring methods
     * @param m
     * @return total score of played move
     */
    public int scoring(Move m){
        if(m.getPlays().size()==7){
            return calculateScoring(m)+findAdditionalWords(m)+50;
        }
        return calculateScoring(m)+findAdditionalWords(m);
    }

    /**
     * Method to find the score of a played word
     * @param m, the move to be scored
     * @return the total score of the move
     */
    private int calculateScoring(Move m) {
        int letter;//represents the score of each individual tile
        int score = 0;//represents the score of an entire word
        int multi = 1;//represents word multipliers
        Placement tile = m.getPlays().get(0);
        int x = tile.getX();
        int y = tile.getY();
        Square sq = this.board.getBoard()[y][x];
        checkSurroundingSquares(m);
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
     * Method which checks if a placed word is being appended to the end or start of another word
     * @param m
     */
    private void checkSurroundingSquares(Move m){
        int xCoord=m.getPlays().get(0).getX();
        int yCoord=m.getPlays().get(0).getY();

        if(m.getDirection()==0){//if word is horizontal
            xCoord--;//check square directly before first tile in move
            Square sq = getBoard().getBoard()[yCoord][xCoord];
            if(sq.isOccupied()) {//if it's occupied
                while(sq.isOccupied()){//go back through the tiles until a blank is found, adding each tile to move
                    m.getPlays().add(new Placement(xCoord, yCoord, sq.getTile().character()));
                    xCoord--;
                    sq = getBoard().getBoard()[yCoord][xCoord];
                }
            }
            xCoord=m.getPlays().get(m.getPlays().size()-1).getX()+1;//set xCoord to first tile after move
            sq = getBoard().getBoard()[yCoord][xCoord];
            while(sq.isOccupied()){//if occupied, go though tiles until an empty square is found - add all tiles to move
                m.getPlays().add(new Placement(xCoord, yCoord, sq.getTile().character()));
                xCoord++;
                sq = getBoard().getBoard()[yCoord][xCoord];
            }
        }
        else{//Same as previously, but for a vertical word
            yCoord--;
            Square sq = getBoard().getBoard()[yCoord][xCoord];
            if(sq.isOccupied()) {
                while (sq.isOccupied()) {
                    m.getPlays().add(new Placement(xCoord, yCoord, sq.getTile().character()));
                    yCoord--;
                    sq = getBoard().getBoard()[yCoord][xCoord];
                }
            }
            yCoord=m.getPlays().get(m.getPlays().size()-1).getY()+1;
            sq = getBoard().getBoard()[yCoord][xCoord];
            while(sq.isOccupied()){
                m.getPlays().add(new Placement(xCoord, yCoord, sq.getTile().character()));
                yCoord++;
                sq = getBoard().getBoard()[yCoord][xCoord];
            }
        }
    }

    /**
     * method to remove value of tiles in a frame at the end of a game
     * @param f
     * @return total score to be deducted
     */
    public int finalScore(Frame f){
        int total=0;
        while(!f.isEmpty()){//goes through the frame, adding the scores of each letter to total
            total+=f.getTiles().get(0).value();
            f.discardTile(f.getTiles().get(0));//remove each tile from the frame after getting score
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

