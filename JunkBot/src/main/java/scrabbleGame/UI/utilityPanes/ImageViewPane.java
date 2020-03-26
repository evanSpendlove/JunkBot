package scrabbleGame.UI.utilityPanes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * <h1>ImageViewPane Class</h1>
 * This class is a modified ImageView which allows for the image to scale with the screen.
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @version 1.0.0
 * @since 18-03-2020
 */
public class ImageViewPane extends Region {

    private ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<ImageView>();

    /**
     * Getter for imageViewProperty
     * @return ObjectProperty
     */
    public ObjectProperty<ImageView> imageViewProperty() {
        return imageViewProperty;
    }

    /**
     * Getter for ImageView
     * @return ImageView
     */
    public ImageView getImageView() {
        return imageViewProperty.get();
    }

    /**
     * Setter for ImageView
     * @param imageView Pass the ImageView to be set
     */
    public void setImageView(ImageView imageView) {
        this.imageViewProperty.set(imageView);
    }

    /**
     * Constructor for ImageViewPane
     */
    public ImageViewPane() {
        this(new ImageView());
    }

    /**
     * Method for laying out child objects of the imageViewPane
     */
    @Override
    protected void layoutChildren() {
        ImageView imageView = imageViewProperty.get();

        if (imageView != null)
        {
            imageView.setFitWidth(getWidth());
            imageView.setFitHeight(getHeight());
            layoutInArea(imageView, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
        }
        super.layoutChildren();
    }

    /**
     * Constructor for ImageViewPane which adds a listener for the the imageView being updated.
     * @param imageView Pass the ImageView object to be set.
     */
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
