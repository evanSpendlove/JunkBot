package scrabbleGame.controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import scrabbleGame.gameModel.Square;

public class SquarePane extends StackPane
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

    Square square;
    // Store rectangle and label
    Label text = new Label();

    Pane p = new Pane();

    // Blank constructor
    public SquarePane(){}

    public SquarePane(Square input)
    {
        updateSquare(input);
    }

    public void updateSquare(Square newSquare)
    {
        this.square = newSquare;
        this.text.setText(newSquare.getType().toString());
        this.text.setStyle("-fx-background-color: magenta");

        p.setStyle("-fx-background-color: yellow");

        this.getChildren().add(p);
        this.getChildren().add(text);
    }

    // Update method
    @Override
    public String toString()
    {
        return "[" + this.square.toString() + ", " + this.text.getText() + "]";
    }

}
