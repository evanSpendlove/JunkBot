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
    ScrabbleEngineController JUnit & TestFX Test Class

    Purpose: Unit-Testing the ScrabbleEngineController Class of the scrabbleGame.gameEngine package.
    Summary: This class attempts to thoroughly test the ScrabbleEngineController Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Reuben Mulligan
    Version: 1.0.0
    Since: 26-02-2020
 */
@ExtendWith(ApplicationExtension.class)
public class ScrabbleEngineControllerTest {
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
    public void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    @Test
    public void testSwitchPlayerDelay()
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            int currPlayerNum = sec.getCurrentPlayerNum();

            sec.switchPlayerDelay();

            if(currPlayerNum == 1)
            {
                currPlayerNum = 2;
            }
            else
            {
                currPlayerNum = 1;
            }

            assertEquals(currPlayerNum, sec.getCurrentPlayerNum());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown.");
        }
    }

    @Test
    public void testUpdateUsername()
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            int currPlayerNum = sec.getCurrentPlayerNum();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown.");
        }

    }

}