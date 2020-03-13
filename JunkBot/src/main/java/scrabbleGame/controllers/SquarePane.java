package scrabbleGame.controllers;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import scrabbleGame.gameModel.Square;

import java.util.Collections;

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
    TilePane tPane;
    // Store rectangle and label
    Label text = new Label();

    // Pane p = new Pane();
    StackPane sp = new StackPane();

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
        this.text.setStyle("-fx-fill: white");
        this.text.setStyle("-fx-font-size: 6");

        //this.text.setStyle("-fx-background-color: magenta");

        setSquareColour(newSquare);

        this.getChildren().add(sp);
        this.getChildren().add(text);
    }

    private void setSquareColour(Square newSquare)
    {
        /*
            RGB Colour codes:

            Regular - 0, 148, 139
            Star - 246, 168, 155
            Dark Blue - 27, 155, 230
            Lighter blue - 137, 194, 213
            Triple - 255, 27, 43
         */

        Paint p = Color.rgb(0, 0, 0);

        if(newSquare.getType() == Square.squareType.REGULAR)
        {
            p = Color.rgb(0, 148, 139);
        }
        else if(newSquare.getType() == Square.squareType.DB_WORD)
        {
            p = Color.rgb(27, 155, 230);
        }
        else if(newSquare.getType() == Square.squareType.DB_LETTER)
        {
            p = Color.rgb(137, 194, 213);
        }
        else if(newSquare.getType() == Square.squareType.TR_WORD)
        {
            p = Color.rgb(255, 27, 43);
        }
        else if(newSquare.getType() == Square.squareType.TR_LETTER)
        {
            p = Color.rgb(246, 168, 155);
        }
        else if(newSquare.getType() == Square.squareType.STAR)
        {
            p = Color.rgb(246, 168, 155);
        }
        this.sp.setBackground(new Background(new BackgroundFill(p, new CornerRadii(0), new Insets(0))));
    }

    public void addTile(TilePane t)
    {
        tPane = t;
        this.getChildren().remove(text);
        sp.getChildren().add(t);
    }

    public TilePane removeTile()
    {
        sp.getChildren().remove(tPane);
        this.getChildren().add(text);

        setSquareColour(square);

        return tPane;
    }

    // Update method
    @Override
    public String toString()
    {
        return "[" + this.square.toString() + ", " + this.text.getText() + "]";
    }

}
