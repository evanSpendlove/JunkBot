package scrabbleGame.UI.utilityPanes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import scrabbleGame.gameModel.Tile;

import java.net.URL;

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

    private Tile tile;
    // Store rectangle and label
    Label text = new Label();

    Pane p = new Pane();
    ImageViewPane i = new ImageViewPane();

    // Blank constructor
    public TilePane(){}

    public TilePane(Tile input)
    {
        updateTile(input);
    }

    // Getters and Setters
    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }


    // Methods

    public void updateTile(Tile newTile)
    {
        this.tile = newTile;
        // this.text.setText(newTile.toString());
        // this.text.setStyle("-fx-background-color: white");

        // p.setStyle("-fx-background-color: green");
        updateTileImageCSS(newTile);

        this.getChildren().add(i);
        // this.getChildren().add(text);
    }

    private void updateTileImage(Tile newTile)
    {
        String imageName = "Tile_";
        String extension = ".jpg";

        imageName = imageName + newTile.toString() + extension;

        System.out.println("URL: " + imageName);

        URL url = getClass().getClassLoader().getResource(imageName);

        System.out.println(url.toString());

        Image i = new Image(url.toExternalForm());

        // Issue with either position of size

        p.setBackground(new Background(new BackgroundImage(i, null, null, BackgroundPosition.DEFAULT, new BackgroundSize(50, 50, false, false, false, false))));
    }

    private void updateTileImageCSS(Tile newTile)
    {
        String imageName = "/assets/Tile_";
        String extension = ".jpg";

        imageName = imageName + newTile.toString() + extension;

        //System.out.println(imageName);

        //System.out.println(getClass().getResource("/assets/Tile_C.jpg"));

        URL url = getClass().getResource(imageName);

        System.out.println(url);

        i.setImageView(new ImageView(new Image(url.toExternalForm())));

        //p.setPrefWidth(this.getWidth());
        //p.setPrefHeight(this.getHeight());
        p.prefHeightProperty().bind(this.prefHeightProperty());
        p.prefWidthProperty().bind(this.prefWidthProperty());

        p.setStyle("-fx-background-image: url(" + url.toExternalForm() + ")");
        p.setStyle("-fx-background-position: center");
        p.setStyle("-fx-background-size: auto");
        //p.setStyle("-fx-background-size: ");
    }

    // Update method
    @Override
    public String toString()
    {
        return "[" + this.tile.toString() + "]";
    }

}

/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */


/**
 *
 * @author akouznet
 */
class ImageViewPane extends Region {

    private ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<ImageView>();

    public ObjectProperty<ImageView> imageViewProperty() {
        return imageViewProperty;
    }

    public ImageView getImageView() {
        return imageViewProperty.get();
    }

    public void setImageView(ImageView imageView) {
        this.imageViewProperty.set(imageView);
    }

    public ImageViewPane() {
        this(new ImageView());
    }

    @Override
    protected void layoutChildren() {
        ImageView imageView = imageViewProperty.get();
        if (imageView != null) {
            imageView.setFitWidth(getWidth());
            imageView.setFitHeight(getHeight());
            layoutInArea(imageView, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
        }
        super.layoutChildren();
    }

    public ImageViewPane(ImageView imageView) {
        imageViewProperty.addListener(new ChangeListener<ImageView>() {

            @Override
            public void changed(ObservableValue<? extends ImageView> arg0, ImageView oldIV, ImageView newIV) {
                if (oldIV != null) {
                    getChildren().remove(oldIV);
                }
                if (newIV != null) {
                    getChildren().add(newIV);
                }
            }
        });
        this.imageViewProperty.set(imageView);
    }
}
