import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import scrabbleGame.UI.utilityPanes.ImageViewPane;
import scrabbleGame.gameEngine.UI;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
    ImageViewPane JUnit & TestFX Test Class

    Purpose: Unit-Testing the ImageViewPane Class of the scrabbleGame.UI.utilityPanes package.
    Summary: This class attempts to thoroughly test the ImageViewPane Class and its methods to ensure they
             are robust.

    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)

    Author: Evan Spendlove
    Version: 1.0.0
    Since: 26-02-2020
 */

@ExtendWith(ApplicationExtension.class)
public class ImageViewPaneTest {

    @Start
    private void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(UI.class.getResource("/view/scrabble.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Scrabble!");
        // stage.show();
    }

    @Before
    public void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    /*
      Goal: To test that the blank constructor works as expected.
      Testing Method: Instantiate ImageViewPane using the blank constructor and verify that no exception is thrown.
     */
    @Test
    public void testBlankConstructor() {
        try {
            ImageViewPane ivp = new ImageViewPane();
        } catch (Exception ex) {
            fail("No exception should be thrown when constructing a valid ImageViewPane.");
        }
    }

    /*
      Goal: To test that the full constructor works as expected.
      Testing Method: Instantiate ImageViewPane using the full constructor and check the tile was correctly set.
     */
    @Test
    public void testFullConstructor() {
        try
        {
            String imageName = "/assets/Tile_";
            String extension = ".jpg";

            imageName = imageName + 'A' + extension;

            URL url = getClass().getResource(imageName);

            ImageView iv =  new ImageView(new Image(url.toExternalForm()));

            ImageViewPane ivp = new ImageViewPane(iv);
        }
        catch (Exception ex) {
            fail("No exception should be thrown when constructing a valid ImageViewPane.");
        }
    }

    /*
      Goal: To test that one can access the ImageViewProperty as expected.
      Testing Method: Instantiate an ImageViewPane using an ImageView and test that the property returned behaves as expected.
     */
    @Test
    public void testImageViewProperty()
    {
        try
        {
            String imageName = "/assets/Tile_";
            String extension = ".jpg";

            imageName = imageName + 'A' + extension;

            URL url = getClass().getResource(imageName);

            ImageView iv =  new ImageView(new Image(url.toExternalForm()));

            ImageViewPane ivp = new ImageViewPane(iv);

            ObjectProperty<ImageView> prop = ivp.imageViewProperty();

            assertEquals(iv, prop.getValue());
        }
        catch (Exception ex) {
            fail("No exception should be thrown when accessing an imageViewProperty");
        }
    }

    /*
      Goal: To test that one can access the ImageView.
      Testing Method: Instantiate an ImageViewPane using an ImageView and then test that the ImageView returned is the one expected.
     */
    @Test
    public void testGetImageView()
    {
        try
        {
            String imageName = "/assets/Tile_";
            String extension = ".jpg";

            imageName = imageName + 'A' + extension;

            URL url = getClass().getResource(imageName);

            ImageView iv =  new ImageView(new Image(url.toExternalForm()));

            ImageViewPane ivp = new ImageViewPane(iv);

            assertEquals(iv, ivp.getImageView());
        }
        catch (Exception ex) {
            fail("No exception should be thrown when getting an ImageView");
        }
    }

    /*
      Goal: To test that one can set the ImageView.
      Testing Method: Instantiate an ImageViewPane without an ImageView. Then, set on. Test that the correct ImageView is set. Then, update it.
     */
    @Test
    public void testSetImageView()
    {
        try
        {
            String imageName = "/assets/Tile_";
            String extension = ".jpg";
            String letter = "A";

            imageName = imageName + letter + extension;

            URL url = getClass().getResource(imageName);

            ImageView iv =  new ImageView(new Image(url.toExternalForm()));

            ImageViewPane ivp = new ImageViewPane();

            ivp.setImageView(iv);
            assertEquals(iv, ivp.getImageView());

            letter = "C";
            imageName = "/assets/Tile_" + letter + extension;
            URL url2 = getClass().getResource(imageName);
            ImageView iv2 =  new ImageView(new Image(url2.toExternalForm()));

            ivp.setImageView(iv2);
            assertEquals(iv2, ivp.getImageView());
        }
        catch (Exception ex) {
            ex.printStackTrace();
            fail("No exception should be thrown when setting an ImageView");
        }
    }
}