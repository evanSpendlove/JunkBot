import org.junit.jupiter.api.Test;
import scrabbleGame.gameModel.Lexicon;

import static org.junit.jupiter.api.Assertions.*;

/*
    LexiconTest JUnit Test Class

    Purpose: Unit-Testing the Lexicon Class of the scrabbleGame package.
    Summary: This class attempts to thoroughly test the Lexicon Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 04-04-2020
 */
public class LexiconTest
{
    /*
        Goal: To test that the size() method works as expected.
        Testing method: Test after initialising the lexicon.
     */
    @Test
    public void test1Size()
    {
        try
        {
            Lexicon.readInDict();

            assertEquals(267751, Lexicon.size());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*
        Goal: To test that the constructor does not throw an exception.
        Testing Method: Call the constructor.
     */
    @Test
    public void testConstructor()
    {
        try
        {
            Lexicon lex = new Lexicon();
            assertEquals(267751, lex.size());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*
        Goal: To test that the readInDict() method works as expected.
        Testing Method: Call readInDict(), verify the size of the dictionary.
     */
    @Test
    public void testReadInDict()
    {
        try
        {
            assertEquals(0, Lexicon.size());
            Lexicon.readInDict();
            assertEquals(267751, Lexicon.size());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*
        Goal: To test that the checkWord() method works as expected.
        Testing Method: Call checkWord(), check result.
     */
    @Test
    public void testCheckWord()
    {
        try
        {
            Lexicon.readInDict();

            assertTrue(Lexicon.checkWord("Computer"));
            assertFalse(Lexicon.checkWord("nihon"));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
