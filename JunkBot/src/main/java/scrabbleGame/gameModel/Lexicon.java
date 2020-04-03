package scrabbleGame.gameModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Public class to provide a hashmap that can be used to find whether a given word is in the dictionary
 * Team: JunkBot
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 */
public class Lexicon
{
    private static HashMap<String, String> lex;

    // Getters and Setters

    // Constructor

    public Lexicon()
    {
        // Read in
        readInDict();
    }

    /**
     * Method to read the dictionary in from a text file
     */
    public static void readInDict()
    {
        lex = new HashMap<>(); // Initialise lex
        try
        {
            File file = new File("src/main/resources/sowpods.txt");
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            while((line = in.readLine()) != null)
            {
                addWord(line.toUpperCase());
            }
            in.close(); // Close reader
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
        return lex.containsKey(word.toUpperCase());
    }

    /**
     * Adds a given word to the dictionary
     * @param word, The word being added to the dictionary
     */
    private static void addWord(String word)
    {
        lex.put(word, word);
    }

}
