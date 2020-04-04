package scrabbleGame.gameModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Public class to provide a HashSet that can be used to find whether a given word is in the dictionary
 * Team: JunkBot
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Cal Nolan
 *  @version 1.0.0
 *  @since 03-03-2020
 */
public class Lexicon
{
    private static HashSet<String> lex; // Dictionary as a hashset
    private static boolean initialised = false; // Boolean for tracking if the dictionary object has been initialised

    /**
     * Blank constructor which initialises the dictionary by calling readInDict()
     */
    public Lexicon()
    {
        // Read in from file
        readInDict();
    }

    /**
     * Static method to initialise the dictionary and read in from a text file containing all words.
     */
    public static void readInDict()
    {
        lex = new HashSet<>(); // Initialise lex

        try
        {
            File file = new File("src/main/resources/sowpods.txt"); // Dictionary file to read in from

            BufferedReader in = new BufferedReader(new FileReader(file)); // Buffered reader
            String line; // Temp line for reading in

            while((line = in.readLine()) != null) // While not at the last line
            {
                addWord(line.toUpperCase()); // Add the uppercase of the line to the dictionary
            }

            in.close(); // Close reader

            initialised = true; // Update initialised to true
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Checks whether a given word exists within the dictionary
     * @param word The word to be checked
     * @return True if the word is found in the dictionary, else returns false
     */
    public static boolean checkWord(String word)
    {
        if(!initialised) // If the dictionary has not yet been initialised
        {
            readInDict(); // Initialise it
        }

        return lex.contains(word.toUpperCase()); // Return true if the set contains the word
    }

    /**
     * Adds a given word to the dictionary
     * @param word, The word being added to the dictionary
     */
    private static void addWord(String word)
    {
        lex.add(word);
    }
}
