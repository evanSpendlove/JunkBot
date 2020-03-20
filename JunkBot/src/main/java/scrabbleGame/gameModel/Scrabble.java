package scrabbleGame.gameModel;

import java.util.ArrayList;

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
    //instance variables
    private Board gameBoard = new Board();
    private Pool gamePool = new Pool();
    private int turnCounter;

    public Scrabble(Player one, Player two){
        Game(one, two);
    }

    public Board getGameBoard() { return gameBoard; }

    private void setGameBoard(Board gameBoard) { this.gameBoard = gameBoard; }

    public Pool getGamePool() { return gamePool; }

    private void setGamePool(Pool gamePool) { this.gamePool = gamePool; }

    public void Game(Player Player1, Player Player2) {
    }

    private int order() {
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
            return 1;
        }
        return 2;
    }

    /*
     *  method for providing scoring for word placements
     *  @param: A move to be scored
     *  @return: the score of the move
     */

    private int findPerpendicularWords(Move m){
        int count=0;
        Placement plays = m.plays.get(count);
        int xCoord = plays.getX();
        int yCoord = plays.getY();
        Square sq = gameBoard.getBoard()[yCoord][xCoord];
        ArrayList<Placement> PerpendicularWord = new ArrayList<>();
        int scores=0;
        String word="";

        if(m.getDirection()==1){
            for(count=0;count<m.getPlays().size();count++){
                if(gameBoard.getBoard()[plays.getY()+1][plays.getX()].isOccupied() || gameBoard.getBoard()[plays.getY()-1][plays.getX()].isOccupied()){
                    while(sq.isOccupied()){
                        yCoord++;
                    }
                    while(sq.isOccupied()){
                        PerpendicularWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        word+=sq.getTile().character();
                        yCoord--;
                    }

                    scores+=scoring(new Move(PerpendicularWord, word, 0));
                    word="";
                    PerpendicularWord.clear();
                }
            }
        }
        else{
            for(count=0;count<m.getPlays().size();count++){
                if(gameBoard.getBoard()[plays.getY()][plays.getX()+1].isOccupied() || gameBoard.getBoard()[plays.getY()][plays.getX()+1].isOccupied()){
                    while(sq.isOccupied()){
                        PerpendicularWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        xCoord++;
                    }
                    xCoord = plays.getX();
                    while(sq.isOccupied()){
                        PerpendicularWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        xCoord--;
                    }
                    scores+=scoring(new Move(PerpendicularWord, word, 0));
                    word="";
                    PerpendicularWord.clear();
                }
            }
        }
        return scores;
    }

    public int scoring(Move m) {
        int letter=0;//represents the score of each individual tile
        int score = 0;//represents the score of an entire word
        int multi = 1;//represents word multipliers
        Placement tile = m.plays.get(0);
        int x = tile.getX();
        int y = tile.getY();
        Square sq = gameBoard.getBoard()[y][x];
        while (sq.isOccupied()) {
            letter = sq.getTile().value();//for each letter in the word, get it's value, and any special tiles
            System.out.println(letter);
            switch (sq.getType()) {//Apply letter multipliers to 'letter', and word multipliers to 'multi'
                case DB_LETTER:
                    letter *= 2;
                    break;
                case TR_LETTER:
                    letter *= 3;
                    break;
                case DB_WORD:
                case STAR:
                    multi *= 2;
                    break;
                case TR_WORD:
                    multi *= 3;
                    break;
            }
            score += letter;//add the value of each tile to total word score
            if(m.getDirection()==0){//if the word is horizontal, increment x-axis, else increment y
                x++;
            }
            else{
                y++;
            }
        }
        return score * multi;
    }

    /*
    * method to remove value of tiles in a frame at the end of a game
    * @param Player who's score is to be calculated
     */
    public void finalScore(Player p){
        Frame f = p.getFrame();
        int count = 0;
        int total=0;
        while(!f.isEmpty()){
            total+=f.getTiles().get(count).value();
        }
        p.setScore(p.getScore()-total);
    }

    public static void main(String[] args){
        System.out.print("ree");
    }
}