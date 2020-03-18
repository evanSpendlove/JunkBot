package scrabbleGame.UI.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scrabbleGame.gameEngine.ScrabbleEngineController;

/*
    This is the class for all the methods to do with the console.

    You can implement the parseCommand() here - see testCommands() as a sample implementation

    You can also implement the console menu here (text based display to user)
    --> Would be helpful to have an 'outputError()' method if you'd like to have a go at it. @Reuben
 */
public class ConsoleController
{
    @FXML
    private ScrabbleEngineController scrabbleEngineController;

    @FXML
    private TextArea consoleDisplay;

    @FXML
    private TextField commandInput;

    // Getters and Setters
    public ScrabbleEngineController getScrabbleEngineController() {
        return scrabbleEngineController;
    }

    public void setScrabbleEngineController(ScrabbleEngineController scrabbleEngineController) {
        this.scrabbleEngineController = scrabbleEngineController;
    }

    @FXML
    void initialise()
    {
        // Blank for now
    }

    @FXML
    void submitCommand(ActionEvent event)
    {
        String newCommand = commandInput.getText();

        // Need more input validation
        if(!newCommand.isBlank() && !newCommand.isEmpty())
        {
            newCommand += "\n";

            String oldCommands = consoleDisplay.getText();

            testCommands(newCommand);

            consoleDisplay.setText(oldCommands + newCommand);
        }

        commandInput.clear();
    }

    @FXML
    public void addLineToConsole(String s)
    {
        if(!s.isEmpty() && !s.isBlank())
        {
            s += "\n";

            consoleDisplay.setText(consoleDisplay.getText() + s);
        }
    }

    private void testCommands(String command)
    {
        if(command.charAt(0) == '1') // If command to add tile
        {
            System.out.println("Substring (2)" + command.substring(2,3));
            int offset = Integer.parseInt(command.substring(2, 3));
            int x = Integer.parseInt(command.substring(4, 5));
            int y = Integer.parseInt(command.substring(6, 7));

            System.out.println("Command: " + command);
            System.out.println("Offset: " + offset);
            System.out.println("X: " + x);
            System.out.println("Y: " + y);

            getScrabbleEngineController().boardController.addTiletoBoard(this.scrabbleEngineController.frameController, offset, x, y);
        }
    }

}
