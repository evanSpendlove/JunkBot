module JunkBot
{
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    /*
    opens scrabbleGame;

     */
    opens scrabbleGame.controllers to javafx.fxml;
    exports scrabbleGame.controllers to javafx.graphics;
}