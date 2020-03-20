package scrabbleGame.UI.components;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import scrabbleGame.UI.utilityPanes.SquarePane;
import scrabbleGame.UI.utilityPanes.TilePane;
import scrabbleGame.exceptions.TileNotFound;
import scrabbleGame.gameModel.*;

public class BoardController
{
    // Components
    private Board boardObject;

    @FXML
    private GridPane boardPanes;

    // Private variables
    private SquarePane[][] board;

    // Getters and Setters
    public Board getBoardObject() {
        return boardObject;
    }

    public void setBoardObject(Board boardObject) {
        this.boardObject = boardObject;
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
    }

    // Board Methods

    @FXML
    public void addSquareToBoard(Square s, int x, int y)
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
    public TilePane removeTileFromBoard(int x, int y) throws TileNotFound {
        TilePane tp = getBoard()[y][x].removeTile();

        if(tp != null)
        {
            return tp;
        }
        else
        {
            throw new TileNotFound("There is not tile located at these coordinates currently on the board.");
        }
    }

    // Update whole board (Read from file, etc.)
    @FXML
    public void updateBoard(Board b)
    {
        this.setBoardObject(b);

        for(int i = 0; i < b.getBoard().length; i++)
        {
            for(int j = 0; j < b.getBoard()[0].length; j++)
            {
                addSquareToBoard(b.getBoard()[i][j], i, j);
            }
        }
    }

    // Add Move to Board

    // Add Tile to Board

    @FXML
    public void addTiletoBoard(FrameController fc, int offset, int x, int y)
    {
        getBoardObject().getBoard()[y][x].setTile(fc.getRack()[offset].getTile()); // Add to board object
        getBoard()[y][x].addTile(fc.getRack()[offset]);

        // System.out.println("FC_RackPanes: " + Arrays.toString(fc.getFramePanes().getChildren().toArray()));
        fc.getFramePanes().getChildren().remove(fc.getRack()[offset]);

        // System.out.println("FC_Rack: " + Arrays.toString(fc.getRack()));
        // System.out.println("FC_RackPanes: " + Arrays.toString(fc.getFramePanes().getChildren().toArray()));

        fc.getFrameObj().playTile(fc.getRack()[offset].getTile());
        fc.getRack()[offset] = null; // Remove from rack

        // System.out.println("FC_Rack: " + Arrays.toString(fc.getRack()));
    }

    // Getters

    @FXML
    public SquarePane getSquareByCoords(int x, int y)
    {
        return getBoard()[y][x];
    }

}

