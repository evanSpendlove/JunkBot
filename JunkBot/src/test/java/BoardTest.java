
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
}