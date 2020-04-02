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
import scrabbleGame.UI.utilityPanes.SquarePane;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.gameEngine.UI;
import scrabbleGame.gameModel.Square;
import scrabbleGame.gameModel.Tile;

import static org.junit.jupiter.api.Assertions.*;

/*
    SquarePane JUnit & TestFX Test Class

    Purpose: Unit-Testing the SquarePane Class of the scrabbleGame.UI.utilityPanes package.
    Summary: This class attempts to thoroughly test the SquarePane Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 26-02-2020
 */

@ExtendWith(ApplicationExtension.class)
public class SquarePaneTest {

    @Start
    private void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(UI.class.getResource("/view/scrabble.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Scrabble!");
        // stage.show();
    }

    @Before
    public void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    /*
      Goal: To test that the blank constructor works as expected.
      Testing Method: Instantiate SquarePane using the blank constructor and verify that no exception is thrown.
     */
    @Test
    public void testBlankConstructor() {
        try {
            SquarePane sp = new SquarePane();
        } catch (Exception ex) {
            fail("No exception should be thrown when constructing a valid SquarePane.");
        }
    }

    /*
      Goal: To test that the full constructor works as expected.
      Testing Method: Instantiate SquarePane using the full constructor and check the tile was correctly set.
     */
    @Test
    public void testFullConstructor()
    {
        try
        {
            Square s = new Square();
            SquarePane sp = new SquarePane(s);

            assertEquals(s.getType(), sp.getSquare().getType());
            assertEquals(s.isOccupied(), sp.getSquare().isOccupied());
            assertEquals(s.toString(), sp.getSquare().toString());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when constructing a valid SquarePane.");
        }
    }

    /*
      Goal: To test that updateSquare() works as expected.
      Testing Method: Set a square, then update it, then verify that the new square is set.
     */
    @Test
    public void testUpdateSquare()
    {
        try
        {
            Square s = new Square();
            SquarePane sp = new SquarePane(s);

            Square star = new Square();
            star.setType(Square.squareType.STAR);

            sp.updateSquare(star);
            assertEquals(star, sp.getSquare());
            assertEquals(star.getType(), sp.getSquare().getType());
            assertEquals(star.toString(), sp.getSquare().toString());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("No exception should be thrown when updating the Square on a SquarePane");
        }
    }

    /*
      Goal: To test that one can add a tile to the SquarePane.
      Testing Method: Add a tile and verify that it has been added.
     */
    @Test
    public void testAddTile()
    {
        try
        {
            Square s = new Square();
            SquarePane sp = new SquarePane(s);

            Tile t = Tile.T;

            sp.addTile(new TilePane(t));

            assertEquals(t, sp.getTilePane().getTile());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when adding a Tile to a SquarePane.");
        }
    }

    /*
      Goal: To test that one can remove a tile from a SquarePane.
      Testing Method: Add a tile to a SquarePane, then remove it and verify the same tile was removed and that there is no longer a tile placed.
     */
    @Test
    public void testRemoveTile()
    {
        try
        {
            Square s = new Square();
            SquarePane sp = new SquarePane(s);

            Tile t = Tile.T;
            TilePane tileSet = new TilePane(t);

            sp.addTile(tileSet);
            assertEquals(t, sp.getTilePane().getTile());

            TilePane removedTile = sp.removeTile();
            assertEquals(tileSet, removedTile);
            assertNull(sp.getTilePane());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when removing a Tile from a SquarePane.");
        }
    }

    /*
      Goal: To test that the toString() method works as expected.
      Testing Method: Call toString() and verify that it returns the expected string.
     */
    @Test
    public void testToString()
    {
        try
        {
            Square s = new Square();
            s.setType(Square.squareType.STAR);
            SquarePane sp = new SquarePane();

            assertEquals("[NULL]", sp.toString());

            SquarePane s2 = new SquarePane(s);
            assertEquals("[S, STAR, ]", s2.toString());
        }
        catch(Exception ex)
        {
            fail("No exception should be thrown when calling toString()");
        }
    }
}