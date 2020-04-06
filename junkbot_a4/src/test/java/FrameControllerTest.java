import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.Test;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameModel.*;
import scrabbleGame.UI.components.FrameController;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
/*
    FrameControllerTest JUnit Test Class

    Purpose: Unit-Testing the FrameController Class of the UI package.
    Summary: This class attempts to thoroughly test the FrameController Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Cal Nolan
    Version: 1.0.0
    Since: 25-03-2020
 */

public class FrameControllerTest {

    /*
       Goal:To assert that the clearFrame() method clears a frame as expected
       Testing Method:instantiate a full frame, and then call clearFrame to empty it.
     */
    @Test
    public void testClearFrame(){
        FrameController fc = new FrameController();
        fc.setFrameObj(new Frame(new Pool()));
        fc.setFramePanes(new GridPane());
        assertTrue(fc.getFrameObj()!=null);
        fc.clearFrame();
        assertEquals(null, fc.getFrameObj());
    }

    /*
      Goal:Ensure that the exchangeCharacters method swaps relevant characters for new ones, and
      throws an exception where necessary
      Testing Method:Call an exchange with invalid letters, and check for relevant error message
                     Call an exchange with no letters, and check for unsuccessful return statement
                     Call an exchange with valid letters, and check for successful return statement
     */
    @Test
    public void testExchangeCharacters(){
        FrameController fc = new FrameController();
        fc.setFramePanes(new GridPane());
        fc.setScrabbleEngineController(new ScrabbleEngineController());
        fc.setFrameObj(new Frame());
        try{
            char[] ch = {'A', 'B', 'C'};
            fc.exchangeTiles(ch);
            fail("Should throw exception");
        }catch (Exception ex){
            assertEquals(ex.getLocalizedMessage(), "Cannot exchange tile that you don't have");
        }

        fc.setFrameObj(new Frame(new Pool()));
        char[] ch = new char[0];
        assertEquals(-1, fc.exchangeTiles(ch));

        char[] ch2 = {fc.getFrameObj().getTiles().get(0).character()};
        assertEquals(1, fc.exchangeTiles(ch2));
    }

    /*
      Goal:Ensure playWord method successfully removes relevant letters from the frame
      Testing Method:Create a list of the last 5 tiles in the frame
                     Call playWord on the first two tiles in the Frame
                     Ensure that the list of tiles matches what remains in the frame
     */
    @Test
    public void testPlayWord(){
        FrameController fc = new FrameController();
        fc.setFramePanes(new GridPane());
        fc.setScrabbleEngineController(new ScrabbleEngineController());
        fc.setFrameObj(new Frame(new Pool()));
        List<Placement> l = new ArrayList<>();
        l.add(new Placement(4, 5, fc.getFrameObj().getTiles().get(0).character()));
        l.add(new Placement(4, 6, fc.getFrameObj().getTiles().get(1).character()));
        List<Tile> l2 = new ArrayList<>();
        for(int x=2;x<7;x++){
            l2.add(fc.getFrameObj().getTiles().get(x));
        }
        fc.playWord(new Move(l, "word", 0));
        assertEquals(fc.getFrameObj().getTiles(), l2);
    }

    /*
      Goal:Ensure the playTile method functions as intended
      Testing Method: Use a defined ArrayList for the frame
                      Make another List excluding tiles that should be played
                      Compare Frame to new list
     */
    @Test
    public void testPlayTile(){
        FrameController fc = new FrameController();
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(Tile.getInstance('A'));
        tiles.add(Tile.getInstance('B'));
        tiles.add(Tile.getInstance('C'));
        tiles.add(Tile.getInstance('D'));
        tiles.add(Tile.getInstance('E'));
        tiles.add(Tile.getInstance('F'));
        tiles.add(Tile.getInstance('G'));
        fc.setFramePanes(new GridPane());
        fc.setFrameObj(new Frame(tiles));
        fc.setRack(new TilePane[7]);
        ArrayList<Tile> tiles2 = new ArrayList<>();
        tiles2.add(Tile.getInstance('A'));
        tiles2.add(Tile.getInstance('C'));
        tiles2.add(Tile.getInstance('D'));
        tiles2.add(Tile.getInstance('F'));
        tiles2.add(Tile.getInstance('G'));
        fc.playTile(1);
        fc.playTile(3);
        assertEquals(fc.getFrameObj().getTiles(), tiles2);
        fc.playTile(4);
        tiles2.remove(4);
        assertEquals(fc.getFrameObj().getTiles(), tiles2);
    }
}
