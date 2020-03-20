package scrabbleGame.UI.components;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import scrabbleGame.gameEngine.ScrabbleEngineController;

public class Timer
{
    private static int seconds;

    public Timer()
    {

    }

    public static void run(ScrabbleEngineController engine, int time, TextArea displayArea, String message)
    {
        seconds = time;
        displayArea.setVisible(true);
        displayArea.setText(message + Integer.toString(seconds) + "s");

        Timeline stopWatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            seconds--;
            displayArea.setText(message + Integer.toString(seconds) + "s");
            System.out.println("Seconds: " + seconds);
        }));

        stopWatchTimeline.setOnFinished(actionEvent ->
        {
            displayArea.setVisible(false);
            displayArea.setText("");
            engine.currentFrameController.getFramePanes().setVisible(true);
            engine.switchPlayer();
        });

        stopWatchTimeline.setCycleCount(time);
        stopWatchTimeline.play();
    }
}
