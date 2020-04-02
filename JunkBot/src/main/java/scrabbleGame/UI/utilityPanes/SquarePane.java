package scrabbleGame.UI.utilityPanes;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import scrabbleGame.gameEngine.ScrabbleEngineController;
import scrabbleGame.gameModel.Square;

/**
 * <h1>SquarePane Class</h1>
 * This JavaFX class represented the squares on the board.
 * <br> As such, it contains the square and can have a tile played on it. <br>
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 26-03-2020
 */
public class SquarePane extends StackPane
{

    Square square;
    TilePane tPane;
    // Store rectangle and label
    Label text = new Label();

    // Pane p = new Pane();
    StackPane sp = new StackPane();

    // Blank constructor

    /**
     * Empty Constructor
     */
    public SquarePane(){}

    /**
     * Partial Constructor
     * @param input Pass the Square to be set on this pane.
     */
    public SquarePane(Square input)
    {
        updateSquare(input);
    }

    /**
     * Getter for Square
     * @return Square Returns the square.
     */
    public Square getSquare() {
        return square;
    }

    /**
     * Getter for TilePane
     * @return TilePane Returns the tilePane object
     */
    public TilePane getTilePane() {
        return tPane;
    }

    /**
     * Setter for the square which updates its graphical representation.
     * @param newSquare Pass the square to be set.
     */
    public void updateSquare(Square newSquare)
    {
        this.square = null;
        this.sp.getChildren().clear();
        this.getChildren().clear();

        this.square = newSquare;

        if(!ScrabbleEngineController.USING_THEMED_BOARD)
        {
            if(newSquare.getType() != Square.squareType.REGULAR)
            {
                this.text.setText(newSquare.getType().toString());
                this.text.setStyle("-fx-font-size: 6");
                this.text.setTextFill(Color.WHITE);
            }

            setSquareColour(newSquare);
        }

        this.getChildren().add(sp);
        this.getChildren().add(text);
    }

    /**
     * Setter for the square which updates its graphical representation.
     * @param newSquare Pass the square to be set.
     */
    public void updateSquare(Square newSquare, String message)
    {
        this.square = null;
        this.sp.getChildren().clear();
        this.getChildren().clear();

        this.square = newSquare;

        if(!ScrabbleEngineController.USING_THEMED_BOARD)
        {
            this.text.setText(message);
            this.text.setStyle("-fx-font-size: 10");
            this.text.setTextFill(Color.LIMEGREEN);

            setSquareColour(newSquare);
        }

        this.getChildren().add(sp);
        this.getChildren().add(text);
    }

    /**
     * Method for styling the square manually.
     * @deprecated We now use a CSS stylesheet for all styling, but this may be used in the future for themes.
     * @param newSquare Pass the new square to be styled.
     */
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

        String squareClass = "";

        //Paint p = Color.rgb(0, 0, 0);

        if(newSquare.getType() == Square.squareType.REGULAR)
        {
            //p = Color.rgb(0, 148, 139);
            squareClass = "regularSquare";
        }
        else if(newSquare.getType() == Square.squareType.DB_WORD)
        {
            //p = Color.rgb(27, 155, 230);
            squareClass = "doubleWord";
        }
        else if(newSquare.getType() == Square.squareType.DB_LETTER)
        {
            //p = Color.rgb(137, 194, 213);
            squareClass = "doubleLetter";
        }
        else if(newSquare.getType() == Square.squareType.TR_WORD)
        {
            //p = Color.rgb(255, 27, 43);
            squareClass = "tripleWord";
        }
        else if(newSquare.getType() == Square.squareType.TR_LETTER)
        {
            //p = Color.rgb(246, 168, 155);
            squareClass = "tripleLetter";
        }
        else if(newSquare.getType() == Square.squareType.STAR)
        {
            //p = Color.rgb(246, 168, 155);
            squareClass = "starSquare";
        }
        this.sp.getStyleClass().add("square");
        this.sp.getStyleClass().add(squareClass);
        //this.sp.setBackground(new Background(new BackgroundFill(p, new CornerRadii(0), new Insets(0))));
    }

    /**
     * Method for adding a tile (tilePane) onto a square.
     * @param t Pass the tilePane to be added to the square.
     */
    public void addTile(TilePane t)
    {
        tPane = t;
        this.getChildren().remove(text);
        sp.getChildren().add(t);
    }

    /**
     * Method for removing a tile from a square.
     * @return TilePane Returns the removed TilePane from the square.
     */
    public TilePane removeTile()
    {
        if(tPane.getTile() == null)
        {
            return null;
        }
        else
        {
            sp.getChildren().remove(tPane);
            this.getChildren().add(text);

            setSquareColour(square);
            TilePane temp = tPane;
            tPane = null;

            return temp;
        }
    }

    /**
     * Method for getting a string representation of the object.
     * @return String Returns the string representation of the object.
     */
    @Override
    public String toString()
    {
        if(this.square != null)
        {
            String tile = "";

            if(this.tPane != null) // If there is a tile on the square
            {
                tile = tPane.getTile().toString();
            }

            return "[" + this.square.toString() + ", " + this.text.getText() + ", " + tile + "]";
        }
        else
        {
            return "[NULL]";
        }
    }

}
