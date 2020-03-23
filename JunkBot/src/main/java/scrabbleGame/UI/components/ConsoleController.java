package scrabbleGame.UI.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameModel.Board;
import scrabbleGame.gameModel.Move;
import scrabbleGame.gameModel.Placement;

import java.util.ArrayList;
import java.util.List;

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

    private boolean gameStarted = false;

    private String helpMessage = "<--------- Help Message --------->\n" +
            "Commands: {Start, Quit, Help, Exchange, (Move), Username}\n" +
            "Start: Can only be used when no game has been started, loads up";

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

        String commands[] = {"Quit", "Help", "Exchange", "Start", "Username"};
        int flag = -1;
        for(int i = 0; i<commands.length; i++){
            if(split[0].equalsIgnoreCase(commands[i]) == true){
                flag = i;
            }
        }
        if(flag == -1){
            if(split[0].matches("([A-Oa-o][1-9])|([A-Oa-o][1][0-5])") == true && split.length == 3){
                flag = 5;
            }
        }

        switch(flag){
            case 0:
                addLineToConsole("Game quiting");
                Platform.exit();
                break;

            case 1:
                addLineToConsole("Help messages");
                addLineToConsole("Commands: \nQuit - used to quit the game \nHelp- displays help " +
                        "\nExchange - Exchange can be used to swap letters from your rack, format exchange Letter Letter\n Eg. Exchange A B \nMoves - " +
                        "To play a move use format <GRID REF> <DIRECTION> <WORD> \nE.G. A1 Across Hello \nUsername entry: Player name can be updated at any point using command <name> <playernum>\n" +
                        "E.g. Reuben 1");
                break;

            case 2:
                char[] letters = new char[(split.length-1)];
                for(int i = 1; i < split.length; i++){
                    letters[i-1] = split[i].charAt(0);
                }
                try{
                    getScrabbleEngineController().currentFrameController.exchangeTiles(letters);
                    getScrabbleEngineController().currentFrameController.updateFrame(getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum()).getFrame());
                    addLineToConsole("Tiles exchanged");
                    getScrabbleEngineController().switchPlayerDelay();
                }catch(Exception ex){
                    addLineToConsole(ex.getMessage());
                }
                break;

            case 3:
                if(gameStarted){
                    addLineToConsole("Game already in progress");
                }else{
                    gameStarted = true;
                    addLineToConsole("Game starting! Player 1 is " + getScrabbleEngineController().getPlayer1().getUsername() + ", Player 2 is " +getScrabbleEngineController().getPlayer2().getUsername());
                    getScrabbleEngineController().switchPlayerDelay();
                }
                break;


            case 4:
                if(split[1].matches("([A-Za-z]{1,9})")){
                    addLineToConsole("Username accepted");
                    if(Integer.parseInt(split[2]) == 1){
                        getScrabbleEngineController().getPlayer1().setUsername(split[1]);
                        System.out.println(getScrabbleEngineController().getPlayer1().dumpPlayerInfo());
                    }else{
                        getScrabbleEngineController().getPlayer2().setUsername(split[1]);
                        System.out.println(getScrabbleEngineController().getPlayer2().dumpPlayerInfo());
                    }
                }else{
                    addLineToConsole("Username not accepted, enter a username only containing alpha characters max length 9 min length 1");
                }

                break;

            case 5:
                int[] gridRef = convertGridRef(split[0].toCharArray());
                int direction = 0;
                if(split[1].equalsIgnoreCase("Across") || split[1].equalsIgnoreCase("Horizontal")){
                    direction = 0;
                }else if(split[1].equalsIgnoreCase("Down") || split[1].equalsIgnoreCase("Vertical")){
                    direction = 1;
                }
                List<Placement> play = createPlacement(split[2],gridRef,direction);
                Move newMove = new Move(play, split[2], direction);
                System.out.println(newMove.toString());
                if(getScrabbleEngineController().getTurnCounter() == 1){
                    if(getScrabbleEngineController().getBoard().placeFirstWord(newMove, getScrabbleEngineController().getPlayer1()) == 2){
                        //getScrabbleEngineController().boardController.addMoveToBoard(getScrabbleEngineController().currentFrameController, newMove);
                        getScrabbleEngineController().boardController.updateBoard(getScrabbleEngineController().getBoard());
                        getScrabbleEngineController().currentFrameController.playWord(split[2]);
                        getScrabbleEngineController().getBoard().printBoard();
                        getScrabbleEngineController().switchPlayerDelay();
                    }else{
                        addLineToConsole("Failed to play a word");
                    }
                }else{
                    if(getScrabbleEngineController().getBoard().placeWord(newMove, getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum())) == 2){
                        getScrabbleEngineController().currentFrameController.playWord(split[2]);
                        getScrabbleEngineController().boardController.updateBoard(getScrabbleEngineController().getBoard());
                        getScrabbleEngineController().getBoard().printBoard();
                        getScrabbleEngineController().switchPlayerDelay();
                    }else{
                        addLineToConsole("Failed to play a word");
                    }
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

    private int[] convertGridRef(char[] x){
        int[] a = new int[x.length];
        for(int i = 0; i < x.length; i++){
            a[i] = x[i];
        }
        if(65 <= a[0] && a[0] <= 89){
            a[0] -= 65;
        }else if(97 <= a[0] && a[0] <= 111){
            a[0] -= 97;
        }
        if(a.length == 2){
            a[1] = a[1]-49;
            return a;
        }else if (a.length == 3){
            a[1] = (a[1]-48)*10 + (a[2]-49);
            return a;
        }
        return a;
    }

    List<Placement> createPlacement(String word, int[] gridRef, int direction){
        List<Placement> placements = new ArrayList<Placement>();
        char[] letters = word.toCharArray();
        int letterPtr = 0;
        if(direction == 0){
            for(int i = gridRef[0]; i < (gridRef[0] + word.length());i++){
                if(!getScrabbleEngineController().boardController.getBoardObject().getBoard()[gridRef[1]][i].isOccupied()){
                    Placement temp = new Placement(i,gridRef[1],letters[letterPtr]);
                    placements.add(temp);
                }
                letterPtr++;
            }
        }else if(direction == 1){
            for(int i = gridRef[1]; i < (gridRef[1] + word.length()); i++){
                if(!getScrabbleEngineController().boardController.getBoardObject().getBoard()[i][gridRef[0]].isOccupied()){
                    System.out.println(letters[letterPtr]);
                    Placement temp = new Placement(gridRef[0], i, letters[letterPtr]);
                    placements.add(temp);
                }
                letterPtr++;
            }
        }
        return placements;
    }

}
