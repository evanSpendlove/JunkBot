package scrabbleGame.UI.components;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.gameModel.*;

public class FrameController
{
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

}

