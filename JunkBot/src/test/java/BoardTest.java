
import org.junit.jupiter.api.Test;
import scrabbleGame.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/*
    BoardTest JUnit Test Class

    Purpose: Unit-Testing the Board Class of the scrabbleGame package.
    Summary: This class attempts to thoroughly test the Board Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Reuben Mulligan, Cal Nolan
    Version: 1.0.0
    Since: 21-02-2020
 */

public class BoardTest
{

    @Test
    public void testFullConstructor(){
        try{
            Board test = new Board();
            assertTrue(true);
        }catch(Exception ex){
            fail("Board should initialise properly and not throw any errors");
        }
    }

    @Test
    public void testGetBoard(){
        try{
            Board test1 = new Board();
            Board test2 = new Board();
            assertEquals(test1.getBoard(), test2.getBoard());
        }catch (Exception ex){
            fail("No exceptions thrown");
        }
    }

    @Test
    public void testGetStatus(){
        try{
            Board test = new Board();
            Board test2 = new Board();

            assertEquals(test.getStatus(), test2.getStatus());
        }catch(Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void testAddandGetWordsPlayed(){
        try{
            Board test = new Board();
            ArrayList<Placement> ply;
            ArrayList<String> check;
            check = new ArrayList<>();
            check.add("DUE");
            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            Move m = new Move(ply, "DUE", 1);
            test.addWordPlayed(m);
            assertEquals(check, test.getWordsPlayed());

        }catch(Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void testResetBoard(){
        try{
            Board test = new Board();
            Board test2 = new Board();
            ArrayList<Placement> ply;
            Move m;
            ply = new ArrayList<>();

            Placement p1 = new Placement(8, 6, 'D');
            Placement p2 = new Placement(8, 8, 'E');

            ply.add(p1);
            ply.add(p2);

            m = new Move(ply, "DUE", 1);
            Player a = new Player("Reuben");

            test.placeWord(m,a);
            test.resetBoard();
            assertEquals(test.getBoard(), test2.getBoard());
            assertEquals(test.getStatus(), test2.getStatus());
            assertEquals(test.getWordsPlayed(),test2.getWordsPlayed());
        }catch(Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    @Test
    public void testAddWord(){

        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<Tile>();
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);
        Placement t = new Placement(7, 7, 'Q');
        List<Placement> y= new ArrayList<>();
        Move e = new Move(y, "Q", 0);

        for(int ew=0;ew<7;ew++)
            x.add(Tile.getInstance('Q'));
        y.add(t);
        test.placeFirstWord(e, one);

        assertEquals("Q", test.wordsPlayed.get(0));
        assertEquals('Q', Tile.getInstance(board[7][7].getTile));
        assertEquals(Square.squareType.REGULAR, board[7][7].getType);
    }

    @Test
    public void testHooked(){

        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<Tile>();
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);
        Placement t = new Placement(7, 7, 'Q');
        List<Placement> y= new ArrayList<>();
        Move e = new Move(y, "Q", 0);

        for(int ew=0;ew<7;ew++)
            x.add(Tile.getInstance('Q'));
        y.add(t);
        test.placeFirstWord(e, one);

        t.setX(8);
        assertEquals(2, test.placeWord(e, one));//succeeds, as it's connected to another word
        t.setX(11);
        assertEquals(-1, test.placeWord(e, one));//fails, as it's disconnected
    }

    @Test
    public void testFirstMove(){

        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<Tile>();
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);
        Placement t = new Placement(7, 7, 'Q');
        List<Placement> y= new ArrayList<>();
        Move e = new Move(y, "Q", 0);

        for(int ew=0;ew<7;ew++)
            x.add(Tile.getInstance('Q'));
        y.add(t);
        t.setX(8);

        assertEquals(-1, test.placeFirstWord(new Move(y, "Q", 1), one));//fails, as there is no tile on central square

        y.add(new Placement(7, 7, 'Q'));

        assertEquals(2, test.placeFirstWord(new Move(y, "Q", 0), one));//passes, as there is a tile on central square
        assertEquals(-1, test.placeFirstWord(new Move(y, "Q", 0), one));//fails, because there is already a word on the board
    }

    @Test
    public void testContainsLetters()
    {
        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<Tile>();
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);
        Placement t = new Placement(7, 7, 'Q');
        List<Placement> y= new ArrayList<>();
        Move e = new Move(y, "Q", 0);

        for(int ew=0;ew<7;ew++)
            x.add(Tile.getInstance('Q'));
        y.add(t);
        test.placeFirstWord(e, one);
        t.setX(8);


        t.setLetter('R');
        assertEquals(-1, test.placeWord(e, one));//fails, because the letter R is not in the frame
        t.setLetter('Q');
        assertEquals(2, test.placeWord(e, one));//passes because Q is in the frame
    }

    @Test
    public void testConnected(){

        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<Tile>();
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);
        Placement t = new Placement(7, 7, 'Q');
        List<Placement> y= new ArrayList<>();
        Move e = new Move(y, "Q", 0);

        for(int ew=0;ew<7;ew++)
            x.add(Tile.getInstance('Q'));
        y.add(t);
        Placement t2 = new Placement(9, 7, 'Q');
        y.add(t2);

        assertEquals(-1, test.placeFirstWord(e, one));//fails, as the two tiles aren't connected
        t2.setX(8);
        assertEquals(2, test.placeFirstWord(e, one));//passes, as the two tiles are connected
        t.setX(6);
        t2.setX(9);
        assertEquals(2, test.placeWord(e, one));//passes, as the tiles are connected via another tile
    }

    @Test
    public void testInLine(){

        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<Tile>();
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);
        Placement t = new Placement(7, 7, 'Q');
        List<Placement> y= new ArrayList<>();
        Move e = new Move(y, "Q", 0);

        for(int ew=0;ew<7;ew++)
            x.add(Tile.getInstance('Q'));

        Placement t2 = new Placement(8, 7, 'Q');
        Placement t3 = new Placement(8, 8, 'Q');

        y.add(t);
        y.add(t2);
        y.add(t3);

        assertEquals(-1, test.placeFirstWord(e, one));//fails, as the tiles aren't in a line

        t3.setX(9);
        t3.setY(7);

        assertEquals(2, test.placeFirstWord(e, one));//passes, as all tiles are in a straight line
    }
}