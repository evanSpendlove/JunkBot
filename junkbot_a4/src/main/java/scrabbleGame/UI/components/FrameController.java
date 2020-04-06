package scrabbleGame.UI.components;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameModel.*;

public class FrameController
{

    @FXML
    private ScrabbleEngineController scrabbleEngineController;

    private Frame frameObj;

    @FXML
    private GridPane framePanes;


    // Private variables
    private TilePane[] rack;

    // Getters and Setters
    public Frame getFrameObj() {
        return frameObj;
    }

    public void setFrameObj(Frame frameObj) {
        this.frameObj = frameObj;
    }

    public TilePane[] getRack() {
        return rack;
    }

    public void setRack(TilePane[] rack) {
        this.rack = rack;
    }

    public GridPane getFramePanes() {
        return framePanes;
    }

    public void setFramePanes(GridPane framePanes) {
        this.framePanes = framePanes;
    }

    public ScrabbleEngineController getScrabbleEngineController() {
        return scrabbleEngineController;
    }

    public void setScrabbleEngineController(ScrabbleEngineController scrabbleEngineController) {
        this.scrabbleEngineController = scrabbleEngineController;
    }

    /**
     * Initialise method prepares the frameController for use by initialising objects inside it.
     */
    @FXML
    void initialize()
    {
        // Initialise the rack
        rack = new TilePane[7];
    }

    // Frame Methods

    /**
     * Method to update the frame object and the visual representation of the frame.
     * @param f Pass the frame to be set.
     */
    @FXML
    public void updateFrame(Frame f)
    {
        clearFrame();

        this.frameObj = f;

        // Need to update frame graphically
        for(int i = 0; i < frameObj.getTiles().size(); i++)
        {
            TilePane tp = new TilePane(frameObj.getTiles().get(i));
            getRack()[i] = tp;

            framePanes.add(tp, i, 0);
        }
    }

    /**
     * Method to play a tile which updates the frame after the letter is played.
     * @param offset Pass the offset (index in the frame object) of the Tile to be played.
     * @return TilePane Returns the tilePane object that is to be played on the board.
     */
    public TilePane playTile(int offset)
    {
        /*
            3 Objects
                - Frame Object
                - Frame Panes
                - Actual displayed frame
         */
        getFrameObj().playTile(getFrameObj().getTiles().get(offset));
        TilePane tile = getRack()[offset];
        getRack()[offset] = null;
        getFramePanes().getChildren().remove(getRack()[offset]);

        return tile;
    }

    /**
     * Method to clear the frame and its graphical representation.
     */
    @FXML
    public void clearFrame()
    {
        this.frameObj = null;

        this.getFramePanes().getChildren().clear();
        rack = null;
        rack = new TilePane[7];
    }

    /**
     * Method to refill the frame (and its graphical representation) using a pool.
     * @param p Pass the pool from which the letters are drawn.
     */
    @FXML
    public void refillFrame(Pool p)
    {
        getFrameObj().refillFrame(p);
        updateFrame(getFrameObj());
    }

    // Getters

    /**
     * Method to exchange tiles for new tiles in the pool.
     * @param toChange Pass the array of characters that you want to exchange.
     * @return int Returns an error-code (-1) if the exchange is unsuccessful.
     * @throws IllegalArgumentException Throws an exception if the frame does not contain the tiles to be exchanged.
     */
    @FXML
    public int exchangeTiles(char[] toChange) throws IllegalArgumentException{
        if(toChange.length < 1){
            return -1;
        }
        Tile[] temp = new Tile[7];
        for(int i = 0; i < toChange.length; i++){
            if(getFrameObj().containsTile(toChange[i]) != true){
                throw new IllegalArgumentException("Cannot exchange tile that you don't have");
            }
        }
        for(int i = 0; i < toChange.length; i++){
            temp[i] = Tile.getInstance(toChange[i]);
            getFrameObj().discardTile(temp[i]);
        }
        getFrameObj().refillFrame(getScrabbleEngineController().getPool());
        for(int i = 0; i <toChange.length; i++){
            getScrabbleEngineController().getPool().addTile(temp[i]);
        }
        return 1;
    }

    /**
     * Method to ply a word and remove its letters from the frame.
     * @param m Pass the move to be played.
     */
    @FXML
    public void playWord(Move m)
    {
        Tile[] temp = new Tile[7];

        for(int i = 0; i < m.getPlays().size(); i++)
        {
            temp[i] = Tile.getInstance(m.getPlays().get(i).getLetter());
            getFrameObj().discardTile(temp[i]);
        }
    }
}

