package scrabbleGame.UI.components;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.gameModel.*;

public class FrameController
{
    private Frame frameObject;

    @FXML
    private GridPane framePanes;


    // Private variables
    private TilePane[] rack;

    // Getters and Setters
    public Frame getFrameObject() {
        return frameObject;
    }

    public void setFrameObject(Frame frameObject) {
        this.frameObject = frameObject;
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
        this.frameObject = f;

        // Need to update rack
        for(int i = 0; i < frameObject.getTiles().size(); i++)
        {
            TilePane tp = new TilePane(frameObject.getTiles().get(i));
            getRack()[i] = tp;

            framePanes.add(tp, i, 0);
        }
    }

    // Getters

    @FXML
    public TilePane getTileByCoords(int x)
    {
        return getRack()[x];
    }

}

