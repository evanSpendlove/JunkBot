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

    private String lastCommand;

    // Getters and Setters
    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

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
            setLastCommand(newCommand);

            newCommand += "\n";


            String oldCommands = consoleDisplay.getText();
            consoleDisplay.setText(oldCommands + newCommand);

            parseInput(newCommand);

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
    private void parseInput(String input) throws IllegalArgumentException{

        if(input.isEmpty() || input.isBlank()){
            throw new IllegalArgumentException("Input cannot be empty");
        }
        String split[] = input.split("\\s");

        String commands[] = {"Quit", "Help", "Exchange"};
        int flag = -1;
        for(int i = 0; i<commands.length; i++){
            if(split[0].equalsIgnoreCase(commands[i]) == true){
                flag = i;
            }
        }
        if(flag == -1){
            if(split[0].matches("[A-Z][0-9]{1,2}") == true && split.length == 3){
                flag = 3;
            } else if (split.length == 2 && split[0].matches("[A-Z]{0,1}[a-z]{3,9}") == true) {
                flag = 4;
            }
        }

        switch(flag){
            case 0:
                addLineToConsole("Game quiting");
                //TODO add quit command
                break;

            case 1:
                addLineToConsole("Help messages");
                addLineToConsole("Commands: \nQuit - used to quit the game \nHelp- displays help " +
                        "\nExchange - Exchange can be used to swap letters from your rack, format exchange Letter Letter\n Eg. Exchange A B \nMoves - " +
                        "To play a move use format <GRID REF> <DIRECTION> <WORD> \nE.G. A1 Across Hello \nUsername entry: Player name can be updated at any point using command <name> <playernum>\n" +
                        "E.g. Reuben 1");
                break;

            case 2:
                addLineToConsole("Exchanging tiles");
                break;

            case 3:
                addLineToConsole("Playing move x y z");
                break;

            case 4:
                addLineToConsole("Username accepted");
                if(Integer.parseInt(split[1]) == 1){
                    getScrabbleEngineController().getPlayer1().setUsername(split[0]);
                    System.out.println(getScrabbleEngineController().getPlayer1().dumpPlayerInfo());
                }else{
                    getScrabbleEngineController().getPlayer2().setUsername(split[0]);
                    System.out.println(getScrabbleEngineController().getPlayer2().dumpPlayerInfo());
                }
                break;

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

            getScrabbleEngineController().boardController.addTiletoBoard(this.scrabbleEngineController.currentFrameController, offset, x, y);
        }
        else if(command.charAt(0) == '5')
        {
            getScrabbleEngineController().switchPlayerDelay();
        }
    }

}
