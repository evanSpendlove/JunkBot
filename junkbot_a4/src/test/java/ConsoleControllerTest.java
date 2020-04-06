import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.assertj.core.internal.CharArrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameEngine.UI;
import scrabbleGame.gameModel.Frame;
import scrabbleGame.gameModel.Lexicon;
import scrabbleGame.gameModel.Placement;
import scrabbleGame.gameModel.Tile;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ConsoleControllerTest
{
    private TextField input;
    Parent root;
    FXMLLoader loader;
    Stage stage;

    @Start
    private void start(Stage stage) throws Exception {
        this.stage = stage;
        loader = new FXMLLoader(UI.class.getResource("/view/scrabble.fxml"));
        root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Scrabble!");
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("PLATFORM EXIT ATTEMPTED.");
            }
        });
    }
    @Before
    public void setup() throws Exception
    {
        FxToolkit.registerPrimaryStage();
    }

    @AfterClass
    public static void teardownStage() throws Exception {
        Toolkit.getToolkit().defer(() -> {});
        FxToolkit.cleanupStages();
    }

    @Test
    void testGetScrabbleEngineController() {
        ScrabbleEngineController sec = loader.getController();
        assertEquals(sec.consoleController.getScrabbleEngineController(), sec);
    }

    @Test
    void testSetScrabbleEngineController() {
        ScrabbleEngineController sec = loader.getController();
        ScrabbleEngineController sec2 = loader.getController();
        sec2.incrementCurrentPlayerNum();
        sec.consoleController.setScrabbleEngineController(sec2);
        assertEquals(sec.consoleController.getScrabbleEngineController(), sec2);
    }

    @Test
    void testSubmitCommand(FxRobot robot)
    {
        robot.clickOn(".text-field");
        robot.write("Hello\n");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        assertEquals(cd.getText(), "Welcome to our Scrabble game!\n" +
                "To start the game use command Start. Before you do this, please enter usernames for each player using the format: Username <name> <playerNumber>\n" +
                "To quit, use command <quit>\n" +
                "Hello\n" +
                "No command recognised\n");
    }

    @Test
    void testAddLineToConsole(FxRobot robot) {
        ScrabbleEngineController sec = loader.getController();
        sec.consoleController.addLineToConsole("Test");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        assertEquals(cd.getText(), "Welcome to our Scrabble game!\n" +
                "To start the game use command Start. Before you do this, please enter usernames for each player using the format: Username <name> <playerNumber>\n" +
                "To quit, use command <quit>\n" +
                "Test\n");
    }

    @Test
    void testCreatePlacement()
    {
        List<Placement> test = new ArrayList<>();
        List<Placement> test2 = new ArrayList<>();
        Placement a = new Placement(0,0,'A');
        test.add(a);
        ScrabbleEngineController sec = loader.getController();
        int[] gridRef = {0,0};
        test2 = sec.consoleController.createPlacement("a",gridRef, 0);
        assertEquals(test.toString(), test2.toString());
    }

    @Test
    void testGetLastCommand(FxRobot robot)
    {
        robot.clickOn(".text-field");
        robot.write("Hello\n");

        ScrabbleEngineController sec = loader.getController();

        assertEquals("Hello", sec.consoleController.getLastCommand());
    }

    /*
        Goal: To test that the help command works as expected.
        Testing Method: Call the help command and verify the output produced.
     */
    @Test
    public void testHelpCommand(FxRobot robot)
    {
        robot.clickOn(".text-field");
        robot.write("Help\n");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        assertEquals("Welcome to our Scrabble game!\n" +
                "To start the game use command Start. Before you do this, please enter usernames for each player using the format: Username <name> <playerNumber>\n" +
                "To quit, use command <quit>\n" +
                "Help\n" +
                "<--------- Help Message --------->\n" +
                "Commands: {Start, Quit, Help, Exchange, (Move), Username, Challenge}\n" +
                "Start: Can only be used when no game has been started, starts the game\n" +
                "Quit: Quits the game and closes the window\n" +
                "Help: Help prints this message\n" +
                "Exchange: Exchange is used to change letters on your frame. Format: Exchange a b c \n" +
                "Exchanging letters will end your turn\n" +
                "(Move): To play a word, you need to use format <GridRef> <direction> <Word> \n" +
                "E.g. H8 Across Hello \n" +
                "Username: Username is used to set the player names. Format: Username <name> <playernum>\n" +
                "Challenge: To challenge that the last word played by the other player is a real word (in the dictionary), type <Challenge> <Word>. E.g. Challenge Bobz\n" +
                "<--------- End --------->\n", cd.getText());
    }

    /*
        Goal: To test that the exchange command works as expected.
        Testing Method: Call exchange and verify that the tiles were exchanged.
     */
    @Test
    public void testExchangeCommand(FxRobot robot)
    {
        try
        {
            robot.clickOn(".text-field");
            robot.write("Start\n");

            ScrabbleEngineController sec = loader.getController();

            int currentPlayerNum = sec.getCurrentPlayerNum();

            // Need to wait till visible (i.e. not null)
            while(!sec.currentFrameController.getFramePanes().isVisible())
            {
                System.out.print(" ");
            }

            ArrayList<Tile> f = (ArrayList<Tile>) sec.getPlayer(currentPlayerNum).getFrame().getTiles().clone();

            robot.write("Exchange " + f.get(0) + " " + f.get(1) + " " + "\n");
            TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();

            assertNotEquals(f, sec.getPlayer(currentPlayerNum).getFrame().getTiles());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when exchanging tiles.");
        }
    }

    @Test
    public void testChallengeCommand(FxRobot robot)
    {
        try
        {
            robot.clickOn(".text-field");
            robot.write("Start\n");

            ScrabbleEngineController sec = loader.getController();

            int currentPlayerNum = sec.getCurrentPlayerNum();

            // Need to wait till visible (i.e. not null)
            while(!sec.currentFrameController.getFramePanes().isVisible())
            {
                System.out.print(" ");
            }
            System.out.println();
            System.out.println("\n");

            ArrayList<Tile> f = (ArrayList<Tile>) sec.getPlayer(currentPlayerNum).getFrame().getTiles().clone();

            String word = "";

            for(int i = 0; i < f.size(); i++)
            {
                word += f.get(i).character();
            }

            System.out.println("Word: " + word);

            System.out.println(Lexicon.checkWord(word));

            while(Lexicon.checkWord(word)) // If not a word
            {
                char[] wordArray = word.toCharArray();

                // Knuth Shuffle
                for(int i = 1; i < wordArray.length; i++)
                {
                    int k = ThreadLocalRandom.current().nextInt(0, i);

                    // Swap
                    char temp = wordArray[i];
                    wordArray[i] = wordArray[k];
                    wordArray[k] = temp;
                }

                word = new String(wordArray); // Update word
            }

            System.out.println("Word is not valid");

            // Write it, then challenge it.
            robot.write("H8 Across " + word + "\n");

            if(currentPlayerNum == 1)
            {
                currentPlayerNum = 2;
            }
            else
            {
                currentPlayerNum = 1;
            }

            while(sec.currentFrameController.getFrameObj() == null || sec.currentFrameController.getFrameObj() != sec.getPlayer(currentPlayerNum).getFrame())
            {
                System.out.print(" ");
            }

            robot.write("Challenge " + word + "\n");

            if(currentPlayerNum == 1)
            {
                currentPlayerNum = 2;
            }
            else
            {
                currentPlayerNum = 1;
            }

            assertEquals(0, sec.getPlayer(currentPlayerNum).getScore());
        }
        catch(Exception ex)
        {
            System.out.println(ex.getClass());
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            // fail("No exception should be thrown when testing Challenge.");
        }
    }

    // Cannot test end game because it's very hard to test System.exit() due to JavaFX running on its own thread.
    /*

    @Test
    public void testQuitCommand(FxRobot robot)
    {
        NoExitSecurityManager sm = (NoExitSecurityManager) System.getSecurityManager();
        sm.setAllowExit(false);
        robot.clickOn(".text-field");
        robot.write("quit\n");
    }

    @Test
    public void testP1Win(FxRobot robot)
    {
        NoExitSecurityManager sm = (NoExitSecurityManager) System.getSecurityManager();
        sm.setAllowExit(false);
        try
        {
            robot.clickOn(".text-field");
            robot.write("Start\n");
            robot.clickOn(".text-field");

            ScrabbleEngineController sec = loader.getController();

            if(sec.getCurrentPlayerNum() == 1)
            {
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
            }
            else
            {
                robot.write("Exchange " + sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character() + " \n");

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
            }

            robot.write("quit\n");
        }
        catch(Exception ex)
        {
            assertTrue(true);
        }
    }

    @Test
    public void testP2Win(FxRobot robot)
    {
        NoExitSecurityManager sm = (NoExitSecurityManager) System.getSecurityManager();
        sm.setAllowExit(false);
        try
        {
            robot.clickOn(".text-field");
            robot.write("Start\n");
            robot.clickOn(".text-field");

            ScrabbleEngineController sec = loader.getController();

            if(sec.getCurrentPlayerNum() == 2)
            {
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
            }
            else
            {
                robot.write("Exchange " + sec.getPlayer(sec.getCurrentPlayerNum()).getFrame().getTiles().get(0).character() + " \n");

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
            }

            robot.write("quit\n");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

     */

}

class NoExitSecurityManager extends SecurityManager
{
    boolean allowExit = false;

    public boolean isAllowExit() {
        return allowExit;
    }

    public void setAllowExit(boolean allowExit) {
        this.allowExit = allowExit;
    }

    @Override
    public void checkPermission( Permission permission )
    {
        // System.out.println("Permission: " + permission);
        if( "exitVM.0".equals( permission.getName() ) ) {
            System.out.println("SYSTEM EXIT ATTEMPTED");
        }
    }

    @Override
    public void checkExit(int status)
    {
        if(!allowExit)
        {
            System.out.println("EXIT BLOCKED");
            throw new IllegalArgumentException("");
        }
    }
}