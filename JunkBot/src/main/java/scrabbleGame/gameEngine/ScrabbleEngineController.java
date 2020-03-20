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

    @FXML
    void initialize() throws TileNotFound {
        // Load components into this FXML file

        loadFXMLFiles();
        initialiseBackEnd();

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

        currentFrameController.getFramePanes().setStyle("-fx-background-color: purple");
    }

    private void loadFXMLFiles()
    {
        try
        {
            // Load the Frame FXML

            FXMLLoader frameLoader = new FXMLLoader(getClass().getResource("/view/frame.fxml"));

            frameBorder.setCenter(frameLoader.load());

            currentFrameController = frameLoader.getController();

            // Load the Board FXML

            FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("/view/board.fxml"));

            boardBorder.setCenter(boardLoader.load());

            boardController = boardLoader.getController();

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
        String p1_username, p2_username;

        p1_username = getUsernameFromUser(1);
        p2_username = getUsernameFromUser(2);

        // TODO: Get these usernames from user input

        // Create Game objects

        Pool pool = new Pool();

        Frame p1Frame = new Frame(pool);
        Frame p2Frame = new Frame(pool);

        Player p1 = new Player(p1_username, 0, p1Frame);
        Player p2 = new Player(p2_username, 0, p2Frame);

        Board board = new Board();

        // Store in this instance
        setBoard(board);
        setCurrentFrame(p1Frame);
        setPlayer1(p1);
        setPlayer2(p2);
        setPool(pool);
        currentPlayerNum = 1;
    }

    /**
     * Gets the chosen username for the Player via console display I/O.
     * @param playerNum Pass the Player number (1 or 2) so we can greet them properly
     * @return String Return the username they have chosen
     */
    private String getUsernameFromUser(int playerNum)
    {
        String username = "2";
        String welcome = "Welcome, Player " + Integer.toString(playerNum) + "!";

        // TODO Get their username via a dialog of some sort interacting with ConsoleController


        return username;
    }

    // Switch Player
    public void switchPlayer()
    {
        switch(getCurrentPlayerNum())
        {
            // If currently 1, transitioning to 2
            case 1:
                setCurrentFrame(getPlayer2().getFrame()); // Update current Frame
                break;
            // If currently 2, transitioning to 1
            case 2:
                setCurrentFrame(getPlayer1().getFrame()); // Update current Frame
                break;
        }

        currentFrameController.updateFrame(getCurrentFrame()); // Update frame controller
        consoleController.addLineToConsole("------- PLAYER " + getCurrentPlayerNum() + "'S TURN -------"); // Notify player
        incrementCurrentPlayerNum();
    }

    @FXML
    public void switchPlayerDelay()
    {
        currentFrameController.getFramePanes().setVisible(false);
        switchPlayerPrompt.setVisible(true);
        String message = "PLEASE SWITCH TO PLAYER " + getCurrentPlayerNum() + "\n\n";
        Timer.run(this,5, switchPlayerPrompt, message);
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
}

