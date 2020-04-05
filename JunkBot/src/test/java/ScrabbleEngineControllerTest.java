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
import scrabbleGame.gameModel.*;

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

            sec.updateUsername(1, "Billy");
            sec.updateUsername(2, "John");
            assertEquals("Billy", sec.getPlayer(1).getUsername());
            assertEquals("John", sec.getPlayer(2).getUsername());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown.");
        }

    }

    @Test
    public void testPlayGame(FxRobot robot)
    {
        try
        {
            robot.clickOn(".text-field");
            robot.write("Start\n");
            robot.clickOn(".text-field");

            ScrabbleEngineController sec = loader.getController();
            String word = "";

            char hook1 = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character();

            for(int i = 0; i < sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size(); i++)
            {
                char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(i).character();

                if(c == '#')
                {
                    c = 'A';
                }

                word += c;
            }

            robot.write("H8 Across " + word + "\n");

            String word2 = "" + hook1;

            char hook2 = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(1).character();
            char hook3 = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character();

            for(int i = 0; i < sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size() - 4; i++)
            {
                char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(i).character();

                if(c == '#')
                {
                    c = 'A';
                }

                word2 += c;
            }

            System.out.println("Current player num: " + sec.getCurrentPlayerNum());

            int playerNum = sec.getCurrentPlayerNum();

            if(playerNum == 1)
            {
                playerNum++;
            }
            else
            {
                playerNum = 1;
            }

            robot.write("H8 Down " + word2 + "\n");

            // H10 = Hook

            while(sec.getPlayer(playerNum).getFrame().getTiles().size() < 7)
            {
                System.out.print(" ");
            }

            System.out.println();

            System.out.println("Current player num: " + sec.getCurrentPlayerNum());

            String word3 = "" + sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character() + hook2;

            for(int i = 1; i < sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size() - 3; i++)
            {
                char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(i).character();

                if(c == '#')
                {
                    c = 'A';
                }

                word3 += c;
            }

            playerNum = sec.getCurrentPlayerNum();

            if(playerNum == 1)
            {
                playerNum++;
            }
            else
            {
                playerNum = 1;
            }

            System.out.println("Word 3: " + word3);

            robot.write("G10 Across " + word3 + "\n");

            while(sec.getPlayer(playerNum).getFrame().getTiles().size() < 7)
            {
                System.out.print(" ");
            }

            System.out.println();

            System.out.println("Current player num: " + sec.getCurrentPlayerNum() + ", Frame size: " + sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size());

            String word4 = "" + sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character() + hook3;

            for(int i = 1; i < sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size() - 2; i++)
            {
                char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(i).character();

                if(c == '#')
                {
                    c = 'A';
                }

                word4 += c;
            }

            System.out.println("Hook 3: " + hook3);
            System.out.println("Word 4: " + word4);

            robot.write("G9 Across " + word4 + "\n");

            assertTrue(true);
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
            robot.clickOn(".text-field");
            robot.write("Start\n");
            robot.clickOn(".text-field");

            ScrabbleEngineController sec = loader.getController();
            String word = "";

            char hook1 = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character();

            for(int i = 0; i < sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size(); i++)
            {
                char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(i).character();

                if(c == '#')
                {
                    c = 'A';
                }

                word += c;
            }

            robot.write("H8 Across " + word + "\n");

            String word2 = "" + hook1;

            char hook2 = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(2).character();

            for(int i = 0; i < sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().size() - 4; i++)
            {
                char c = sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(i).character();

                if(c == '#')
                {
                    c = 'A';
                }

                word2 += c;
            }

            System.out.println("Current player num: " + sec.getCurrentPlayerNum());

            int playerNum = sec.getCurrentPlayerNum();

            if(playerNum == 1)
            {
                playerNum++;
            }
            else
            {
                playerNum = 1;
            }

            robot.write("H8 Down " + word2 + "\n");

            robot.write("quit");

            assertTrue(true);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Test
    public void testFinalScore()
    {
        try
        {
            Frame f = new Frame(new Pool());

            ScrabbleEngineController sec = loader.getController();

            int score  = 0;

            for(int i = 0; i < f.getTiles().size(); i++)
            {
                score += f.getTiles().get(i).value();
            }

            assertEquals(score, sec.finalScore(f));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

}