package scrabbleGame.gameEngine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <h1>UI Class</h1>
 * This class extends Application to launch the JavaFX application.
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 25-03-2020
 */
public class UI extends Application
{
    /**
     * Minimum screen height
     */
    private static final int minHeight = 720; // 720

    /**
     * Minimum screen width
     */
    private static final int minWidth = 1280; // 1280

    /**
     * The start method for the JavaFX application - automaticalyl called by launch().
     * @param primaryStage Pass the stage to be set (automatically done by launch();
     * @throws Exception Throws an IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/view/scrabble.fxml"));
        primaryStage.setTitle("Scrabble");
        primaryStage.setScene(new Scene(root, minWidth, minHeight));
        primaryStage.setMinHeight(minHeight);
        primaryStage.setMinWidth(minWidth);
        primaryStage.show();
    }

    /**
     * Main method to launch the JavaFX application.
     * @param args Pass any arguments to be handled inside the start of the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}