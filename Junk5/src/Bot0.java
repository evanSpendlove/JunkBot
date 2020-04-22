
import java.io.*;
import java.util.*;

public class Bot0 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    private PlayerAPI me;
    private OpponentAPI opponent;
    private BoardAPI board;
    private UserInterfaceAPI info;
    private DictionaryAPI dictionary;
    private int turnCount;
    private boolean isNamed = false;

    private BoardObj boardObj;
    private FrameObj frameObj;

    Bot0(PlayerAPI me, OpponentAPI opponent, BoardAPI board, UserInterfaceAPI ui, DictionaryAPI dictionary) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.info = ui;
        this.dictionary = dictionary;
        turnCount = 0;
    }

    public String getCommand() {
        // Add your code here to input your commands
        // Your code must give the command NAME <botname> at the start of the game

        if(turnCount == 0 && isNamed == false)
        {
            isNamed = true;
            return "NAME JUNKBOT \n";
        }

        this.boardObj = parseBoardFromAPI();
        this.frameObj = parseFrameFromString(me.getFrameAsString());

        LeaveValues.initialise();

        System.out.println(LeaveValues.leaveMaps.get(0).toString());
        System.out.println(LeaveValues.leaveMaps.get(1).toString());

        for(int i = 0; i < 10; i++)
        {
            FrameObj f = new FrameObj();
            f.refill(new Pool());
            //f.removeTile(f.getTiles().get(0));
            //f.removeTile(f.getTiles().get(0));
            f.removeTile(f.getTiles().get(0));

            System.out.println(LeaveValues.getLeaveFromFrame(f));
        }

        System.out.println(LeaveValues.leaveMaps.get(0).toString());
        System.out.println("Largest map: " + LeaveValues.leaveMaps.get(5).size());

        System.out.println(info.getAllInfo());

        String command = "";
        switch (turnCount) {
            case 0:
                command = "NAME Bot0";
                break;
            case 1:
                command = "PASS";
                break;
            case 2:
                command = "HELP";
                break;
            case 3:
                command = "SCORE";
                break;
            case 4:
                command = "POOL";
                break;
            default:
                command = "H8 A AN";
                break;
        }
        turnCount++;
        return command;
    }

    protected ArrayList<Word> getAllPossibleMoves()
    {
        return new ArrayList<Word>();
    }

    protected FrameObj parseFrameFromString(String input)
    {
        ArrayList<Tile> tiles = new ArrayList<>();

        int offset = 1;
        int multiplier = 3;

        for(int i = 0; i < 7; i++)
        {
            tiles.add(new Tile(input.charAt(offset + (i * multiplier))));
        }

        FrameObj frame = new FrameObj();
        frame.addTiles(tiles);

        return frame;
    }

    protected BoardObj parseBoardFromAPI()
    {
        return new BoardObj();
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

        private Square[][] squares;
        private int errorCode;
        private int numPlays;
        private ArrayList<Coordinates> newLetterCoords;

        public BoardObj() {
            squares = new Square[BOARD_SIZE][BOARD_SIZE];
            for (int r=0; r<BOARD_SIZE; r++)  {
                for (int c=0; c<BOARD_SIZE; c++)   {
                    squares[r][c] = new Square(LETTER_MULTIPLIER[r][c],WORD_MULTIPLIER[r][c]);
                }
            }
            numPlays = 0;
        }

        public boolean isLegalPlay(Frame frame, Word word) {
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
        public void place(Frame frame, Word word) {
            newLetterCoords = new ArrayList<>();
            int r = word.getFirstRow();
            int c = word.getFirstColumn();
            for (int i = 0; i<word.length(); i++) {
                if (!squares[r][c].isOccupied()) {
                    char letter = word.getLetter(i);
                    Tile tile = frame.getTile(letter);
                    if (tile.isBlank()) {
                        tile.designate(word.getDesignatedLetter(i));
                    }
                    squares[r][c].add(tile);
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

        public Square getSquare(int row, int col) {
            return squares[row][col];
        }

        public Square getSquareCopy(int row, int col) {
            return new Square(squares[row][col]);
        }

        public boolean isFirstPlay() {
            return numPlays == 0;
        }

        public BoardObj deepCopy()
        {
            BoardObj b = new BoardObj();

            b.squares = this.squares.clone();
            b.errorCode = this.errorCode;
            b.newLetterCoords = (ArrayList<Coordinates>) this.newLetterCoords.clone();
            b.numPlays = this.numPlays;

            return new BoardObj();
        }
    }

    /**
     * Private Frame class with extra accessors for easier manipulation of the frame
     */
    private class FrameObj extends Frame
    {
        public FrameObj deepCopy()
        {
            FrameObj f = new FrameObj();

            f.addTiles(this.getTiles());
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
        private FrameObj frame;

        public PlayerObj(int id)  {
            this.id = id;
            name = "";
            score = 0;
            frame = new FrameObj();
        }

        public PlayerObj deepCopy()
        {
            PlayerObj p = new PlayerObj(this.getPrintableId());
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

        public FrameObj getFrame() {
            return frame;
        }

        public void setFrame(FrameObj newFrame)
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

    private class MCTS
    {
        public static final int MAX_PLAYOUT_ITERATIONS = 10;
        private Tree MCT;

        public MCTS(BoardObj board, PlayerObj main, PlayerObj opp)
        {
            this.MCT = new Tree();
            this.MCT.getRoot().getState().setBoard(board);
            this.MCT.getRoot().getState().setMainPlayer(main.deepCopy());
            this.MCT.getRoot().getState().setOpponent(opp.deepCopy());
        }

        // TODO: WRITE AN OVERALL CONTROL METHOD THAT CALLS EXPANDS NODE AND APPENDS BEST NODE TO TREE

        public Word generateOptimalMove(BoardObj b, PlayerObj main, PlayerObj opp)
        {
            // Add new Node to the current node and update current
            this.MCT.updateCurrent(this.MCT.current.addChild(new Node(new State(b, main.deepCopy(), opp.deepCopy()), this.MCT.current)));

            ArrayList<Word> possibleWords = getAllPossibleMoves();

            Node optimalNode = expandNode(possibleWords);

            // Return the word played to get the optimal node
            return possibleWords.get(MCT.current.getChildren().indexOf(optimalNode));
        }

        /**
         * Method to perform a dynamic 2-ply playout for the game.
         */
        public Node expandNode(ArrayList<Word> words)
        {
            // For each word, create a child node and statically evaluate the node.
            for (Word word : words)
            {
                Node currentWord = MCT.current.addChild(new Node(MCT.current));
                evaluateNode(currentWord, word);
            }

            // Pick the best of the nodes.
            return findBestNode(MCT.current);
        }

        /**
         * Method to perform N iterations of a static 2-ply playout and update the .
         * @param currentWord Pass the current node to be played out.
         * @param initial_ply0 Pass the word to be played
         */
        private void evaluateNode(Node currentWord, Word initial_ply0)
        {
            // Run MAX_PLAYOUT_ITERATION times

            for(int i = 0; i < MAX_PLAYOUT_ITERATIONS; i++)
            {
                // Append new node to current word
                Node currentPlayout = currentWord.addChild(new Node(currentWord));

                // Playout node
                singleStaticPlayout(currentPlayout, initial_ply0);
            }

            // Find best node from nodes appended
            currentWord.updateAverageScore();
            currentWord.updateScoreDifferential();
        }

        private void singleStaticPlayout(Node currentWord, Word initial_ply0)
        {
            // Store final score and score differential (me - opponent) in nodes.

            // Me: Play move 0
            BoardObj board = currentWord.getState().getBoard();

            if(board.isLegalPlay(currentWord.getState().getMainPlayer().getFrame(), initial_ply0)) // Check validity
            {
                board.place(currentWord.getState().getMainPlayer().getFrame(), initial_ply0);
                int latestPoints = board.getAllPoints(board.getAllWords(initial_ply0));

                // Update score
                currentWord.getState().getMainPlayer().addPoints(latestPoints);
            }

            // Opponent: Generate random frame
            currentWord.getState().getOpponent().setFrame(generateRandomFrame());
            // Generate all moves
            ArrayList<Word> oppWords = getAllPossibleMoves();
            // Statically evaluate best
            Word oppBestWord = StaticValueGenerator.findBestWord(board, currentWord.getState().getOpponent().getFrame(), oppWords);
            // Play move 1
            board.place(currentWord.getState().getOpponent().getFrame(), oppBestWord);
            // Update score
            currentWord.getState().getOpponent().addPoints(board.getAllPoints(board.getAllWords(oppBestWord)));

            // Me: Refill frame with random tiles
            currentWord.getState().getMainPlayer().getFrame().refill(new Pool());
            // Generate all moves
            ArrayList<Word> mainWords = getAllPossibleMoves();
            // Statically evaluate best
            Word mainBestWord = StaticValueGenerator.findBestWord(board, currentWord.getState().getMainPlayer().getFrame(), mainWords);
            double scoreWithLeave = StaticValueGenerator.generateStaticValuation(board, mainBestWord, currentWord.getState().getMainPlayer().getFrame());
            // Play move 2
            board.place(currentWord.getState().getMainPlayer().getFrame(), mainBestWord);
            // Update score with leave
            currentWord.getState().getMainPlayer().addPoints(scoreWithLeave);

            // Update average score of node here ?
        }

        private FrameObj generateRandomFrame()
        {
            FrameObj f = new FrameObj();
            f.refill(new Pool());
            return f;
        }

        /**
         * Method to find the best move from a series of Nodes containing game states after static playout.
         * Current heuristic: Best average score.
         * @param root Pass the root whose children are to be parsed.
         * @return
         */
        private Node findBestNode(Node root)
        {
            return root.getHighestAvgScoreChild();
        }
    }

    private class State
    {
        private BoardObj board;
        private PlayerObj mainPlayer;
        private PlayerObj opponent;

        // Constructor

        public State(BoardObj board, PlayerObj mainPlayer, PlayerObj opponent) {
            this.board = board;
            this.mainPlayer = mainPlayer;
            this.opponent = opponent;
        }

        public State() {
        }

        // Getter and Setters

        public BoardObj getBoard() {
            return board;
        }

        public void setBoard(BoardObj board) {
            this.board = board;
        }

        public PlayerObj getMainPlayer() {
            return mainPlayer;
        }

        public void setMainPlayer(PlayerObj mainPlayer) {
            this.mainPlayer = mainPlayer;
        }

        public PlayerObj getOpponent() {
            return opponent;
        }

        public void setOpponent(PlayerObj opponent) {
            this.opponent = opponent;
        }
    }

    private class Node
    {
        // Fields
        private State state;
        private Node parent;
        private ArrayList<Node> children;

        private double averageScoreDifferential;
        private double averageScore;

        // Constructors
        public Node()
        {
            this.parent = null;
            this.state = new State();
            this.children = new ArrayList<>();
            this.averageScore = 0;
            this.averageScoreDifferential = 0;
        }

        public Node(State state) {
            this.state = state;
            this.children = new ArrayList<>();
            this.averageScore = 0;
            this.averageScoreDifferential = 0;
        }

        public Node(State state, Node parent) {
            this.state = state;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.averageScore = 0;
            this.averageScoreDifferential = 0;
        }

        public Node(State state, Node parent, ArrayList<Node> children) {
            this.state = state;
            this.parent = parent;
            this.children = children;
            this.averageScore = 0;
            this.averageScoreDifferential = 0;
        }

        public Node(Node parent)
        {
            this.state = parent.state;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.averageScore = 0;
            this.averageScoreDifferential = 0;
        }


        // Getters and Setters
        public double getAverageScoreDifferential() {
            return averageScoreDifferential;
        }

        public void setAverageScoreDifferential(double averageScoreDifferential) {
            this.averageScoreDifferential = averageScoreDifferential;
        }

        public void updateScoreDifferential()
        {
            double scoreDiff = 0;

            for(int i = 0; i < getChildren().size(); i++)
            {
                // Add the score of the main player less their opponent
                scoreDiff += (getChildren().get(i).getState().getMainPlayer().getScore() - getChildren().get(i).getState().getOpponent().getScore());
            }

            this.averageScoreDifferential = (scoreDiff / getChildren().size());
        }

        public double getAverageScore() {
            return averageScore;
        }

        public void incrementAverageScore(double increment) {
            this.averageScore = averageScore + increment;
        }

        public void updateAverageScore()
        {
            double averageScore = 0;

            for(int i = 0; i < getChildren().size(); i++)
            {
                // Add the score of the main player
                averageScore += getChildren().get(i).getState().getMainPlayer().getScore();
            }

            this.averageScore = (averageScore / getChildren().size());
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        /**
         * Getter for children ArrayList
         * @return ArrayList Returns the children ArrayList of nodes
         */
        public ArrayList<Node> getChildren() {
            return children;
        }

        /**
         * Method to add a child to children ArrayList
         * @param child Pass the child to be added
         */
        public Node addChild(Node child)
        {
            if(child != null)
            {
                this.children.add(child);
            }

            return child;
        }

        /**
         * Method to get the highest average score of the children of this Node
         * @return Node Returns the node with the highest average score
         */
        public Node getHighestAvgScoreChild()
        {
            Node bestChild = getChildren().get(0);

            for(int i = 1; i < getChildren().size(); i++) // Find max score of children
            {
                if(getChildren().get(i).getAverageScore() > bestChild.getAverageScore()) // If more than max
                {
                    bestChild = getChildren().get(i); // Update max
                }
            }

            return bestChild;
        }

        /**
         * Method to conserve memory by freeing unused nodes from expanding and exploring potential plays.
         * @param target Pass the target node that was chosen as to continue the tree
         */
        public void freeUnusedChildren(Node target)
        {
            int n = this.getChildren().size();

            for (int i = 0; i < n; i++)
            {
                if(this.getChildren().get(i) != target)
                {
                    this.getChildren().remove(i);
                }
            }
        }
    }

    private class Tree {

        private Node root;
        private Node current;

        public Tree() {
            root = new Node();
            current = root;
        }

        public Tree(Node root) {
            this.root = root;
        }

        public Node getRoot() {
            return root;
        }

        public void addChild(Node parent, Node child) {
            parent.addChild(child);
        }

        public void updateCurrent(Node current)
        {
            this.current = current;
        }

        public Node getCurrent()
        {
            return this.current;
        }

    }

    /**
     * Class for generating a static value from the score of the word on the board + the leave
     */
    private static class StaticValueGenerator
    {
        // Static method to value something
        public static double generateStaticValuation(BoardObj b, Word w, FrameObj f)
        {
            double score = 0f;

            // Score placement on board

            if(b.isLegalPlay(f, w))
            {
                score += b.getAllPoints(b.getAllWords(w));
            }
            else
            {
                throw new IllegalArgumentException("Invalid play, cannot score this play.");
            }

            // Get the leave score
            BoardObj board = b.deepCopy();
            FrameObj frame = f.deepCopy();

            board.place(frame, w);
            score += LeaveValues.getLeaveFromFrame(frame);

            return score;
        }

        public static Word findBestWord(BoardObj b, FrameObj f, ArrayList<Word> words)
        {
            double max = Integer.MIN_VALUE;
            int max_index = -1;

            // Find max of valuations
            for(int i = 0; i < words.size(); i++)
            {
                double current = generateStaticValuation(b, words.get(i), f);
                if(current > max)
                {
                    max = current;
                    max_index = i;
                }
            }

            if(max_index == -1)
            {
                throw new IllegalStateException("No word scored better than MIN_VALUE.");
            }

            return words.get(max_index);
        }
    }

    /**
     * Static class for getting the leave values of a rack.
     */
    private static class LeaveValues
    {
        private static boolean isInitialised = false;
        private static List<HashMap<String, Double>> leaveMaps = new ArrayList<>();

        // Leaves
        private static ArrayList<StringBuilder> leaves0 = new ArrayList<>();
        private static ArrayList<StringBuilder> leaves1 = new ArrayList<>();
        private static ArrayList<StringBuilder> leaves2 = new ArrayList<>();
        private static ArrayList<StringBuilder> leaves3 = new ArrayList<>();
        private static ArrayList<StringBuilder> leaves4 = new ArrayList<>();
        private static ArrayList<StringBuilder> leaves5 = new ArrayList<>();

        public LeaveValues()
        {
            initialise();
        }

        public static void initialise()
        {
            if(!isInitialised)
            {
                boolean fileInput = true;

                // Initialise maps
                for(int i = 0; i < 6; i++)
                {
                    leaveMaps.add(new HashMap<String, Double>());
                }

                if(fileInput)
                {
                    try
                    {
                        readInLeaves("src/leave1.txt", 0);
                        readInLeaves("src/leave2.txt", 1);
                        readInLeaves("src/leave3.txt", 2);
                        readInLeaves("src/leave4.txt", 3);
                        readInLeaves("src/leave5.txt", 4);
                        readInLeaves("src/leave6.txt", 5);
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    //setupLeaves();

                    addAllLeaves(leaves0, 0);
                    addAllLeaves(leaves1, 1);
                    addAllLeaves(leaves2, 2);
                    addAllLeaves(leaves3, 3);
                    addAllLeaves(leaves4, 4);
                    addAllLeaves(leaves5, 5);

                    leaves0.clear();
                    leaves1.clear();
                    leaves2.clear();
                    leaves3.clear();
                    leaves4.clear();
                    leaves5.clear();
                }

                try
                {
                    //convertToStringBuilder("src/leave1.txt", "leave1_SB.txt", 0);
                    //convertToStringBuilder("src/leave2.txt", "leave2_SB.txt", 1);
                    //convertToStringBuilder("src/leave3.txt", "leave3_SB.txt", 2);
                    //convertToStringBuilder("src/leave4.txt", "leave4_SB.txt", 3);
                    //convertToStringBuilder("src/leave5.txt", "leave5_SB.txt", 4);
                    //convertToStringBuilder("src/leave6.txt", "leave6_SB.txt", 5);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

                isInitialised = true;
            }
        }

        // Method to check a leave value
        // What if not in leaves?

        public static double getLeaveFromFrame(FrameObj frame)
        {
            if(!isInitialised)
            {
                initialise();
            }

            String leave = convertFrameToLeave(frame);

            if(leave.length() == 0)
            {
                return 0;
            }

            if(leaveMaps.get(leave.length() - 1).containsKey(leave))
            {
                System.out.println("MAP CONTAINS KEY");
                return leaveMaps.get(leave.length() - 1).get(leave);
            }
            else
            {
                System.out.println("NO KEY FOUND");
                return calculateDefaultLeave(leave);
            }
        }

        private static String convertFrameToLeave(FrameObj frame)
        {
            char[] leave = new char[frame.getTiles().size()];

            for(int i = 0; i < frame.getTiles().size(); i++)
            {
                char c = frame.getTiles().get(i).getLetter();
                if(c == '_')
                {
                    c = '?';
                }
                leave[i] = c;
            }

            return sortString(leave);
        }

        private static String sortString(char[] input)
        {
            Arrays.sort(input);

            String currentLeave = new String(input);

            return new String(input);
        }

        // Could implement an algorithm to check every permutation as a key.
        // Would be better to implement a longer but more intelligent algorithm searching for max subset that can be found
        private static double calculateDefaultLeave(String leave)
        {
            int length = leave.length();
            double value = 0.0;

            for(int i = 0; i < length; i++)
            {
                value += leaveMaps.get(0).get(Character.toString(leave.charAt(i)));
            }

            return value;
        }

        private static void readInLeaves(String filePath, int fileIndex) throws IOException {
            File in = new File(filePath);

            BufferedReader buf = new BufferedReader(new FileReader(in));
            String line;
            int lineCounter = 0;

            while((line = buf.readLine()) != null)
            {
                String[] currentLeave = line.split("\\s");
                leaveMaps.get(fileIndex).put(currentLeave[0], Double.parseDouble(currentLeave[1]));
                lineCounter++;
            }

            System.out.println("FILE: " + filePath + ", LINE COUNT: " + lineCounter);
            lineCounter = 0;

            buf.close();
        }

        // Method for initialising leaves:
        /*
             --- LINE COUNTS ---
            FILE: src/leave1.txt, LINE COUNT: 27
            FILE: src/leave2.txt, LINE COUNT: 373
            FILE: src/leave3.txt, LINE COUNT: 3509
            FILE: src/leave4.txt, LINE COUNT: 25254
            FILE: src/leave5.txt, LINE COUNT: 148150
            FILE: src/leave6.txt, LINE COUNT: 737311

            Strategy:

            Read in chunks into a single String.
            Since we have line counts, pre-define the chunk sizes (500 lines).
            Now, we have the number of Strings.

            Add all of these to the Arraylist.

            For parsing, write a function that parses the arraylists, add each line into the map
         */

        private static void addAllLeaves(ArrayList<StringBuilder> list, int leaveLength)
        {
            for(int i = 0; i < list.size(); i++)
            {
                String input = list.get(i).toString();

                if(!input.equals(""))
                {
                    String[] leaves = input.split("\n");
                    int N = leaves.length;

                    for(int j = 0; j < N; j++)
                    {
                        String[] currentLeave = leaves[j].split("\\s");

                        //System.out.println("Current leave: " + currentLeave[0] + " :: " + currentLeave[1]);

                        leaveMaps.get(leaveLength).put(currentLeave[0], Double.parseDouble(currentLeave[1]));
                    }
                }
            }
        }

        public static void convertToStringBuilder(String filePath, String desiredFileName, int fileIndex) throws IOException
        {
            File in = new File(filePath);
            PrintWriter writer = new PrintWriter(desiredFileName, "UTF-8");

            BufferedReader buf = new BufferedReader(new FileReader(in));
            String line;

            StringBuilder buffer = new StringBuilder();
            int lineCounter = 0;
            int appendCounter = 0;
            long totalLineCounter = 0;

            final long LOWER = 0;
            final long UPPER = 100000;

            writer.println("leaves" + fileIndex + ".add(new StringBuilder(\"\")");

            while((line = buf.readLine()) != null)
            {
                //System.out.println("Line: " + line);
                // writer.println(".append(\"" + line + "\\n" + "\")");

                //if(totalLineCounter < UPPER && totalLineCounter > LOWER)
                //{
                    if(lineCounter < 1000)
                    {
                        buffer.append(line).append("\\n");
                    }
                    else
                    {
                        lineCounter = 0;
                        if(appendCounter > 5)
                        {
                            // System.out.println("NEW SB ADDED");
                            writer.println(");\n\n");
                            writer.println("leaves" + fileIndex + ".add(new StringBuilder(\"\")");
                            appendCounter = 0;
                        }
                        else
                        {
                            appendCounter++;
                        }
                        writer.println(".append(\"" + buffer.toString() + "\")");
                        buffer.setLength(0);
                    }

                    lineCounter++;
                //}

                totalLineCounter++;
            }

            writer.println(".append(\"" + buffer.toString() + "\")");

            writer.println(");");

            System.out.println("FILE CONVERSION FINISHED - " + desiredFileName);

            writer.close();
            buf.close();
        }
    }
    /**
     * Class for representing a node in a GADDAG.
     */
    class GNode
    {
        // Fields
        GNode[] children;
        byte[] arcs;
        byte[] end;
        short childPointer = 0;
        byte endPointer = 0;

        @Override
        public String toString() {
            return "Node: " + "\narcs: " + Arrays.toString(this.byteArrayToCharArray(arcs))
                    + "\nEnd: " + Arrays.toString(this.getEndArray()) + "\n";
        }

        // Constructors
        public GNode()
        {
            children = new GNode[1];
            arcs = new byte[1];
            end = new byte[1];
            gNodeCounter++;
        }

        // Methods
        public GNode put(char newArc, GNode node)
        {
            GNode child = this.get(newArc);

            putCounter++;

            if (child == null)  // If there is no child yet
            {
                // Check if there is space
                children = checkArrayLength(children, childPointer);
                arcs = checkArrayLength(arcs, childPointer);

                //System.out.println("Child pointer: " + childPointer);

                // Add a new arc
                arcs[childPointer] = charToByte(newArc);

                // Update children
                children[childPointer] = node;
                //System.out.println("CP incremented: " + childPointer);
                childPointer++;

                if(childPointer > maxPutCounter)
                {
                    maxPutCounter = childPointer;
                }


                if(childPointer > 100)
                {
                    if(DEBUG)
                    {
                        System.out.println("Child Length: " + children.length);
                        System.out.println("End length: " + end.length);
                        System.out.println("Arcs length: " + arcs.length);
                        System.out.println("Num Children: " + childPointer);
                    }

                    invalidPutCounter++;
                }


                // Return the new child
                return node;
            }
            else  // Otherwise, there is a child so return it.
            {
                return child;
            }
        }

        // Public wrapper for put
        // Done
        public GNode put(char transitionChar)
        {
            return this.put(transitionChar, new GNode());
        }

        // Done
        // Getter for a character
        public GNode get(char transitionChar)
        {
            // For each of the children, search for the transition char and return that node if it exists
            for (int i = 0; i < childPointer; i++)
            {
                if (arcs[i] == charToByte(transitionChar))
                {
                    return children[i];
                }
            }
            return null; // If it doesn't exist, return null
        }

        // Check if the node contains a string
        public boolean contains(String word)
        {
            // We use # as a delimiter as it not a reserved character for regex matching or other java methods
            if (!word.matches(".*#.*"))
            {
                return containsWord(word.charAt(0) + "#" + word.substring(1)); // Add delimiter
            }
            else
            {
                return containsWord(word); // Pass the word as it is formatted correctly
            }
        }

        private boolean containsWord(String word)
        {
            GNode current = this;
            int length = word.length();

            // Check if the word is empty
            if(length == 0)
            {
                return false;
            }

            // Iteratively parse through the word checking if each character is valid
            for(int i = 0; i < length; i++)
            {
                char c = word.charAt(i); // Get the current character

                // If this is the last character of the word and is a valid end
                if(i == length - 1 && current.isValidEnd(c))
                {
                    return true;
                }

                // Otherwise, parse as normal.

                current = this.get(c);

                if(current == null) // If this node does not contain the letter
                {
                    return false;
                }
            }

            return false;
        }

        public boolean isValidEnd(char endChar)
        {
            return arrayContainsChar(end, endChar);
        }

        public void addNewEnd(char endChar)
        {
            end = checkArrayLength(end, endPointer); // Check that sufficient space exists
            end[endPointer] = charToByte(endChar);
            endPointer++;
        }

        public char[] getEndArray() {
            return byteArrayToCharArray(end);
        }

        public Set<Character> getEndSet()
        {
            Set<Character> endSet = new HashSet<Character>();

            for (char c : byteArrayToCharArray(end)) {
                endSet.add(c);
                //System.out.println("Char: " + c);
            }

            return endSet;
        }

        public GNode[] getChildrenArray() {
            return Arrays.copyOf(children, childPointer);
        }

        public char[] getArcs() {
            return byteArrayToCharArray(arcs);
        }

        /**
         * Method to convert a byte array to a char array.
         * @param bytes Pass the byte array to be converted.
         * @return char[] Returns a char array.
         */
        private char[] byteArrayToCharArray(byte[] bytes)
        {
            char[] out = new char[bytes.length];

            for(int i = 0; i < bytes.length; i++)
            {
                out[i] = (char) bytes[i];
            }

            return out;
        }

        /**
         * Method to ensure that there is enough space in an array for a new insertion
         * @param array Pass the array to be checked
         * @param targetIndex Pass the index at which you want to insert an item
         * @return
         */
        private GNode[] checkArrayLength(GNode[] array, int targetIndex)
        {

            if (targetIndex >= array.length)
            {
                return Arrays.copyOf(array, array.length * 2);
            }
            return array;
        }

        /**
         * Method to ensure that there is enough space in an array for a new insertion
         * @param array Pass the array to be checked
         * @param targetIndex Pass the index at which you want to insert an item
         * @return
         */
        private byte[] checkArrayLength(byte[] array, int targetIndex)
        {
            //System.out.println("Target index: " + targetIndex);
            //System.out.println("Array length: " + array.length);

            if (Math.abs(targetIndex) >= array.length)  // If the target index lays outside of the current indices for the array
            {
                return Arrays.copyOf(array, array.length * 2); // Double the length
            }

            return array;
        }

        /**
         * Method to search a byte array for a character
         * @param array Pass the array to be searched
         * @param targetChar Pass the target character
         * @return boolean Returns true if the array contains the target character
         */
        private boolean arrayContainsChar(byte[] array, char targetChar)
        {
            byte targetByte = (byte) targetChar;

            // Linear search for the byte version of the character
            for (byte b : array)
            {
                if (b == targetByte)
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * Method to manipulate a character so it's only one byte instead of two.
         * @param c Pass the character to be converted
         * @return byte Returns a single byte representing the character
         */
        private byte charToByte(char c) {
            return (byte) (c & 0x00FF);
        }
    }

    class Move
    {
        ArrayList<Play> plays;

        public Move()
        {
            plays = new ArrayList<>();
        }

        public Move(Move other)
        {
            this.plays = new ArrayList<>(other.plays);
        }

        public void addPlay(int x, int y, char c)
        {
            plays.add(new Play(x, y, c));
        }

        public void addPlay(Play p)
        {
            plays.add(p);
        }
    }

    class Play
    {
        int r, c;
        char letter;

        public Play(int r, int c, char letter) {
            this.r = r;
            this.c = c;
            this.letter = letter;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public char getLetter() {
            return letter;
        }

        public void setLetter(char letter) {
            this.letter = letter;
        }
    }


    static class UtilityMethods
    {
        /**
         * Method to reverse a String
         * @param input Pass the input string to be reversed
         * @return String Returns the input reversed.
         */
        public static String reverseString(String input)
        {
            return new StringBuilder(input).reverse().toString();
        }

        /**
         * Method to generate a set containing every character in the alphabet
         * @return Set Returns a set containing every character in the English alphabet
         */
        public static Set<Character> generateAlphabetSet()
        {
            HashSet<Character> alphabet = new HashSet<>();
            String englishAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            for(char c : englishAlphabet.toCharArray()) // Add all letters in the alphabet to the set
            {
                alphabet.add(c);
            }

            return alphabet;
        }

        public static boolean areEqual(Word w1, Word w2)
        {
            return (w1.getFirstColumn() == w2.getFirstColumn()) && (w1.getFirstRow() == w2.getFirstRow()) && (w1.isHorizontal() == w2.isHorizontal()) && (w1.getLetters().equals(w2.getLetters()));
        }

        public static ArrayList<Word> removeDuplicates(ArrayList<Word> list)
        {
            ArrayList<WordWithEquals> words = new ArrayList<>();
            ArrayList<WordWithEquals> uniques = new ArrayList<>();

            for(Word item : list)
            {
                words.add(new WordWithEquals(item));
            }

            for(WordWithEquals item : words)
            {
                if(!uniques.contains(item))
                {
                    uniques.add(item);
                }
            }

            list.clear();

            for(int i = 0; i < uniques.size(); i++)
            {
                list.add(uniques.get(i).w);
            }

            System.out.println(uniques.size() + " should be < " + words.size());

            System.out.println("-------- ARE UNIQUE AFTER REMOVING DUPLICATES: " + StaticValueGenerator.areUnique(list));

            return list;
        }
    }

    // Gaddag following java implementation class
    private class GADDAG
    {
        // Fields
        private GNode root;

        // Constructor
        public GADDAG()
        {
            root = null;

            // Find file and construct the gaddag from it
            File file = new File("src/csw.txt");
            try
            {
                root = buildGADDAG(new BufferedReader(new FileReader(file)));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            System.out.println("Nodes: " + gNodeCounter);
        }

        // Methods

        /**
         * Method to build the GADDAG using a BufferedReader.
         * @param dict Pass the BufferedReadeer (linked to a dictionary file)
         * @return GNode Returns the root of the GADDAG
         * @throws IOException Throws an IOException if there is an issue with the file input
         */
        public GNode buildGADDAG(BufferedReader dict) throws IOException {
            GNode initNode = new GNode();
            String line;
            int counter = 0;

            // For each word in the dictionary
            while((line = dict.readLine()) != null)
            {
                if(counter < 5)
                {
                    System.out.println("[" + line + "]");
                    System.out.println("Reverse 2: " + UtilityMethods.reverseString(line.substring(0, line.length() - 1)) + "@" + line.substring(line.length() - 1));
                }

                counter++;
                // Temp node for constructing branches
                GNode curNode = initNode;

                // Reverse the word and add it
                String reversedWord = UtilityMethods.reverseString(line);

                curNode = buildBranch(curNode, reversedWord);
                //System.out.println("Reversed word: " + reversedWord);

                // Reset curNode to the initial node
                curNode = initNode;

                // Handle the edge case of the almost reversed word
                String almostReversedWord = UtilityMethods.reverseString(line.substring(0, line.length() - 1)) + "#" + line.substring(line.length() - 1);
                //System.out.println("Almost reversed: " + almostReversedWord);
                curNode = buildBranch(initNode, almostReversedWord);

                // Build remaining cases
                for(int i = line.length() - 3; i >= 0; i--)
                {
                    // Update pointers
                    GNode tempNode = curNode;
                    curNode = initNode;

                    // Put all of the remaining characters in the word into the current node
                    for(int j = i; j >= 0; j--)
                    {
                        curNode = curNode.put(line.charAt(j));
                    }

                    // Add the delimiter and update curNode
                    curNode = curNode.put('#');

                    // Add the initial character to tempNode
                    curNode.put(line.charAt(i + 1), tempNode);

                    if(curNode.childPointer > 100)
                    {
                        System.out.println("WEIRD...");
                    }
                }
            }

            System.out.println("GADDAG BUILT, Num words added: " + counter);

            return initNode; // Return the newly created Gaddag
        }

        public GNode buildBranch(GNode node, String word)
        {
            branchCounter++;
            GNode curNode = node;

            //System.out.println("Word: " + word);
            //System.out.println("Word length: " + word.length());

            // For each character in the word except the last one
            for(int i = 0; i < word.length() - 1; i++)
            {
                // Put the word into the curNode and update curNode
                curNode = curNode.put(word.charAt(i));

                // If final letter, add to end set
                if(i == word.length() - 2)
                {
                    curNode.addNewEnd(word.charAt(word.length() - 1));
                }

                if(curNode.childPointer > 100 && DEBUG)
                {
                    System.out.println("CurNode Children: " + Arrays.toString(curNode.getChildrenArray()));
                    System.out.println("WEIRD...");
                }
            }

            return curNode;
        }

        // TODO
        public ArrayList<String> traverse(GNode root)
        {
            ArrayList<String> words = new ArrayList<>();

            // For each letter in the arcs of the root
            for(char c : root.getArcs())
            {
                // Get the child
                GNode child = root.get(c);

                // If child is not null
                if(child != null)
                {
                    // Add each word from traversing the child into the list
                    for(String s : traverse(child))
                    {
                        words.add("" + c + s);
                    }
                }
            }

            for(char end : root.getEndArray())
            {
                words.add("" + end);
            }

            return words;
        }

        /**
         * Method to get all words given a GADDAG, frame and board
         * @param root Pass the current GADDAG root
         * @param frame Pass the frame
         * @param board Pass the board
         * @return ArrayList Returns an ArrayList of valid word placements
         */
        public ArrayList<Word> getAllWords(GNode root, FrameObj frame, BoardObj board)
        {
            ArrayList<Word> words = new ArrayList<>();

            // For each row and column
            for(int r = 0; r < Board.BOARD_SIZE; r++)
            {
                for(int c = 0; c < Board.BOARD_SIZE; c++)
                {
                    // If the square is an anchor
                    if(board.getSquare(r, c).isAnchor())
                    {
                        //System.out.println("[" + r + ", " + c + "] is anchor!");
                        // Find words horizontally and vertically
                        findHorizontal(0, r, c, board, frame, root, true, new Move(), words);
                        findVertical(0, r, c, board, frame, root, true, new Move(), words);
                    }
                }
            }

            return words;
        }

        // Done
        public void findHorizontal(int pos, int anchor_x, int anchor_y, BoardObj board, FrameObj frame, GNode oldArc, boolean reverse, Move initialMove, ArrayList<Word> words)
        {
            int curX = anchor_x + pos;

            if(curX >= Board.BOARD_SIZE || curX < 0)
            {
                return;
            }

            if(board.squareIsOccupied(curX, anchor_y))
            {
                // If occupied, get the letter on the square
                char letter = board.getSquare(curX, anchor_y).getTile().getLetter();
                GNode newArc = oldArc.get(letter); // Create new node by traversing oldArc using letter
                Move newMove = new Move(initialMove);
                newMove.addPlay(curX, anchor_y, letter); // Add the letter to the move
                goOnHorizontal(pos, anchor_x, anchor_y, letter, board, frame, oldArc, newArc, reverse, newMove, words);
            }
            else if(!frame.isEmpty()) // If we have letters to play
            {
                // For each tile
                for(Tile tile : frame.getTiles())
                {
                    // If not a blank and a valid vertical placement for this square
                    if(!tile.isBlank() && board.getSquare(curX, anchor_y).isValidHoriz(tile.getLetter()))
                    {
                        FrameObj frameCopy = frame.deepCopy(); // Copy on write
                        frameCopy.removeTile(tile); // Remove the tile
                        GNode newArc = oldArc.get(tile.getLetter()); // Traverse to this letter's node
                        Move newMove = new Move(initialMove);
                        newMove.addPlay(curX, anchor_y, tile.getLetter()); // Add the move
                        goOnHorizontal(pos, anchor_x, anchor_y, tile.getLetter(), board, frameCopy, oldArc, newArc, reverse, newMove, words);
                    }
                }
            }
        }

        // Done
        public void goOnHorizontal(int pos, int anchor_x, int anchor_y, char letter, BoardObj board, FrameObj frame, GNode oldArc, GNode newArc, boolean reverse, Move move, ArrayList<Word> words)
        {
            // If a prefix
            if(pos <= 0)
            {
                // If valid move ending and there is not letter left of it on the board
                if(oldArc.isValidEnd(letter) && !board.getSquare(anchor_x + pos - 1, anchor_y).isOccupied() && !board.getSquare(anchor_x + 1, anchor_y).isOccupied())
                {
                    recordWord(words, move, false);
                }

                // Continue creating prefixes
                if(newArc != null)
                {
                    findHorizontal(pos-1, anchor_x, anchor_y, board, frame, newArc, reverse, move, words);

                    newArc = newArc.get('#');

                    // If prefixes can be produced
                    if(newArc != null && !board.getSquare(anchor_x + pos - 1, anchor_y).isOccupied())
                    {
                        findHorizontal(1, anchor_x, anchor_y, board, frame, newArc, false, move, words);
                    }

                }
            }
            else // Suffix as pos > 0
            {
                // If valid move ending and there is not letter right of it on the board
                if(oldArc.isValidEnd(letter) && !board.getSquare(anchor_x + pos + 1, anchor_y).isOccupied())
                {
                    recordWord(words, move, false);
                }

                // If suffixes can be produced
                if(newArc != null && !board.getSquare(anchor_x + pos + 1, anchor_y).isOccupied())
                {
                    oldArc = newArc;
                    findHorizontal(pos + 1, anchor_x, anchor_y, board, frame, oldArc, reverse, move, words);
                }
            }
        }

        public void findVertical(int pos, int anchor_x, int anchor_y, BoardObj board, FrameObj frame, GNode oldArc, boolean reverse, Move move, ArrayList<Word> words)
        {
            int curY = anchor_y + pos;

            if(curY >= Board.BOARD_SIZE || curY < 0)
            {
                return;
            }

            if(board.squareIsOccupied(anchor_x, curY))
            {
                // If occupied, get the letter on the square
                char letter = board.getSquare(anchor_x, curY).getTile().getLetter();
                GNode newArc = oldArc.get(letter); // Create new node by traversing oldArc using letter
                Move newMove = new Move(move);
                newMove.addPlay(anchor_x, curY, letter); // Add the letter to the move
                goOnVertical(pos, anchor_x, anchor_y, letter, board, frame, oldArc, newArc, reverse, newMove, words);
            }
            else if(!frame.isEmpty()) // If we have letters to play
            {
                // For each tile
                for(Tile tile : frame.getTiles())
                {
                    // If not a blank and a valid vertical placement for this square
                    if(!tile.isBlank() && board.getSquare(anchor_x, curY).isValidVert(tile.getLetter()))
                    {
                        FrameObj frameCopy = frame.deepCopy(); // Copy on write
                        frameCopy.removeTile(tile); // Remove the tile
                        GNode newArc = oldArc.get(tile.getLetter()); // Traverse to this letter's node
                        Move newMove = new Move(move);
                        newMove.addPlay(new Play(anchor_x, curY, tile.getLetter())); // Add the move
                        goOnVertical(pos, anchor_x, anchor_y, tile.getLetter(), board, frameCopy, oldArc, newArc, reverse, newMove, words);
                    }
                }
            }
        }

        public void goOnVertical(int pos, int anchor_x, int anchor_y, char letter, BoardObj board, FrameObj frame, GNode oldArc, GNode newArc, boolean reverse, Move move, ArrayList<Word> words)
        {
            // If a prefix
            if(pos <= 0)
            {
                // If valid move ending and there is not letter left of it on the board
                if(oldArc.isValidEnd(letter) && !board.getSquare(anchor_x + pos - 1, anchor_y).isOccupied() && !board.getSquare(anchor_x, anchor_y + 1).isOccupied())
                {
                    recordWord(words, move, true);
                }

                // Continue creating prefixes
                if(newArc != null)
                {
                    findVertical(pos-1, anchor_x, anchor_y, board, frame, newArc, false, move, words);

                    newArc = newArc.get('#');

                    // If prefixes can be produced
                    if(newArc != null && !board.getSquare(anchor_x, anchor_y + pos - 1).isOccupied())
                    {
                        findVertical(1, anchor_x, anchor_y, board, frame, newArc, false, move, words);
                    }

                }
            }
            else // Suffix as pos > 0
            {
                // If valid move ending and there is not letter right of it on the board
                if(oldArc.isValidEnd(letter) && !board.getSquare(anchor_x, anchor_y + pos + 1).isOccupied())
                {
                    recordWord(words, move, true);
                }

                // If suffixes can be produced
                if(newArc != null && !board.getSquare(anchor_x, anchor_y + pos + 1).isOccupied())
                {
                    oldArc = newArc;
                    findVertical(pos + 1, anchor_x, anchor_y, board, frame, newArc, reverse, move, words);
                }
            }
        }

        private void recordWord(ArrayList<Word> words, Move m, boolean isHorizontal)
        {
            /*
            if(m.plays.get(0).getC() != m.plays.get(1).getC())
            {
                System.out.println("HORIZONTAL == TRUE == " + isHorizontal);
            }
            else
            {
                System.out.println("HORIZONTAL == FALSE == " + isHorizontal);
            }

             */

            sortMove(m, isHorizontal);

            StringBuilder sb = new StringBuilder();

            for(Play p : m.plays)
            {
                sb.append(p.getLetter());
            }

            /*

                System.out.println("Word: " + sb.toString());

                for(int i = 0; i < m.plays.size(); i++)
                {
                    System.out.println("Tile: " + m.plays.get(i).letter + " | Row: " + m.plays.get(i).getR() + " | Col: " + m.plays.get(i).getC() );
                }
                System.out.println("Is horizontal: " + isHorizontal);
                System.out.println();

             */

            Word w = new Word(m.plays.get(0).getR(), m.plays.get(0).getC(), isHorizontal, sb.toString());

            /*
            System.out.println();
            for(int i = 0; i < w.getLetters().length(); i++)
            {
                if(w.isHorizontal())
                {
                    System.out.println("HORIZONTAL: Tile: " + w.getLetters().charAt(i) + " | Row: " + (w.getFirstRow()) + " | Col: " + (w.getFirstColumn() + i));
                }
                else
                {
                    System.out.println("VERTICAL: Tile: " + w.getLetters().charAt(i) + " | Row: " + (w.getFirstRow() + i) + " | Col: " + (w.getFirstColumn()));
                }
            }
            System.out.println();

            ArrayList<Word> testWords = new ArrayList<>();
            testWords.add(w);

             */

            /*
            if(dictionary.areWords(testWords))
            {
                words.add(w);
            }

             */

            words.add(w);
        }

        private void sortMove(Move m, boolean isHorizontal)
        {
            if(isHorizontal) // X changes, y does not
            {
                m.plays.sort(Comparator.comparingInt(Play::getC));
            }
            else
            {
                m.plays.sort(Comparator.comparingInt(Play::getR));
            }
        }

        // Getters & Setters
        public GNode getRoot()
        {
            return this.root;
        }

    }

    /**
     * Class for representing a node in a GADDAG.
     */
    class GNode
    {
        // Fields
        GNode[] children;
        byte[] arcs;
        byte[] end;
        short childPointer = 0;
        byte endPointer = 0;

        @Override
        public String toString() {
            return "Node: " + "\narcs: " + Arrays.toString(this.byteArrayToCharArray(arcs))
                    + "\nEnd: " + Arrays.toString(this.getEndArray()) + "\n";
        }

        // Constructors
        public GNode()
        {
            children = new GNode[1];
            arcs = new byte[1];
            end = new byte[1];
            gNodeCounter++;
        }

        // Methods
        public GNode put(char newArc, GNode node)
        {
            GNode child = this.get(newArc);

            putCounter++;

            if (child == null)  // If there is no child yet
            {
                // Check if there is space
                children = checkArrayLength(children, childPointer);
                arcs = checkArrayLength(arcs, childPointer);

                //System.out.println("Child pointer: " + childPointer);

                // Add a new arc
                arcs[childPointer] = charToByte(newArc);

                // Update children
                children[childPointer] = node;
                //System.out.println("CP incremented: " + childPointer);
                childPointer++;

                if(childPointer > maxPutCounter)
                {
                    maxPutCounter = childPointer;
                }


                if(childPointer > 100)
                {
                    if(DEBUG)
                    {
                        System.out.println("Child Length: " + children.length);
                        System.out.println("End length: " + end.length);
                        System.out.println("Arcs length: " + arcs.length);
                        System.out.println("Num Children: " + childPointer);
                    }

                    invalidPutCounter++;
                }


                // Return the new child
                return node;
            }
            else  // Otherwise, there is a child so return it.
            {
                return child;
            }
        }

        // Public wrapper for put
        // Done
        public GNode put(char transitionChar)
        {
            return this.put(transitionChar, new GNode());
        }

        // Done
        // Getter for a character
        public GNode get(char transitionChar)
        {
            // For each of the children, search for the transition char and return that node if it exists
            for (int i = 0; i < childPointer; i++)
            {
                if (arcs[i] == charToByte(transitionChar))
                {
                    return children[i];
                }
            }
            return null; // If it doesn't exist, return null
        }

        // Check if the node contains a string
        public boolean contains(String word)
        {
            // We use # as a delimiter as it not a reserved character for regex matching or other java methods
            if (!word.matches(".*#.*"))
            {
                return containsWord(word.charAt(0) + "#" + word.substring(1)); // Add delimiter
            }
            else
            {
                return containsWord(word); // Pass the word as it is formatted correctly
            }
        }

        private boolean containsWord(String word)
        {
            GNode current = this;
            int length = word.length();

            // Check if the word is empty
            if(length == 0)
            {
                return false;
            }

            // Iteratively parse through the word checking if each character is valid
            for(int i = 0; i < length; i++)
            {
                char c = word.charAt(i); // Get the current character

                // If this is the last character of the word and is a valid end
                if(i == length - 1 && current.isValidEnd(c))
                {
                    return true;
                }

                // Otherwise, parse as normal.

                current = this.get(c);

                if(current == null) // If this node does not contain the letter
                {
                    return false;
                }
            }

            return false;
        }

        public boolean isValidEnd(char endChar)
        {
            return arrayContainsChar(end, endChar);
        }

        public void addNewEnd(char endChar)
        {
            end = checkArrayLength(end, endPointer); // Check that sufficient space exists
            end[endPointer] = charToByte(endChar);
            endPointer++;
        }

        public char[] getEndArray() {
            return byteArrayToCharArray(end);
        }

        public Set<Character> getEndSet()
        {
            Set<Character> endSet = new HashSet<Character>();

            for (char c : byteArrayToCharArray(end)) {
                endSet.add(c);
                //System.out.println("Char: " + c);
            }

            return endSet;
        }

        public GNode[] getChildrenArray() {
            return Arrays.copyOf(children, childPointer);
        }

        public char[] getArcs() {
            return byteArrayToCharArray(arcs);
        }

        /**
         * Method to convert a byte array to a char array.
         * @param bytes Pass the byte array to be converted.
         * @return char[] Returns a char array.
         */
        private char[] byteArrayToCharArray(byte[] bytes)
        {
            char[] out = new char[bytes.length];

            for(int i = 0; i < bytes.length; i++)
            {
                out[i] = (char) bytes[i];
            }

            return out;
        }

        /**
         * Method to ensure that there is enough space in an array for a new insertion
         * @param array Pass the array to be checked
         * @param targetIndex Pass the index at which you want to insert an item
         * @return
         */
        private GNode[] checkArrayLength(GNode[] array, int targetIndex)
        {

            if (targetIndex >= array.length)
            {
                return Arrays.copyOf(array, array.length * 2);
            }
            return array;
        }

        /**
         * Method to ensure that there is enough space in an array for a new insertion
         * @param array Pass the array to be checked
         * @param targetIndex Pass the index at which you want to insert an item
         * @return
         */
        private byte[] checkArrayLength(byte[] array, int targetIndex)
        {
            //System.out.println("Target index: " + targetIndex);
            //System.out.println("Array length: " + array.length);

            if (Math.abs(targetIndex) >= array.length)  // If the target index lays outside of the current indices for the array
            {
                return Arrays.copyOf(array, array.length * 2); // Double the length
            }

            return array;
        }

        /**
         * Method to search a byte array for a character
         * @param array Pass the array to be searched
         * @param targetChar Pass the target character
         * @return boolean Returns true if the array contains the target character
         */
        private boolean arrayContainsChar(byte[] array, char targetChar)
        {
            byte targetByte = (byte) targetChar;

            // Linear search for the byte version of the character
            for (byte b : array)
            {
                if (b == targetByte)
                {
                    return true;
                }
            }

            return false;
        }

        /**
         * Method to manipulate a character so it's only one byte instead of two.
         * @param c Pass the character to be converted
         * @return byte Returns a single byte representing the character
         */
        private byte charToByte(char c) {
            return (byte) (c & 0x00FF);
        }
    }

    class Move
    {
        ArrayList<Play> plays;

        public Move()
        {
            plays = new ArrayList<>();
        }

        public Move(Move other)
        {
            this.plays = new ArrayList<>(other.plays);
        }

        public void addPlay(int x, int y, char c)
        {
            plays.add(new Play(x, y, c));
        }

        public void addPlay(Play p)
        {
            plays.add(p);
        }
    }

    class Play
    {
        int r, c;
        char letter;

        public Play(int r, int c, char letter) {
            this.r = r;
            this.c = c;
            this.letter = letter;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public char getLetter() {
            return letter;
        }

        public void setLetter(char letter) {
            this.letter = letter;
        }
    }


    static class UtilityMethods
    {
        /**
         * Method to reverse a String
         * @param input Pass the input string to be reversed
         * @return String Returns the input reversed.
         */
        public static String reverseString(String input)
        {
            return new StringBuilder(input).reverse().toString();
        }

        /**
         * Method to generate a set containing every character in the alphabet
         * @return Set Returns a set containing every character in the English alphabet
         */
        public static Set<Character> generateAlphabetSet()
        {
            HashSet<Character> alphabet = new HashSet<>();
            String englishAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            for(char c : englishAlphabet.toCharArray()) // Add all letters in the alphabet to the set
            {
                alphabet.add(c);
            }

            return alphabet;
        }

        public static boolean areEqual(Word w1, Word w2)
        {
            return (w1.getFirstColumn() == w2.getFirstColumn()) && (w1.getFirstRow() == w2.getFirstRow()) && (w1.isHorizontal() == w2.isHorizontal()) && (w1.getLetters().equals(w2.getLetters()));
        }

        public static ArrayList<Word> removeDuplicates(ArrayList<Word> list)
        {
            ArrayList<WordWithEquals> words = new ArrayList<>();
            ArrayList<WordWithEquals> uniques = new ArrayList<>();

            for(Word item : list)
            {
                words.add(new WordWithEquals(item));
            }

            for(WordWithEquals item : words)
            {
                if(!uniques.contains(item))
                {
                    uniques.add(item);
                }
            }

            list.clear();

            for(int i = 0; i < uniques.size(); i++)
            {
                list.add(uniques.get(i).w);
            }

            //System.out.println(uniques.size() + " should be < " + words.size());

            //System.out.println("-------- ARE UNIQUE AFTER REMOVING DUPLICATES: " + StaticValueGenerator.areUnique(list));

            return list;
        }
    }
    

}
