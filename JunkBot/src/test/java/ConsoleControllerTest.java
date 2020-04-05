import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameEngine.UI;
import scrabbleGame.gameModel.Frame;
import scrabbleGame.gameModel.Placement;
import scrabbleGame.gameModel.Tile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ConsoleControllerTest
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
        System.out.println(cd.getText());
        assertEquals(cd.getText(), "Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\nHello\nNo command recognised\n");
    }

    @Test
    void testAddLineToConsole(FxRobot robot) {
        ScrabbleEngineController sec = loader.getController();
        sec.consoleController.addLineToConsole("Test");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        assertEquals(cd.getText(), "Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\nTest\n");
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
        assertEquals("Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\n" +
                "Help\n" +
                "<--------- Help Message --------->\n" +
                "Commands: {Start, Quit, Help, Exchange, (Move), Username}\n" +
                "Start: Can only be used when no game has been started, starts the game\n" +
                "Quit: Quits the game and closes the window\n" +
                "Help: Help prints this message\n" +
                "Exchange: Exchange is used to change letters on your frame. Format: Exchange a b c \n" +
                "Exchanging letters will end your turn\n" +
                "(Move): To play a word, you need to use format <GridRef> <direction> <Word> \n" +
                "E.g. H8 Across Hello \n" +
                "Username: Username is used to set the player names. Format: Username <name> <playernum>\n" +
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

            ArrayList<Tile> f = (ArrayList<Tile>) sec.getPlayer(currentPlayerNum).getFrame().getTiles().clone();

            robot.write("Exchange " + f.get(0) + " " + f.get(1) + " " + "\n");
            TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();

            assertNotEquals(f, sec.getPlayer(currentPlayerNum).getFrame().getTiles());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when exchanging tiles.");
        }
    }

    @Test
    public void testQuitCommand(FxRobot robot)
    {
        robot.clickOn(".text-field");
        robot.write("Quit\n");
    }

}