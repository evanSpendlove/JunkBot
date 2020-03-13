package scrabbleGame.gameModel;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Scrabble Class</h1>
 * This represents the game itself, runs initial setup
 * <p></p>
 * Team: JunkBot
 *  * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Cal Nolan
 * @version 1.0.0
 * @since 02-03-2020
 */

public class Scrabble {

    private Board gameBoard = new Board();
    private Pool gamePool = new Pool();

    public Scrabble(Player one, Player two){
        Game(one, two);
    }

    public void Game(Player Player1, Player Player2){

        Player First = order(Player1, Player2);
        Player Second;

        if(Player1.equals(First)) {
            Second = Player2;
        }
        else {
            Second = Player1;
        }
        Player1.setFrame(new Frame(gamePool));
        Player2.setFrame(new Frame(gamePool));

        System.out.print("Player " + First.getUsername() + " will go first");
    }

    private Player order(Player player1, Player player2) {
        Tile tile1 = gamePool.draw();
        Tile tile2 = gamePool.draw();

        while(tile1.character() == tile2.character()){

            tile1 = gamePool.draw();
            tile2 = gamePool.draw();
        }
        System.out.println(tile1.character());
        System.out.println(tile2.character());
        gamePool.reset();

        if (tile1.character() < tile2.character()) {
            return player1;
        }
        return player2;
    }

    public Board getGameBoard() { return gameBoard; }

    private void setGameBoard(Board gameBoard) { this.gameBoard = gameBoard; }

    public Pool getGamePool() { return gamePool; }

    private void setGamePool(Pool gamePool) { this.gamePool = gamePool; }

    public int scoring(Move m) {
        int letter=0;
        int score = 0;
        int multi = 1;
        Placement tile = m.plays.get(0);
        int x = tile.getX();
        int y = tile.getY();
        Square sq = gameBoard.getBoard()[y][x];
        while (sq.isOccupied()) {
            letter = sq.getTile().value();
            System.out.println(letter);
            switch (sq.getType()) {
                case DB_LETTER:
                    letter *= 2;
                    break;
                case TR_LETTER:
                    letter *= 3;
                    break;
                case DB_WORD:
                    multi *= 2;
                    break;
                case TR_WORD:
                    multi *= 3;
                    break;
            }
            score += letter;
            if(m.getDirection()==1){
                x++;
            }
            else{
                y++;
            }
        }
        return score * multi;
    }

    public static void main(String[] args){
    }
}