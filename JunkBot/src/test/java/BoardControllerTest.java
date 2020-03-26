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
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameEngine.UI;
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

    }

    @Test
    public void testSetBoardObj()
    {
    }

    @Test
    public void testGetBoard()
    {

    }

    @Test
    public void testSetBoard()
    {

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
        System.out.println(cd.getText());
        assertEquals(cd.getText(), "Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\nHello\nNo command recognised\n");
    }
}