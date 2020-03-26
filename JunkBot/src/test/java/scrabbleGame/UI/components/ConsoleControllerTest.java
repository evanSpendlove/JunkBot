package scrabbleGame.UI.components;

import com.sun.javafx.robot.FXRobot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import scrabbleGame.gameEngine.Scrabble;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameEngine.UI;
import scrabbleGame.gameModel.Placement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ConsoleControllerTest {

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
    void testSubmitCommand(FXRobot robot) {
        robot.clickOn(".text-field");
        robot.write("Hello\n");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        System.out.println(cd.getText());
        assertEquals(cd.getText(), "Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\nHello\nNo command recognised\n");
    }

    @Test
    void testAddLineToConsole(FXRobot robot) {
        ScrabbleEngineController sec = loader.getController();
        sec.consoleController.addLineToConsole("Test");
        TextArea cd = (TextArea) robot.lookup("#consoleDisplay").query();
        assertEquals(cd.getText(), "Welcome to our Scrabble game! \n" +
                " To start the game use command Start. Before you do this, please enter Usernames for each player using Format: Username <name> <playerNumber>\n" +
                "To quit, use command Quit\nTest\n");
    }

    @Test
    void testCreatePlacement() {
        List<Placement> test = new ArrayList<>();
        List<Placement> test2 = new ArrayList<>();
        Placement a = new Placement(0,0,'A');
        test.add(a);
        ScrabbleEngineController sec = loader.getController();
        int[] gridRef = {0,0};
        test2 = sec.consoleController.createPlacement("a",gridRef, 0);
        assertEquals(test.toString(), test2.toString());
    }
}