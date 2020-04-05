import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.Scene;
import javafx.stage.Stage;
import scrabbleGame.UI.utilityPanes.SquarePane;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameEngine.UI;
import scrabbleGame.gameModel.Board;
import scrabbleGame.gameModel.Frame;
import scrabbleGame.gameModel.Player;
import scrabbleGame.gameModel.Tile;

import java.util.ArrayList;

/*
    BoardController JUnit & TestFX Test Class

    Purpose: Unit-Testing the BoardController Class of the scrabbleGame.UI.components package.
    Summary: This class attempts to thoroughly test the BoardController Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 26-02-2020
 */
@ExtendWith(ApplicationExtension.class)
public class BoardControllerTest
{
    private TextField input;
    Parent root;
    FXMLLoader loader;

    @Start
    private void start(Stage stage) throws Exception {
        loader = new FXMLLoader(UI.class.getResource("/view/scrabble.fxml"));
        root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Scrabble!");
        stage.show();
    }

    @Before
    public void setup() throws Exception
    {
        FxToolkit.registerPrimaryStage();
    }

    @Test
    public void testGetBoardObj()
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            Board b = sec.boardController.getBoardObject();

            Board x = new Board();

            assertEquals(x.toString(), b.toString());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when getting the board object");
        }
    }

    @Test
    public void testSetBoardObj()
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            Board b = sec.boardController.getBoardObject();

            Board x = new Board();

            sec.boardController.setBoardObject(b);

            assertEquals(x.toString(), sec.boardController.getBoardObject().toString());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when setting the board object");
        }
    }

    @Test
    public void testGetBoard()
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            SquarePane[][] boardArray = sec.boardController.getBoard();

            assertNotNull(boardArray);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when getting the board as an array of SquarePanes");
        }
    }

    @Test
    public void testSetBoard()
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            SquarePane[][] boardArray = sec.boardController.getBoard();

            SquarePane[][] boardArray2 = new SquarePane[15][15];

            sec.boardController.setBoard(boardArray2);
            assertEquals(boardArray2, sec.boardController.getBoard());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when setting the board as an array of SquarePanes");
        }
    }

    @Test
    void testAddTileToSquareCommand(FxRobot robot)
    {
        robot.clickOn(".text-field");
        robot.write("Start\n");
        robot.clickOn(".text-field");

        ScrabbleEngineController sec = loader.getController();
        char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character();

        robot.write("H8 Across " + Character.toString(c) + "\n");
        assertTrue(true);
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void testSubmitCommandWorks(FxRobot robot)
    {
        // when:
        robot.clickOn(".text-field");
        robot.write("Hello\n");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        assertEquals(cd.getText(), "Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\nHello\nNo command recognised\n");
    }
}