import org.junit.jupiter.api.Test;
import scrabbleGame.Player;
import scrabbleGame.Pool;
import scrabbleGame.Frame;

import static org.junit.jupiter.api.Assertions.*;

/*
    Assignment 1 - Integration JUnit Test Class

    Purpose: Perform an integration test on the classes for Assignment 1
    Summary: This class runs all tests relating to all classes for Assignment 1
             and runs a sample integration test simulating game-play.

    Authors: Evan Spendlove, Reuben Mulligan, Cal Nolan
    Version: 1.0.0
    Since: 08-02-2020
 */
public class A1_IntegrationTest
{
    /*
        Goal: To run all tests for classes for Assignment 1.
        Testing Method: Call all methods for all other unit-test classes for Assignment 1
     */
    @Test
    public void testAllClasses()
    {
        // Run all PoolTest tests
        PoolTest poolTest = new PoolTest();
        poolTest.testToString();
        poolTest.testConstructor();
        poolTest.testDraw();
        poolTest.testGetInvalidValue();
        poolTest.testGetSize();
        poolTest.testIsEmpty();
        poolTest.testReset();
        poolTest.testGetValue();

        // Run all TileTest tests
        TileTest tileTest = new TileTest();
        tileTest.testAllLetters();
        tileTest.testBlankTile();
        tileTest.testGetInvalidInstance();
        tileTest.testGetInvalidValChar();
        tileTest.testGetInvalidValString();
        tileTest.testGetValChar();
        tileTest.testGetValString();
        tileTest.testToString();

        // Run all PlayerTest tests
        PlayerTest playerTest = new PlayerTest();
        playerTest.testFrameGetter();
        playerTest.testFrameSetter();
        playerTest.testFullConstructor();
        playerTest.testPartialConstructor_uname();
        playerTest.testPartialConstructor_uname_score();
        playerTest.testPPartialConstructor_uname_frame();
        playerTest.testUsernameNotNull();
        playerTest.testUsernameNotBlank();
        playerTest.testUsernameNotAllSpaces();
        playerTest.testUsernameContainsTab();
        playerTest.testUsernameContainsNewline();
        playerTest.testUsernameContainsExtraWhitespace();
        playerTest.testSetScoreToZero();
        playerTest.testSetPositiveScore();
        playerTest.testSetNegativeScore();
        playerTest.testResetPlayer();
        playerTest.testPlayerToString();
        playerTest.testPlayerDumpInfo();

        // Run all FrameTest tests
        FrameTest frameTest = new FrameTest();
        frameTest.testContainCorrectTileChar();
        frameTest.testContainCorrectTileTile();
        frameTest.testContainIncorrectTileTile();
        frameTest.testContainsIncorrectTileChar();
        frameTest.testDiscardTile();
        frameTest.testFullConstructor();
        frameTest.testGetTile();
        frameTest.testIsEmpty();
        frameTest.testNonAlphaTileContain();
        frameTest.testOversizedConstructor();
        frameTest.testPlayTile();
        frameTest.testPlayTileNotInFrame();
        frameTest.testRefillFrame();
        frameTest.testReturnTiles();
        frameTest.testToString();
        frameTest.testUndersizedConstructor();
    }

    /*
        Goal: To simulate game-play with the classes available.
        Testing Method: Instantiate the required classes, set up the Player and play several moves.
     */
    @Test
    public void testPlayerCreation()
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
        }
        catch(Exception ex)
        {
            fail("Integration test with valid method calls should not fail.");
        }
    }


    public static void main(String[] args)
    {
        A1_IntegrationTest test = new A1_IntegrationTest();
        test.testAllClasses();
        test.testPlayerCreation();
        System.out.println("All tests passed!");
    }

}
