import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.gameEngine.UI;
import scrabbleGame.gameModel.Tile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
    TilePane JUnit & TestFX Test Class

    Purpose: Unit-Testing the TilePane Class of the scrabbleGame.UI.utilityPanes package.
    Summary: This class attempts to thoroughly test the TilePane Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 26-02-2020
 */

@ExtendWith(ApplicationExtension.class)
public class TilePaneTest
{

    @Start
    private void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(UI.class.getResource("/view/scrabble.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Scrabble!");
        // stage.show();
    }

    @Before
    public void setup() throws Exception
    {
        FxToolkit.registerPrimaryStage();
    }

    /*
      Goal: To test that the blank constructor works as expected.
      Testing Method: Instantiate TilePane using the blank constructor and verify that no exception is thrown.
     */
    @Test
    public void testBlankConstructor()
    {
        try
        {
            TilePane tp = new TilePane();
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when constructing a valid TilePane.");
        }
    }

    /*
      Goal: To test that the full constructor works as expected.
      Testing Method: Instantiate TilePane using the full constructor and check the tile was correctly set.
     */
    @Test
    public void testFullConstructor()
    {
        try
        {
            Tile t = Tile.getInstance('a');
            TilePane tp = new TilePane(t);

            assertEquals(t.character(), tp.getTile().character());
            assertEquals(t.value(), tp.getTile().value());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when constructing a valid TilePane.");
        }
    }

    /*
      Goal: To test the tile getter works as expected.
      Testing Method: Set a tile using the constructor and test that the getter returns the appropriate object.
     */
    @Test
    public void testTileGetter()
    {
        try
        {
            Tile t = Tile.getInstance('a');
            TilePane tp = new TilePane(t);

            assertEquals(t.character(), tp.getTile().character());
            assertEquals(t.value(), tp.getTile().value());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when getting the tile.");
        }
    }

    /*
      Goal: To test the tile setter works as expected.
      Testing Method: Set a tile using the constructor and test that the setter sets the appropriate object.
     */
    @Test
    public void testTileSetter()
    {
        try
        {
            Tile t = Tile.getInstance('a');
            TilePane tp = new TilePane();

            tp.setTile(t);

            assertEquals(t.character(), tp.getTile().character());
            assertEquals(t.value(), tp.getTile().value());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when setting the tile.");
        }
    }

    /*
      Goal:
      Testing Method:
     */
    @Test
    public void testUpdateTile()
    {
        try
        {
            Tile t = Tile.getInstance('a');
            TilePane tp = new TilePane();

            tp.updateTile(t);

            assertEquals(t.character(), tp.getTile().character());
            assertEquals(t.value(), tp.getTile().value());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when updating the tile.");
        }
    }

    /*
      Goal: To test that the toString() method works as expected.
      Testing Method: Call toString() and check that it matches the expected value.
     */
    @Test
    public void testToString()
    {
        try
        {
            Tile t = Tile.getInstance('a');
            TilePane tp = new TilePane();

            assertEquals("[NULL]", tp.toString());

            tp.setTile(t);
            assertEquals("[A]", tp.toString());
        }
        catch(Exception ex) {
            fail("No exception should be thrown when setting the tile.");
        }
    }
}
