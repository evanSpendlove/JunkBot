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
    //instance variables
    private Board gameBoard = new Board();
    private Pool gamePool = new Pool();
    private int turnCounter;

    public Scrabble(Move m, Move m2, Player p){
        gameBoard.placeFirstWord(m, p);
        System.out.println(scoring(m));
        System.out.println(gameBoard);
        gameBoard.placeWord(m2, p);
        System.out.println(scoring(m2));
        System.out.println(gameBoard);
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
     *  method to find any words that have been altered by this play, in order to score them
     *  @param: A move that has been made
     *  @return: the score of the words
     */
    private int findAdditionalWords(Move m){
        int count;
        Placement plays = m.plays.get(0);
        int xCoord = plays.getX();//Set X,Y co-ords to first tile played in move
        int yCoord = plays.getY();
        Square sq = gameBoard.getBoard()[yCoord][xCoord];
        ArrayList<Placement> AdditionalWord = new ArrayList<>();
        int scores=0;
        String word="";

        if(m.getDirection()==1){
            for(count=0;count<m.getPlays().size();count++){
                if(gameBoard.getBoard()[plays.getY()+1][plays.getX()].isOccupied() || gameBoard.getBoard()[plays.getY()-1][plays.getX()].isOccupied()){
                    while(sq.isOccupied()){
                        yCoord++;
                        sq = gameBoard.getBoard()[yCoord][xCoord];
                    }
                    while(sq.isOccupied()){
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        word += Character.toString(sq.getTile().character());
                        yCoord--;
                    }
                    scores+=scoring(new Move(AdditionalWord, word, 0));
                    word="";
                    AdditionalWord.clear();
                }
            }
        }
        else{
            for(count=0;count<m.getPlays().size();count++){
                if(gameBoard.getBoard()[plays.getY()][plays.getX()+1].isOccupied() || gameBoard.getBoard()[plays.getY()][plays.getX()+1].isOccupied()){
                    while(sq.isOccupied()){
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        xCoord++;
                    }
                    xCoord = plays.getX();
                    while(sq.isOccupied()){
                        AdditionalWord.add(new Placement(xCoord, yCoord, sq.getTile().character()));
                        xCoord--;
                    }
                    scores+=scoring(new Move(AdditionalWord, word, 0));
                    word="";
                    AdditionalWord.clear();
                }
            }
        }
        return scores;
    }

    /*
    * Method to find the score of a played word
    * @param the move to be scored
    * @return the total score of the move
     */
    public int scoring(Move m) {
        int letter;//represents the score of each individual tile
        int score = 0;//represents the score of an entire word
        int multi = 1;//represents word multipliers
        Placement tile = m.plays.get(0);
        int x = tile.getX();
        int y = tile.getY();
        Square sq = gameBoard.getBoard()[y][x];
        while (sq.isOccupied()) {
            System.out.println(sq.getType());
            letter = sq.getTile().value();//for each letter in the word, get it's value, and any special tiles
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
                default:
                    break;
            }
            score += letter;//add the value of each tile to total word score
            sq.setType(Square.squareType.REGULAR);//Set type of each square to Regular
            if(m.getDirection()==0){//if the word is horizontal, increment x-axis, else increment y
                x++;
            }
            else{
                y++;
            }
            sq=gameBoard.getBoard()[y][x];//check the next square on the board
        }
        return score * multi;//multiply the total score by the any word multipliers
    }

    /*
    * method to remove value of tiles in a frame at the end of a game
    * @param Player who's score is to be calculated
    * @return total score to be deducted
     */
    public int finalScore(Player p){
        int total=0;
        while(!p.getFrame().isEmpty()){//goes through the frame, adding the scores of each letter to total
            total+=p.getFrame().getTiles().get(0).value();
            p.getFrame().discardTile(p.getFrame().getTiles().get(0));//remove each tile from the frame after getting score
        }
        return total;
    }

    public static void main(String[] args){
        Pool pool = new Pool();
        Frame f = new Frame(pool);
        System.out.println(f);
        Placement q = new Placement(7, 7, f.getTiles().get(0).character());
        Placement w = new Placement(8, 7, f.getTiles().get(1).character());
        Placement u = new Placement(9, 7, f.getTiles().get(2).character());
        List<Placement> d = new ArrayList<>();
        d.add(q);
        d.add(w);
        d.add(u);
        Move m = new Move(d, "testword", 0);
        f.refillFrame(pool);
        Placement w2 = new Placement(7, 6, f.getTiles().get(5).character());
        Placement u2 = new Placement(7, 5, f.getTiles().get(6).character());
        List<Placement> d2 = new ArrayList<>();
        d2.add(w2);
        d2.add(u2);
        Move m2 = new Move(d2, "testword", 1);
        Scrabble x = new Scrabble(m, m2, new Player("ree", f));
    }
}