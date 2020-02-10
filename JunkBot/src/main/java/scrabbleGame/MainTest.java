package scrabbleGame;

/*
    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 */
public class MainTest
{
    public static void main(String[] args)
    {
        try
        {
            // Create a new Player and fill their fields
            Player p = new Player("Bob");
            Frame f = new Frame();
            Pool pool = new Pool();
            f.refillFrame(pool);
            p.setFrame(f);

            // Play several moves
            p.getFrame().playTile(p.getFrame().getTiles().get(0));
            p.getFrame().playTile(p.getFrame().getTiles().get(1));
            p.getFrame().playTile(p.getFrame().getTiles().get(2));

            System.out.println("Success! Test passed.");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
