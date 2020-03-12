package scrabbleGame.gameModel;

import scrabbleGame.*;

/**
 * <h1>UI Class</h1>
 * This represents UI of the game
 * <p></p>
 * Team: JunkBot
 *  * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Reuben Mulligan
 * @version 1.0.0
 * @since 02-03-2020
 */

public class UI{

    //Variables
    private int playerTurn;

    //Setters
    /**
     * Private method: setPlayerTurn(int nextTurn)
     * Use: Used to set whose turn it is next
     * @param nextTurn Takes an integer for which player's turn is next e.g. player 2 is next, given 2
     */
    private void setPlayerTurn(int nextTurn){
        this.playerTurn = nextTurn;
    }

    //Getters

    /**
     * Public method: getPlayerTurn()
     * @return int Returns the player number of whose turn is next
     */
    public int getPlayerTurn(){
        return this.playerTurn;
    }

}