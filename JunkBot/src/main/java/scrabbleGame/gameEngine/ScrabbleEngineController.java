package scrabbleGame.gameEngine;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
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
    public FrameController frameController;

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
    void initialize()
    {
        // Load components into this FXML file

        try
        {
            FXMLLoader frameLoader = new FXMLLoader(getClass().getResource("/view/frame.fxml"));

            frameBorder.setCenter(frameLoader.load());

            frameController = frameLoader.getController();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("/view/board.fxml"));

            boardBorder.setCenter(boardLoader.load());

            boardController = boardLoader.getController();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            FXMLLoader consoleLoader = new FXMLLoader(getClass().getResource("/view/console.fxml"));

            consoleBorder.setCenter(consoleLoader.load());

            consoleController = consoleLoader.getController();

            consoleController.setScrabbleEngineController(this);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

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

        frameController.updateFrame(testFrame);

        System.out.println("FC_Rack: " + Arrays.toString(frameController.getRack()));
        System.out.println("FC_RackPanes: " + Arrays.toString(frameController.getFramePanes().getChildren().toArray()));

        Board testBoard = new Board();
        testBoard.resetBoard();
        boardController.updateBoard(testBoard);

        boardController.addTiletoBoard(this.frameController, 1, 6, 8);
        boardController.addTiletoBoard(this.frameController, 0, 7, 8);
        boardController.removeTileFromBoard(7, 8);

        frameController.getFramePanes().setStyle("-fx-background-color: purple");
    }

    // Switch Player

}

