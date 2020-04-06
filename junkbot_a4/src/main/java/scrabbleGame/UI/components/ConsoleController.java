package scrabbleGame.UI.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scrabbleGame.exceptions.TileNotFound;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameModel.Move;
import scrabbleGame.gameModel.Placement;
import scrabbleGame.gameModel.Player;
import scrabbleGame.gameModel.Tile;

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
     * Stores the lastMove played
     */
    private Move lastMove; // Changed to Move type instead of String

    /**
     * String holding the help message for the console command "help"
     */
    private String helpMessage = "<--------- Help Message --------->\n" +
            "Commands: {Start, Quit, Help, Exchange, (Move), Username, Challenge}\n" +
            "Start: Can only be used when no game has been started, starts the game\n" +
            "Quit: Quits the game and closes the window\n" +
            "Help: Help prints this message\n" +
            "Exchange: Exchange is used to change letters on your frame. Format: Exchange a b c \nExchanging letters will end your turn\n" +
            "(Move): To play a word, you need to use format <GridRef> <direction> <Word> \nE.g. H8 Across Hello \n" +
            "Username: Username is used to set the player names. Format: Username <name> <playernum>\n" +
            "Challenge: To challenge that the last word played by the other player is a real word (in the dictionary), type <Challenge> <Word>. E.g. Challenge Bobz\n" +
            "<--------- End --------->";

    // Getters and Setters

    /**
     * Getter for lastCommand
     * @return lastCommand
     */
    public String getLastCommand() {
        return lastCommand;
    }

    /**
     * Setter for lastCommand
     * @param lastCommand Pass the string to be set
     */
    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    /**
     * Getter for ScrabbleEngineController
     * @return scrabbleEngineController
     */
    public ScrabbleEngineController getScrabbleEngineController() {
        return scrabbleEngineController;
    }

    /**
     * Setter for ScrabbleEngineController
     * @param scrabbleEngineController Pass the controller to be set
     */
    public void setScrabbleEngineController(ScrabbleEngineController scrabbleEngineController) {
        this.scrabbleEngineController = scrabbleEngineController;
    }

    /**
     * Initialisation method to display the Console
     */
    @FXML
    void initialise()
    {
        // Autoscroll the console display to the bottom.
        consoleDisplay.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
            {
                consoleDisplay.setScrollTop(Double.MAX_VALUE);
            }
        });
    }

    /**
     * submitCommand Method acts on the actionEvent from the text field box. It then grabs the input command, does some validation then hands it to parseInput
     * It also appends a new line character and sets the lastCommand variable to the new command. It also updates the console window to show the last command
     * @param event Pass the event to be parsed.
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
            consoleDisplay.appendText(newCommand);

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

            consoleDisplay.appendText(s);
        }
    }


    /**
     * <p>
     * parseInput method takes an input string, validates it then checks it against a set of acceptable commands.
     * <p>
     * It uses a switch statement with a flag to handle what to output.
     * It can handle inputs such as: Quit, Start, Help, Exchange, Username, Move
     * @param input The command string to be parsed
     */
    private void parseInput(String input)
    {
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
            if(split[0].equalsIgnoreCase("Challenge")){
                flag = 6;
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
                        setLastMove(newMove);
                        // Update the score
                        int newScore = getScrabbleEngineController().scoring(newMove);
                        getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum()).increaseScore(newScore);
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
                        setLastMove(newMove);
                        // Update the score
                        int newScore = getScrabbleEngineController().scoring(newMove);
                        getScrabbleEngineController().getPlayer(getScrabbleEngineController().getCurrentPlayerNum()).increaseScore(newScore);
                    }
                    else {
                        //Print an error message
                        addLineToConsole("Failed to play a word");
                        break;
                    }
                }

                getScrabbleEngineController().updateScore();
                setLastMove(newMove);

                //Switch the turn after move complete
                getScrabbleEngineController().switchPlayerDelay();
                break;

            case 6:
                try
                {
                    challengeWord(split[1]);
                }
                catch(Exception ex)
                {
                    if(ex.getClass() == ArrayIndexOutOfBoundsException.class)
                    {
                        addLineToConsole("Error: Incorrect usage - missing word.\n<Challenge> <Word>");
                    }
                    else
                    {
                        ex.printStackTrace();
                        addLineToConsole("Failed to challenge a word - TileNotFound.");
                    }
                }
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
     * @param word Pass the word to be set for the placement.
     * @param gridRef Pass the array [x, y] of integer grid references.
     * @param direction Pass the direction of the placement : 0 = horizontal, 1 = vertical
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


    /**
     * Method updateLastWordsPlayed takes a word and adds it to the lastWordsPlayed function
     * @param word Pass the word to be added to lastWordsPlayed
     */
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

    /**
     * Setter for lastMoveScore
     * @param score Pass the score to update lastMoveScore with.
     */
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

    /**
     * Getter for lastMoveScore
     * @return int Returns the lastMoveScore integer variable
     */
    public int getLastMoveScore() {
        return lastMoveScore;
    }

    /**
     * Getter for lastWordsPlayed
     * @return ArrayList(String) Returns the lastWordsPlayed variable.
     */
    public ArrayList<String> getLastWordsPlayed()
    {
        return lastWordsPlayed;
    }

    /**
     * Method challengeWord takes a word and checks if it is a real word against a set dictionary, if it isn't it removes the previous players turn.
     * If the challenge fails, it ends the players turn.
     * @param word Pass the word to be challenge.
     * @throws TileNotFound Throws a TileNotFound if the tile to be removed for a move cannot be found on the board.
     */
    public void challengeWord(String word) throws TileNotFound {

        //System.out.println("Last words played: " + lastWordsPlayed);

        if(word.isEmpty()) // If empty
        {
            addLineToConsole("Error: Incorrect usage - missing word.\n<Challenge> <Word>"); // Warn player
            return;
        }

        int prevPlayer = 0;

        // Find previous player number

        if(getScrabbleEngineController().getCurrentPlayerNum() == 1){
            prevPlayer = 2;
        }
        else{
            prevPlayer = 1;
        }

        // Cannot challenge a word that wasn't played as part of last move
        if(!getLastWordsPlayed().contains(word)){
            addLineToConsole("Cannot challenge a word that wasn't played. Try again");
            return;
        }

        // Check in dictionary
        if(getScrabbleEngineController().getDictionary().checkWord(word) == false){
            removeWordFromBoard(getLastMove());
            addTilesToFrame(getLastMove(), prevPlayer);
            deductScore(getLastMoveScore(), prevPlayer);
            addLineToConsole("Challenge successful");
            return;
        }
        else {
            addLineToConsole("Challenge unsuccessful");
            getScrabbleEngineController().switchPlayerDelay();
        }
    }

    /**
     * Method removeWordFromBoard takes a move and removes the tiles from the board.
     * @param word Pass the move to be removed from the board.
     * @throws TileNotFound Throws a TileNotFound if the tile to be removed for a move cannot be found on the board.
     */
    public void removeWordFromBoard(Move word) throws TileNotFound
    {
        // Remove each tile played on the board as part of the move
        for(int i = 0; i < word.getPlays().size(); i++){
            int x = word.getPlays().get(i).getX();
            int y = word.getPlays().get(i).getY();
            getScrabbleEngineController().boardController.removeTileFromBoard(x,y);
        }
    }

    /**
     * Method addTilesToFrame takes a move and a player number and adds the tiles they played from the last turn back to their frame
     * @param lastMove Pass the lastMove - the tiles from this will be added to the frame.
     * @param prevPlayer Pass the previous player whose frame is to be updated.
     */
    public void addTilesToFrame(Move lastMove, int prevPlayer){
        Player prevPlayerObj = getScrabbleEngineController().getPlayer(prevPlayer);

        // Add the tiles from the last move into the previous player's frame.

        for(int i = 0; i < lastMove.getPlays().size(); i++){
            char letter = lastMove.getPlays().get(i).getLetter();
            prevPlayerObj.getFrame().addTile(Tile.getInstance(letter));
        }
    }

    /**
     * Method deductScore takes a score for the last word and a player number and adjusts the players score accordingly
     * @param scoreLastWord Pass the score of the last word to be deducted from the Player's score.
     * @param prevPlayer Pass the player number to be updated
     */
    public void deductScore(int scoreLastWord, int prevPlayer){
        Player prevPlayerObj = getScrabbleEngineController().getPlayer(prevPlayer);
        int newScore = prevPlayerObj.getScore() - scoreLastWord;
        prevPlayerObj.setScore(newScore); // Set score for Player object
        getScrabbleEngineController().updateScore(); // Update visual scoreboard
    }

    /**
     * Setter for lastMove.
     * @param m Pass the move to be set.
     */
    public void setLastMove(Move m){
        this.lastMove = m;
    }

    /**
     * Getter for lastMove which returns the lastMove played.
     * @return lastMove Returns the lastMove played.
     */
    public Move getLastMove(){
        return this.lastMove;
    }
}
