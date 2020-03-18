package scrabbleGame.gameEngine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScrabbleFX extends Application
{
    private static final int minHeight = 720; // 720
    private static final int minWidth = 1280; // 1280

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


    public static void main(String[] args) {
        launch(args);
    }
}