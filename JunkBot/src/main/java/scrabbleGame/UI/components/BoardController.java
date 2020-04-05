package scrabbleGame.UI.components;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import scrabbleGame.UI.utilityPanes.SquarePane;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.exceptions.TileNotFound;
import scrabbleGame.gameModel.*;

/**
 * <h1>BoardController Class</h1>
 * This class is the controller for the board.
 * This class contains all of the methods for interacting with the board </br>
 * Team: JunkBot </br>
 * Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 * @author Evan Spendlove
 * @version 1.0.0
 * @since 26-03-2020
 */
public class BoardController
{
    // Components
    private Board boardObject;

    @FXML
    private GridPane boardPanes;

    // Private variables
    private SquarePane[][] board;

    // Getters and Setters
    /**
     * Getter for the board object.
     * @return Board Returns the board object.
     */
    public Board getBoardObject() {
        return boardObject;
    }

    /**
     * Setter for the board object.
     * @param boardObject Pass the board object to be set.
     */
    public void setBoardObject(Board boardObject) {
        this.boardObject = boardObject;
    }

    /**
     * Getter for the board (SquarePane array)
     * @return SquarePane[][] Returns the board as a SquarePane array.
     */
    public SquarePane[][] getBoard() {
        return board;
    }

    /**
     * Setter for the board (SquarePane array)
     * @param board Pass the board to be set.
     */
    public void setBoard(SquarePane[][] board) {
        this.board = board;
    }

    /**
     * Method for initialising the board Controller.
     */
    @FXML
    void initialize()
    {
        // Initialise the board
        board = new SquarePane[15][15];
    }

    // Board Methods

    /**
     * Method for adding a square to the boardPane object when initialising the board.
     * @param s Pass the square to be added.
     * @param x Pass the x coordinate of the square.
     * @param y Pass the y coordinate of the square.
     */
    @FXML
    private void addSquareToBoard(Square s, int x, int y)
    {
        if(x >= 0 && x <= 14 && y >= 0 && y <= 14) // If the coordinates are on the board
        {
            SquarePane sPane = new SquarePane(s); // Create new SquarePane

            if(s.isOccupied())
            {
                TilePane tp = new TilePane(s.getTile());
                sPane.addTile(tp);
            }

            getBoard()[y][x] = sPane; // Add to board
            boardPanes.add(sPane, y, x);
        }
        else
        {
            throw new IllegalArgumentException("The x and y coordinates provided are not within the board limits.");
        }
    }

    /**
     * Method for removing a tile from the board based on x, y coordinates.
     * @param x Pass the x coordinate of the tile
     * @param y Pass the y coordinate of the tile
     * @return TilePane Returns the tilePane object that was removed from the Board.
     * @throws TileNotFound Throws an exception if not Tile is found on the board at that location.
     */
    @FXML
    public TilePane removeTileFromBoard(int x, int y) throws TileNotFound {
        TilePane tp = getBoard()[y][x].removeTile();

        boardObject.getBoard()[y][x].setTile(null);

        if(tp != null)
        {
            return tp;
        }
        else
        {
            throw new TileNotFound("There is not tile located at these coordinates currently on the board.");
        }
    }

    /**
     * Method for updating the board and its graphical representation.
     * @param b Pass the new board to be set.
     */
    @FXML
    public void updateBoard(Board b)
    {
        this.board = null;
        this.boardObject = null;
        this.boardPanes.getChildren().clear();

        this.board = new SquarePane[15][15];

        this.setBoardObject(b);

        for(int i = 0; i < b.getBoard().length; i++)
        {
            for(int j = 0; j < b.getBoard()[0].length; j++)
            {
                addSquareToBoard(b.getBoard()[i][j], i, j);
            }
        }
    }

}

