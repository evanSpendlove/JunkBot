package scrabbleGame;

/**
 * <h1>Player Class</h1>
 * This class represents the Player in Scrabble.
 * The player has a username, a score and their frame (rack).
 * The class has the relevant getters and setters, and a reset() method.
 * @author Reuben Mulligan
 * @version 1.0.0
 * @since 07-02-2020
 */
public class Player {

    // Private fields
    /**
     * Username field:
     * Stores the username of the Player as a String.
     */
    private String username;

    /**
     * Score field:
     * Stores the current score of the Player as an int.
     */
    private int score;

    /**
     * Frame field:
     * Stores the frame (rack) of the Player.
     */
    private Frame frame;

    // Overloaded Constructors

    /**
     * This is the full constructor that initialises the username, score and frame.
     * @param username Pass the username of the Player as a String.
     * @param score Pass the current score of the Player as an int.
     * @param frame Pass the frame of the Player.
     */
    public Player(String username, int score, Frame frame) {
        setUsername(username);
        setScore(score);
        setFrame(frame);
    }

    /**
     * This is a partial constructor that initialises the username, score of the Player and the frame to a new Frame object.
     * @param username Pass the username of the Player as a String.
     * @param score Pass the current score of the Player as an int.
     */
    public Player(String username, int score){
        setUsername(username);
        setScore(score);
        this.frame = new Frame(); // Initialise frame
    }

    /**
     * This is a partial constructor that initialises the username and frame of the Player and the score to 0.
     * @param username Pass the username of the Player as a String.
     * @param frame Pass the frame of the Player.
     */
    public Player(String username, Frame frame){
        setUsername(username);
        setScore(0); // Set to 0 by default
        this.frame = frame;
    }

    /**
     * This is a partial constructor that initialises the username of the Player, the score to 0 and the frame to a new Frame object.
     * @param username Pass the username of the Player as a String.
     */
    public Player(String username){
        setUsername(username);
        setScore(0); // Set to 0 by default
        this.frame = new Frame(); // Initialise frame
    }

    // Getters and Setters

    /**
     * Getter for private username field.
     * @return String Returns the username of the Player in String format.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for private score field.
     * @return int Returns the score of the Player in int format.
     */
    public int getScore() {
        return score;
    }

    /**
     * Getter for private frame field.
     * @return Frame Returns the Frame object from the Player instance.
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * Setter for the private frame field.
     * @param frame Pass the frame that you want to set.
     */
    public void setFrame(Frame frame){
        this.frame = frame;
    }

    /**
     * Setter for the private username field with error checking.
     * @param username Pass the username (in String format) that you want to set.
     */
    public void setUsername(String username) {

        username = username.trim();

        if(!username.isBlank() && !username.isEmpty()) // Check if blank or filled with spaces
        {
            this.username = username; // Set the username
        }
        else // Otherwise
        {
            throw new IllegalArgumentException("Username cannot be empty or filled with spaces."); // Throw an exception
        }
    }

    /**
     * Setter for the private score field with error checking.
     * @param score Pass the score (in int format) that you want to set.
     */
    public void setScore(int score) {
        // TODO: Review possibility of negative score (all pass, and - the tiles on your rack = negative score) ?

        if(score >= 0) // Check the score is greater than or equal to 0
        {
            this.score = score; // If so, set it
        }
        else // Otherwise, negative score
        {
            throw new IllegalArgumentException("Score cannot be set to a negative value."); // Throw an exception
        }
    }

    /**
     * Method to reset the entire Player object (username, score, frame).
     */
    public void resetPlayer(){
        this.username = ""; // Reset username to blank string
        this.score = 0; // Reset score to zero
        this.frame = new Frame(); // Reinitialise the frame to a new, blank object
    }

    /**
     * This method overrides the default String method of the Object class.
     * @return Returns a String containing the username and score in a formatted manner.
     */
    @Override
    public String toString() {
        return "Player {" +
                "Username: '" + username + '\'' +
                ", Score: " + score +
                "}";
    }

    /**
     * Method for dumping all of the information about the Player object as a String.
     * @return Returns a string containing the username, score and frame of the Player.
     */
    public String dumpPlayerInfo() {
        return "Player {" +
                "Username: '" + username + '\'' +
                ", Score: " + score + ", " + frame.toString() +
                "}";
    }
}
