package scrabbleGame.UI.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameModel.Move;
import scrabbleGame.gameModel.Placement;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>ConsoleController Class</h1>
 * <p>This is the Console Controller class, this will handle all of the input and output for the console menu of our scrabble game</br>
 * Team: JunkBot
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * </p>
 * @author Reuben Mulligan, Evan Spendlove
 * @version 1.0.0
 * @since 18-03-20
 */
public class ConsoleController
{
    /**
     * Holds the main scrabble engine controller
     */
    @FXML
    private ScrabbleEngineController scrabbleEngineController;

    /**
     * Text Area variable to hold the console Display
     */
    @FXML
    private TextArea consoleDisplay;

    /**
     * Text Field variable to hold the input box
     */
    @FXML
    private TextField commandInput;

    /**
     * String variable holding the last command inputted into the terminal
     */
    private String lastCommand;

    /**
     * ArrayList of the last words played.
     */
    private ArrayList<String> lastWordsPlayed = new ArrayList<>();

    /**
     * Integer variable holding the score of the last word
     */
    private int lastMoveScore;

    /**
     * Game started boolean flag
     */
    private boolean gameStarted = false;

    /**
     * String holding the help message for the console command "help"
     */
    private String helpMessage = "<--------- Help Message --------->\n" +
            "Commands: {Start, Quit, Help, Exchange, (Move), Username}\n" +
            "Start: Can only be used when no game has been started, starts the game\n" +
            "Quit: Quits the game and closes the window\n" +
            "Help: Help prints this message\n" +
            "Exchange: Exchange is used to change letters on your frame. Format: Exchange a b c \nExchanging letters will end your turn\n" +
            "(Move): To play a word, you need to use format <GridRef> <direction> <Word> \nE.g. H8 Across Hello \n" +
            "Username: Username is used to set the player names. Format: Username <name> <playernum>\n" +
            "<--------- End --------->";

    // Getters and Setters

    /**
     * @return lastCommand
     */
    public String getLastCommand() {
        return lastCommand;
    }

    /**
     * @param lastCommand
     */
    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    /**
     * @return scrabbleEngineController
     */
    public ScrabbleEngineController getScrabbleEngineController() {
        return scrabbleEngineController;
    }

    /**
     * @param scrabbleEngineController
     */
    public void setScrabbleEngineController(ScrabbleEngineController scrabbleEngineController) {
        this.scrabbleEngineController = scrabbleEngineController;
    }

    /**
     * Initialisation method to make the console window show up
     */
    @FXML
    void initialise()
    {}

    /**
     * submitCommand Method acts on the actionEvent from the text field box. It then grabs the input command, does some validation then hands it to parseInput
     * It also appends a new line character and sets the lastCommand variable to the new command. It also updates the console window to show the last command
     * @param event
     * @author Evan Spendlove
     */
    @FXML
    void submitCommand(ActionEvent event)
    {
        //Grab new command
        String newCommand = commandInput.getText();

        if(!newCommand.isEmpty())
        {
            //Update the lastCommand variable
            setLastCommand(newCommand);

            //Append a new line character
            newCommand += "\n";

            //Update the console window by appending the old text with the new command
            String oldCommands = consoleDisplay.getText();
            consoleDisplay.setText(oldCommands + newCommand);

            //Hand new command to the parseInput method which will decide what to output
            parseInput(newCommand);

        }
        //Clear the input field
        commandInput.clear();
    }


    /**
     * Method takes an input string and appends it to the console window
     * @param s The string you want to add to the console
     * @author Evan Spendlove
     */
    @FXML
    public void addLineToConsole(String s)
    {
        if(!s.isEmpty())
        {
            s += "\n";

            consoleDisplay.setText(consoleDisplay.getText() + s);
        }
    }


    /**
     * <p>
     * parseInput method takes an input string, validates it then checks it against a set of acceptable commands.
     * <p>
     * It uses a switch statement with a flag to handle what to output.
     * It can handle inputs such as: Quit, Start, Help, Exchange, Username, Move
     * @param input The command string to be parsed
     * @throws IllegalArgumentException
     */
    private void parseInput(String input) throws IllegalArgumentException
    {

        if(input.isEmpty()){
            throw new IllegalArgumentException("Input cannot be empty");
        }

        //Split string based on white space
        String split[] = input.split("\\s");

        //Define the array of set acceptable commands
        String commands[] = {"Quit", "Help", "Exchange", "Start", "Username"};

        //Set the flag for the switch to -1
        int flag = -1;

        //Loop through the array of commands and see if the first word of the input string matches any (case insensitive)
        //If so set flag equal to the index of the command
        for(int i = 0; i<commands.length; i++){
            if(split[0].equalsIgnoreCase(commands[i]) == true){
                flag = i;
            }
        }

        //If the input didn't match any set command, check if the first word matches a grid ref using a regex
        if(flag == -1){
            if(split[0].matches("([A-Oa-o][1-9])|([A-Oa-o][1][0-5])") == true && split.length == 3){
                flag = 5;
            }
        }

        //Switch statement using flag
        switch(flag){

            //Case -1 no command recognised, break after
            case -1:
                addLineToConsole("No command recognised");
                break;

            //Case 0, Quit command
            case 0:
                //Use platform.exit to close the window
                int player1Score = getScrabbleEngineController().getPlayer1().getScore() - getScrabbleEngineController().finalScore(getScrabbleEngineController().getPlayer1().getFrame());
                int player2Score = getScrabbleEngineController().getPlayer2().getScore() - getScrabbleEngineController().finalScore(getScrabbleEngineController().getPlayer2().getFrame());

                String message = "";

                if(player1Score > player2Score)
                {
                    message = "PLAYER ONE HAS WON!";
                }
                else if(player2Score > player1Score)
                {
                    message = "PLAYER TWO HAS WON!";
                }
                else
                {
                    message = "IT'S A TIE!";
                }

                message += "\n\n\n";

                Timer.endGame(getScrabbleEngineController(), 3, getScrabbleEngineController().switchPlayerPrompt, message);
                break;

            //Case 1, print the help message
            case 1:
                addLineToConsole(helpMessage);
                break;

            //Case 2, exchange letters command
            case 2:
                //Check to see if the game has started, if not then break
                if(!gameStarted){
                    addLineToConsole("Unable to exchange letters before a match has started");
                    break;
                }

                //Initialise the character array to hold the letters that will be exchanged
                char[] letters = new char[(split.length-1)];

                //Loop through the array of words and get the characters they wish to exchange, assumes letters will be space delimited
                for(int i = 1; i < split.length; i++){
                    letters[i-1] = split[i].charAt(0);
                }
                //Try to exchange the letters and catch an exception if one gets thrown
                try{
                    //Call the exchangeTiles command from the frame controller
                    getScrabbleEngineController().currentFrameController.exchangeTiles(letters);

                    //Update the frame for the current player
                    getScrabbleEngineController().currentFrameController.updateFrame(getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum()).getFrame());

                    //Print a success message to the console
                    addLineToConsole("Tiles exchanged");

                    //End the players turn by calling the switchPlayerDelay method
                    getScrabbleEngineController().switchPlayerDelay();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    //If an exception is caught, add the exception message to the console
                    addLineToConsole(ex.getMessage());
                }
                break;

            //Case 3, Start command. Check if the game has started yet, if no then start the game.
            //Assumes the currentPlayerNum in scrabble game controller has been set to the person going second
            case 3:
                //If started do nothing
                if(gameStarted){
                    addLineToConsole("Game already in progress");
                }
                else{
                    //Update the boolean flag
                    gameStarted = true;
                    //Print a game starting message
                    addLineToConsole("Game starting! Player 1 is " + getScrabbleEngineController().getPlayer1().getUsername() + ", Player 2 is " +getScrabbleEngineController().getPlayer2().getUsername());
                    //Switch players to start the game
                    getScrabbleEngineController().switchPlayerDelay();
                }
                break;

            //Case 4, Username command. Checks the username matches a regex (alpha length 1 to 9)
            case 4:
                //Check the second word in the input string is a name matching our requirements
                if(split[1].matches("([A-Za-z]{1,9})")){
                    //Check the third word in the input string is equal to a number, either 1 or 2
                    if(Integer.parseInt(split[2]) == 1){
                        //Update the player info accordingly
                        getScrabbleEngineController().getPlayer1().setUsername(split[1]);
                        //Print a confirmation message
                        addLineToConsole("Username accepted");
                        getScrabbleEngineController().updateUsername(1, split[1]);
                    }else{
                        //Update the player info accordingly
                        getScrabbleEngineController().getPlayer2().setUsername(split[1]);
                        //Print a confirmation message
                        addLineToConsole("Username accepted");
                        getScrabbleEngineController().updateUsername(2, split[1]);
                    }
                }else{
                    //Print out an error message
                    addLineToConsole("Username not accepted, enter a username only containing alpha characters max length 9 min length 1");
                }

                break;

            //Case 5, Move command
            //This is the most complex part of parseInput, this creates the gridRef, direction, placement and move from the input

            case 5:
                if(!gameStarted){
                    addLineToConsole("Can't play a move without a match started");
                    break;
                }
                //Set the integer array for grid reference by calling the convertGridRef method on the first word in the input string
                int[] gridRef = convertGridRef(split[0].toCharArray());
                //Initialise the direction to 0
                int direction = 0;

                //Check the direction is equal to {'Across', 'Down', 'Horizontal', 'Vertical'} else break and don't accept
                if(split[1].equalsIgnoreCase("Across") || split[1].equalsIgnoreCase("Horizontal")){
                    direction = 0;
                }else if(split[1].equalsIgnoreCase("Down") || split[1].equalsIgnoreCase("Vertical")){
                    direction = 1;
                }else{
                    addLineToConsole("Direction not accepted, please use {'Across', 'Down', 'Horizontal', 'Vertical'}");
                    break;
                }

                //Create the placement for the newMove by calling the createPlacement method on the 3 word in the input string
                List<Placement> play = createPlacement(split[2],gridRef,direction);

                //Create the newMove variable using the placement, word in uppercase and direction
                Move newMove = new Move(play, split[2].toUpperCase(), direction);

                //Check if its the first turn or not
                if(getScrabbleEngineController().getTurnCounter() == 1){
                    //If its the first turn, call the placeFirstWord method from Board, check it return 2 for success
                    if(getScrabbleEngineController().getBoard().placeFirstWord(newMove, getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum())) == 2){
                        //getScrabbleEngineController().boardController.addMoveToBoard(getScrabbleEngineController().currentFrameController, newMove);
                        lastWordsPlayed.clear();
                        updateLastWordsPlayed(newMove.getWord());

                        //Update the board object after the word is placed to display the new word
                        getScrabbleEngineController().boardController.updateBoard(getScrabbleEngineController().getBoard());

                        //Update the frame controller with the word played (removing the tiles from the frame)
                        getScrabbleEngineController().currentFrameController.playWord(newMove);

                        // Update the score
                        getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum()).increaseScore(getScrabbleEngineController().scoring(newMove));
                    }
                    else{
                        //Print a fail message
                        addLineToConsole("Failed to play a word");
                        break;
                    }
                }
                else{
                    //If not the first turn then call the placeWord method. Check returns 2 for valid move
                    if(getScrabbleEngineController().getBoard().placeWord(newMove, getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum())) == 2){

                        lastWordsPlayed.clear();
                        updateLastWordsPlayed(newMove.getWord());

                        //Update the frame with played word, removing the tiles
                        getScrabbleEngineController().currentFrameController.playWord(newMove);

                        //Update the board to display the played word
                        getScrabbleEngineController().boardController.updateBoard(getScrabbleEngineController().getBoard());

                        // Update the score
                        getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum()).increaseScore(getScrabbleEngineController().scoring(newMove));
                    }
                    else {
                        //Print an error message
                        addLineToConsole("Failed to play a word");
                        break;
                    }
                }

                getScrabbleEngineController().updateScore();
                //Switch the turn after move complete
                getScrabbleEngineController().switchPlayerDelay();
                break;

        }
    }

    /**
     * Method convertGridRef takes in a grid reference in the format: [A-Oa-o][1-15] and converts it
     * to the format: [0-14][0-14] as to work with how arrays are indexed.</br>
     * No need to check if the input is outside of the A-O range or 1-15 range as that is done via the regex in parseInput
     * @param inputGridRef Input grid Reference
     * @return convertedGridRef Returns a converted Grid Reference
     */
    private int[] convertGridRef(char[] inputGridRef){
        //Initialise the convertedGridRef
        int[] convertedGridRef = new int[2];

        //Assign the values to convertedGridRef from input
        //First check if the first letter is capital or not then convert that to 0-14 format
        if(65 <= inputGridRef[0] && inputGridRef[0] <= 89){
            convertedGridRef[0] = inputGridRef[0]-65;
        }else if(97 <= inputGridRef[0] && inputGridRef[0] <= 111){
            convertedGridRef[0] = inputGridRef[0] - 97;
        }

        //Due to the input being a character array, we have to double check for a double digit number
        //If length is 2 we have single digit number, if 3 then double digit number.
        //We then convert this to a single integer value
        if(inputGridRef.length == 2){
            convertedGridRef[1] = inputGridRef[1]-49;
        }else if (inputGridRef.length == 3){
            convertedGridRef[1] = (inputGridRef[1]-48)*10 + (inputGridRef[2]-49);
        }

        //Return the convertedGridRef integer array
        return convertedGridRef;
    }


    /**
     * Method createPlacement takes a word, grid reference and a direction and create a list of Placement Objects from it
     * @param word
     * @param gridRef
     * @param direction
     * @return placements, a list of Placement objects
     */
    public List<Placement> createPlacement(String word, int[] gridRef, int direction){
        //Initialise a List, character array (set equal to the input word.toCharArray()) and a letter pointer
        List<Placement> placements = new ArrayList<Placement>();
        char[] letters = word.toCharArray();
        int letterPtr = 0;

        //If statement for direction
        if(direction == 0){
            //Loop based on the grid reference to create a placement for each tile
            for(int i = gridRef[0]; i < (gridRef[0] + word.length());i++){
                //If the square on the board isn't already occupied then create a placement
                if(!getScrabbleEngineController().boardController.getBoardObject().getBoard()[gridRef[1]][i].isOccupied()){
                    //Create placement
                    Placement temp = new Placement(i,gridRef[1],letters[letterPtr]);
                    //Add the placement to the list of placements
                    placements.add(temp);
                }
                //Increment the letter pointer for the word
                letterPtr++;
            }
        }else if(direction == 1){
            //Loop based on the grid reference to create a placement for each tile
            for(int i = gridRef[1]; i < (gridRef[1] + word.length()); i++){
                //If the square on the board isn't already occupied then create a placement
                if(!getScrabbleEngineController().boardController.getBoardObject().getBoard()[i][gridRef[0]].isOccupied()){
                    //Create placement
                    Placement temp = new Placement(gridRef[0], i, letters[letterPtr]);
                    //Add the placement to the list of placements
                    placements.add(temp);
                }
                //Increment the letter pointer for the word
                letterPtr++;
            }
        }
        //Return the placements variable
        return placements;
    }

    // TODO: Comment pls
    public void updateLastWordsPlayed(String word)
    {
        if(!word.isEmpty())
        {
            this.lastWordsPlayed.add(word);
        }
        else
        {
            throw new IllegalArgumentException("A word played cannot be blank.");
        }
    }

    // TODO: Comment pls
    public void setLastMoveScore(int score)
    {
        if(score  > 0)
        {
            this.lastMoveScore = score;
        }
        else
        {
            throw new IllegalArgumentException("Score of the last move cannot be 0.");
        }
    }

    // TODO: Comment pls
    public ArrayList<String> getLastWordsPlayed()
    {
        return lastWordsPlayed;
    }
}
