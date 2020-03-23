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

    @FXML
    void initialize()
    {
        // Initialise the rack
        rack = new TilePane[7];
    }

    // Frame Methods

    @FXML
    public void updateFrame(Frame f)
    {
        clearFrame();

        this.frameObj = f;

        // Need to update rack
        for(int i = 0; i < frameObj.getTiles().size(); i++)
        {
            TilePane tp = new TilePane(frameObj.getTiles().get(i));
            getRack()[i] = tp;

            framePanes.add(tp, i, 0);
        }
    }

    public TilePane playTile(int offset)
    {
        getFrameObj().playTile(getFrameObj().getTiles().get(offset));
        TilePane tile = getRack()[offset];
        getRack()[offset] = null;
        getFramePanes().getChildren().remove(getRack()[offset]);

        return tile;
    }

    @FXML
    public void clearFrame()
    {
        this.frameObj = null;

        this.getFramePanes().getChildren().clear();
        rack = null;
        rack = new TilePane[7];
    }

    @FXML
    public void refillFrame(Pool p)
    {
        getFrameObj().refillFrame(p);
        updateFrame(getFrameObj());
    }

    // Getters

    @FXML
    public TilePane getTileByCoords(int x)
    {
        return getRack()[x];
    }

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

    @FXML
    public void playWord(String word){
        Tile[] temp = new Tile[7];
        for(int i = 0; i < word.length(); i++){
            temp[i] = Tile.getInstance(word.charAt(i));
            getFrameObj().discardTile(temp[i]);
        }
    }
}

