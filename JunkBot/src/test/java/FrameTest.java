
import org.junit.jupiter.api.Test;
import scrabbleGame.Frame;
import scrabbleGame.Pool;
import scrabbleGame.Tile;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/*
    FrameTest JUnit Test Class

    Purpose: Unit-Testing the Frame Class of the scrabbleGame package.
    Summary: This class attempts to thoroughly test the Frame Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 07-02-2020
 */
public class FrameTest
{
    /*
      Goal: To test the full constructor of the Frame Class
      Testing Method: Create an array list of tiles of size 7 and hand it to the frame. Frame shouldn't throw any errors
     */
    @Test
    public void testFullConstructor()
    {
        try{

            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            // Create a frame and hand it the array list
            Frame test = new Frame(tiles);

            // Pass the test if no exceptions are thrown
            assertTrue(true);

        }catch(Exception ex){

            // Fail the test if an exception is thrown for valid input
            fail("Constructor should not throw an exception when handed a correct number of tiles");

        }
    }

    /*
      Goal: To test if the constructor handles undersized tile arrays properly
      Testing Method: Create an array of size 6 and see if any errors are thrown
     */
    @Test
    public void testUndersizedConstructor()
    {
        try{

            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);

            // Attempt to create a frame with the array list
            Frame test = new Frame(tiles);

            // Fail the test if the code gets to this point
            fail("Test should not reach this point and an exception should be thrown");

        }catch (Exception ex){

            // Pass the test if an exception has been thrown
            assertTrue(true);

        }
    }

    /*
      Goal: To test if the constructor handles oversized tile arrays properly
      Testing Method: Create an array of size 8 and see if any errors are thrown
     */
    @Test
    public void testOversizedConstructor(){
        try{

            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);
            tiles.add(Tile.H);

            // Create a frame and hand it tiles
            Frame test = new Frame(tiles);

            // Fail the test if the code reaches this point
            fail("Test should not reach this point and an exception should've been thrown");

        }catch (Exception ex){

            // Pass the test if an exception is thrown
            assertTrue(true);

        }
    }

    /*
      Goal: To test if the constructor handles pool
      Testing Method: Pass a pool object to the constructor and test that the Frame object contains 7 tiles.
     */
    @Test
    public void testPoolConstructor()
    {
        try{
            Pool p = new Pool();
            Frame f = new Frame(p);

            int i = 0;

            while(!f.isEmpty()) // Draw tiles to count how many are in the Frame
            {
                f.playTile(f.getTiles().get(0));
                i++;
            }

            assertEquals(7, i);
            assertEquals((100-7), p.size()); // Because 7 tiles drawn
        }
        catch(Exception ex)
        {
            fail("Valid constructor passing pool object should not throw an exception.");
        }



    }

    /*
      Goal: To test if the private removeTile() method throws an exception when the frame is empty.
      Testing Method: Try removing a tile from the frame when it is empty. Check that the correct exception is thrown.
     */
    @Test
    public void testRemoveTileFromEmptyFrame()
    {
        try
        {
            Frame f = new Frame();

            f.discardTile(Tile.B); // Try removing a tile from the empty frame

            fail("Cannot discard a tile from an empty Frame.");
        }
        catch(Exception ex)
        {
            assertEquals(IllegalStateException.class, ex.getClass());
            assertEquals("Cannot remove a letter from an empty Frame.", ex.getMessage());
        }
    }

    /*
      Goal: To test that the getTile() method returns null if the tile is not contained in the frame.
      Testing Method: Try getting a tile that definitely isn't in the frame and check that the return value is null.
     */
    @Test
    public void testGetTileNotInFrame()
    {
        try
        {
            ArrayList<Tile> tiles = new ArrayList<>(7);

            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame f = new Frame(tiles);

            Tile test = f.getTile('Z'); //

            assertNull(test); // Should be null as the Tile
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            fail("Getting a tile from the rack should not throw an exception.");
        }
    }

    /*
      Goal: To test if the getter correctly returns the array list
      Testing Method: Create an arraylist of tiles, create a frame and test it correctly returns the same array
     */
    @Test
    public void testReturnTiles(){

        try{

            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertEquals(tiles, test.getTiles());

        }catch (Exception ex){

            fail("No exceptions should be thrown");

        }
    }

    /*
      Goal: To test if the contain tile Tile checker returns correct when given a valid tile
      Testing Method: Create a frame and check if the containTile returns true
     */
    @Test
    public void testContainCorrectTileTile(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertTrue(test.containsTile(Tile.A));
        }catch (Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    /*
      Goal: To test if the tile checker returns false for an invalid tile
      Testing Method: Create a frame and try to check for a tile not in the frame
     */
    @Test
    public void testContainIncorrectTileTile(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertFalse(test.containsTile(Tile.Z));
        }catch (Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    /*
      Goal: To test the contain tile methods with a character input
      Testing Method: Create a frame and check for a tile
     */
    @Test
    public void testContainCorrectTileChar(){
        try {
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertTrue(test.containsTile('a'));
        }catch (Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    /*
      Goal: To test the contains tile method returns false correctly on a tile not on the frame
      Testing Method: Create a frame and check for a tile
     */
    @Test
    public void testContainsIncorrectTileChar(){
        try {
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertFalse(test.containsTile('z'));
        }catch (Exception ex){
            fail("No exceptions should be thrown");
        }
    }

    /*
      Goal: To test if containsTile correctly throws an exception on a non alpha character
      Testing Method: Create a frame and attempt to check for a non alpha character
     */
    @Test
    public void testNonAlphaTileContain(){
        try{

            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertFalse(test.containsTile('#'));
        } catch (Exception ex){
            fail("Test should not reach here");
        }
    }

    /*
      Goal: To test the getTile method correctly returns a tile
      Testing Method: Create a frame and use an assertEquals to test if it returns a tile properly
     */
    @Test
    public void testGetTile(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            assertEquals(test.getTile('a'), Tile.A);
        } catch (Exception ex){
            fail("Code should not reach this point");
        }
    }

    /*
      Goal: Test the playTile function which in turn allows us to test the removeTile method
      Testing Method: Create a frame, attempt to play a tile and check if it is still in the frame
     */
    @Test
    public void testPlayTile(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            test.playTile(Tile.A);
            assertFalse(test.containsTile('a'));
        } catch (Exception ex){
            fail("No exception should be thrown when playing and removing a valid tile");
        }
    }

    /*
      Goal: To test the playTile function against a tile that isn't in the frame
      Testing Method: Create frame, attempt to play a tile not in the frame and throw an exception
     */
    @Test
    public void testPlayTileNotInFrame(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            test.playTile(Tile.Z);
            fail("An exception should be thrown when attempting to play a tile that isn't in the frame");
        } catch (Exception ex){
            assertTrue(true);
        }
    }

    /*
      Goal: To test if the discardTile Function works correctly
      Testing Method: Create a frame, remove the tile and see if it is on the frame anymore
     */
    @Test
    public void testDiscardTile(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            test.discardTile(Tile.A);
            assertFalse(test.containsTile('a'));
        } catch (Exception ex){
            fail("No exception should be thrown when discarding a valid tile");
        }
    }

    /*
      Goal: Test isEmpty works properly
      Testing Method: Create a frame, remove all tiles then check if return true
     */
    @Test
    public void testIsEmpty(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            test.discardTile(Tile.A);
            test.discardTile(Tile.B);
            test.discardTile(Tile.C);
            test.discardTile(Tile.D);
            test.discardTile(Tile.E);
            test.discardTile(Tile.F);
            test.discardTile(Tile.G);
            assertTrue(test.isEmpty());

        } catch (Exception ex){
            fail("No exception should be thrown");
        }
    }

    /*
      Goal: Test if the refillFrame method works correctly, proving the addTile method works properly too
      Testing Method: Create a frame, remove some tiles, refill it and check the size
     */
    @Test
    public void testRefillFrame(){
        try{
            Pool test2 = new Pool();
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);
            test.discardTile(Tile.A);
            test.discardTile(Tile.B);

            test.refillFrame(test2);
            assertEquals(7, test.getTiles().size());
        } catch (Exception ex){
            fail("No exception should be thrown");
        }
    }

    /*
      Goal: Test if the toString method works correctly
      Testing Method: Create a frame and check the toString outputs the correct string
     */
    @Test
    public void testToString(){
        try{
            // Create an array list and fill it
            ArrayList<Tile> tiles = new ArrayList<Tile>();
            tiles.add(Tile.A);
            tiles.add(Tile.B);
            tiles.add(Tile.C);
            tiles.add(Tile.D);
            tiles.add(Tile.E);
            tiles.add(Tile.F);
            tiles.add(Tile.G);

            Frame test = new Frame(tiles);

            assertEquals("{A, B, C, D, E, F, G}", test.toString());
        } catch (Exception ex){
            fail("Shouldn't throw any exceptions");
        }
    }
}