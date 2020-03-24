package scrabbleGame.gameEngine;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import scrabbleGame.exceptions.TileNotFound;
import scrabbleGame.gameModel.*;
import scrabbleGame.UI.components.*;

import java.net.URL;
import java.util.Arrays;

/*
    This is the main Scrabble Engine Controller

    This is where all the game control should go.

    TODO - Remove bloat, consolidate methods, integrate JavaFX into existing objects (wrapper methods).
    TODO - Comment + remove prints
 */

public class ScrabbleEngineController
{

    // Controllers

    @FXML
    public FrameController currentFrameController;

    @FXML
    public ConsoleController consoleController;

    @FXML
    public BoardController boardController;

    // Components

    @FXML
    private BorderPane frameBorder;

    @FXML
    private BorderPane boardBorder;

    @FXML
    private BorderPane consoleBorder;

    @FXML
    private TextArea switchPlayerPrompt;

    // Back-End Objects

    private Frame currentFrame;

    private Board board;

    private Pool pool;

    private Player player1;

    private Player player2;

    private int currentPlayerNum;

    private int turnCounter = 0;

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

        // Testing

        /*
        Frame testFrame = new Frame();
        testFrame.refillFrame(new Pool());

        System.out.println("Test frame: " + testFrame);

        currentFrameController.updateFrame(testFrame);

        System.out.println("FC_Rack: " + Arrays.toString(currentFrameController.getRack()));
        System.out.println("FC_RackPanes: " + Arrays.toString(currentFrameController.getFramePanes().getChildren().toArray()));

        Board testBoard = new Board();
        // testBoard.resetBoard();
        boardController.updateBoard(testBoard);

        boardController.addTiletoBoard(this.currentFrameController, 1, 6, 8);
        boardController.addTiletoBoard(this.currentFrameController, 0, 7, 8);
        boardController.removeTileFromBoard(7, 8);

        boardController.updateBoard(board);
        currentFrameController.getFramePanes().setStyle("-fx-background-color: purple");
         */
        consoleController.addLineToConsole("Welcome to our Scrabble game! \n To start the game use command Start," +
                " before you do this, please enter usernames for each player by typing a name then either 1 or 2 (e.g. Reuben 1)\n" +
                "To quit, use command Quit");
    }

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
        currentPlayerNum = 2;
    }

    // Switch Player
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
        if(turnCounter != 0){
            currentFrameController.refillFrame(getPool());
        }
        currentFrameController.updateFrame(getCurrentFrame()); // Update frame controller
        consoleController.addLineToConsole("------- PLAYER " + getCurrentPlayerNum() + "'S TURN -------"); // Notify player
        incrementTurnCounter();
    }

    @FXML
    public void switchPlayerDelay()
    {
        currentFrameController.getFramePanes().setVisible(false);
        switchPlayerPrompt.setVisible(true);
        incrementCurrentPlayerNum();
        String message = "PLEASE SWITCH TO PLAYER " + getCurrentPlayerNum() + "\n\n";
        Timer.run(this,1, switchPlayerPrompt, message);
    }


    // Public Getters and Private Setters

    public int getCurrentPlayerNum() {
        return currentPlayerNum;
    }

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

    public Frame getCurrentFrame() {
        return currentFrame;
    }

    private void setCurrentFrame(Frame currentFrame) {
        this.currentFrame = currentFrame;
    }

    public Board getBoard() {
        return board;
    }

    private void setBoard(Board board) {
        this.board = board;
    }

    public Pool getPool() {
        return pool;
    }

    private void setPool(Pool pool) {
        this.pool = pool;
    }

    public Player getPlayer(int playerNum){
        if(playerNum == 1){
            return player1;
        }else{
            return player2;
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    private void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    private void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void incrementTurnCounter(){
        turnCounter++;
    }

    public int getTurnCounter() {
        return turnCounter;
    }
}

