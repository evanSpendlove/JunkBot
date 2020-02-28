package scrabbleGame;

import java.util.ArrayList;
import java.util.List;

/*
    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 */
public class MainTest
{
    public static void main(String[] args)
    {
        Board test = new Board();
        ArrayList<Tile> x = new ArrayList<>();
        for(int ew=0;ew<5;ew++)
            x.add(Tile.getInstance('Q'));
        x.add(Tile.getInstance('U'));
        x.add(Tile.getInstance('E'));
        Frame q = new Frame(x);
        Player one = new Player("ree", 0, q);

        Placement t = new Placement(7, 7, 'Q');
        Placement t1 = new Placement(8, 7, 'u');
        Placement t2 = new Placement(9, 7, 'e');
        List<Placement> y= new ArrayList<>();
        y.add(t);
        y.add(t1);
        y.add(t2);
        int success1 = test.placeFirstWord(new Move(y, "QUE", 0), one);
        if(success1 == 2)
        {
            System.out.println("Test 1 - Adding the first word - Success!");
        }

        Placement o = new Placement(8, 6, 'D');
        Placement o1 = new Placement(8, 8, 'E');

        x.clear();
        for(int ew=0;ew<5;ew++)
            x.add(Tile.getInstance('Q'));
        x.add(Tile.getInstance('D'));
        x.add(Tile.getInstance('E'));
        one.setFrame(new Frame(x));

        List<Placement> z = new ArrayList<>();
        z.add(o);
        z.add(o1);

        int success2 = test.placeWord(new Move(z, "DUE", 1), one);
        if(success2 == 2)
        {
            System.out.println("Test 2 - Adding the second word - Success!");
        }

        Placement z0 = new Placement(9, 6, 'A');
        Placement z1 = new Placement(9, 8, 'B');
        Placement z2 = new Placement(9, 9, 'C');
        Placement z3 = new Placement(9, 10, 'D');

        x.clear();
        x.add(Tile.getInstance('A'));
        x.add(Tile.getInstance('A'));
        x.add(Tile.getInstance('B'));
        x.add(Tile.getInstance('B'));
        x.add(Tile.getInstance('C'));
        x.add(Tile.getInstance('C'));
        x.add(Tile.getInstance('D'));

        one.setFrame(new Frame(x));

        z.clear();
        z.add(z0);
        z.add(z1);
        z.add(z2);
        z.add(z3);

        int success3 = test.placeWord(new Move(z, "AEBCD", 1), one);
        if(success3 == 2)
        {
            System.out.println("Test 3 - Adding a third (hooked) word - Success");
        }
    }
}
