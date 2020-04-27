
import javafx.util.Pair;
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

public class JunkBot3 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    // Constants
    public static final int NUM_BEST_WORDS = 21;

    private PlayerAPI me;
    private OpponentAPI opponent;
    private BoardAPI board;
    private UserInterfaceAPI info;
    private DictionaryAPI dictionary;
    private int turnCount;

    private boolean shouldChallenge = false;
    private boolean otherBotChallenges = false;
    private boolean shouldUpdateBoard = false;

    private PlayerObj meObj;
    private BoardObj boardObj;
    private FrameObj frameObj;
    private GADDAG gaddag;
    private MCTS mcts;

    int gNodeCounter = 0;
    int putCounter = 0;
    int maxPutCounter = -1;
    int branchCounter = 0;
    int invalidPutCounter = 0;
    boolean DEBUG = true;

    String currentInfo;
    String allInfo;

    JunkBot3(PlayerAPI me, OpponentAPI opponent, BoardAPI board, UserInterfaceAPI ui, DictionaryAPI dictionary) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.info = ui;
        this.dictionary = dictionary;
        turnCount = 0;
    }

    public String getCommand()
    {
        System.out.println("NEW TURN");
        // Add your code here to input your commands
        // Your code must give the command NAME <botname> at the start of the game

        // PRE-COMPUTATION


        LeaveValues.initialise();

        boardObj = parseBoardFromAPI();
        frameObj = parseFrameFromString(me.getFrameAsString());

        System.out.println("CURRENT FRAME\n" + frameObj.toString());
        System.out.println("CURRENT BOARD\n" + boardObj.toString());
        System.out.flush();

        /*
        for(int i = 0; i < LeaveValues.leaveMaps.size(); i++)
        {
            if(LeaveValues.leaveMaps.get(i).size() > 0)
            {
                System.out.println("MAP " + i + " CORRECTLY READ IN, SIZE: " + LeaveValues.leaveMaps.get(i).size());
            }
        }

         */

        if(turnCount == 0)
        {
            this.meObj = new PlayerObj(me);
            this.boardObj = parseBoardFromAPI();
            this.frameObj = parseFrameFromString(me.getFrameAsString());
            this.gaddag = new GADDAG();
            this.mcts = new MCTS(boardObj, new PlayerObj(me), new PlayerObj(opponent));

            turnCount++;
            shouldUpdateBoard = false;
            return "NAME JUNKBOT \n";
        }

        if(shouldUpdateBoard)
        {
            updateLastTurn();
        }

        if(shouldChallenge)
        {
            shouldChallenge = false;
            shouldUpdateBoard = false;
            turnCount++;
            return "CHALLENGE";
        }

        //turnCount++;


        // Static testing
        /*
        this.frameObj.getTiles().clear();

        ArrayList<Tile> tiles = new ArrayList<>();

        tiles.add(new Tile('H'));
        tiles.add(new Tile('T'));
        tiles.add(new Tile('T'));
        tiles.add(new Tile('Y'));
        tiles.add(new Tile('O'));
        tiles.add(new Tile('U'));
        tiles.add(new Tile('I'));

        this.frameObj.addTiles(tiles);

         */

        System.out.println("Current frame: " + frameObj.toString());

        ArrayList<Word> words = getAllPossibleMoves(this.boardObj, this.frameObj);
        System.out.println("Words length: " + words.size());
        System.out.println("Turn count: " + turnCount);

        /*
        System.out.println("PUT COUNTER: " + putCounter);
        System.out.println("MAX Put: " + maxPutCounter);
        System.out.println("BRANCH COUNTER: " + branchCounter);
        System.out.println("INVALID PUT COUNTER " + invalidPutCounter);

         */

        String testWord = "";

        for(int i = 0; i < 3; i++)
        {
            testWord += frameObj.getTiles().get(i);
        }

        System.out.println("Legal? " + boardObj.isLegalPlay(frameObj, new Word(7, 7, true, testWord)));

        if(boardObj.isLegalPlay(frameObj, new Word(7, 7, true, testWord)))
        {
            boardObj.deepCopy().place(frameObj.deepCopy(), new Word(7, 7, true, testWord));
        }

        FrameObj testBlanks = new FrameObj();
        ArrayList<Tile> blankTestTiles = new ArrayList<>();

        for(int i = 0; i < 6; i++)
        {
            blankTestTiles.add(new Tile('E'));
        }

        blankTestTiles.add(new Tile('_'));

        testBlanks.addTiles(blankTestTiles);

        // To convert, find the missing letters, and replace them in the string with '_' while keeping them in a dif string to pass
        Word testBlankWord = new Word(7, 7, true, "_EE", "S");

        System.out.println("Legal? " + boardObj.isLegalPlay(testBlanks, testBlankWord));

        /*
        for(int i = 0; i < words.size(); i++)
        {
            System.out.println(words.get(i));
            ArrayList<Word> w = new ArrayList<>();
            w.add(words.get(i));
            System.out.println(dictionary.areWords(w));
        }

         */

        if(!dictionary.areWords(words))
        {
            System.out.println("PROBLEM!");
        }
        else
        {
            System.out.println("ALL VALID WORDS");
        }

        // ------------- MCTS TESTING BEGINS

        BoardObj testBoard = boardObj.deepCopy();
        FrameObj testFrame = frameObj.deepCopy();

        ArrayList<Word> wordsToTest = getAllPossibleMoves(testBoard, testFrame);

        System.out.println("WTT Length: " + wordsToTest.size());
        System.out.println("Are unique: " + StaticValueGenerator.areUnique(wordsToTest));

        for(int i = 0; i < 5 && wordsToTest.size() > 5; i++)
        {
            System.out.println("Word (" + i + ") : " + wordsToTest.get(i) + " || " + StaticValueGenerator.generateStaticValuation(testBoard, wordsToTest.get(i), testFrame));
        }

        for(int i = 0; i < 5 && wordsToTest.size() > 5; i++)
        {
            System.out.println("Word (" + i + ") : " + wordsToTest.get(i) + " || " + StaticValueGenerator.generateStaticValuation(testBoard, wordsToTest.get(i), testFrame));
        }

        wordsToTest = StaticValueGenerator.sortWordsByValue(testBoard, testFrame, wordsToTest);

        System.out.println("POST SORTED::\n");

        for(int i = 0; i < 5 && wordsToTest.size() > 5; i++)
        {
            System.out.println("Word (" + i + ") : " + wordsToTest.get(i) + " || " + StaticValueGenerator.generateStaticValuation(testBoard, wordsToTest.get(i), testFrame));
        }

        double value = StaticValueGenerator.generateStaticValuation(testBoard, wordsToTest.get(0), testFrame);

        System.out.println("VALUE OF WORD 1: " +value);

        System.out.println("---- WORD 1 GENERATED: " + wordsToTest.get(0));

        System.out.println("IS LEGAL: "+ testBoard.isLegalPlay(testFrame, wordsToTest.get(0)));

        if(!testBoard.isLegalPlay(testFrame, wordsToTest.get(0)))
        {
            return "PASS";
        }

        testBoard.place(testFrame, wordsToTest.get(0));

        System.out.println("WORD 1 PLACED\n" + testBoard.toString());

        int row = wordsToTest.get(0).getFirstRow();
        int col = wordsToTest.get(0).getFirstColumn();
        char direction = wordsToTest.get(0).isHorizontal() ? 'A' : 'D';

        row++;

        char colChar = (char) ('A' + col);

        String command1 = "" + colChar + row + " " + direction + " " + wordsToTest.get(0).getLetters();

        for(int i = 0; i < wordsToTest.get(0).length(); i++)
        {
            int col2 = wordsToTest.get(0).getFirstColumn();
            if(wordsToTest.get(0).isHorizontal()) // Column changes
            {
                col2 += i;
                char colString = (char) ('A' + col2);
                System.out.println("Tile: " + wordsToTest.get(0).getLetter(i) + " | C: " + colString + " | R: " + (wordsToTest.get(0).getFirstRow()));
            }
            else
            {
                char colString = (char) ('A' + col2);
                System.out.println("Tile: " + wordsToTest.get(0).getLetter(i) + " | C: " + colString + " | R: " + (wordsToTest.get(0).getFirstRow() + i));
            }
        }

        System.out.println("Command: " + command1);

        turnCount++;
        shouldUpdateBoard = true;

        return command1;

        /*

        System.out.println("\n\n WORD 2 GENERATION BEGINS\n");

        testFrame.refill(new Pool());

        wordsToTest = getAllPossibleMoves(testBoard, testFrame);

        wordsToTest = StaticValueGenerator.sortWordsByValue(testBoard, testFrame, wordsToTest);

        for(int i = 0; i < 5 && wordsToTest.size() > 5; i++)
        {
            System.out.println("Word (" + i + ") : " + wordsToTest.get(i) + " || " + StaticValueGenerator.generateStaticValuation(testBoard, wordsToTest.get(i), testFrame));
        }

        System.out.println("---- WORD 2 GENERATED: " + wordsToTest.get(0));

        testBoard.place(testFrame, wordsToTest.get(0));

        System.out.println("WORD 2 PLACED\n" + testBoard.toString());

        System.out.println("--- MOVE GENERATION FINISHED --- ");

        //System.out.println("Best word: " + mcts.generateOptimalMove(boardObj, meObj, new PlayerObj(opponent)));

        System.out.flush();


        String command = "";
        switch (turnCount) {
            case 1:
                command = "PASS";
                shouldUpdateBoard = true;
                break;
            case 2:
                command = "HELP";
                shouldUpdateBoard = false;
                break;
            case 3:
                command = "SCORE";
                shouldUpdateBoard = false;
                break;
            case 4:
                command = "POOL";
                shouldUpdateBoard = false;
                break;
            default:
                command = "H8 A AN";
                shouldUpdateBoard = true;
                break;
        }
        turnCount++;
        return command;

         */
    }

    // Additional methods for interfacing with the APIs

    protected ArrayList<Word> getAllPossibleMoves(BoardObj b, FrameObj f)
    {
        b.updateAnchors();
        b.computeCrossSets(b, gaddag.getRoot());
        return UtilityMethods.removeDuplicates(gaddag.getAllWords(gaddag.getRoot(), f, b));
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
        SquareObj[][] squares = new SquareObj[Board.BOARD_SIZE][Board.BOARD_SIZE];

        System.out.println("--- READING FROM API ---\n");
        for(int i = 0; i < Board.BOARD_SIZE; i++)
        {
            for(int j = 0; j < Board.BOARD_SIZE; j++)
            {
                squares[i][j] = new SquareObj(board.getSquareCopy(i,j));
                System.out.print(board.getSquareCopy(i, j).toString() + " ");
            }
            System.out.println();
        }

        if(this.boardObj == null)
        {
            this.boardObj = new BoardObj();
        }

        this.boardObj.setSquares(squares);
        return this.boardObj;
    }

    private void updateLastTurn()
    {
        parseCurrentInfo();

        String oppMove = parseMoveFromNewInfo(currentInfo);

        System.out.println("Opp move: " + oppMove);

        int moveCode = processPlay(oppMove);

        if(moveCode == 2)
        {
            // Get the word played
            Word w = parsePlay(oppMove);
            System.out.println("Word played: " + w);

            ArrayList<Word> words = new ArrayList<>();
            words.add(w);

            // Check if we should challenge the word
            if(!dictionary.areWords(words))
            {
                shouldChallenge = true;
            }
            else
            {
                boardObj.placeCheat(w); // Update board with new move.
            }
        }

        if(moveCode == 3)
        {
            otherBotChallenges = true;
        }
    }

    protected void parseCurrentInfo()
    {
        String newInfo = info.getAllInfo();

        newInfo.replaceAll(newInfo, "");

        allInfo = info.getAllInfo();

        currentInfo = newInfo;
    }

    protected String parseMoveFromNewInfo(String newInfo)
    {
        int oppMoveIndex = newInfo.lastIndexOf('>');

        StringBuilder word = new StringBuilder();
        boolean wordComplete = false;

        for(int i = oppMoveIndex; i < newInfo.length() && !wordComplete; i++)
        {
            char c = newInfo.charAt(i);

            if(c == '\n')
            {
                wordComplete = true;
            }
            else
            {
                word.append(c);
            }
        }
        return word.toString().substring(2);
    }

    private int processPlay(String command)
    {
        /*
            Codes:
            1 = not important { Pass, Help, Pool, Score}
         */

        if ((command.equals("PASS") || command.equals("P"))) {
            return 1;
        }
        else if ((command.equals("HELP") || command.equals("H"))) {
            return 1;
        }
        else if ((command.equals("SCORE") || command.equals("S"))) {
            return 1;
        }
        else if ((command.equals("POOL") || command.equals("O"))) {
            return 1;
        }
        else if ((command.matches("[A-O](\\d){1,2}( )+[A,D]( )+([A-Z]){1,15}") ||                // no blanks
                (command.matches("[A-O](\\d){1,2}( )+[A,D]( )+([A-Z_]){1,17}( )+([A-Z]){1,2}"))))
        {
            // no blanks
            return 2;
        }
        else if ((command.matches("EXCHANGE( )+([A-Z_]){1,7}") || command.matches("X( )+([A-Z_]){1,7}"))) {
            return 1;
        }
        else if ((command.matches("NAME( )+[A-Z][A-Z0-9]*") || command.matches("N( )+[A-Z][A-Z0-9]*"))) {
            return 1;
        }
        else if ((command.equals("CHALLENGE") || command.equals("C"))) {
            return 3; // Challenge made.
        }
        else {
            return -1;
        }
    }

    private Word parsePlay(String command) {
        // this converts the play command into a Word
        String[] parts = command.split("( )+");
        String gridText = parts[0];
        int column = ((int) gridText.charAt(0)) - ((int) 'A');
        String rowText = parts[0].substring(1);
        int row = Integer.parseInt(rowText)-1;
        String directionText = parts[1];
        boolean isHorizontal = directionText.equals("A");
        String letters = parts[2];
        Word word;
        if (parts.length == 3) {
            word = new Word(row, column, isHorizontal, letters);
        } else {
            String designatedBlanks = parts[3];
            word = new Word(row, column, isHorizontal, letters, designatedBlanks);
        }
        return word;
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
            for(boolean b : verticalCrossSet)
            {
                b = false;
            }
        }

        public void clearHorizontalCrossSet()
        {
            for(boolean b : horizontalCrossSet)
            {
                b = false;
            }
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
        public void place(FrameObj frame, Word word) {
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

        public SquareObj getSquare(int row, int col) {
            if(row >= BOARD_SIZE || col >= BOARD_SIZE || row < 0 || col < 0)
                return new SquareObj(1, 1);
            return squares[row][col];
        }

        public boolean isFirstPlay() {
            return numPlays == 0;
        }

        // Additional methods added

        public BoardObj deepCopy()
        {
            BoardObj b = new BoardObj();

            for(int i = 0; i < BOARD_SIZE; i++)
            {
                for(int j = 0; j < BOARD_SIZE; j++)
                {
                    b.squares[i][j] = new SquareObj(this.squares[i][j]);
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

        public boolean isValidAnchor(int r, int c)
        {
            // First, check if occupied
            if(!squares[r][c].isOccupied())
            {
                if(r == BOARD_CENTRE && c == BOARD_CENTRE) // If the centre square and not occupied, always valid.
                {
                    return true;
                }

                // Otherwise, check for another square around this (above, below, left, right)
                return squareIsOccupied(r-1, c) || squareIsOccupied(r+1,c) || squareIsOccupied(r, c-1) || squareIsOccupied(r, c+1);
            }

            return false;
        }

        private boolean squareIsOccupied(int i, int j)
        {
            try
            {
                return squares[i][j].isOccupied();
            }
            catch(Exception ex)
            {
                return false;
            }
        }

        public void updateAnchors()
        {
            // For each square, recalculate isValidAnchor
            for(int r = 0; r < BOARD_SIZE; r++)
            {
                for(int c = 0; c < BOARD_SIZE; c++)
                {
                    squares[r][c].setAnchor(isValidAnchor(r, c));
                }
            }
        }

        public void placeCheat(Word w)
        {
            FrameObj f = new FrameObj();
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

        // TODO: Update cross sets
        public void computeCrossSets(BoardObj board, GNode gaddag)
        {
            for(int i = 0; i < Board.BOARD_SIZE; i++)
            {
                for(int j = 0; j < Board.BOARD_SIZE; j++)
                {
                    if(board.getSquare(i, j).isAnchor())
                    {
                        // Check horizontal
                        if(board.getSquare(i+1, j).isOccupied() || board.getSquare(i-1, j).isOccupied())
                        {
                            board.getSquare(i, j).clearHorizontalCrossSet();
                            computeHorizontalCrossSet(i, j, gaddag);
                        }

                        // Check vertical
                        if(board.getSquare(i, j+1).isOccupied() || board.getSquare(i, j-1).isOccupied())
                        {
                            board.getSquare(i, j).clearVerticalCrossSet();
                            computeVerticalCrossSet(i, j, gaddag);
                        }
                    }
                }
            }
        }

        private void computeHorizontalCrossSet(int i, int j, GNode root)
        {
            GNode curNode = root;

            // If it has a tile to the left and right
            if(getSquare(i-1, j).isOccupied() && getSquare(i + 1, j).isOccupied())
            {
                int preX = i - 1; // i Coordinate of prefix

                // Traverse to the beginning of the prefix
                while(getSquare(preX, j).isOccupied())
                {
                    curNode = curNode.get(getSquare(preX, j).getTile().getLetter());
                    if(curNode == null) // If it does not exist, return
                    {
                        return;
                    }
                    preX--; // Decrement i coordinate
                }


                // Start making the prefix
                curNode = curNode.get('#');

                if(curNode != null)
                {
                    GNode base = curNode;

                    for(char c : UtilityMethods.generateAlphabetSet())
                    {
                        curNode = base;
                        curNode = curNode.get(c);
                        preX = i + 1;

                        // While there is a letter to the right and haven't reached a null node
                        while(curNode != null && getSquare(preX + 1, j).isOccupied())
                        {
                            curNode = curNode.get(getSquare(preX, j).getTile().getLetter());
                            preX++;
                        }

                        if(curNode != null)
                        {
                            if(curNode.isValidEnd(getSquare(preX, j).getTile().getLetter()))
                            {
                                squares[i][j].addValidHorizontal(c);
                            }
                        }
                    }
                }
            }
            // Otherwise, if there is a tile before it
            else if(getSquare(i - 1, j).isOccupied())
            {
                int x = i - 1;

                while(getSquare(x, j).isOccupied())
                {
                    curNode = curNode.get(getSquare(x, j).getTile().getLetter());

                    if(curNode == null) {
                        return;
                    }

                    x--;
                }

                curNode = curNode.get('#');

                if(curNode != null)
                {
                    squares[i][j].addAllValidHoriz(curNode.getEndSet());
                }
            }
            // Else if there is a tile after it
            else if(getSquare(i+1, j).isOccupied())
            {
                int x = i + 1;

                while(getSquare(x + 1, j).isOccupied())
                {
                    x++;
                }

                while(x > i)
                {
                    curNode = curNode.get(getSquare(x, j).getTile().getLetter());

                    if(curNode == null)
                    {
                        return;
                    }

                    x--;
                }
                //System.out.println("End set size: " + curNode.getEndArray().length);
                //System.out.println("Last char: " + (byte) curNode.getEndArray()[curNode.getEndArray().length - 1]);
                //System.out.println("End set: " + Arrays.toString(curNode.getEndArray()));
                squares[i][j].addAllValidHoriz(curNode.getEndSet());
            }
        }

        private void computeVerticalCrossSet(int i, int j, GNode root)
        {
            GNode curNode = root;

            // If it has a tile to the left and right
            if(getSquare(i, j - 1).isOccupied() && getSquare(i, j + 1).isOccupied())
            {
                int preY = j - 1; // i Coordinate of prefix

                // Traverse to the beginning of the prefix
                while(getSquare(i, preY).isOccupied())
                {
                    curNode = curNode.get(getSquare(i, preY).getTile().getLetter());
                    if(curNode == null) // If it does not exist, return
                    {
                        return;
                    }
                    preY--; // Decrement i coordinate
                }


                // Start making the prefix
                curNode = curNode.get('#');

                if(curNode != null)
                {
                    GNode base = curNode;

                    for(char c : UtilityMethods.generateAlphabetSet())
                    {
                        curNode = base;
                        curNode = curNode.get(c);
                        preY = j + 1;

                        // While there is a letter to the right and haven't reached a null node
                        while(curNode != null && getSquare(i, preY + 1).isOccupied())
                        {
                            curNode = curNode.get(getSquare(i, preY).getTile().getLetter());
                            preY++;
                        }

                        if(curNode != null)
                        {
                            if(curNode.isValidEnd(getSquare(i, preY).getTile().getLetter()))
                            {
                                squares[i][j].addValidVertical(c);
                            }
                        }
                    }
                }
            }
            // Otherwise, if there is a tile before it
            else if(getSquare(i, j - 1).isOccupied())
            {
                int preY = j - 1;

                while(getSquare(i, preY).isOccupied())
                {
                    curNode = curNode.get(getSquare(i, preY).getTile().getLetter());

                    if(curNode == null) {
                        return;
                    }

                    preY--;
                }

                curNode = curNode.get('#');

                if(curNode != null)
                {
                    squares[i][j].addAllValidVertical(curNode.getEndSet());
                }
            }
            // Else if there is a tile after it
            else if(getSquare(i, j + 1).isOccupied())
            {
                int preY = j + 1;

                while(getSquare(i, preY + 1).isOccupied())
                {
                    preY++;
                }

                while(preY > j)
                {
                    curNode = curNode.get(getSquare(i, preY).getTile().getLetter());

                    if(curNode == null)
                    {
                        return;
                    }

                    preY--;
                }
                squares[i][j].addAllValidVertical(curNode.getEndSet());
            }
        }

        // TODO: Remove
        public void printAnchors(){
            System.out.print("   ");
            for(int x=0; x<15; x++)
                System.out.print(" " + x);
            System.out.println();

            for(int j=0; j<15; j++){
                String temp = Integer.toString(j);
                if(temp.length() < 2)
                    temp = " " + temp;
                System.out.print(temp + "|");

                for(int i=0; i<15; i++){
                    if(squares[i][j].isAnchor())
                        System.out.print(" 1");
                    else
                        System.out.print(" 0");
                }
                System.out.println();
            }
            System.out.println();
        }

        // TODO: Remove
        public void printNumCrossSets(){
            System.out.print("Horizontal\n   ");
            for(int x=0; x<15; x++)
                System.out.print(" " + x);
            System.out.println();

            for(int j=0; j<15; j++){
                String temp = Integer.toString(j);
                if(temp.length() < 2)
                    temp = " " + temp;
                System.out.print(temp + "|");

                for(int i=0; i<15; i++){
                    System.out.print(" " + getSquare(i, j).getHorizontalCrossSet().length);
                }
                System.out.println();
            }
            System.out.println();

            System.out.print("Vertical\n   ");
            for(int x=0; x<15; x++)
                System.out.print(" " + x);
            System.out.println();

            for(int j=0; j<15; j++){
                String temp = Integer.toString(j);
                if(temp.length() < 2)
                    temp = " " + temp;
                System.out.print(temp + "|");

                for(int i=0; i<15; i++){
                    System.out.print(" " + getSquare(i, j).getVerticalCrossSet().length);
                }
                System.out.println();
            }
            System.out.println();
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

        public PlayerObj(OpponentAPI opp)
        {
            this.id = opp.getPrintableId();
            this.name = opp.getName();
            this.score = opp.getScore();
            this.frame = new FrameObj();
        }

        public PlayerObj(PlayerAPI me)
        {
            this.id = me.getPrintableId();
            this.name = me.getName();
            this.score = me.getScore();
            this.frame = parseFrameFromString(me.getFrameAsString());
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

    protected class MCTS
    {
        public static final int MAX_PLAYOUT_ITERATIONS = 1;
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
            this.MCT.updateCurrent(this.MCT.current.addChild(new Node(new State(b.deepCopy(), main.deepCopy(), opp.deepCopy()), this.MCT.current)));

            ArrayList<Word> possibleWords = StaticValueGenerator.selectBestNWords(b, main.getFrame(), getAllPossibleMoves(b, main.getFrame()));

            // System.out.println("1333: Possible words: " + possibleWords.toString());

            Node optimalNode = expandNode(possibleWords);

            MCT.current.freeUnusedChildren(optimalNode);

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

            System.out.println("NUMBER OF CHILDREN: " + words.size() + " == " + MCT.current.getChildren().size());

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

                // Play Move 0
                // Me: Play move 0

                currentWord.getState().getBoard().place(currentWord.getState().getMainPlayer().getFrame(), initial_ply0);
                int latestPoints = currentWord.getState().getBoard().getAllPoints(currentWord.getState().getBoard().getAllWords(initial_ply0));

                // Update score
                currentWord.getState().getMainPlayer().addPoints(latestPoints);
                /*

                System.out.println("INITIAL PLAY: " + initial_ply0);

                for(int m = 0; m < initial_ply0.getLetters().length(); m++)
                {
                    if(initial_ply0.isHorizontal())
                    {
                        System.out.println("Tile: " + initial_ply0.getLetters().charAt(m) + " | Row: " + (initial_ply0.getFirstRow()) + " | Col: " + (initial_ply0.getFirstColumn() + m));
                    }
                    else
                    {
                        System.out.println("Tile: " + initial_ply0.getLetters().charAt(m) + " | Row: " + (initial_ply0.getFirstRow() + m) + " | Col: " + (initial_ply0.getFirstColumn()));
                    }

                }
                System.out.println();

                 */

                // Playout node
                singleStaticPlayout(currentPlayout, currentWord.getState().getBoard());
            }

            // Find best node from nodes appended
            currentWord.updateAverageScore();
            currentWord.updateScoreDifferential();
        }

        private void singleStaticPlayout(Node currentWord, BoardObj board)
        {
            // Store final score and score differential (me - opponent) in nodes.


            // ------------------ MOVE 1 ------------------
            // Opponent: Generate random frame
            currentWord.getState().getOpponent().setFrame(generateRandomFrame());
            // Generate all moves
            ArrayList<Word> oppWords = getAllPossibleMoves(board, currentWord.getState().getOpponent().getFrame());
            // Statically evaluate best

            if(currentWord.getState().getOpponent().getFrame().size() < 7)
            {
                System.out.println("ERROR OCCURRED.");
            }

            //System.out.println("Frame: " + currentWord.getState().getOpponent().getFrame().toString());

            Word oppBestWord = StaticValueGenerator.findBestWord(board, currentWord.getState().getOpponent().getFrame().deepCopy(), oppWords);
            // Play move 1
            board.place(currentWord.getState().getOpponent().getFrame(), oppBestWord);
            // Update score
            currentWord.getState().getOpponent().addPoints(board.getAllPoints(board.getAllWords(oppBestWord)));

            // ------------------ MOVE 2 ------------------
            // Me: Refill frame with random tiles
            currentWord.getState().getMainPlayer().getFrame().refill(new Pool());
            // Generate all moves
            ArrayList<Word> mainWords = getAllPossibleMoves(board, currentWord.getState().getMainPlayer().getFrame().deepCopy());
            // Statically evaluate best
            Word mainBestWord = StaticValueGenerator.findBestWord(board, currentWord.getState().getMainPlayer().getFrame(), mainWords);
            double scoreWithLeave = StaticValueGenerator.generateStaticValuation(board, mainBestWord, currentWord.getState().getMainPlayer().getFrame());
            // Play move 2
            board.place(currentWord.getState().getMainPlayer().getFrame(), mainBestWord);
            // Update score with leave
            currentWord.getState().getMainPlayer().addPoints(scoreWithLeave);

            // Update average score of node here ?
        }

        public FrameObj generateRandomFrame()
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
            Node best = root.getHighestAvgScoreChild();
            root.freeUnusedChildren(best);
            return best;
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
                BoardObj board = b.deepCopy();
                FrameObj frame = f.deepCopy();

                try
                {
                    //System.out.println("TRY BLOCK\n" + board.toString());
                    board.place(frame, w);
                }
                catch(Exception ex)
                {

                    System.out.println("First Play: " + b.isFirstPlay());
                    System.out.println("Frame Copy: " + frame.toString());
                    System.out.println("Error code: " + b.getErrorCode());
                    System.out.println("WORD: " + w);
                    System.out.println("CATCH BLOCK\n" + b.toString());


                    ex.printStackTrace();
                    throw new IllegalStateException("EXCEPTION THROWN");
                }

                score += board.getAllPoints(board.getAllWords(w));

                // Get the leave score

                score += LeaveValues.getLeaveFromFrame(frame);
            }
            else
            {
                return Double.MIN_VALUE;
                /*
                System.out.println("Frame: " + f.getTiles().toString());
                System.out.println("Word: " + w);
                System.out.println("Error code: " + b.getErrorCode());

                for(int i = 0; i < w.getLetters().length(); i++)
                {
                    if(w.isHorizontal())
                    {
                        System.out.println("Tile: " + w.getLetters().charAt(i) + " | Row: " + (w.getFirstRow()) + " | Col: " + (w.getFirstColumn() + i));
                    }
                    else
                    {
                        System.out.println("Tile: " + w.getLetters().charAt(i) + " | Row: " + (w.getFirstRow() + i) + " | Col: " + (w.getFirstColumn()));
                    }
                }
                System.out.println();

                System.out.println("WORD IS HORIZONTAL: " + w.isHorizontal());
                System.out.println("Row: " + w.getFirstRow() + ", Col: " + w.getFirstColumn());
                System.out.println("Last row: " + w.getLastRow() + ", Last Col: " + w.getLastColumn());
                System.out.println();

                throw new IllegalArgumentException("Invalid play, cannot score this play.");

                 */


            }

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
                return null;
                //throw new IllegalStateException("No word scored better than MIN_VALUE.");
            }

            return words.get(max_index);
        }

        public static ArrayList<Word> sortWordsByValue(BoardObj b, FrameObj f, ArrayList<Word> words)
        {
            // System.out.println("Words: " + words.toString());

            List<Pair<Word, Double>> listToSort = new ArrayList<>();

            // Add all to the list
            for(int i = 0; i < words.size(); i++)
            {
                Pair<Word, Double> p = new Pair<Word, Double>(words.get(i), generateStaticValuation(b, words.get(i), f));
                listToSort.add(p);

                //System.out.println(p.toString());
            }

            //words.sort(Comparator.comparingDouble((Word w) -> generateStaticValuation(b, w, f)));

            listToSort = mergeSortEnhanced(listToSort);

            //System.out.println("--- SORTED --- \n\n");

            /*
            for(int i = 0; i < listToSort.size(); i++)
            {
                System.out.println(listToSort.get(i).toString());
            }

             */

            words.clear();

            for(int i = listToSort.size() - 1; i >= 0; i--)
            {
                words.add(listToSort.get(i).getKey());
            }

            // System.out.println("Words: " + words.toString());

            System.out.println("Are unique: " + areUnique(words));

            System.out.println("Word list length: " + words.size());

            return words;
        }

        public static ArrayList<Word> selectBestNWords(BoardObj b, FrameObj f, ArrayList<Word> words)
        {
            ArrayList<Word> result = new ArrayList<>();

            words = sortWordsByValue(b, f, words);

            for(int i = 0; i < NUM_BEST_WORDS && i < words.size(); i++)
            {
                result.add(words.get(i));
            }

            return result;
        }

        public static boolean areUnique(ArrayList<Word> words)
        {
            boolean areUnique = true;

            for(int i = 0; i < words.size(); i++)
            {
                for(int j = 0; j < words.size(); j++)
                {
                    if(i != j)
                    {
                        if(UtilityMethods.areEqual(words.get(i), words.get(j)))
                        {
                            // System.out.println(words.get(i) + " = "+ words.get(j));
                            areUnique = false;
                        }
                    }
                }
            }

            return areUnique;
        }

        public static <T> List<Pair<T, Double>> insertionSort(List<Pair<T, Double>> array)
        {
            int n = array.size();

            for(int i = 1; i < n; i++)
            {
                Pair<T, Double> key = array.get(i);
                int j = i -1;

                while(j >= 0 && array.get(j).getValue() > key.getValue()) // While there are items greater than the key in the sorted section
                {
                    array.set(j+1, array.get(j));
                    j--; // Move backwards
                }

                array.set(j+1, key);
            }

            return array;
        }

        /*
        public static <T> T[] mergeSort(T[] a, int lower, int upper)
        {
            if(lower < upper)
            {
                int middle = (lower + (upper - lower) / 2); // Avoid overflow

                mergeSort(a, lower, middle);
                mergeSort(a, middle+1, upper);

                return merge(a, lower, middle, upper);
            }

            return a;
        }
         */

        public static <T> List<Pair<T, Double>> merge(List<Pair<T, Double>> left, List<Pair<T, Double>> right)
        {
            List<Pair<T, Double>> mergedOutput = new ArrayList<>();

            while(!left.isEmpty() && !right.isEmpty())
            {
                if(left.get(0).getValue().compareTo(right.get(0).getValue()) <= 0)
                {
                    mergedOutput.add(left.remove(0));
                }
                else
                {
                    mergedOutput.add(right.remove(0));
                }
            }

            mergedOutput.addAll(left);
            mergedOutput.addAll(right);
            return mergedOutput;

            /*

            int n1 = middle - lower + 1;
            int n2 = upper - middle;

            // Create temp arrays
            List<Pair<T, Double>> tempLower = new ArrayList<>(n1);

            List<Pair<T, Double>> tempUpper = new ArrayList<>(n2);

            // Copy data into temp arrays
            for(int i = 0; i < n1; i++)
            {
                tempLower.add(array.get(lower + i));
                //tempLower.set(i, array.get(lower + i));
            }

            for(int i = 0; i < n2; i++)
            {
                tempUpper.add(array.get(middle + i + 1));
                //tempUpper.set(i, array.get(middle + i + 1));
            }

            int i = 0, j = 0, k = lower;

            // Merge Arrays
            while(i < n1 && j < n2)
            {
                if(tempLower.get(i).getValue() <= tempUpper.get(j).getValue())
                {
                    array.set(k, tempLower.get(i));
                    i++;
                }
                else
                {
                    array.set(k, tempUpper.get(j));
                    j++;
                }
                k++;
            }

            // Add remaining elements in arrays
            while(i < n1)
            {
                array.set(k, tempLower.get(i));
                k++;
                i++;
            }

            while(j < n2)
            {
                array.set(k, tempUpper.get(j));
                k++;
                i++;
            }

            return array;

             */
        }

        public static <T> List<Pair<T, Double>> mergeSortEnhanced(List<Pair<T, Double>> objects)
        {
            if(objects.size() < 15)
            {
                return insertionSort(objects);
            }
            else
            {
                List<Pair<T, Double>> left = new ArrayList<>();
                List<Pair<T, Double>> right = new ArrayList<>();

                int i = 0;

                while(!objects.isEmpty())
                {
                    if(i % 2 != 0)
                    {
                        left.add(objects.remove(0));
                        i++;
                    }
                    else
                    {
                        right.add(objects.remove(0));
                        i--;
                    }
                }
                mergeSortEnhanced(left);
                mergeSortEnhanced(right);
                objects.addAll(merge(left, right));
            }

            /*
            System.out.println("LOWER " + lower + " || UPPER " + upper);
            if(lower < upper)
            {
                int middle = (lower + (upper - lower) / 2); // Avoid overflow

                List<Pair<T, Double>> left = mergeSortEnhanced(objects, lower, middle);
                List<Pair<T, Double>> right = mergeSortEnhanced(objects, middle+1, upper);

                System.out.println("LOWER " + lower + " < UPPER " + upper);
                System.out.println("Left length: " + left.size());
                System.out.println("Middle" + middle);
                System.out.println("Right length: " + right.size());

                if(left.size() > middle && right.size() > 0)
                {
                    if(left.get(middle).getValue() <= right.get(0).getValue()) // If you don't need to merge
                    {
                        ArrayList<Pair<T, Double>> array = new ArrayList<>();
                        int j = 0, k = 0;

                        for(int i = 0; i < upper; i++)
                        {
                            if(i < middle)
                            {
                                array.add(left.get(j));
                                //array.set(i, left.get(j));
                                j++;
                            }
                            else
                            {
                                array.add(right.get(k));
                                //array.set(i, right.get(k));
                                k++;
                            }
                        }

                        return array;
                    }
                }

                return merge(objects, lower, middle, upper);
            }

             */


            return objects;
        }
    }

    /**
     * Static class for getting the leave values of a rack.
     */
    private static class LeaveValues
    {
        private static boolean isInitialised = false;
        private static List<HashMap<String, Double>> leaveMaps = new ArrayList<>();
        private static String l0;
        private static String l1;
        private static ArrayList<StringBuilder> leaves0;
        private static ArrayList<StringBuilder> leaves1;
        private static ArrayList<StringBuilder> leaves2;
        private static ArrayList<StringBuilder> leaves3;
        private static ArrayList<StringBuilder> leaves4;
        private static ArrayList<StringBuilder> leaves5;

        public LeaveValues()
        {
            initialise();
        }

        public static void initialise()
        {
            if(!isInitialised)
            {
                boolean fileInput = false;
                boolean encode = false;
                boolean encodeString = true;

                // Initialise maps
                for(int i = 0; i < 6; i++)
                {
                    leaveMaps.add(new HashMap<String, Double>());
                }


                try
                {
                    //convertLeavesToSB();
                    initialiseLeaves();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }



                if(encodeString)
                {
                    try
                    {
                        addSingleLeave(l0, 0);
                        addSingleLeave(l1, 0);
                        /*
                        addAllLeaves(leaves0, 0);
                        addAllLeaves(leaves1, 1);
                        //addAllLeaves(leaves2, 2);
                        //addAllLeaves(leaves3, 3);
                        //addAllLeaves(leaves4, 4);
                        //addAllLeaves(leaves5, 5);

                         */
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                if(encode)
                {
                    try
                    {
                        for(int i = 0; i < 6; i++)
                        {
                            String outputFileName = ("compressedGZ" + (i+1) + ".txt");
                            compressAndEncode(("src/leave" + (i + 1) + ".txt"), outputFileName, i);

                            String originalFile = decodeAndDecompressFile(outputFileName);

                            //System.out.println("Original: " + originalFile);

                            System.out.println("--- WORKING ON FILE: " + i);
                            addSingleLeave(originalFile, i);
                        }
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
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

                isInitialised = true;
            }
        }

        // Method to check a leave value
        // What if not in leaves?

        private static void initialiseLeaves()
        {
            leaves0 = new ArrayList<>();
            leaves1 = new ArrayList<>();
            leaves2 = new ArrayList<>();
            leaves3 = new ArrayList<>();
            leaves4 = new ArrayList<>();
            leaves5 = new ArrayList<>();

            l0 = "? 25.5731\n" +
                    "S 8.0431\n" +
                    "Z 5.1233\n" +
                    "X 3.3136\n" +
                    "R 1.098\n" +
                    "H 1.0877\n" +
                    "C 0.851\n" +
                    "M 0.58\n" +
                    "D 0.4502\n" +
                    "E 0.3458\n" +
                    "N 0.2242\n" +
                    "T -0.0968\n" +
                    "L -0.1679\n" +
                    "P -0.4599\n" +
                    "K -0.5406\n" +
                    "Y -0.6328\n" +
                    "A -0.633\n" +
                    "J -1.4746\n" +
                    "B -2.0041\n" +
                    "I -2.0719\n" +
                    "F -2.2071\n" +
                    "O -2.503\n" +
                    "G -2.8546\n" +
                    "W -3.8211\n" +
                    "U -5.1045\n" +
                    "V -5.5484\n" +
                    "Q -6.7852\n";
            l1 = "?? 44.8539136418433\n" +
                    "?S 37.4184410464236\n" +
                    "?R 31.8557927202704\n" +
                    "?Z 31.8010717949466\n" +
                    "?C 31.7821615202293\n" +
                    "?N 31.5037776163168\n" +
                    "?L 31.1715735309402\n" +
                    "?T 30.4359967837927\n" +
                    "?H 30.3872850451218\n" +
                    "?D 30.3400499371379\n" +
                    "?M 29.8305707278426\n" +
                    "?E 28.9777509005388\n" +
                    "?P 28.5597553722318\n" +
                    "?X 28.0735181168243\n" +
                    "?Y 27.7850361012113\n" +
                    "?K 27.7716321497679\n" +
                    "?A 27.644024296663\n" +
                    "?B 27.176483374594\n" +
                    "?G 26.7431653077349\n" +
                    "?I 26.0771451945132\n" +
                    "?F 25.89766577678\n" +
                    "?O 25.5339497122098\n" +
                    "?W 23.8121635418206\n" +
                    "?J 23.0461585645474\n" +
                    "?V 21.4864604100979\n" +
                    "?U 20.786577579791\n" +
                    "?Q 17.6061667255051\n" +
                    "SZ 12.3238110308089\n" +
                    "RS 11.859882828733\n" +
                    "SS 11.319941773107\n" +
                    "HS 11.0061611117658\n" +
                    "NS 10.6826690812795\n" +
                    "ST 10.5408344035394\n" +
                    "CS 10.5406571181957\n" +
                    "ES 10.4039038521997\n" +
                    "LS 10.2833995687388\n" +
                    "MS 9.55902213917659\n" +
                    "SX 9.36382909847317\n" +
                    "PS 9.30544232905332\n" +
                    "DS 9.18118650749678\n" +
                    "KS 8.96509852253886\n" +
                    "AS 8.15541957880605\n" +
                    "SY 7.61026017927536\n" +
                    "IS 7.06739284795848\n" +
                    "XZ 6.83658856711559\n" +
                    "BS 6.72799474103226\n" +
                    "EZ 6.52086366215093\n" +
                    "RZ 6.33467150929301\n" +
                    "YZ 6.23181573023418\n" +
                    "JS 5.96554890113589\n" +
                    "OS 5.94481193343714\n" +
                    "NZ 5.86758831669745\n" +
                    "HZ 5.81770489512678\n" +
                    "FS 5.72094824019413\n" +
                    "CH 5.60232660066198\n" +
                    "GS 5.58473187216407\n" +
                    "AZ 5.11991093842729\n" +
                    "LZ 5.07081655826473\n" +
                    "DZ 4.93112158447968\n" +
                    "SW 4.72482252860247\n" +
                    "TZ 4.49197296434428\n" +
                    "CZ 4.3623286550345\n" +
                    "MZ 4.23619889203708\n" +
                    "KZ 4.14479747980941\n" +
                    "BZ 3.99382264923754\n" +
                    "OZ 3.9599219074803\n" +
                    "CK 3.86132783178567\n" +
                    "ER 3.85426951730304\n" +
                    "EX 3.80139728909833\n" +
                    "FZ 3.76098685293069\n" +
                    "IZ 3.74640093182077\n" +
                    "CR 3.66722722739505\n" +
                    "PZ 3.3994371473205\n" +
                    "HR 3.34409442514454\n" +
                    "XY 3.34235590636668\n" +
                    "HX 3.33961932713383\n" +
                    "RT 3.11979973766811\n" +
                    "NX 3.05159889939497\n" +
                    "CN 3.00571093353267\n" +
                    "DR 2.90282314096045\n" +
                    "NR 2.8887708301448\n" +
                    "CX 2.87491480422192\n" +
                    "RX 2.79655587737267\n" +
                    "DE 2.66252452217703\n" +
                    "EN 2.61814549853869\n" +
                    "MX 2.59256091229339\n" +
                    "LX 2.45801898903261\n" +
                    "CE 2.44164207810415\n" +
                    "MR 2.37771445784462\n" +
                    "CL 2.32723918724922\n" +
                    "HT 2.31796017994436\n" +
                    "AX 2.3062071464447\n" +
                    "LY 2.26253951127715\n" +
                    "HY 2.25714844759837\n" +
                    "EL 2.23823297474359\n" +
                    "EH 2.19140393946364\n" +
                    "NT 2.15918416051895\n" +
                    "PR 2.15624421635916\n" +
                    "DX 2.13283159288486\n" +
                    "DN 2.1206100019679\n" +
                    "TX 2.10947805773161\n" +
                    "KY 2.02790550846398\n" +
                    "RY 2.00534606261779\n" +
                    "HK 1.99964373780769\n" +
                    "HN 1.99892505604248\n" +
                    "JZ 1.99038501320937\n" +
                    "LR 1.9378314570029\n" +
                    "PX 1.9361498566525\n" +
                    "ET 1.92478893844836\n" +
                    "CT 1.86589726694739\n" +
                    "AR 1.86231374666834\n" +
                    "GZ 1.75258763410408\n" +
                    "MN 1.74847873825179\n" +
                    "SU 1.72632651666989\n" +
                    "KR 1.69589105505736\n" +
                    "CD 1.58770893174191\n" +
                    "KN 1.56950459622553\n" +
                    "HP 1.54914928006265\n" +
                    "FX 1.5002646010395\n" +
                    "CY 1.50009524494862\n" +
                    "NY 1.49532221438055\n" +
                    "AC 1.46576263796401\n" +
                    "KX 1.44631149629839\n" +
                    "LN 1.41548113107459\n" +
                    "GN 1.36317074466502\n" +
                    "HL 1.355772661408\n" +
                    "HM 1.35502114968617\n" +
                    "DH 1.34565209164358\n" +
                    "EM 1.31237697905132\n" +
                    "SV 1.28122132644969\n" +
                    "DL 1.25261998392299\n" +
                    "QU 1.21759057819481\n" +
                    "AN 1.2115432220337\n" +
                    "MY 1.11091568915405\n" +
                    "LT 1.00389919696574\n" +
                    "PY 1.00129951835577\n" +
                    "JX 0.974322531694359\n" +
                    "AL 0.968090630695688\n" +
                    "KL 0.94273694495988\n" +
                    "WZ 0.821683967842372\n" +
                    "AH 0.817437494917597\n" +
                    "AM 0.804689048964155\n" +
                    "EK 0.80395967971207\n" +
                    "DY 0.701800062790129\n" +
                    "CM 0.574580813355517\n" +
                    "CP 0.54904610493247\n" +
                    "BR 0.546538716375601\n" +
                    "LP 0.538187159782571\n" +
                    "IX 0.538036239269409\n" +
                    "OX 0.532391732989782\n" +
                    "EP 0.484080830684445\n" +
                    "IN 0.477774087935075\n" +
                    "TY 0.421728820462932\n" +
                    "AT 0.416887685716763\n" +
                    "DM 0.40400345980477\n" +
                    "LM 0.401458255408719\n" +
                    "FR 0.357343066309084\n" +
                    "DT 0.287051707664162\n" +
                    "AD 0.143825585655136\n" +
                    "JK 0.1433538237129\n" +
                    "BL 0.0926548651320438\n" +
                    "NP 0.0872922983165897\n" +
                    "MT 0.0444662036186798\n" +
                    "EY -0.069808194986886\n" +
                    "CI -0.0725069183549439\n" +
                    "IR -0.0747619813847978\n" +
                    "PT -0.145792264401276\n" +
                    "BX -0.233042662564064\n" +
                    "FL -0.306397030516946\n" +
                    "JY -0.354047084037539\n" +
                    "HW -0.439469883251291\n" +
                    "OR -0.495382817774008\n" +
                    "CO -0.526813476574636\n" +
                    "AK -0.546752204035601\n" +
                    "DK -0.589366856520787\n" +
                    "KP -0.707963563451332\n" +
                    "HJ -0.778435934240457\n" +
                    "BY -0.781298901920663\n" +
                    "DP -0.785137627903731\n" +
                    "BE -0.818949500893064\n" +
                    "GR -0.820062063779725\n" +
                    "IL -0.838809042179596\n" +
                    "JN -0.872954688792288\n" +
                    "EJ -0.901599166742738\n" +
                    "KT -1.02836181230139\n" +
                    "AY -1.05653388567727\n" +
                    "AP -1.05675804366669\n" +
                    "NO -1.07170511507966\n" +
                    "BK -1.09164601678435\n" +
                    "JR -1.09858416614043\n" +
                    "KM -1.10469829780619\n" +
                    "MP -1.10934916676024\n" +
                    "DI -1.14989605440695\n" +
                    "HO -1.1627797001881\n" +
                    "FY -1.32532402562006\n" +
                    "BD -1.32955714718423\n" +
                    "IT -1.33981718505591\n" +
                    "IM -1.35123207108388\n" +
                    "EF -1.37467301170794\n" +
                    "BN -1.3940312400584\n" +
                    "HI -1.41798416762706\n" +
                    "WX -1.42512342933031\n" +
                    "GH -1.43509858554794\n" +
                    "WY -1.58848690914564\n" +
                    "FN -1.61455200843927\n" +
                    "BH -1.61920842973586\n" +
                    "RR -1.624316039454\n" +
                    "QS -1.63520129093221\n" +
                    "RW -1.71119362716698\n" +
                    "LO -1.7412436415896\n" +
                    "GL -1.75199816466802\n" +
                    "MO -1.75381389767423\n" +
                    "HH -1.76730267066083\n" +
                    "DO -1.78502591293855\n" +
                    "AJ -1.79954836931061\n" +
                    "FH -1.85170611292642\n" +
                    "AB -1.85499929335878\n" +
                    "DJ -1.91428251707304\n" +
                    "KW -1.92295427956092\n" +
                    "CC -1.92852532299038\n" +
                    "JT -1.93799519797738\n" +
                    "GY -2.00400611473396\n" +
                    "OT -2.08085612961304\n" +
                    "FT -2.1111064399542\n" +
                    "BT -2.1432953577087\n" +
                    "EG -2.1454268067884\n" +
                    "BM -2.21964114287945\n" +
                    "DF -2.24070330771841\n" +
                    "JL -2.25807015209906\n" +
                    "IK -2.27163523008705\n" +
                    "FK -2.30101182195707\n" +
                    "GX -2.30600081305686\n" +
                    "BC -2.32295509286265\n" +
                    "CJ -2.34037431453629\n" +
                    "CF -2.42846855363975\n" +
                    "NW -2.44042123501297\n" +
                    "DW -2.54025400040121\n" +
                    "NN -2.59590271020244\n" +
                    "BJ -2.60593356699553\n" +
                    "EW -2.68234267979565\n" +
                    "IP -2.71164232574366\n" +
                    "QZ -2.77397792444431\n" +
                    "KO -2.79766951974652\n" +
                    "DD -2.88273060113316\n" +
                    "OP -2.92178240112308\n" +
                    "MM -2.9590338905594\n" +
                    "OY -3.00090266576879\n" +
                    "JM -3.00351698616343\n" +
                    "LL -3.02664358402697\n" +
                    "AG -3.05048762634208\n" +
                    "DG -3.09549048230596\n" +
                    "LW -3.13991780480156\n" +
                    "AF -3.25139584336792\n" +
                    "UZ -3.29684873431895\n" +
                    "TT -3.39472240148897\n" +
                    "GT -3.44552859207434\n" +
                    "PP -3.4925102590819\n" +
                    "FM -3.53827753526723\n" +
                    "JO -3.59160879481337\n" +
                    "GM -3.60645112225121\n" +
                    "FF -3.72104999835048\n" +
                    "JP -3.72348648480111\n" +
                    "IY -3.76337594913062\n" +
                    "BO -3.95441890605899\n" +
                    "TW -4.00215852707617\n" +
                    "AE -4.0874663561618\n" +
                    "GI -4.1580053060163\n" +
                    "UX -4.17606691838855\n" +
                    "RV -4.20078848168628\n" +
                    "BI -4.2702313446809\n" +
                    "AW -4.30593640849617\n" +
                    "FI -4.3213890247115\n" +
                    "JW -4.36475132168082\n" +
                    "VX -4.42192380731006\n" +
                    "EV -4.44845583030432\n" +
                    "VZ -4.46010966083615\n" +
                    "IJ -4.47176447841118\n" +
                    "GJ -4.56884829114611\n" +
                    "CG -4.61845950336163\n" +
                    "GK -4.71349468658452\n" +
                    "MW -4.81578616105\n" +
                    "CW -4.86510773272666\n" +
                    "FO -4.87294203092779\n" +
                    "YY -4.95153380338566\n" +
                    "FJ -4.97041759196028\n" +
                    "GP -5.05804524817055\n" +
                    "RU -5.10064903204627\n" +
                    "PW -5.34496640192706\n" +
                    "BG -5.36220982645418\n" +
                    "EE -5.41131336109023\n" +
                    "LV -5.49821494088364\n" +
                    "GO -5.50935741669737\n" +
                    "CU -5.57437783211195\n" +
                    "FG -5.60124291156506\n" +
                    "NU -5.62434798405214\n" +
                    "EI -5.69142689090384\n" +
                    "BB -5.78888658942435\n" +
                    "QX -5.81723732772169\n" +
                    "OW -5.86920055979155\n" +
                    "LU -5.91136472469177\n" +
                    "FW -5.93994724445631\n" +
                    "BF -5.97534120859847\n" +
                    "FP -6.01546183297288\n" +
                    "NV -6.0419649458135\n" +
                    "VY -6.07353075845238\n" +
                    "DU -6.21795540243098\n" +
                    "BP -6.27063125935199\n" +
                    "EO -6.2827894893213\n" +
                    "CV -6.28326776234191\n" +
                    "AV -6.35972255698476\n" +
                    "TU -6.37331480072199\n" +
                    "BW -6.44092703493939\n" +
                    "GW -6.60457379192923\n" +
                    "HU -6.62205960443398\n" +
                    "IW -6.62351699517929\n" +
                    "MU -6.63367973313755\n" +
                    "QT -6.71709547129215\n" +
                    "DV -6.74668742129872\n" +
                    "AI -6.94584309355921\n" +
                    "AQ -6.99964365880359\n" +
                    "JU -7.11973483440333\n" +
                    "KU -7.11989966499677\n" +
                    "TV -7.25563866108097\n" +
                    "HV -7.40533847144422\n" +
                    "PU -7.48661911792593\n" +
                    "HQ -7.53426516222775\n" +
                    "IV -7.61286395692808\n" +
                    "QR -7.77885794328493\n" +
                    "QY -7.79928072547647\n" +
                    "GG -7.92965229567045\n" +
                    "AO -7.95699711801094\n" +
                    "NQ -8.0800426456978\n" +
                    "DQ -8.09336318475886\n" +
                    "BU -8.17719689376792\n" +
                    "EQ -8.19083201408003\n" +
                    "UY -8.26918222962626\n" +
                    "AA -8.42576134077527\n" +
                    "KQ -8.43694268485677\n" +
                    "FU -8.82987204481911\n" +
                    "OV -8.90622437349095\n" +
                    "JV -8.99946332109166\n" +
                    "KV -9.03464240933892\n" +
                    "IO -9.16233205657987\n" +
                    "CQ -9.3440998259017\n" +
                    "MV -9.45530842728281\n" +
                    "LQ -9.62268949495009\n" +
                    "GU -9.71676165992503\n" +
                    "PQ -9.83894586801703\n" +
                    "EU -10.0252395561028\n" +
                    "GV -10.0825518543598\n" +
                    "PV -10.1415877985557\n" +
                    "WW -10.1929784917866\n" +
                    "IQ -10.2040077025637\n" +
                    "MQ -10.2318500436097\n" +
                    "VW -10.2612421890337\n" +
                    "JQ -10.5705320474472\n" +
                    "FQ -10.6731839804112\n" +
                    "BV -11.0486835582652\n" +
                    "OO -11.33696721772\n" +
                    "AU -11.4523752308627\n" +
                    "FV -11.4577700640964\n" +
                    "II -11.6811816390005\n" +
                    "BQ -11.7702242059314\n" +
                    "QW -11.9899957105352\n" +
                    "OQ -11.9914713697251\n" +
                    "GQ -12.7057596233878\n" +
                    "UW -12.9526877784755\n" +
                    "OU -13.2669510518672\n" +
                    "UV -14.1715742175375\n" +
                    "IU -14.4615939610684\n" +
                    "VV -14.5382260819852\n" +
                    "QV -15.639904442781\n" +
                    "UU -18.8019788086433\n";
            /*
            leaves0.add(new StringBuilder("")
                    .append("H4sIAAAAAAAAACXPx20EMAxE0ftUwQZEiEmiTobXOad17L8QU/bpPxCYA09Ig2Oa4I2Suxd+KFjUDF9kbGIDryTcV+J6N+fEGXXOEDxUI3Fe8eiKi4J5XR4Lqq44Uuu1HYn7LRlz4XnLYy3cbYX3ge+tYZo4/ZfhlpqwTx84UFPu3QU3f5qycLmlRTxtRTdcbWTU4JOacaoI3qnVN90DH1vh6XihNnhmKH4BSytqsf4AAAA=                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        \n")
            );
            leaves1.add(new StringBuilder("")
                    .append("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\n")
                    .append("")
            );
            leaves2.add(new StringBuilder("")
                    .append("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\n")
                    .append("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\n")
                    .append("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\n")
                    .append("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\n")
                    .append("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\n")
                    .append("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\n")
            );


            leaves2.add(new StringBuilder("")
                    .append("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\n")
                    .append("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\n")
                    .append("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\n")
                    .append("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\n")
                    .append("")
            );

             */

        }

        private static void convertLeavesToSB() throws IOException {
            for(int i = 0; i < 6; i++)
            {
                convertToStringBuilder("compressedGZ" + (i+1) + ".txt", "compressedGZ_SB" + (i+1) + ".txt", i);
            }
            convertToStringBuilder("compressedGZ6.txt", "compressedGZ_SB6.txt", 5);
        }

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
                //System.out.println("MAP CONTAINS KEY");
                return leaveMaps.get(leave.length() - 1).get(leave);
            }
            else
            {
                //System.out.println("NO KEY FOUND");
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

        // DEPRECATED
        // Method for parsing arraylists of Stringbuilders
        private static void addAllLeaves(ArrayList<StringBuilder> list, int leaveLength) throws IOException {
            StringBuilder buffer = new StringBuilder();
            for(int i = 0; i < list.size(); i++)
            {
                buffer.append(list.get(i).toString());
            }
            addSingleLeave(decodeAndDecompressString(buffer.toString()), leaveLength);
        }

        // Method for parsing single, decoded, uncompressed String
        private static void addSingleLeave(String input, int leaveLength)
        {
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

        // DEPRECATED
        public static void convertToStringBuilder(String filePath, String desiredFileName, int fileIndex) throws IOException
        {
            File in = new File(filePath);
            PrintWriter writer = new PrintWriter(desiredFileName, "UTF-8");

            BufferedReader buf = new BufferedReader(new FileReader(in));
            //String line;
            char[] bufferArray = new char[1000];

            StringBuilder buffer = new StringBuilder();
            int lineCounter = 0;
            int appendCounter = 0;
            long totalLineCounter = 0;

            final long LOWER = 0;
            final long UPPER = 100000;

            writer.println("leaves" + fileIndex + ".add(new StringBuilder(\"\")");

            while((buf.read(bufferArray)) != -1)
            {
                //System.out.println("Line: " + line);
                // writer.println(".append(\"" + line + "\\n" + "\")");

                //if(totalLineCounter < UPPER && totalLineCounter > LOWER)
                //{
                if(lineCounter < 5)
                {
                    buffer.append(new String(bufferArray)).append("\\n");
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

        private static void compressAndEncode(String filePath, String desiredFileName, int fileIndex) throws IOException
        {
            String gzipFileName = "compressed_leave" + fileIndex + ".gz";

            compressGZIP(new File(filePath), new File(gzipFileName));
            byte[] byte_leave = Files.readAllBytes(Paths.get(gzipFileName));

            PrintWriter writer = new PrintWriter(desiredFileName, "UTF-8");

            writer.print(Base64.getEncoder().encodeToString(byte_leave));

            writer.close();
        }

        // Todo: merge DDFile with adding to maps for less memory waste, less loops.
        private static String decodeAndDecompressFile(String filePath) throws IOException
        {
            File initialFileInput = new File(filePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(initialFileInput));
            String line;

            StringBuilder encodedInputBuffer = new StringBuilder();

            while((line = bufferedReader.readLine()) != null)
            {
                encodedInputBuffer.append(line);
            }

            bufferedReader.close();

            // Once read in, decode
            byte[] decodedBytes = Base64.getDecoder().decode(encodedInputBuffer.toString());

            System.out.println("Length of decoded bytes: " + decodedBytes.length);

            // Convert to decodedInputStream
            InputStream decodedInputStream = new ByteArrayInputStream(decodedBytes);

            System.out.println("Available from decodedInputStream: " + decodedInputStream.available());
            int decodedAvailable = decodedInputStream.available();

            /*
            try (GZIPInputStream in = new GZIPInputStream(decodedInputStream)){
                try (FileOutputStream out = new FileOutputStream("TEST_OUT" + filePath)){
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }

             */

            /*
            try (GZIPInputStream in = new GZIPInputStream(decodedInputStream))
            {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                int numBuffers = decodedInputStream.available() / 1024;

                StringBuilder sb = new StringBuilder();

                if(numBuffers == 0)
                {
                    numBuffers = 1;
                }

                for(int i = 0; i < numBuffers; i++)
                {
                    int len;
                    byte[] buffer = new byte[1024];

                    while((len = in.read(buffer)) != -1)
                    {
                        sb.append(new String(shrinkArray(buffer)));
                    }
                }
                /*
                try (FileOutputStream out = new FileOutputStream("TEST_OUT" + filePath)){
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }

                 */

            //System.out.println(sb.toString());

            //return sb.toString();
            //}


            /*
            try (GZIPInputStream gzIn = new GZIPInputStream(decodedInputStream))
            {
                //ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                int numBytes = decodedBytes.length;

                System.out.println("NumBytes: " + decodedInputStream.available());


                byte[] compressedBytes = new byte[8192];

                System.out.println("byte[0] = " + compressedBytes[0]);

                int len = gzIn.read(compressedBytes);
                System.out.println("Read 2 test: " + gzIn.read(new byte[1024]));

                System.out.println("Length read in: " + len);

                System.out.println("After gzRead in, length: " + compressedBytes.length);

                //byte[] uncompressedBytes = new byte[compressedBytes.length];
                //byteOut.write(uncompressedBytes, 0, compressedBytes.length);

                //System.out.println("After gzWrite out, length: " + uncompressedBytes.length);

                String finalOutput = new String(compressedBytes, StandardCharsets.UTF_8);

                System.out.println("String length: " + finalOutput.length());

                return finalOutput;
            }

             */

            try(GZIPInputStream gzIn = new GZIPInputStream(decodedInputStream))
            {
                BufferedReader gzBuffered = new BufferedReader(new InputStreamReader(gzIn));

                StringBuilder output = new StringBuilder();

                String currentLine;

                while((currentLine = gzBuffered.readLine()) != null)
                {
                    output.append(currentLine).append("\n");
                }

                return output.toString();
            }



            //return "";
        }

        private static String decodeAndDecompressString(String data) throws IOException
        {
            // Once read in, decode
            byte[] decodedBytes = Base64.getDecoder().decode(data);

            System.out.println("Length of decoded bytes: " + decodedBytes.length);

            // Convert to decodedInputStream
            InputStream decodedInputStream = new ByteArrayInputStream(decodedBytes);

            System.out.println("Available from decodedInputStream: " + decodedInputStream.available());
            int decodedAvailable = decodedInputStream.available();

            /*
            try (GZIPInputStream in = new GZIPInputStream(decodedInputStream)){
                try (FileOutputStream out = new FileOutputStream("TEST_OUT" + filePath)){
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }

             */

            /*
            try (GZIPInputStream in = new GZIPInputStream(decodedInputStream))
            {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                int numBuffers = decodedInputStream.available() / 1024;

                StringBuilder sb = new StringBuilder();

                if(numBuffers == 0)
                {
                    numBuffers = 1;
                }

                for(int i = 0; i < numBuffers; i++)
                {
                    int len;
                    byte[] buffer = new byte[1024];

                    while((len = in.read(buffer)) != -1)
                    {
                        sb.append(new String(shrinkArray(buffer)));
                    }
                }
                /*
                try (FileOutputStream out = new FileOutputStream("TEST_OUT" + filePath)){
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len = in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }

                 */

            //System.out.println(sb.toString());

            //return sb.toString();
            //}


            /*
            try (GZIPInputStream gzIn = new GZIPInputStream(decodedInputStream))
            {
                //ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                int numBytes = decodedBytes.length;

                System.out.println("NumBytes: " + decodedInputStream.available());


                byte[] compressedBytes = new byte[8192];

                System.out.println("byte[0] = " + compressedBytes[0]);

                int len = gzIn.read(compressedBytes);
                System.out.println("Read 2 test: " + gzIn.read(new byte[1024]));

                System.out.println("Length read in: " + len);

                System.out.println("After gzRead in, length: " + compressedBytes.length);

                //byte[] uncompressedBytes = new byte[compressedBytes.length];
                //byteOut.write(uncompressedBytes, 0, compressedBytes.length);

                //System.out.println("After gzWrite out, length: " + uncompressedBytes.length);

                String finalOutput = new String(compressedBytes, StandardCharsets.UTF_8);

                System.out.println("String length: " + finalOutput.length());

                return finalOutput;
            }

             */

            try(GZIPInputStream gzIn = new GZIPInputStream(decodedInputStream))
            {
                BufferedReader gzBuffered = new BufferedReader(new InputStreamReader(gzIn));

                StringBuilder output = new StringBuilder();

                String currentLine;

                while((currentLine = gzBuffered.readLine()) != null)
                {
                    output.append(currentLine).append("\n");
                }

                return output.toString();
            }



            //return "";
        }

        public static void compressGZIP(File input, File output) throws IOException {
            try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))){
                try (FileInputStream in = new FileInputStream(input)){
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len=in.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                }
            }
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
            File file = new File("csw.txt");
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
                        System.out.println("[" + r + ", " + c + "] is anchor!");
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

/*
class WordWithEquals
{
    Word w;

    public WordWithEquals(Word w) {
        this.w = w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordWithEquals that = (WordWithEquals) o;
        return Bot0.UtilityMethods.areEqual(w, that.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(w.getFirstColumn(), w.getFirstRow(), w.getLetters(), w.isHorizontal(), w.getDesignatedLetters());
    }
}

 */
