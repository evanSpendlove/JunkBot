package scrabbleGame.UI.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import scrabbleGame.gameEngine.ScrabbleEngineController;

/**
 * <h1>Timer Class</h1>
 * This class is a timer which is used for delaying the screen between player changes.
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 26-03-2020
 */
public class Timer
{
    private static int seconds;

    public Timer()
    {

    }

    /**
     * Method to run the timer for the chosen period of time and update the textarea.
     * @param engine Pass the current instance of the engine controller.
     * @param time Pass the number of seconds to wait for.
     * @param displayArea Pass the TextArea to be updated with the wait message.
     * @param message Pass the message to be displayed.
     */
    public static void run(ScrabbleEngineController engine, int time, TextArea displayArea, String message)
    {
        seconds = time;
        displayArea.setVisible(true);
        displayArea.setText(message + Integer.toString(seconds) + "s");

        // Create new timeline for displaying the wait message
        Timeline stopWatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            seconds--;
            displayArea.setText(message + Integer.toString(seconds) + "s");
        }));

        // When the event is over, hide the screen and update the frame.
        stopWatchTimeline.setOnFinished(actionEvent ->
        {
            displayArea.setVisible(false);
            displayArea.setText("");
            engine.currentFrameController.getFramePanes().setVisible(true);
            engine.switchPlayer();
        });

        // Play
        stopWatchTimeline.setCycleCount(time);
        stopWatchTimeline.play();
    }

    public static void endGame(ScrabbleEngineController engine, int time, TextArea displayArea, String message)
    {
        seconds = time;
        displayArea.setVisible(true);
        displayArea.setText(message + Integer.toString(seconds) + "s");

        // Create new timeline for displaying the wait message
        Timeline stopWatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            seconds--;
            displayArea.setText(message + Integer.toString(seconds) + "s");
        }));

        // When the event is over, hide the screen and update the frame.
        stopWatchTimeline.setOnFinished(actionEvent ->
        {
            displayArea.setVisible(false);
            displayArea.setText("");
            Platform.exit();
            System.exit(0);
        });

        // Play
        stopWatchTimeline.setCycleCount(time);
        stopWatchTimeline.play();
    }
}
