import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import scrabbleGame.UI.components.Timer;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameEngine.UI;

import static org.junit.jupiter.api.Assertions.*;

/*
    TimerTest JUnit & TestFX Test Class

    Purpose: Unit-Testing the Timer Class of the scrabbleGame.UI.components package.
    Summary: This class attempts to thoroughly test the Timer Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 26-02-2020
 */

@ExtendWith(ApplicationExtension.class)
public class TimerTest
{
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

    /*
      Goal: To test that the blank constructor works as expected.
      Testing Method: Instantiate Timer using the blank constructor and verify that no exception is thrown.
     */
    @Test
    public void testBlankConstructor()
    {
        try {
            Timer timer = new Timer();
        } catch (Exception ex) {
            fail("No exception should be thrown when constructing a valid Timer.");
        }
    }

    /*
      Goal: To test that the run method works as expected.
      Testing Method: Instantiate Timer using the blank constructor and call run. Verify that it runs and no exception is thrown.
     */
    @Test
    public void testRun(FxRobot robot)
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            int time = 3;
            String message = "Test: ";
            Timer.run(sec, time, sec.switchPlayerPrompt, message);

            TextArea prompt = (TextArea) robot.lookup("#switchPlayerPrompt").query();
            assertEquals((message + time + "s"), prompt.getText());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Test
    public void testEndGame(FxRobot robot)
    {
        try
        {
            ScrabbleEngineController sec = loader.getController();
            int time = 1;
            String message = "END GAME: ";

            Timer.endGame(sec, time, sec.switchPlayerPrompt, message);

            TextArea prompt = (TextArea) robot.lookup("#switchPlayerPrompt").query();
            String text = prompt.getText();

            assertEquals((message + time + "s"), text);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
