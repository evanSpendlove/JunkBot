package scrabbleGame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import scrabbleGame.gameModel.*;

public class BoardController
{
    @FXML
    private GridPane boardPanes;

    @FXML
    private GridPane rackPanes;

    @FXML
    private TextArea consoleDisplay;

    @FXML
    private TextField commandInput;

    // Private variables
    private TilePane[] rack;
    private SquarePane[][] board;

    // Getters and Setters
    public TilePane[] getRack() {
        return rack;
    }

    public void setRack(TilePane[] rack) {
        this.rack = rack;
    }

    public SquarePane[][] getBoard() {
        return board;
    }

    public void setBoard(SquarePane[][] board) {
        this.board = board;
    }

    @FXML
    void initialize()
    {
        // Initialise the board
        board = new SquarePane[15][15];

        // Initialise the rack
        rack = new TilePane[7];

        // Testing
        TextArea t = new TextArea();
        t.setText("Bob");

        SquarePane s = new SquarePane();
        Square square = new Square();
        square.setType(Square.squareType.REGULAR);
        s.updateSquare(square);

        System.out.println(boardPanes.getChildren().toString());

        boardPanes.add(s, 5, 2);
        System.out.println(boardPanes.getChildren().toString());
        System.out.println(boardPanes.getChildren().get(0));

        addSquareToBoard(new Square(), 3, 5);

        Square billy = new Square();
        billy.setType(Square.squareType.STAR);

        addSquareToBoard(billy, 3, 5);

        Frame testFrame = new Frame();
        testFrame.refillFrame(new Pool());

        updateFrame(testFrame);
    }

    @FXML
    void submitCommand(ActionEvent event)
    {
        String newCommand = commandInput.getText();

        // Need more input validation
        if(!newCommand.isBlank() && !newCommand.isEmpty())
        {
            newCommand += "\n";

            String oldCommands = consoleDisplay.getText();

            consoleDisplay.setText(oldCommands + newCommand);
        }

        commandInput.clear();
    }

    @FXML
    void addLineToConsole(String s)
    {
        if(!s.isEmpty() && !s.isBlank())
        {
            s += "\n";

            consoleDisplay.setText(consoleDisplay.getText() + s);
        }
    }

    @FXML
    void addSquareToBoard(Square s, int x, int y)
    {
        // Need to validate input
        if(x >= 0 && x <= 14 && y >= 0 && y <= 14) // If the coordinates are on the board
        {
            SquarePane sPane = new SquarePane(s); // Create new SquarePane
            getBoard()[y][x] = sPane; // Add to board
            boardPanes.add(sPane, x, y);
        }
        else
        {
            throw new IllegalArgumentException("The x and y coordinates provided are not within the board limits.");
        }
    }

    @FXML
    void updateFrame(Frame f)
    {
        // Need to update rack
        for(int i = 0; i < f.getTiles().size(); i++)
        {
            TilePane tp = new TilePane(f.getTiles().get(i));
            getRack()[i] = tp;

            rackPanes.add(tp, i, 0);
        }
    }

    @FXML
    SquarePane getSquareByCoords(int x, int y)
    {
        return getBoard()[y][x];
    }

    @FXML
    TilePane getTileByCoords(int x)
    {
        return getRack()[x];
    }

}

