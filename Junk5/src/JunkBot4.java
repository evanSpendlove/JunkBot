import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
    Team: JunkBot
    Members: Reuben Mulligan (18733589), Evan Spendlove (18492656), Cal Nolan(18355103)
 */
public class JunkBot4 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    // Constants
    public static final int NUM_BEST_WORDS = 3;

    private PlayerAPI me;
    private OpponentAPI opponent;
    private BoardAPI board;
    private UserInterfaceAPI info;
    private DictionaryAPI dictionary;
    private int turnCount;

    private boolean shouldChallenge = false;
    private boolean otherBotChallenges = false;
    private boolean shouldUpdateBoard = false;
    private boolean outOfTiles = false;
    private boolean shouldPass = false;

    private PlayerObj meObj;
    private BoardObj boardObj;
    private FrameObj frameObj;
    private GADDAG gaddag;
    private MCTS mcts;

    int wordsPlaced = 0;

    int gNodeCounter = 0;
    int putCounter = 0;
    int maxPutCounter = -1;
    int branchCounter = 0;
    int invalidPutCounter = 0;
    public static final boolean DEBUG = false;

    String lastTurnInfo;
    String allInfo;

    JunkBot4(PlayerAPI me, OpponentAPI opponent, BoardAPI board, UserInterfaceAPI ui, DictionaryAPI dictionary) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.info = ui;
        this.dictionary = dictionary;
        turnCount = 0;
        this.lastTurnInfo = "";
        this.allInfo = "";
    }

    /**
     * Method to place the selected move for a turn onto the board
     * @param w Pass the word to be placed.
     */
    private void submitMove(Word w)
    {
        placeWord(this.boardObj, this.frameObj, w);
        turnCount++;
        shouldUpdateBoard = true;
    }

    // Additional game model classes with extended functionality

    /**
     * Private Square class with anchor boolean and cross sets.
     */
    private class SquareObj extends Square
    {
        private boolean isAnchor;
        public boolean[] horizontalCrossSet;
        public boolean[] verticalCrossSet;

        public SquareObj(Square square)
        {
            super(square.getLetterMuliplier(), square.getWordMultiplier());

            if(square.isOccupied())
            {
                this.add(square.getTile());
            }

            horizontalCrossSet = new boolean[26];
            verticalCrossSet = new boolean[26];

            for(char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
            {
                int index = computeIndex(c);

                horizontalCrossSet[index] = true;
                verticalCrossSet[index] = true;
            }

            isAnchor = false;
        }

        public SquareObj(SquareObj square)
        {
            super(square.getLetterMuliplier(), square.getWordMultiplier());

            horizontalCrossSet = new boolean[26];
            verticalCrossSet = new boolean[26];

            if(square.isOccupied())
            {
                this.add(square.getTile());
            }

            for(int i = 0; i < square.getHorizontalCrossSet().length; i++)
            {
                this.horizontalCrossSet[i] = square.getHorizontalCrossSet()[i];
            }

            for(int i = 0; i < square.getVerticalCrossSet().length; i++)
            {
                this.verticalCrossSet[i] = square.getVerticalCrossSet()[i];
            }

            this.isAnchor = square.isAnchor();
        }

        public SquareObj(int letterMult, int wordMult)
        {
            super(letterMult, wordMult);

            horizontalCrossSet = new boolean[26];
            verticalCrossSet = new boolean[26];

            for(char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
            {
                int index = computeIndex(c);

                horizontalCrossSet[index] = true;
                verticalCrossSet[index] = true;
            }
        }

        public boolean isAnchor() {
            return isAnchor;
        }

        public void setAnchor(boolean anchor) {
            isAnchor = anchor;
        }

        public boolean[] getHorizontalCrossSet() {
            return horizontalCrossSet;
        }

        public boolean[] getVerticalCrossSet() {
            return verticalCrossSet;
        }

        public boolean isValidVert(char c)
        {
            return verticalCrossSet[computeIndex(c)];
        }

        public boolean isValidHoriz(char c)
        {
            return horizontalCrossSet[computeIndex(c)];
        }

        public void clearVerticalCrossSet()
        {
            Arrays.fill(verticalCrossSet, false);
        }

        public void clearHorizontalCrossSet()
        {
            Arrays.fill(horizontalCrossSet, false);
        }

        public void addValidVertical(char c)
        {
            int index = computeIndex(c);

            if(index != -1)
            {
                verticalCrossSet[index] = true;
            }
        }

        public void addValidHorizontal(char c)
        {
            int index = computeIndex(c);

            if(index != -1)
            {
                horizontalCrossSet[index] = true;
            }
        }

        public void addAllValidVertical(Set<Character> input)
        {
            for(Character c : input)
            {
                addValidVertical(c);
            }
        }

        public void addAllValidHoriz(Set<Character> input)
        {
            for(Character c : input)
            {
                addValidHorizontal(c);
            }
        }

        private int computeIndex(char c)
        {
            if((byte) c == 0)
            {
                return -1;
            }
            if(c >= 'a' && c <= 'z')
            {
                return (int) (c - 'a');
            }
            else if(c >= 'A' && c <= 'Z')
            {
                return (int) (c - 'A');
            }
            else
            {
                throw new IllegalArgumentException("Invalid character index conversion.");
            }
        }

        public int verticalSize()
        {
            int size = 0;

            for (boolean b : verticalCrossSet) {
                if (b) {
                    size++;
                }
            }

            return size;
        }

        public int horizontalSize()
        {
            int size = 0;

            for (boolean b : horizontalCrossSet) {
                if (b) {
                    size++;
                }
            }

            return size;
        }

        @Override public String toString()
        {
            if(isOccupied())
            {
                return getTile().toString();
            }
            else
            {
                return "_";
            }
        }

    }

    /**
     * Private Board class with extra accessors for easier manipulation of the Board
     */
    private class BoardObj
    {
        public static final int BOARD_SIZE = 15;
        public static final int BOARD_CENTRE = 7;
        private int BONUS = 50;

        public static final int WORD_INCORRECT_FIRST_PLAY = 0;
        public static final int WORD_OUT_OF_BOUNDS = 1;
        public static final int WORD_LETTER_NOT_IN_FRAME = 2;
        public static final int WORD_LETTER_CLASH = 3;
        public static final int WORD_NO_LETTER_PLACED = 4;
        public static final int WORD_NO_CONNECTION = 5;
        public static final int WORD_EXCLUDES_LETTERS = 6;
        public static final int WORD_ONLY_ONE_LETTER = 7;

        private final int[][] LETTER_MULTIPLIER =
                { 	{1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 3, 1, 1, 1, 3, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1},
                        {2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 3, 1, 1, 1, 3, 1, 1, 1, 3, 1, 1, 1, 3, 1},
                        {1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 1},
                        {1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1},
                        {1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 1},
                        {1, 3, 1, 1, 1, 3, 1, 1, 1, 3, 1, 1, 1, 3, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2},
                        {1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 3, 1, 1, 1, 3, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1} };
        private final int[][] WORD_MULTIPLIER =
                {   {3, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 3},
                        {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
                        {1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1},
                        {1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1},
                        {1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {3, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 3},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                        {1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1},
                        {1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1},
                        {1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1},
                        {1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
                        {3, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 3} };

        private SquareObj[][] squares;
        private int errorCode;
        private int numPlays;
        private ArrayList<Coordinates> newLetterCoords;

        public BoardObj() {
            squares = new SquareObj[BOARD_SIZE][BOARD_SIZE];
            for (int r=0; r<BOARD_SIZE; r++)  {
                for (int c=0; c<BOARD_SIZE; c++)   {
                    squares[r][c] = new SquareObj(LETTER_MULTIPLIER[r][c],WORD_MULTIPLIER[r][c]);
                }
            }
            numPlays = 0;
        }

        public void setSquares(SquareObj[][] input)
        {
            this.squares = input;
        }


        public boolean isLegalPlay(FrameObj frame, Word word) {
            boolean isLegal = true;
            //check for invalid first play
            if (numPlays == 0 &&
                    ((word.isHorizontal() && (word.getRow()!=BOARD_CENTRE || word.getFirstColumn()>BOARD_CENTRE ||
                            word.getLastColumn()<BOARD_CENTRE)) ||
                            (word.isVertical() && (word.getColumn()!=BOARD_CENTRE || word.getFirstRow()>BOARD_CENTRE ||
                                    word.getLastRow()<BOARD_CENTRE)))) {
                isLegal = false;
                errorCode = WORD_INCORRECT_FIRST_PLAY;
            }
            // check for word out of bounds
            if (isLegal &&
                    ((word.getRow() >= BOARD_SIZE) ||
                            (word.getFirstColumn() >= BOARD_SIZE) ||
                            (word.getLastRow()>= BOARD_SIZE) ||
                            (word.getLastColumn()>= BOARD_SIZE)) ) {
                isLegal = false;
                errorCode = WORD_OUT_OF_BOUNDS;
            }
            // check that letters in the word do not clash with those on the board
            String lettersPlaced = "";
            if (isLegal) {
                int r = word.getFirstRow();
                int c = word.getFirstColumn();
                for (int i = 0; i < word.length() && isLegal; i++) {
                    if (squares[r][c].isOccupied() && squares[r][c].getTile().getLetter() != word.getLetter(i)) {
                        isLegal = false;
                        errorCode = WORD_LETTER_CLASH;
                    } else if (!squares[r][c].isOccupied()) {
                        lettersPlaced = lettersPlaced + word.getLetter(i);
                    }
                    if (word.isHorizontal()) {
                        c++;
                    } else {
                        r++;
                    }
                }
            }
            // check that more than one letter is placed
            if (isLegal && lettersPlaced.length() == 0) {
                isLegal = false;
                errorCode = WORD_NO_LETTER_PLACED;
            }
            // check that the letters placed are in the frame
            if (isLegal && !frame.isAvailable(lettersPlaced)) {
                isLegal = false;
                errorCode = WORD_LETTER_NOT_IN_FRAME;
            }
            // check that the letters placed connect with the letters on the board
            if (isLegal && numPlays>0) {
                int boxTop = Math.max(word.getFirstRow()-1,0);
                int boxBottom = Math.min(word.getLastRow()+1, BOARD_SIZE-1);
                int boxLeft = Math.max(word.getFirstColumn()-1,0);
                int boxRight = Math.min(word.getLastColumn()+1, BOARD_SIZE-1);
                boolean foundConnection = false;
                for (int r=boxTop; r<=boxBottom && !foundConnection; r++) {
                    for (int c=boxLeft; c<=boxRight && !foundConnection; c++) {
                        if (squares[r][c].isOccupied()) {
                            foundConnection = true;
                        }
                    }
                }
                if (!foundConnection) {
                    isLegal = false;
                    errorCode = WORD_NO_CONNECTION;
                }
            }
            // check there are no tiles before the word
            if (isLegal &&
                    (word.isHorizontal() && word.getFirstColumn()>0 &&
                            squares[word.getRow()][word.getFirstColumn()-1].isOccupied()) ||
                    (word.isHorizontal() && word.getLastColumn()<BOARD_SIZE-1 &&
                            squares[word.getRow()][word.getLastColumn()+1].isOccupied()) ||
                    (word.isVertical() && word.getFirstRow()>0 &&
                            squares[word.getFirstRow()-1][word.getColumn()].isOccupied()) ||
                    (word.isVertical() && word.getLastRow()<BOARD_SIZE-1 &&
                            squares[word.getLastRow()+1][word.getColumn()].isOccupied())) {
                isLegal = false;
                errorCode = WORD_EXCLUDES_LETTERS;
            }
            // check more than one letter
            if (isLegal && word.length()==1) {
                isLegal = false;
                errorCode = WORD_ONLY_ONE_LETTER;
            }
            return isLegal;
        }

        // getCheckCode precondition: isLegal is false
        public int getErrorCode() {
            return errorCode;
        }

        // place precondition: isLegal is true
        public void place(Bot0.FrameObj frame, Word word) {
            newLetterCoords = new ArrayList<>();
            int r = word.getFirstRow();
            int c = word.getFirstColumn();
            for (int i = 0; i<word.length(); i++) {
                //System.out.println("LETTER: " + word.getLetter(i) + ", R: " + r + ", C: " + c + ", isOccupied: " + squares[r][c].isOccupied());
                if (!squares[r][c].isOccupied())
                {
                    char letter = word.getLetter(i);
                    Tile tile = null;
                    tile = frame.getTile(letter);

                    if(tile == null)
                    {
                        System.out.println("TILE NOT OCCUPIED, PROBLEM...");
                        System.out.println(this.toString());
                    }
                    if (tile.isBlank()) {
                        tile.designate(word.getDesignatedLetter(i));
                    }
                    squares[r][c].add(tile);

                    squares[r][c].setAnchor(false); // No longer an anchor
                    squares[r][c].clearHorizontalCrossSet(); // No valid letters to place on this square
                    squares[r][c].clearHorizontalCrossSet(); // No valid letters to place on this square

                    frame.removeTile(tile);
                    newLetterCoords.add(new Coordinates(r,c));
                }
                if (word.isHorizontal()) {
                    c++;
                } else {
                    r++;
                }
            }
            numPlays++;
        }

        public ArrayList<Tile> pickupLatestWord() {
            ArrayList<Tile> tiles = new ArrayList<>();
            for (Coordinates coord : newLetterCoords) {
                Tile tile = squares[coord.getRow()][coord.getCol()].removeTile();
                if (tile.isBlank()) {
                    tile.removeDesignation();
                }
                tiles.add(tile);
            }
            return tiles;
        }

        private boolean isAdditionalWord(int r, int c, boolean isHorizontal) {
            if ((isHorizontal &&
                    (r>0 && squares[r-1][c].isOccupied() || (r<BOARD_SIZE-1 && squares[r+1][c].isOccupied()))) ||
                    (!isHorizontal) &&
                            (c>0 && squares[r][c-1].isOccupied() || (c<BOARD_SIZE-1 && squares[r][c+1].isOccupied())) ) {
                return true;
            }
            return false;
        }

        private Word getAdditionalWord(int mainWordRow, int mainWordCol, boolean mainWordIsHorizontal) {
            int firstRow = mainWordRow;
            int firstCol = mainWordCol;
            // search up or left for the first letter
            while (firstRow >= 0 && firstCol >= 0 && squares[firstRow][firstCol].isOccupied()) {
                if (mainWordIsHorizontal) {
                    firstRow--;
                } else {
                    firstCol--;
                }
            }
            // went too far
            if (mainWordIsHorizontal) {
                firstRow++;
            } else {
                firstCol++;
            }
            // collect the letters by moving down or right
            String letters = "";
            int r = firstRow;
            int c = firstCol;
            while (r<BOARD_SIZE && c<BOARD_SIZE && squares[r][c].isOccupied()) {
                letters = letters + squares[r][c].getTile().getLetter();
                if (mainWordIsHorizontal) {
                    r++;
                } else {
                    c++;
                }
            }
            return new Word (firstRow, firstCol, !mainWordIsHorizontal, letters);
        }

        public ArrayList<Word> getAllWords(Word mainWord) {
            ArrayList<Word> words = new ArrayList<>();
            words.add(mainWord);
            int r = mainWord.getFirstRow();
            int c = mainWord.getFirstColumn();
            for (int i=0; i<mainWord.length(); i++) {
                if (newLetterCoords.contains(new Coordinates(r,c))) {
                    if (isAdditionalWord(r, c, mainWord.isHorizontal())) {
                        words.add(getAdditionalWord(r, c, mainWord.isHorizontal()));
                    }
                }
                if (mainWord.isHorizontal()) {
                    c++;
                } else {
                    r++;
                }
            }
            return words;
        }

        private int getWordPoints(Word word) {
            int wordValue = 0;
            int wordMultipler = 1;
            int r = word.getFirstRow();
            int c = word.getFirstColumn();
            for (int i = 0; i<word.length(); i++) {
                int letterValue = squares[r][c].getTile().getValue();
                if (newLetterCoords.contains(new Coordinates(r,c))) {
                    wordValue = wordValue + letterValue * squares[r][c].getLetterMuliplier();
                    wordMultipler = wordMultipler * squares[r][c].getWordMultiplier();
                } else {
                    wordValue = wordValue + letterValue;
                }
                if (word.isHorizontal()) {
                    c++;
                } else {
                    r++;
                }
            }
            return wordValue * wordMultipler;
        }

        public int getAllPoints(ArrayList<Word> words) {
            int points = 0;
            for (Word word : words) {
                points = points + getWordPoints(word);
            }
            if (newLetterCoords.size() == Frame.MAX_TILES) {
                points = points + BONUS;
            }
            return points;
        }

        public boolean isFirstPlay() {
            return numPlays == 0;
        }

        // Additional methods added

        /*
        public SquareObj getSquare2(int row, int col) {
            if(row >= BOARD_SIZE || col >= BOARD_SIZE || row < 0 || col < 0)
                return new SquareObj(1, 1);
            return squares[col][row];
        }

         */

        public Bot0.SquareObj getSquare(int row, int col)
        {
            if(row >= BOARD_SIZE || col >= BOARD_SIZE || row < 0 || col < 0)
                return new Bot0.SquareObj(1, 1);
            return squares[row][col];
        }

        /**
         * Utility Method to get the letter from a specific square
         * @param row Pass the row of the square
         * @param col Pass the column of the square
         * @return char Returns the character (letter) of the tile on the square.
         */
        public char getLetter(int row, int col)
        {
            if(this.squares[row][col].isOccupied())
            {
                return this.squares[row][col].getTile().getLetter();
            }
            else
            {
                throw new IllegalArgumentException();
                // return '!';
            }
        }

        public Bot0.BoardObj deepCopy()
        {
            Bot0.BoardObj b = new Bot0.BoardObj();

            for(int i = 0; i < BOARD_SIZE; i++)
            {
                for(int j = 0; j < BOARD_SIZE; j++)
                {
                    b.squares[i][j] = new Bot0.SquareObj(this.squares[i][j]);
                }
            }

            b.errorCode = this.errorCode;
            if(this.newLetterCoords != null)
            {
                b.newLetterCoords = new ArrayList<>(this.newLetterCoords);
            }
            else
            {
                b.newLetterCoords = new ArrayList<>();
            }

            b.numPlays = this.numPlays;

            return b;
        }

        /**
         * Method to check if a square is a valid anchor for a new word placement
         * @param row Pass the row of the square
         * @param col Pass the column of the square
         * @return boolean Returns true if the square is a valid anchor, else false.
         */
        public boolean isValidAnchor(int row, int col)
        {
            // First, check if occupied
            if(!squareIsOccupied(row, col))
            {
                if(row == BOARD_CENTRE && col == BOARD_CENTRE) // If the centre square and not occupied, always valid.
                {
                    return true;
                }

                // Otherwise, check for another square around this (above, below, left, right)
                return squareIsOccupied(row-1, col) || squareIsOccupied(row+1,col) || squareIsOccupied(row, col-1) || squareIsOccupied(row, col+1);
            }

            return false;
        }

        /**
         * Method to check if a square is occupied
         * @param row Pass the row of the square
         * @param col Pass the column of the square
         * @return boolean Returns true if the square is occupied, else false.
         */
        private boolean squareIsOccupied(int row, int col)
        {
            try
            {
                return squares[row][col].isOccupied();
            }
            catch(Exception ex)
            {
                return false;
            }
        }

        /*
        public boolean squareIsOccupied2(int col, int row)
        {
            try
            {
                return squares[row][col].isOccupied();
            }
            catch(Exception ex)
            {
                return false;
            }
        }
         */

        /**
         * Method to update the anchors on the board.
         */
        public void updateAnchors()
        {
            // For each square, recalculate isValidAnchor
            for(int row = 0; row < BOARD_SIZE; row++)
            {
                for(int col = 0; col < BOARD_SIZE; col++)
                {
                    squares[row][col].setAnchor(isValidAnchor(row, col));
                }
            }
        }

        /**
         * Method to place a word without a frame for processing opponent's moves.
         * @param w Pass the word to be placed.
         */
        public void placeCheat(Word w)
        {
            Bot0.FrameObj f = new Bot0.FrameObj();
            ArrayList<Tile> tiles = new ArrayList<>();

            for(char c : w.getLetters().toCharArray())
            {
                tiles.add(new Tile(c));
            }

            f.addTiles(tiles);

            if(f.size() != 7)
            {
                f.refill(new Pool());
            }

            place(f, w);
        }

        /**
         * Method to update the cross sets of the squares on the board.
         * @param gaddag Pass the current root of the Gaddag
         */
        public void updateCrossSets(Bot0.GNode gaddag)
        {
            for(int rows = 0; rows < Board.BOARD_SIZE; rows++)
            {
                for(int cols = 0; cols < Board.BOARD_SIZE; cols++)
                {
                    // If a square is occupied, clear it's horizontal and vertical cross sets
                    if(this.squareIsOccupied(rows, cols))
                    {
                        this.getSquare(rows, cols).clearVerticalCrossSet();
                        this.getSquare(rows, cols).clearHorizontalCrossSet();
                    }

                    if(this.getSquare(rows, cols).isAnchor())
                    {
                        // Check for occupied squares to the left and right
                        if(this.squareIsOccupied(rows, cols-1) || this.squareIsOccupied(rows, cols+1))
                        {
                            this.getSquare(rows, cols).clearHorizontalCrossSet();
                            updateHorizontalCrossSet(rows, cols, gaddag);
                        }

                        // Check for occupied squares above and below this square
                        if(this.squareIsOccupied(rows-1, cols) || this.squareIsOccupied(rows+1, cols))
                        {
                            this.getSquare(rows, cols).clearVerticalCrossSet();
                            updateVerticalCrossSet(rows, cols, gaddag);
                        }
                    }
                }
            }
        }

        /**
         * Method to update the horizontal cross set of a square.
         * @param rows Pass the row of the square
         * @param cols Pass the column of the square
         * @param root Pass the root of the GADDAG
         */
        private void updateHorizontalCrossSet(int rows, int cols, Bot0.GNode root)
        {
            // Start from root
            Bot0.GNode curNode = root;

            // If it has a tile left and right
            if(squareIsOccupied(rows, cols - 1) && squareIsOccupied(rows, cols + 1))
            {
                int j = cols - 1; // cols Coordinate of prefix

                // Traverse to the beginning of the prefix
                while(squareIsOccupied(rows, j))
                {
                    curNode = curNode.get(getLetter(rows, j)); // Update curNode by traversing GADDAG with each letter of the prefix

                    if(curNode == null) // If it does not exist, return because not a valid prefix
                    {
                        return;
                    }

                    j--; // Decrement cols coordinate to move further up the prefix
                }


                // Start making the prefix
                curNode = curNode.get('#'); // Traverse to the Delimiter

                if(curNode != null) // If there are valid endings to pair with the prefix
                {
                    Bot0.GNode startNode = curNode; // start node

                    for(char c : Bot0.UtilityMethods.generateAlphabetSet()) // For each letter in the alphabet
                    {
                        curNode = startNode; // Update curNode
                        curNode = curNode.get(c); // Traverse with the current character
                        j = cols + 1; // Update column coordinate

                        // While there is a letter to the right and haven't reached a null node
                        while(curNode != null && squareIsOccupied(rows, j + 1))
                        {
                            curNode = curNode.get(getLetter(rows, j)); // Traverse the gaddag with the letter
                            j++; // Increment j to move along the board
                        }

                        if(curNode != null) // If the word traversed so far is valid in the gaddag
                        {
                            if(curNode.isValidEnd(getLetter(rows, j))) // If this is a valid word end
                            {
                                squares[rows][cols].addValidHorizontal(c); // Add the letter to the the horizontal cross set for the square
                            }
                        }
                    }
                }
            }
            // Otherwise, if there is a tile before it
            else if(squareIsOccupied(rows, cols - 1))
            {
                int j = cols - 1;

                // Traverse prefix
                while(squareIsOccupied(rows, j))
                {
                    curNode = curNode.get(getLetter(rows, j)); // Traverse gaddag

                    if(curNode == null) // If invalid, return
                    {
                        return;
                    }

                    j--; // Decrement to traverse backwards to construct prefix
                }

                curNode = curNode.get('#'); // Get delimiter

                if(curNode != null) // If a valid node position
                {
                    squares[rows][cols].addAllValidHoriz(curNode.getEndSet()); // Add the end set of this node (delimiter) to horizontal cross set
                }
            }
            // Else if there is a tile after this square
            else if(squareIsOccupied(rows, cols + 1))
            {
                int j = cols + 1;

                while(squareIsOccupied(rows, j + 1)) // Traverse to the end of this line of tiles
                {
                    j++;
                }

                while(j > cols) // While not at the starting position
                {
                    curNode = curNode.get(getLetter(rows, j)); // Traverse gaddag with current letter

                    if(curNode == null) // If invalid, return
                    {
                        return;
                    }

                    j--; // Decrement to move backwards
                }

                squares[rows][cols].addAllValidHoriz(curNode.getEndSet()); // Add the current end set to the horizontal cross set
            }
        }

        /**
         * Method to update the vertical cross set of a square.
         * @param rows Pass the row of the square
         * @param cols Pass the column of the square
         * @param root Pass the root of the GADDAG
         */
        private void updateVerticalCrossSet(int rows, int cols, Bot0.GNode root)
        {
            Bot0.GNode curNode = root;

            // If it has a tile to the left and right
            if(squareIsOccupied(rows - 1, cols) && squareIsOccupied(rows + 1, cols))
            {
                int i = rows - 1; // rows Coordinate of prefix

                // Traverse to the beginning of the prefix
                while(squareIsOccupied(i, cols))
                {
                    curNode = curNode.get(getLetter(i, cols)); // Traverse gaddag
                    if(curNode == null) // If it does not exist, return
                    {
                        return;
                    }
                    i--; // Decrement rows coordinate to move upwards
                }


                curNode = curNode.get('#'); // Traverse to delimiter

                if(curNode != null) // If valid
                {
                    Bot0.GNode startNode = curNode; // Keep curNode starting position

                    for(char c : Bot0.UtilityMethods.generateAlphabetSet()) // For each letter in the alphabet
                    {
                        curNode = startNode; // Reset curNode position
                        curNode = curNode.get(c); // Travere gaddag
                        i = rows + 1; //

                        // While there is a letter to the right and haven't reached a null node
                        while(curNode != null && squareIsOccupied(i + 1, cols))
                        {
                            curNode = curNode.get(getLetter(i, cols)); // Traverse gaddag
                            i++; // Increment i
                        }

                        if(curNode != null) // If valid node position
                        {
                            if(curNode.isValidEnd(getLetter(i, cols))) // If valid end
                            {
                                squares[rows][cols].addValidVertical(c); // Add char to legal vertical set
                            }
                        }
                    }
                }
            }
            // Otherwise, if there is a tile before it
            else if(squareIsOccupied(rows - 1, cols))
            {
                int i = rows - 1;

                // Traverse prefix
                while(squareIsOccupied(i, cols))
                {
                    curNode = curNode.get(getLetter(i, cols));

                    if(curNode == null) {
                        return;
                    }

                    i--;
                }

                curNode = curNode.get('#'); // Traverse to delimiter

                if(curNode != null) // If valid node
                {
                    squares[rows][cols].addAllValidVertical(curNode.getEndSet()); // Add endset to legal vertical
                }
            }
            // Else if there is a tile after it
            else if(squareIsOccupied(rows + 1, cols))
            {
                int i = rows + 1;

                // Traverse to end of the suffix
                while(squareIsOccupied(i + 1, cols))
                {
                    i++;
                }

                // While not back at starting position
                while(i > rows)
                {
                    curNode = curNode.get(getLetter(i, cols)); // Traverse prefix

                    if(curNode == null) // If invalid position, return
                    {
                        return;
                    }

                    i--; // Move backwards up the suffix
                }
                squares[rows][cols].addAllValidVertical(curNode.getEndSet()); // Add end set to valid vertical
            }
        }

        @Override public String toString()
        {
            StringBuilder boardString = new StringBuilder();

            for(int i = 0; i < BOARD_SIZE; i++)
            {
                for(int j = 0; j < BOARD_SIZE; j++)
                {
                    boardString.append(getSquare(i, j).toString() + " ");
                }
                boardString.append("\n");
            }

            return boardString.toString();
        }

    }

    // Extend square class to include cross sets (can super the constructor)

    /**
     * Private Frame class with extra methods for easier manipulation of the frame
     */
    private class FrameObj extends Frame
    {
        public Bot0.FrameObj deepCopy()
        {
            Bot0.FrameObj f = new Bot0.FrameObj();

            ArrayList<Tile> tilesToAdd = new ArrayList<>();

            for(int i = 0; i < this.getTiles().size(); i++)
            {
                tilesToAdd.add(new Tile(this.getTiles().get(i).getLetter()));
            }

            f.addTiles(tilesToAdd);
            f.errorCode = this.getErrorCode();

            return f;
        }
    }

    /**
     * Private player class
     */
    private class PlayerObj
    {
        private int id;
        private String name;
        private double score;
        private Bot0.FrameObj frame;

        public PlayerObj(int id)  {
            this.id = id;
            name = "";
            score = 0;
            frame = new Bot0.FrameObj();
        }

        public PlayerObj(OpponentAPI opp)
        {
            this.id = opp.getPrintableId();
            this.name = opp.getName();
            this.score = opp.getScore();
            this.frame = new Bot0.FrameObj();
        }

        public PlayerObj(PlayerAPI me)
        {
            this.id = me.getPrintableId();
            this.name = me.getName();
            this.score = me.getScore();
            this.frame = parseFrameFromString(me.getFrameAsString());
        }

        public Bot0.PlayerObj deepCopy()
        {
            Bot0.PlayerObj p = new Bot0.PlayerObj(this.getPrintableId());
            p.setFrame(this.getFrame().deepCopy());
            p.setName(this.getName());
            p.score = this.getScore();

            return p;
        }

        public int getPrintableId() {
            return id+1;
        }

        public void setName(String text) {
            name = text;
        }

        public String getName() {
            return name;
        }

        public void addPoints(double increase) {
            score = score + increase;
        }

        public void subtractPoints(double decrease) {
            score = score - decrease;
        }

        public double getScore() {
            return score;
        }

        public void setScore(int score)
        {
            this.score = score;
        }

        public Bot0.FrameObj getFrame() {
            return frame;
        }

        public void setFrame(Bot0.FrameObj newFrame)
        {
            this.frame = newFrame;
        }

        public String getFrameAsString() {return frame.toString();}

        public void adjustScore() {
            int unused = 0;
            ArrayList<Tile> tiles = frame.getTiles();
            for (Tile tile : tiles) {
                unused = unused + tile.getValue();
            }
            score = score - unused;
        }

        public String toString() {

            if (name.isEmpty()) {
                return "Player " + getPrintableId();
            } else {
                return name;
            }
        }
    }
}