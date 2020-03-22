module JunkBot
{
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    /*
    opens scrabbleGame;

     */
    opens scrabbleGame.gameEngine to javafx.fxml;
    opens scrabbleGame.UI.components to javafx.fxml;
    opens scrabbleGame.UI.utilityPanes to javafx.fxml;

    exports scrabbleGame.gameEngine to javafx.graphics;
}