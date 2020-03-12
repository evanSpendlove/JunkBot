package scrabbleGame.controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import scrabbleGame.gameModel.Square;
import scrabbleGame.gameModel.Tile;

public class TilePane extends StackPane
{
    /*
        Two things to display:
        - Square type
        - Tile

        What types do we need:
        - Square: >> Just need fill colour
        - Tile >> Image, right?

        - How do we make these responsive?
     */

    Tile tile;
    // Store rectangle and label
    Label text = new Label();

    Pane p = new Pane();

    // Blank constructor
    public TilePane(){}

    public TilePane(Tile input)
    {
        updateTile(input);
    }

    public void updateTile(Tile newTile)
    {
        this.tile = newTile;
        this.text.setText(newTile.toString());
        this.text.setStyle("-fx-background-color: white");

        p.setStyle("-fx-background-color: green");

        this.getChildren().add(p);
        this.getChildren().add(text);
    }

    // Update method
    @Override
    public String toString()
    {
        return "[" + this.tile.toString() + ", " + this.text.getText() + "]";
    }

}
