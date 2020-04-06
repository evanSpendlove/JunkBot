package scrabbleGame.UI.utilityPanes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import scrabbleGame.gameModel.Tile;

import java.net.URL;

/**
 * <h1>TilePane Class</h1>
 * This class represents the Tile object in JavaFX form.
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 26-03-2020
 */
public class TilePane extends StackPane
{

    private Tile tile;
    Pane p = new Pane();
    ImageViewPane i = new ImageViewPane();

    /**
     * Empty Constructor
     */
    public TilePane(){}

    /**
     * Partial Constructor
     * @param input Pass the tile to be set.
     */
    public TilePane(Tile input)
    {
        updateTile(input);
    }

    // Getters and Setters

    /**
     * Getter for Tile object
     * @return Tile Returns the tile object
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Setter for Tile object
     * @param tile Pass the tile to be set.
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }


    // Methods

    /**
     * Method for updating the tile object and its graphical representation.
     * @param newTile
     */
    public void updateTile(Tile newTile)
    {
        this.tile = newTile;
        updateTileImageCSS(newTile);

        this.getChildren().add(i);
    }

    /**
     * Method for updating the styling (background image) of the Tile.
     * @param newTile Pass the tile to be styled.
     */
    private void updateTileImageCSS(Tile newTile)
    {
        String imageName = "/assets/Tile_";
        String extension = ".jpg";

        imageName = imageName + newTile.toString() + extension;

        URL url = getClass().getResource(imageName);

        i.setImageView(new ImageView(new Image(url.toExternalForm())));

        p.prefHeightProperty().bind(this.prefHeightProperty());
        p.prefWidthProperty().bind(this.prefWidthProperty());

        p.setStyle("-fx-background-image: url(" + url.toExternalForm() + ")");
        p.setStyle("-fx-background-position: center");
        p.setStyle("-fx-background-size: auto");
    }

    /**
     * Method for getting a string representation of the TilePane.
     * @return String Returns the string representation of the TilePane.
     */
    @Override
    public String toString()
    {
        if(getTile() != null)
        {
            return "[" + this.tile.toString() + "]";
        }
        else
        {
            return "[" + "NULL" + "]";
        }
    }

}
