import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        getPossibleWords();

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

    protected ArrayList<String> getPossibleWords()
    {
        Frame frame = parseFrameFromString(me.getFrameAsString());

        return null;
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
     * Private Board class with extra accessors for easier manipulation of the frame
     * TODO: Cal - Finish this class
     */
    private class BoardObj extends Board
    {

    }

    /**
     * Private class with extra accessors for easier manipulation of the frame
     * TODO: Cal - Finish this class
     */
    private class FrameObj extends Frame
    {

    }

    /**
     * Private player class
     */
    private class PlayerObj
    {
        private int id;
        private String name;
        private int score;
        private FrameObj frame;

        public PlayerObj(int id)  {
            this.id = id;
            name = "";
            score = 0;
            frame = new FrameObj();
        }

        public PlayerObj(Player p)
        {
            this.id = p.getPrintableId();
            this.score = p.getScore();
            this.name = p.getName();
            this.frame = parseFrameFromString(p.getFrameAsString());
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

        public void addPoints(int increase) {
            score = score + increase;
        }

        public void subtractPoints(int decrease) {
            score = score - decrease;
        }

        public int getScore() {
            return score;
        }

        public Frame getFrame() {
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

        private BoardObj board;
        private Player mainPlayer;
        private Player oppponent;

        // TODO: WRITE AN OVERALL CONTROL METHOD THAT CALLS EXPANDS NODE.

        /**
         * Method to perform a dynamic 2-ply playout for the game.
         */
        public Node expandNode(ArrayList<Word> words)
        {
            // For each word, create a child node and statically evaluate the node.
            for(int i = 0; i < words.size(); i++)
            {
                PlayerObj main = new PlayerObj(mainPlayer);
                PlayerObj opponentObj = new PlayerObj(oppponent);
                Node currentWord = MCT.current.addChild(new Node(new State(board, main, opponentObj, words.get(i)), MCT.current));
                evaluateNode(currentWord, words.get(i));
            }

            // Pick the best of the nodes.
            return findBestMove(MCT.current);
        }

        /**
         * Method to perform a static 2-ply playout for the game.
         */
        public Node evaluateNode(Node currentWord, Word initial_ply0)
        {
            // Run MAX_PLAYOUT_ITERATION times

            for(int i = 0; i < MAX_PLAYOUT_ITERATIONS; i++)
            {
                // Store final score and score differential (me - opponent) in nodes.

                // Me: Play move 0
                Board board = currentWord.getState().getBoard();

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
                ArrayList<Word> words = getAllPossibleMoves();
                // Statically evaluate best

                // Play move 1
                // Update score

                // Me: Refill frame with random tiles
                // Generate all moves
                // Statically evaluate best
                // Play move 2
                // Update score with leave
            }

            // findBestMove() -> Can change implementation of the find best here.

            return new Node();
        }

        private FrameObj generateRandomFrame()
        {
            FrameObj f = new FrameObj();
            f.refill(new Pool());
            return f;
        }

        private void replenishFrame(Frame f)
        {
            Pool p = new Pool();
            f.refill(p);
        }

        /**
         * Method to find the best move from a series of Nodes containing game states after static playout.
         * Current heuristic: Best average score.
         * @param root Pass the root whose children are to be parsed.
         * @return
         */
        private Node findBestMove(Node root)
        {
            return root.getHighestAvgScoreChild();
        }
    }

    private class State
    {
        private Word move;
        private BoardObj board;
        private PlayerObj mainPlayer;
        private PlayerObj opponent;

        // Constructor

        public State(BoardObj board, PlayerObj mainPlayer, PlayerObj opponent, Word word) {
            this.board = board;
            this.mainPlayer = mainPlayer;
            this.opponent = opponent;
            this.move = word;
        }

        public State() {
        }

        // Getter and Setters

        public Word getMove() {
            return move;
        }

        public void setMove(Word move) {
            this.move = move;
        }

        public Board getBoard() {
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
         * @param root Pass the root node that was explored
         * @param target Pass the target node that was chosen as to continue the tree
         */
        public void freeUnusedChildren(Node root, Node target)
        {
            int n = root.getChildren().size();

            for (int i = 0; i < n; i++)
            {
                if(root.getChildren().get(i) != target)
                {
                    root.getChildren().remove(i);
                }
            }
        }
    }

    private class Tree {

        private Node root;
        private Node current;

        public Tree() {
            root = new Node();
        }

        public Tree(Node root) {
            this.root = root;
        }

        public Node getRoot() {
            return root;
        }

        public void setRoot(Node root) {
            this.root = root;
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
     * Class for generating a static value from the score of the word on the board + the score
     */
    private class StaticValueGenerator
    {
        private Word word;
        private BoardObj board;

        public StaticValueGenerator(BoardObj b, Word w)
        {
            this.board = b;
            this.word = w;
        }
    }

    /**
     * Static class for getting the leave values of a rack.
     */
    private static class LeaveValues
    {
        private static boolean isInitialised = false;
        private static List<HashMap<String, Double>> leaveMaps = new ArrayList<>();
        private static String[] leaves = new String[6];

        public LeaveValues()
        {
            initialise();
        }

        public static void initialise()
        {
            if(!isInitialised)
            {
                // Initialise maps
                for(int i = 0; i < 6; i++)
                {
                    leaveMaps.add(new HashMap<String, Double>());
                }

                // Initialise from String or file
                for(int i = 0; i < 6; i++)
                {
                    addAllLeaves(leaves[i], i);
                }

                isInitialised = true;
            }
        }

        private static void addAllLeaves(String input, int leaveLength)
        {
            if(!input.equals(""))
            {
                String[] leaves = input.split("\n");
                int N = leaves.length;

                for(int i = 0; i < N; i++)
                {
                    String[] currentLeave = leaves[i].split("\\s");

                    System.out.println("Current leave: " + currentLeave[0] + " :: " + currentLeave[1]);

                    leaveMaps.get(leaveLength).put(currentLeave[0], Double.parseDouble(currentLeave[1]));
                }
            }
        }

        // Method to check a leave value
        // What if not in leaves?

        private static void setupLeaves()
        {
            leaves[0] = "? 25.5731\n" +
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
            leaves[1] = "?? 44.8539136418433\n" +
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
            leaves[2] = "??S 49.7706567086272\n" +
                    "??Z 47.7412733610943\n" +
                    "??C 45.939926343205\n" +
                    "??R 45.5868797674427\n" +
                    "??N 45.2325365566833\n" +
                    "??H 45.1777669138905\n" +
                    "??L 44.9663469115827\n" +
                    "??D 44.5469944710988\n" +
                    "??T 44.4426486375838\n" +
                    "??M 44.316586613801\n" +
                    "??E 43.8988067228037\n" +
                    "??X 43.4043799497028\n" +
                    "??Y 43.2520289269548\n" +
                    "??P 43.2473376141728\n" +
                    "??K 43.2016782163895\n" +
                    "??A 42.8455461368977\n" +
                    "??B 42.1668461545526\n" +
                    "??I 41.6049772505674\n" +
                    "??G 41.5739815291172\n" +
                    "??F 41.3120997010138\n" +
                    "??O 41.005031988733\n" +
                    "??W 39.3188169723436\n" +
                    "??J 39.2121024371927\n" +
                    "?RS 38.8719358926545\n" +
                    "?ES 38.7618094604234\n" +
                    "?CS 38.1156866592091\n" +
                    "?HS 38.0746050623559\n" +
                    "?ST 37.9639622104689\n" +
                    "?NS 37.936574777398\n" +
                    "?LS 37.8082050720075\n" +
                    "??U 37.4572308642343\n" +
                    "??V 37.2402046442613\n" +
                    "?SS 37.0600310559352\n" +
                    "?MS 36.7947044419497\n" +
                    "?AS 36.588977910173\n" +
                    "?PS 36.273180920459\n" +
                    "?DS 36.2380958303305\n" +
                    "?SZ 36.0528078199551\n" +
                    "?IS 35.9007868947831\n" +
                    "?CH 34.853224105473\n" +
                    "?ER 34.8375511141649\n" +
                    "?KS 34.7569387539776\n" +
                    "?OS 34.6723546415005\n" +
                    "??Q 33.9042094670745\n" +
                    "?BS 33.8371244830921\n" +
                    "?SY 33.6971673249908\n" +
                    "?DE 33.4960452923894\n" +
                    "?CR 33.4948717128902\n" +
                    "?EZ 33.4869353224372\n" +
                    "?EL 33.3851139844212\n" +
                    "?EN 33.3363420009129\n" +
                    "?CE 33.2810609318402\n" +
                    "?GS 32.9917044372301\n" +
                    "?CN 32.9302081056672\n" +
                    "?ET 32.7077700553396\n" +
                    "?AR 32.6999329120626\n" +
                    "?AC 32.6819966132242\n" +
                    "?RT 32.62365146947\n" +
                    "?CL 32.3690761623525\n" +
                    "?AL 32.3437482873198\n" +
                    "?NR 32.1582008605607\n" +
                    "?IN 32.0967095828063\n" +
                    "?AN 32.0544009575155\n" +
                    "?DR 32.0420460722088\n" +
                    "?AZ 32.0038330112825\n" +
                    "?HR 31.9991880729946\n" +
                    "?SX 31.9837004860805\n" +
                    "?FS 31.9653623870303\n" +
                    "?CK 31.8297276288156\n" +
                    "?NT 31.7678253171508\n" +
                    "?CT 31.7416697686124\n" +
                    "?EH 31.6278634610155\n" +
                    "?GN 31.5429187887128\n" +
                    "?DN 31.514948369927\n" +
                    "?EM 31.4857296365045\n" +
                    "?RZ 31.4760232182858\n" +
                    "?MR 31.4417261536247\n" +
                    "?AM 31.4266673315935\n" +
                    "?CI 31.424718894806\n" +
                    "?IZ 31.3725753404121\n" +
                    "?HT 31.3466168346451\n" +
                    "?LR 31.3358596982354\n" +
                    "?PR 31.3065058893126\n" +
                    "?AT 31.2852481260452\n" +
                    "?LY 31.2522256666426\n" +
                    "?NZ 31.1719011743948\n" +
                    "?LN 31.0342709619205\n" +
                    "?CD 30.9210603733766\n" +
                    "?SU 30.9014744197447\n" +
                    "?LT 30.8562991450009\n" +
                    "?MN 30.8480236898803\n" +
                    "?DL 30.840708168773\n" +
                    "?OZ 30.8368644433881\n" +
                    "?IR 30.8107247593789\n" +
                    "?IL 30.7617036509515\n" +
                    "?CO 30.7262231796903\n" +
                    "?LZ 30.6981654351437\n" +
                    "?HN 30.6420976087903\n" +
                    "?EP 30.5905483786566\n" +
                    "?SW 30.5699760763158\n" +
                    "?AD 30.5017912042711\n" +
                    "?OR 30.3712522373972\n" +
                    "?AH 30.2510813419472\n" +
                    "?RY 30.2092169639033\n" +
                    "?HL 30.1300720593972\n" +
                    "?LP 29.9295547143678\n" +
                    "?IT 29.9239439321041\n" +
                    "?NO 29.9149598383692\n" +
                    "?TZ 29.8797567086346\n" +
                    "?LM 29.8314201391537\n" +
                    "?CM 29.7316204317599\n" +
                    "?CZ 29.7254911443292\n" +
                    "?EX 29.7206107833497\n" +
                    "?BR 29.7060824844696\n" +
                    "?DI 29.6982622433195\n" +
                    "?DH 29.6322532810701\n" +
                    "?LO 29.5624672555316\n" +
                    "?DZ 29.5509900636663\n" +
                    "?BL 29.5341462572601\n" +
                    "?NY 29.51467950721\n" +
                    "?CP 29.4958994758763\n" +
                    "?IM 29.4627150199305\n" +
                    "?HP 29.4563211894255\n" +
                    "?CY 29.4386999376134\n" +
                    "?EK 29.323371111941\n" +
                    "?DT 29.3015275292566\n" +
                    "?KN 29.2988690563617\n" +
                    "?HM 29.2775235572502\n" +
                    "?KR 29.2242522703304\n" +
                    "?HZ 29.1471769779347\n" +
                    "?BE 29.1350568750479\n" +
                    "?MT 29.0709030614909\n" +
                    "?NP 29.040386607474\n" +
                    "?DM 29.0244251857181\n" +
                    "?AP 29.0099064761234\n" +
                    "?FR 28.9349863778521\n" +
                    "?MO 28.9243803441931\n" +
                    "?JS 28.8719759398225\n" +
                    "?KL 28.8593630709756\n" +
                    "?YZ 28.8381834689166\n" +
                    "?OT 28.8359829335022\n" +
                    "?DO 28.8337079566758\n" +
                    "?PT 28.7816439230955\n" +
                    "?GR 28.7522322714343\n" +
                    "?HY 28.7341541991935\n" +
                    "?FL 28.6635189866292\n" +
                    "?MZ 28.5371034222643\n" +
                    "?TY 28.4533838491466\n" +
                    "?HO 28.3969979381187\n" +
                    "?HI 28.3212287747996\n" +
                    "?AB 28.311307392253\n" +
                    "?BZ 28.3106221213327\n" +
                    "?GL 28.2726006308322\n" +
                    "?PY 28.1645591030418\n" +
                    "?EG 28.145602944322\n" +
                    "?MY 28.1448164477751\n" +
                    "?QU 28.1318576858844\n" +
                    "?DY 27.9874221767241\n" +
                    "?AX 27.9644845259532\n" +
                    "?EY 27.810621513585\n" +
                    "?AK 27.7539683945015\n" +
                    "?HK 27.7270128696953\n" +
                    "?IP 27.7135919143365\n" +
                    "?DP 27.6620516972923\n" +
                    "?SV 27.6379281577111\n" +
                    "?EF 27.6232341899635\n" +
                    "?AG 27.5582695172129\n" +
                    "?PZ 27.5476590289484\n" +
                    "?BN 27.4341281630116\n" +
                    "?AE 27.4203421224257\n" +
                    "?OP 27.3993229115845\n" +
                    "?MP 27.3989895400452\n" +
                    "?NX 27.397057850916\n" +
                    "?AY 27.2950965925122\n" +
                    "?CX 27.2149342334805\n" +
                    "?LX 27.0817146718789\n" +
                    "?BD 27.0692287178246\n" +
                    "?GH 26.9593777489337\n" +
                    "?GI 26.925369591012\n" +
                    "?KY 26.8436704685944\n" +
                    "?TX 26.8387627937456\n" +
                    "?FZ 26.8088400135406\n" +
                    "?RX 26.7664308387253\n" +
                    "?RR 26.7046846471894\n" +
                    "?BT 26.693405121889\n" +
                    "?GZ 26.6734564469207\n" +
                    "?CC 26.5819481359719\n" +
                    "?KZ 26.579349292411\n" +
                    "?IK 26.4292974336282\n" +
                    "?IX 26.4151130069813\n" +
                    "?BC 26.4096842410405\n" +
                    "?FN 26.3970708723416\n" +
                    "?BO 26.3941906267994\n" +
                    "?XZ 26.3530438900881\n" +
                    "?LL 26.272585241621\n" +
                    "?FT 26.1446778297018\n" +
                    "?BM 26.1402925942226\n" +
                    "?EI 26.1096122589099\n" +
                    "?KT 26.1091511138196\n" +
                    "?DK 26.1004723432337\n" +
                    "?RW 26.0928007665265\n" +
                    "?DX 26.0621223968572\n" +
                    "?BI 26.0450268563785\n" +
                    "?MX 26.0268069464228\n" +
                    "?NN 26.022064065335\n" +
                    "?OX 26.0182259207982\n" +
                    "?DG 26.012946046087\n" +
                    "?GT 25.9582490753764\n" +
                    "?RU 25.8946867565079\n" +
                    "?BY 25.845941781556\n" +
                    "?HW 25.8444690181023\n" +
                    "?HX 25.8199579128984\n" +
                    "?BH 25.7874131533476\n" +
                    "?PX 25.7403920329511\n" +
                    "?EW 25.7260317500795\n" +
                    "?CU 25.6535917016898\n" +
                    "?LU 25.5864554379568\n" +
                    "?NU 25.5629483059537\n" +
                    "?AF 25.5509125503166\n" +
                    "?KP 25.4896606856424\n" +
                    "?GY 25.462901255717\n" +
                    "?CF 25.4194415215248\n" +
                    "?XY 25.3833255834349\n" +
                    "?KO 25.3824787951427\n" +
                    "?EV 25.3779697411984\n" +
                    "?TT 25.3564853070228\n" +
                    "?DF 25.2898810621999\n" +
                    "?NW 25.2758944709165\n" +
                    "?OY 25.2432957860491\n" +
                    "?DD 25.2382167550174\n" +
                    "?FI 25.2381359784654\n" +
                    "?BK 25.2276065430593\n" +
                    "?GM 25.1434560411594\n" +
                    "?EO 25.1336775873978\n" +
                    "?GO 25.0907261686653\n" +
                    "?KM 25.0811885904155\n" +
                    "?TU 24.8564133829977\n" +
                    "?DW 24.8272201686382\n" +
                    "?AI 24.823562155874\n" +
                    "?MM 24.8181138928542\n" +
                    "?LW 24.6389041235802\n" +
                    "?EE 24.5269368359981\n" +
                    "?IY 24.4879787795613\n" +
                    "?CG 24.4732358562859\n" +
                    "?EJ 24.3900439901554\n" +
                    "?PP 24.3698405831299\n" +
                    "?DU 24.3622970356882\n" +
                    "?FY 24.2752976576331\n" +
                    "?RV 24.2173919408816\n" +
                    "?FH 24.1521862318553\n" +
                    "?FX 24.14458527697\n" +
                    "?FO 24.1229770319798\n" +
                    "?AW 23.9908580112091\n" +
                    "?HH 23.9497531863575\n" +
                    "?MU 23.936018581363\n" +
                    "?FF 23.8536934243755\n" +
                    "?JN 23.7823831012167\n" +
                    "?GP 23.5513146359233\n" +
                    "?WY 23.5432345702449\n" +
                    "?FM 23.4988303486003\n" +
                    "?TW 23.4682590305801\n" +
                    "?AJ 23.4551108993637\n" +
                    "?JR 23.2184596048955\n" +
                    "?AO 23.2012085294853\n" +
                    "?BG 23.1955274944126\n" +
                    "?AV 23.0846652838382\n" +
                    "?LV 23.081768173647\n" +
                    "?WZ 23.0613211192265\n" +
                    "?KW 22.9326888928112\n" +
                    "?BX 22.8896878819566\n" +
                    "?HU 22.7030537567767\n" +
                    "?OW 22.6675084066935\n" +
                    "?PU 22.5450646708869\n" +
                    "?UZ 22.5393167476603\n" +
                    "?FK 22.5108071215663\n" +
                    "?IO 22.3986331757096\n" +
                    "?QS 22.3864815874216\n" +
                    "?JT 22.3172738485079\n" +
                    "?JL 22.2948059315246\n" +
                    "?IV 22.2662060726308\n" +
                    "?KX 22.0985815972647\n" +
                    "?CJ 22.0726389839704\n" +
                    "?FG 22.0464631724945\n" +
                    "?JY 22.0017810239961\n" +
                    "?CW 21.9799058778083\n" +
                    "?NV 21.9783209473877\n" +
                    "?DJ 21.9583498709668\n" +
                    "?BU 21.9257079239283\n" +
                    "?CV 21.9193409483548\n" +
                    "?JK 21.9166435016474\n" +
                    "?JO 21.8167605684134\n" +
                    "?GK 21.7140189126212\n" +
                    "?BB 21.6936019901684\n" +
                    "?MW 21.6100615083946\n" +
                    "?IW 21.5769686572196\n" +
                    "?EU 21.5046109375989\n" +
                    "?AA 21.4977649182231\n" +
                    "?JZ 21.2955357442232\n" +
                    "?HJ 21.2791295606098\n" +
                    "?IJ 21.0667570603468\n" +
                    "?UX 20.9700607315306\n" +
                    "?GX 20.9662720063005\n" +
                    "?GU 20.94729473104\n" +
                    "?KU 20.8779309570116\n" +
                    "?PW 20.862128644615\n" +
                    "?BP 20.8309706117581\n" +
                    "?TV 20.7868987928266\n" +
                    "?DV 20.7692078333431\n" +
                    "?BJ 20.7059333240658\n" +
                    "?GW 20.593895408898\n" +
                    "?BF 20.552305656527\n" +
                    "?JM 20.5201639380815\n" +
                    "?GG 20.5011700180133\n" +
                    "?FU 20.4078368447343\n" +
                    "?FP 20.2465936246805\n" +
                    "?OV 20.2406320662802\n" +
                    "?AU 19.9657740364308\n" +
                    "?GJ 19.8046701488934\n" +
                    "?BW 19.7947943599738\n" +
                    "?VY 19.6998429837268\n" +
                    "?UY 19.6667194939921\n" +
                    "?WX 19.5837111683228\n" +
                    "?YY 19.4687838892809\n" +
                    "?FW 19.3426615626868\n" +
                    "?JP 19.2605281294142\n" +
                    "?AQ 18.9928456506539\n" +
                    "?JX 18.9749622079571\n" +
                    "?QT 18.9136566722773\n" +
                    "?HV 18.6591018501847\n" +
                    "?JU 18.6403464033435\n" +
                    "?VX 18.4374594147638\n" +
                    "?OO 18.4018146848139\n" +
                    "?VZ 18.382265692475\n" +
                    "?OU 18.0777277384123\n" +
                    "?II 17.9593458996982\n" +
                    "?JW 17.9125740651046\n" +
                    "?QR 17.780358847671\n" +
                    "?QZ 17.7685988906716\n" +
                    "?EQ 17.7413713579505\n" +
                    "?MV 17.6644313728583\n" +
                    "?GV 17.6360252406165\n" +
                    "?IQ 17.4443713918107\n" +
                    "?NQ 17.2047545139071\n" +
                    "?IU 16.9600631157775\n" +
                    "ERS 16.7946783993547\n" +
                    "?FJ 16.7310882911559\n" +
                    "?PV 16.6124407735354\n" +
                    "?DQ 16.3913042203339\n" +
                    "?CQ 16.1047940722981\n" +
                    "?LQ 16.0345499639061\n" +
                    "?HQ 15.940762108283\n" +
                    "?KV 15.6101131040054\n" +
                    "?BV 15.5305387155007\n" +
                    "?WW 15.0966336614871\n" +
                    "?VW 15.0780721422879\n" +
                    "?QY 15.0578750984249\n" +
                    "?UV 14.8987839453628\n" +
                    "EST 14.6423102060095\n" +
                    "?KQ 14.3795828823789\n" +
                    "?UW 14.1095349899502\n" +
                    "?FV 14.0676020240394\n" +
                    "?PQ 14.0645694493634\n" +
                    "ESZ 14.0510695429024\n" +
                    "?MQ 14.0110721571271\n" +
                    "ENS 13.7825196743371\n" +
                    "?QX 13.7424665493735\n" +
                    "ELS 13.7128621186231\n" +
                    "?OQ 13.6123294797129\n" +
                    "RST 13.5625704224253\n" +
                    "?JV 13.4174822806782\n" +
                    "ESS 13.3205049687749\n" +
                    "CHS 13.309779585425\n" +
                    "DES 13.2682905541728\n" +
                    "CES 12.9215501681818\n" +
                    "ARS 12.7008432068003\n" +
                    "EHS 12.6965561645155\n" +
                    "?BQ 12.245182731588\n" +
                    "CRS 12.1399166433084\n" +
                    "EPS 12.136449379705\n" +
                    "?VV 12.1255610334981\n" +
                    "HRS 12.0422675962243\n" +
                    "?FQ 12.0094115648332\n" +
                    "EMS 11.9934816743007\n" +
                    "INS 11.9507670232219\n" +
                    "PRS 11.826924354025\n" +
                    "?GQ 11.7065864411386\n" +
                    "ASZ 11.6267503479843\n" +
                    "AST 11.5035713307396\n" +
                    "ISZ 11.4495947052279\n" +
                    "NST 11.4008221114372\n" +
                    "RSS 11.372709793075\n" +
                    "IRS 11.3707387934624\n" +
                    "NRS 11.3687882043246\n" +
                    "HST 11.3121198976654\n" +
                    "RSZ 11.1945499794231\n" +
                    "ALS 11.0552794509703\n" +
                    "ACS 10.9912611824615\n" +
                    "OSZ 10.9798115290073\n" +
                    "ANS 10.9793617123244\n" +
                    "IST 10.9163694449239\n" +
                    "ESX 10.8424181252055\n" +
                    "MRS 10.8291169558555\n" +
                    "AMS 10.6931944913643\n" +
                    "DRS 10.6636946476474\n" +
                    "CKS 10.4825461652873\n" +
                    "EKS 10.47812014602\n" +
                    "CST 10.3842997726146\n" +
                    "CNS 10.3745134558431\n" +
                    "SST 10.3558990422898\n" +
                    "ORS 10.2871948739921\n" +
                    "LRS 10.2730197260931\n" +
                    "ASS 10.2160273381253\n" +
                    "LST 10.1858737330443\n" +
                    "?QW 10.1357707625558\n" +
                    "AHS 10.1335605285295\n" +
                    "CIS 10.1330891155239\n" +
                    "ILS 10.1323505035846\n" +
                    "NSZ 10.1195572692431\n" +
                    "HSS 10.0201942995278\n" +
                    "STZ 10.0157320758577\n" +
                    "SSZ 9.9841292176366\n" +
                    "HSZ 9.9223066708255\n" +
                    "HPS 9.8385805310307\n" +
                    "CLS 9.82006104369435\n" +
                    "NSS 9.73746435082195\n" +
                    "?UU 9.65538807831067\n" +
                    "ISS 9.6395576321129\n" +
                    "IMS 9.63337235493703\n" +
                    "PST 9.59485835190545\n" +
                    "SYZ 9.5277990473534\n" +
                    "LSS 9.44782005948235\n" +
                    "HNS 9.42936390746265\n" +
                    "QSU 9.42224595839923\n" +
                    "CSS 9.3693965506153\n" +
                    "LSZ 9.33239744498275\n" +
                    "KRS 9.30844300671375\n" +
                    "APS 9.24958295321085\n" +
                    "MNS 9.16987673063755\n" +
                    "GNS 9.16447728217851\n" +
                    "?JQ 9.11884015405115\n" +
                    "LPS 9.055886462543\n" +
                    "BES 9.04857687544879\n" +
                    "COS 9.01059340372571\n" +
                    "ERZ 8.98817199205505\n" +
                    "HLS 8.95386893874005\n" +
                    "HIS 8.95290092845297\n" +
                    "LNS 8.95063116185055\n" +
                    "DNS 8.9108104613206\n" +
                    "HKS 8.8928175169982\n" +
                    "PSS 8.8923136757178\n" +
                    "ADS 8.86782372458758\n" +
                    "OST 8.86434053983185\n" +
                    "MST 8.8610299068773\n" +
                    "MSS 8.8283838563117\n" +
                    "DIS 8.79345901587144\n" +
                    "NOS 8.77496490627273\n" +
                    "HMS 8.74674051867535\n" +
                    "IPS 8.67742507243497\n" +
                    "KNS 8.64719845811655\n" +
                    "SXZ 8.63630470680365\n" +
                    "BRS 8.54431259094833\n" +
                    "CTX 8.5266506055004e-05\n" +
                    "KSZ 8.5256747131117\n" +
                    "PSZ 8.46052923654705\n" +
                    "CSZ 8.45798966843195\n" +
                    "MSZ 8.41694449256025\n" +
                    "NPS 8.40554697537993\n" +
                    "LSY 8.29545639281095\n" +
                    "RSY 8.27182540953695\n" +
                    "DER 8.25613092886828\n" +
                    "HOS 8.2295291514067\n" +
                    "SSS 8.2119552814114\n" +
                    "CPS 8.20669707353731\n" +
                    "AES 8.18318163486337\n" +
                    "HSY 8.16333033726175\n" +
                    "DSZ 8.1398048996148\n" +
                    "LOS 8.13904177748872\n" +
                    "ASX 8.1300750046611\n" +
                    "DLS 8.11315177616724\n" +
                    "AKS 8.05250702312401\n" +
                    "MOS 8.03603931312932\n" +
                    "DHS 8.01062882394428\n" +
                    "OSS 8.01054311909548\n" +
                    "LMS 7.99499472012147\n" +
                    "ERT 7.93589419044747\n" +
                    "?QV 7.87269842261429\n" +
                    "BSZ 7.8211850892025\n" +
                    "DST 7.80073450254038\n" +
                    "KSS 7.80021893110465\n" +
                    "KLS 7.7842813313902\n" +
                    "EXZ 7.78116749933445\n" +
                    "EIS 7.7592670827917\n" +
                    "ENZ 7.75225495188868\n" +
                    "DSS 7.73302956287915\n" +
                    "ESY 7.66895977459721\n" +
                    "EJS 7.66610289368075\n" +
                    "CEH 7.65226512699951\n" +
                    "EFS 7.63736079559143\n" +
                    "OPS 7.62413943706405\n" +
                    "CDS 7.61697940984195\n" +
                    "EGS 7.55346574327865\n" +
                    "DEZ 7.54559108123406\n" +
                    "AXZ 7.48355396154829\n" +
                    "PSY 7.46601197966162\n" +
                    "FRS 7.4286869511241\n" +
                    "CMS 7.3995949115469\n" +
                    "CER 7.38466526848438\n" +
                    "IKS 7.34702850781012\n" +
                    "KSY 7.25390872276238\n" +
                    "ISX 7.239832758\n" +
                    "BLS 7.20333377080096\n" +
                    "STY 7.18840606248889\n" +
                    "FSZ 7.10983366653591\n" +
                    "NSY 7.07135234469644\n" +
                    "ABS 7.04860344813094\n" +
                    "GRS 7.03979870762431\n" +
                    "ARZ 7.03909368380762\n" +
                    "DMS 7.03285568256537\n" +
                    "ELZ 6.98723147289745\n" +
                    "DOS 6.96480513044065\n" +
                    "STX 6.9474246432594\n" +
                    "RSX 6.94266312906691\n" +
                    "HSX 6.9277747124273\n" +
                    "MPS 6.87267112721016\n" +
                    "EHZ 6.80856190538899\n" +
                    "SSX 6.79491867739935\n" +
                    "CHK 6.72779894877908\n" +
                    "ESW 6.7235672219594\n" +
                    "CHR 6.72247520308742\n" +
                    "KST 6.7159500517456\n" +
                    "MSY 6.68315670770719\n" +
                    "NSX 6.67738053244963\n" +
                    "CSY 6.67477864036492\n" +
                    "INZ 6.66730903041797\n" +
                    "ETZ 6.58752058915245\n" +
                    "KPS 6.57348461468512\n" +
                    "OSX 6.56885046860288\n" +
                    "PSX 6.56084207284002\n" +
                    "ENR 6.55305949902535\n" +
                    "HSW 6.55291033440066\n" +
                    "ACH 6.51798171802738\n" +
                    "ANZ 6.50884012734354\n" +
                    "DPS 6.45372481163194\n" +
                    "EES 6.43107901647695\n" +
                    "BEZ 6.35654449887683\n" +
                    "RSU 6.35271867581187\n" +
                    "CSX 6.25011142582079\n" +
                    "EHR 6.24014715108721\n" +
                    "EPR 6.21371032698509\n" +
                    "LSX 6.18362587818892\n" +
                    "SSY 6.13396091503784\n" +
                    "EYZ 6.12443047700327\n" +
                    "MSX 6.10916790783806\n" +
                    "ALZ 6.08939816086255\n" +
                    "FLS 6.04311329277548\n" +
                    "SXY 6.040615152134\n" +
                    "ASY 6.03259580147424\n" +
                    "DEN 6.0022476637921\n" +
                    "ORZ 5.97511360174438\n" +
                    "AMZ 5.96975306002673\n" +
                    "OYZ 5.92051624018252\n" +
                    "AJS 5.88995891008947\n" +
                    "ELR 5.87445470429445\n" +
                    "IRZ 5.87121447280336\n" +
                    "AHZ 5.8553529849234\n" +
                    "CEZ 5.84586815510009\n" +
                    "EOS 5.82781679273733\n" +
                    "AGS 5.81494113210157\n" +
                    "EMR 5.81445708979746\n" +
                    "AYZ 5.79252955625657\n" +
                    "GIS 5.77643833168101\n" +
                    "RSW 5.77468664895424\n" +
                    "XYZ 5.77025241895683\n" +
                    "JKS 5.73996311489803\n" +
                    "KOS 5.73610224113154\n" +
                    "OXZ 5.72698186409242\n" +
                    "BST 5.70467387749166\n" +
                    "NOZ 5.69410921370106\n" +
                    "GSZ 5.67340619898566\n" +
                    "EMZ 5.63038506357032\n" +
                    "EFZ 5.62028244493454\n" +
                    "KMS 5.57225284889031\n" +
                    "ACR 5.56960976884993\n" +
                    "DEL 5.52172851286739\n" +
                    "RRS 5.50131323524306\n" +
                    "QUZ 5.49648606863521\n" +
                    "DSY 5.45517157984525\n" +
                    "ENT 5.45329667643239\n" +
                    "CEN 5.44333171714123\n" +
                    "ESV 5.43566267867099\n" +
                    "BSS 5.43465450623752\n" +
                    "CHZ 5.40773094558424\n" +
                    "BIS 5.38661427488596\n" +
                    "DEX 5.38218021149669\n" +
                    "EKZ 5.3599779207594\n" +
                    "STU 5.33685922600466\n" +
                    "CHT 5.32804857382853\n" +
                    "GHS 5.30867671030088\n" +
                    "AEZ 5.30346667906256\n" +
                    "HYZ 5.30120085509839\n" +
                    "ERX 5.2990446094577\n" +
                    "GLS 5.26121556138944\n" +
                    "LYZ 5.25072186917772\n" +
                    "ATZ 5.21353924139146\n" +
                    "ART 5.21245622499451\n" +
                    "ACZ 5.2002398077913\n" +
                    "BNS 5.17245803815319\n" +
                    "IXZ 5.16030763712321\n" +
                    "DSX 5.15364958772115\n" +
                    "CDE 5.15349028699813\n" +
                    "QTU 5.12289944645813\n" +
                    "DKS 5.11021111470879\n" +
                    "CEK 5.10311323082821\n" +
                    "FST 5.04628009383727\n" +
                    "EPZ 5.04543617336321\n" +
                    "KYZ 5.03162777213897\n" +
                    "BOS 5.01669747237829\n" +
                    "GIN 4.99614961980784\n" +
                    "ADZ 4.99488792936095\n" +
                    "NYZ 4.96699084896601\n" +
                    "JRS 4.95411689521617\n" +
                    "ABZ 4.89626133294408\n" +
                    "BHS 4.87780274548776\n" +
                    "SSU 4.87481010480715\n" +
                    "JSZ 4.85837745611884\n" +
                    "CEL 4.84601669677648\n" +
                    "LOZ 4.83284799569169\n" +
                    "AIS 4.83096197201371\n" +
                    "BKS 4.8192909786198\n" +
                    "RYZ 4.8062145382751\n" +
                    "COZ 4.7943053581972\n" +
                    "JNS 4.79231579354674\n" +
                    "NSU 4.79002669332206\n" +
                    "CET 4.76728480888555\n" +
                    "AMR 4.76439362582514\n" +
                    "DOZ 4.72151911167788\n" +
                    "ENX 4.71286962760358\n" +
                    "JSS 4.71202250370188\n" +
                    "JST 4.69036398758688\n" +
                    "CHN 4.68617457358768\n" +
                    "CIN 4.68145259505946\n" +
                    "HJS 4.68140749400792\n" +
                    "CEX 4.68076629802813\n" +
                    "ILZ 4.67866109606678\n" +
                    "FIS 4.67545362957654\n" +
                    "ACL 4.64697611817659\n" +
                    "CHY 4.64136587212916\n" +
                    "ACN 4.63883568699062\n" +
                    "CIZ 4.59935163464448\n" +
                    "EOZ 4.59758245718994\n" +
                    "DET 4.58627157848558\n" +
                    "CSU 4.57001083408815\n" +
                    "EHT 4.56822150469199\n" +
                    "STT 4.56570850428201\n" +
                    "AFS 4.55886359702901\n" +
                    "CHI 4.54865394545607\n" +
                    "HOZ 4.54602286294086\n" +
                    "LSU 4.54516622407096\n" +
                    "GSS 4.54096073228518\n" +
                    "DIZ 4.51766199639048\n" +
                    "FSS 4.51037782198093\n" +
                    "CHO 4.51003143067878\n" +
                    "BDS 4.48892626972555\n" +
                    "EKR 4.43443717207029\n" +
                    "ELT 4.43052343877172\n" +
                    "JOS 4.42667662971837\n" +
                    "FSX 4.41819332136768\n" +
                    "SWZ 4.41040866357869\n" +
                    "ITZ 4.403651730632\n" +
                    "ELX 4.37124115605893\n" +
                    "MYZ 4.3460055024478\n" +
                    "KSX 4.32761382629501\n" +
                    "CKR 4.3139240330643\n" +
                    "GST 4.30872199984124\n" +
                    "BMS 4.29273265605172\n" +
                    "ETX 4.29198290533907\n" +
                    "AKZ 4.27411699310813\n" +
                    "FNS 4.26066655227427\n" +
                    "ACK 4.25612262069302\n" +
                    "FHS 4.24797338527306\n" +
                    "MOZ 4.24733699940978\n" +
                    "OSY 4.20649072506667\n" +
                    "NSW 4.18711365267263\n" +
                    "ADR 4.17606911865483\n" +
                    "BSY 4.17010273936621\n" +
                    "DEH 4.15072064896215\n" +
                    "IMZ 4.12220093683148\n" +
                    "EHX 4.11682583414761\n" +
                    "ANR 4.10024377729708\n" +
                    "AMN 4.08959061903291\n" +
                    "BOZ 4.07896205117316\n" +
                    "ASW 4.0681667504455\n" +
                    "OTZ 4.06404032285243\n" +
                    "BER 4.06154262539569\n" +
                    "HIZ 4.04713155051228\n" +
                    "CKY 4.02619132454312\n" +
                    "HRZ 4.02375010581655\n" +
                    "EMX 4.02051716215323\n" +
                    "EIZ 4.0194849334457\n" +
                    "ANT 4.01547998991872\n" +
                    "CHP 4.00835035075504\n" +
                    "EMN 4.00418954219503\n" +
                    "ACT 3.99717612138396\n" +
                    "KSW 3.98234062301727\n" +
                    "EFR 3.96095724648148\n" +
                    "RTZ 3.92719773364781\n" +
                    "ELN 3.92110110149761\n" +
                    "FYZ 3.90376786113571\n" +
                    "IJS 3.90085140763192\n" +
                    "DEM 3.87480961105621\n" +
                    "ISY 3.86132497370291\n" +
                    "APZ 3.85328677657785\n" +
                    "HHS 3.83808452104594\n" +
                    "INT 3.81928699863601\n" +
                    "JSY 3.81334628480371\n" +
                    "AHR 3.81289378368519\n" +
                    "CHL 3.80804907101918\n" +
                    "EGN 3.75188774848755\n" +
                    "ALR 3.74733012976992\n" +
                    "EPX 3.74360599920983\n" +
                    "MSU 3.72475513455167\n" +
                    "EJZ 3.72411006929174\n" +
                    "CYZ 3.72004855703972\n" +
                    "HXZ 3.69919562765435\n" +
                    "FRZ 3.69149660020905\n" +
                    "SSW 3.69010657254925\n" +
                    "BIZ 3.68642436033953\n" +
                    "BYZ 3.68391759107065\n" +
                    "INR 3.67641460381749\n" +
                    "BCS 3.66479332385865\n" +
                    "NRZ 3.66342263267786\n" +
                    "DYZ 3.63837668064657\n" +
                    "CIR 3.63108900712235\n" +
                    "SUZ 3.62934531749928\n" +
                    "HRT 3.619976581314\n" +
                    "BRZ 3.60789339509198\n" +
                    "STW 3.58775675468639\n" +
                    "PPS 3.55685838932449\n" +
                    "PYZ 3.5559086021429\n" +
                    "TYZ 3.53953640125184\n" +
                    "CCS 3.53843518477555\n" +
                    "FXZ 3.53079955464751\n" +
                    "CRT 3.52370863537918\n" +
                    "JLS 3.51645454457568\n" +
                    "APR 3.51223135448102\n" +
                    "NNS 3.49818490603263\n" +
                    "IYZ 3.47261500461608\n" +
                    "AFZ 3.47147937801157\n" +
                    "KOZ 3.45745290490037\n" +
                    "NXZ 3.45671062581475\n" +
                    "SWY 3.44612959959384\n" +
                    "QRU 3.41760528541578\n" +
                    "ELP 3.413934234124\n" +
                    "COR 3.40238069111215\n" +
                    "HSU 3.39792597742891\n" +
                    "GOS 3.38775495841003\n" +
                    "EHN 3.37638697602164\n" +
                    "FIZ 3.37504038212489\n" +
                    "PSU 3.33706242937007\n" +
                    "AMX 3.33687909132589\n" +
                    "LLS 3.33521338141418\n" +
                    "LSW 3.32669559288914\n" +
                    "DSW 3.32458688686127\n" +
                    "MMS 3.29216042025658\n" +
                    "HTZ 3.29116303671086\n" +
                    "AGN 3.28915693056562\n" +
                    "RSV 3.2705202222076\n" +
                    "EKN 3.25726170278652\n" +
                    "ACX 3.24710603224345\n" +
                    "DSU 3.24566368439005\n" +
                    "EGZ 3.22479779300244\n" +
                    "ERY 3.22095848952662\n" +
                    "AER 3.2083106249802\n" +
                    "EKL 3.20652898594539\n" +
                    "PXY 3.20051961501737\n" +
                    "EXY 3.18017585580529\n" +
                    "QUX 3.17788114478302\n" +
                    "CDH 3.17267993128525\n" +
                    "ANX 3.17144394027978\n" +
                    "DRZ 3.17062449981186\n" +
                    "IRT 3.16519330475924\n" +
                    "ALX 3.16038977614097\n" +
                    "DIN 3.15433302979688\n" +
                    "RXZ 3.14072946000424\n" +
                    "CRZ 3.12050094372263\n" +
                    "CKZ 3.09790091125503\n" +
                    "GNZ 3.09738708529063\n" +
                    "CKN 3.07837457218371\n" +
                    "CKL 3.05381204488793\n" +
                    "CFS 3.04434287617096\n" +
                    "FOS 3.04118450993345\n" +
                    "EHL 3.03817179377455\n" +
                    "ELY 3.0255920307056\n" +
                    "BEL 3.0217168996293\n" +
                    "FKS 3.00808451570142\n" +
                    "OPZ 3.00259529023418\n" +
                    "JSX 3.00029938895291\n" +
                    "EHP 2.99825134063091\n" +
                    "GSY 2.99138811347952\n" +
                    "AQU 2.98851410131007\n" +
                    "CHX 2.98135455583242\n" +
                    "CNR 2.97202919509634\n" +
                    "DNZ 2.96967030001072\n" +
                    "IMN 2.96576315166547\n" +
                    "ALT 2.95834388202918\n" +
                    "EPT 2.94985098501478\n" +
                    "ALN 2.94950347224778\n" +
                    "ADN 2.94280339306433\n" +
                    "GMS 2.93999201737413\n" +
                    "ARX 2.93700374849373\n" +
                    "NRT 2.89612912088974\n" +
                    "FSY 2.88001860838948\n" +
                    "IPZ 2.85855753967433\n" +
                    "DEP 2.85705693816193\n" +
                    "NQU 2.85458662149479\n" +
                    "BSX 2.84328427582938\n" +
                    "IKZ 2.84098962121119\n" +
                    "ALM 2.82729437129045\n" +
                    "AHT 2.8208527246782\n" +
                    "DJS 2.81464478556305\n" +
                    "CEP 2.8127835428362\n" +
                    "KNZ 2.810907705554\n" +
                    "CNO 2.80577181249379\n" +
                    "INX 2.79186900570878\n" +
                    "CNZ 2.78724951877679\n" +
                    "AGZ 2.78094277431454\n" +
                    "CJS 2.77836692047079\n" +
                    "HKY 2.77805745550614\n" +
                    "DFS 2.77003357462827\n" +
                    "EMT 2.76960340065042\n" +
                    "ALY 2.76068678179079\n" +
                    "HKZ 2.75861137129466\n" +
                    "CIK 2.75697132868011\n" +
                    "EEZ 2.75343214488452\n" +
                    "HNZ 2.7429088873636\n" +
                    "ELM 2.74145389245004\n" +
                    "LXY 2.73619184769426\n" +
                    "AIZ 2.71239614902587\n" +
                    "FOZ 2.70330533041479\n" +
                    "MXZ 2.70251904002032\n" +
                    "EHK 2.70182038923477\n" +
                    "CHM 2.6967957085991\n" +
                    "AHX 2.68561167608028\n" +
                    "LXZ 2.67772509297585\n" +
                    "PRZ 2.6769070658189\n" +
                    "ACM 2.66635937874909\n" +
                    "MRZ 2.65258572256509\n" +
                    "EFX 2.6464795390186\n" +
                    "AOZ 2.6336031889461\n" +
                    "BJS 2.61469574216216\n" +
                    "NTZ 2.59790887937464\n" +
                    "DIR 2.59668894136641\n" +
                    "ACD 2.58682297680153\n" +
                    "IOS 2.58188726461609\n" +
                    "EHM 2.57500584981315\n" +
                    "JMS 2.57346620043961\n" +
                    "ESU 2.56364585203011\n" +
                    "DXZ 2.56095932615695\n" +
                    "CIL 2.56068865594302\n" +
                    "JPS 2.55167082083409\n" +
                    "AJZ 2.54768165959432\n" +
                    "ATX 2.54498534949666\n" +
                    "BLZ 2.53873160639189\n" +
                    "KRZ 2.51529708606962\n" +
                    "OSW 2.51074224319449\n" +
                    "DGS 2.50698364474668\n" +
                    "KLY 2.50393918079109\n" +
                    "ILN 2.50284549952812\n" +
                    "HPZ 2.49391358501143\n" +
                    "ADL 2.47792989676954\n" +
                    "HXY 2.47590634740675\n" +
                    "HPY 2.46360606301876\n" +
                    "LRZ 2.45777328547215\n" +
                    "EWZ 2.45497352349979\n" +
                    "EGR 2.45316694610896\n" +
                    "AOS 2.43240123995971\n" +
                    "AXY 2.42534126947911\n" +
                    "JKY 2.41029655841043\n" +
                    "HRY 2.40815312306481\n" +
                    "ENP 2.40628414695957\n" +
                    "JYZ 2.40222002584591\n" +
                    "CIT 2.39178989864063\n" +
                    "GNR 2.37750730074554\n" +
                    "ISW 2.37083429466296\n" +
                    "ABR 2.36197061154512\n" +
                    "PRY 2.35471161271622\n" +
                    "SUX 2.34782756097482\n" +
                    "CLY 2.31610596509565\n" +
                    "AMT 2.31441645132993\n" +
                    "CKO 2.30103039449556\n" +
                    "AHM 2.2955909873137\n" +
                    "AKR 2.29455400170236\n" +
                    "CXZ 2.28110333095623\n" +
                    "DDS 2.265595077576\n" +
                    "CDR 2.24629365707091\n" +
                    "GIZ 2.24479027951998\n" +
                    "TXZ 2.24294798217803\n" +
                    "IMR 2.22955524648314\n" +
                    "PXZ 2.22629894348657\n" +
                    "ASV 2.22150482334925\n" +
                    "EFL 2.21653846397173\n" +
                    "ABL 2.20802872918738\n" +
                    "ALP 2.19848393780203\n" +
                    "CEM 2.19405134718123\n" +
                    "ORT 2.18166205870854\n" +
                    "LPY 2.17520515595202\n" +
                    "FMS 2.17177799089929\n" +
                    "FFS 2.16863289057545\n" +
                    "DQU 2.16002553140404\n" +
                    "ADX 2.1534912292129\n" +
                    "BDE 2.14242294290616\n" +
                    "DNR 2.14123672799353\n" +
                    "PRT 2.13884787839999\n" +
                    "EQU 2.13296075000727\n" +
                    "HPR 2.13173943074547\n" +
                    "DEK 2.12823446201181\n" +
                    "EKX 2.08969025775141\n" +
                    "GYZ 2.08853809981299\n" +
                    "CIX 2.08808327059789\n" +
                    "CNT 2.07965385577408\n" +
                    "KQU 2.07265195216841\n" +
                    "CRY 2.06450353053913\n" +
                    "LRY 2.06350298297945\n" +
                    "ARY 2.04702521418819\n" +
                    "EKY 2.04487534121275\n" +
                    "GPS 2.04112178848841\n" +
                    "KSU 2.03891107367959\n" +
                    "IMX 2.03617665404844\n" +
                    "ADM 2.02812329202388\n" +
                    "LTZ 2.02805244806981\n" +
                    "EHY 2.01314608839254\n" +
                    "ISV 2.00832722061222\n" +
                    "AHL 2.00212694800799\n" +
                    "HLY 1.99645981282406\n" +
                    "AHN 1.9896481593415\n" +
                    "KXZ 1.98490859881304\n" +
                    "ERR 1.96425056710006\n" +
                    "COX 1.96194020159565\n" +
                    "DHZ 1.96190041127478\n" +
                    "DEY 1.94652738560799\n" +
                    "FLZ 1.94248527744283\n" +
                    "BXZ 1.93720363578153\n" +
                    "HMZ 1.93019246656391\n" +
                    "WYZ 1.9277971658809\n" +
                    "MNZ 1.92651545352995\n" +
                    "HLZ 1.91690127937009\n" +
                    "EJK 1.90827293568586\n" +
                    "AHK 1.8977962699686\n" +
                    "MOR 1.89603658216671\n" +
                    "DOR 1.88145961855097\n" +
                    "IKN 1.85840321486591\n" +
                    "AKN 1.83265110507371\n" +
                    "KRY 1.83147951185755\n" +
                    "LNZ 1.81199004488897\n" +
                    "QUY 1.80859638963185\n" +
                    "JOZ 1.80404545591312\n" +
                    "ACP 1.78414598760289\n" +
                    "PSW 1.77979136603275\n" +
                    "ENY 1.77947413395482\n" +
                    "CPR 1.77865205440815\n" +
                    "JSU 1.77769408287488\n" +
                    "AHP 1.77288030066294\n" +
                    "KNY 1.77102595117174\n" +
                    "CDI 1.75919023611717\n" +
                    "APX 1.75843296778102\n" +
                    "BSU 1.74068775356466\n" +
                    "RTY 1.7383084778582\n" +
                    "IOZ 1.73551190537629\n" +
                    "AEX 1.73544315357303\n" +
                    "AKL 1.73250372236409\n" +
                    "EJX 1.72396538827358\n" +
                    "IPR 1.72031217925413\n" +
                    "MXY 1.70254074245414\n" +
                    "NXY 1.69407276558565\n" +
                    "HOR 1.67298375670466\n" +
                    "CLR 1.66522098507629\n" +
                    "ERW 1.66221102229349\n" +
                    "CLO 1.62255406551292\n" +
                    "AEL 1.60679156789116\n" +
                    "ERV 1.60432977479458\n" +
                    "CLZ 1.59340710952499\n" +
                    "DHR 1.5883788348255\n" +
                    "AMY 1.58476285905728\n" +
                    "DLZ 1.57696161096121\n" +
                    "LQU 1.57295595838192\n" +
                    "HTY 1.57019698704058\n" +
                    "EJR 1.56662913821317\n" +
                    "BNZ 1.56046611357906\n" +
                    "MSW 1.55965732458591\n" +
                    "KLZ 1.5580355508677\n" +
                    "NOR 1.5543765495414\n" +
                    "OPR 1.55047707898744\n" +
                    "AAS 1.54902877718279\n" +
                    "EIR 1.52486433358124\n" +
                    "SWX 1.52048263160827\n" +
                    "KXY 1.51779012768355\n" +
                    "CNY 1.48990758494207\n" +
                    "CEY 1.46900216471739\n" +
                    "ACY 1.46122874894685\n" +
                    "EPY 1.45755130405329\n" +
                    "ACE 1.45540232758338\n" +
                    "COT 1.44975928288723\n" +
                    "HIR 1.43869897085003\n" +
                    "OXY 1.43449287033225\n" +
                    "ILR 1.43034599426092\n" +
                    "ADH 1.41747900106267\n" +
                    "ADE 1.38418858246672\n" +
                    "FHZ 1.36612281270718\n" +
                    "HQU 1.35529355249997\n" +
                    "DRT 1.34860570793529\n" +
                    "AHY 1.32948659439934\n" +
                    "LNY 1.32690876446416\n" +
                    "CXY 1.3163958734829\n" +
                    "DEJ 1.31396353481332\n" +
                    "HMY 1.30552741001327\n" +
                    "DLY 1.30047321660302\n" +
                    "DRY 1.29938576659485\n" +
                    "HWZ 1.29712270179501\n" +
                    "MRY 1.26081296407924\n" +
                    "APT 1.2285978371437\n" +
                    "NRY 1.21844785513558\n" +
                    "LTY 1.21341853314831\n" +
                    "EIN 1.21084563195588\n" +
                    "MNO 1.21060088999016\n" +
                    "HNT 1.20337857531495\n" +
                    "HIN 1.19619429687316\n" +
                    "DIL 1.18590934172319\n" +
                    "ANY 1.18429240011977\n" +
                    "NOX 1.17746648674848\n" +
                    "HMR 1.17455010383178\n" +
                    "DEF 1.16859387493685\n" +
                    "BEX 1.16617741985754\n" +
                    "AET 1.1595602793445\n" +
                    "MNR 1.14685604330169\n" +
                    "DEW 1.14351397961228\n" +
                    "JXZ 1.14319332469879\n" +
                    "EHW 1.14007292373016\n" +
                    "BLY 1.13998848048273\n" +
                    "CDN 1.13072113657572\n" +
                    "EMY 1.12798478263106\n" +
                    "CGS 1.12796913537445\n" +
                    "ILT 1.12396810909335\n" +
                    "AEN 1.12390385906241\n" +
                    "ANP 1.12049287024076\n" +
                    "ADT 1.1167799357041\n" +
                    "MOX 1.11520504601184\n" +
                    "RXY 1.11356095564188\n" +
                    "HNR 1.11082577433527\n" +
                    "FNZ 1.10707971478706\n" +
                    "GOZ 1.10345962880392\n" +
                    "DIX 1.10338035761986\n" +
                    "BKZ 1.09982387955618\n" +
                    "AKY 1.08766379936967\n" +
                    "HOX 1.08215652732218\n" +
                    "LMY 1.07811977333557\n" +
                    "ILX 1.07672919809849\n" +
                    "HNY 1.0711618537413\n" +
                    "HWY 1.06970109341979\n" +
                    "CKP 1.05789749277431\n" +
                    "PTZ 1.05403933642951\n" +
                    "HKR 1.0489040048699\n" +
                    "KPY 1.03037574551625\n" +
                    "EKP 1.0261302315296\n" +
                    "CIM 1.02425913194318\n" +
                    "GNY 1.01781632696371\n" +
                    "LPZ 1.01409981964966\n" +
                    "CLN 1.00928257995835\n" +
                    "EER 0.995966735367637\n" +
                    "MRT 0.99525767021946\n" +
                    "JXY 0.993535819818865\n" +
                    "CDK 0.990377124564675\n" +
                    "CTZ 0.976564377374655\n" +
                    "AFX 0.97468163776463\n" +
                    "FXY 0.9614103388039\n" +
                    "OWZ 0.961245700642455\n" +
                    "GLN 0.960228670018038\n" +
                    "ETY 0.956864923824765\n" +
                    "EMP 0.949255315291675\n" +
                    "LSV 0.947037126030815\n" +
                    "NOT 0.938876866466214\n" +
                    "CSW 0.932465218495515\n" +
                    "JSW 0.929045034126825\n" +
                    "CQU 0.92564615561717\n" +
                    "LRT 0.913420747847835\n" +
                    "HIT 0.909352387731615\n" +
                    "KNR 0.90616525297063\n" +
                    "AGR 0.895579210519044\n" +
                    "DNO 0.88739420454249\n" +
                    "CKX 0.882545244310755\n" +
                    "CDO 0.880578272966025\n" +
                    "AWZ 0.864652784694185\n" +
                    "CMR 0.86243814022125\n" +
                    "IRX 0.856457422630715\n" +
                    "DOX 0.85098132842671\n" +
                    "CDZ 0.845197273371075\n" +
                    "ORX 0.834001299133005\n" +
                    "DDE 0.820282186000393\n" +
                    "INP 0.819964260588888\n" +
                    "CKT 0.80649446140653\n" +
                    "GKS 0.805554041160745\n" +
                    "EKT 0.80543060523135\n" +
                    "AEM 0.80292326236819\n" +
                    "EJN 0.793767352561735\n" +
                    "AFR 0.79324937939703\n" +
                    "MNY 0.78749141853901\n" +
                    "HPX 0.77466006411759\n" +
                    "BHZ 0.75714338865768\n" +
                    "AJX 0.75452838321908\n" +
                    "FIX 0.75028081170105\n" +
                    "DXY 0.73644548007201\n" +
                    "AJK 0.7355466699888\n" +
                    "CRX 0.728163302286165\n" +
                    "DPR 0.727838058676305\n" +
                    "DMR 0.72120336593159\n" +
                    "FLY 0.71585260233171\n" +
                    "ITX 0.71215651785491\n" +
                    "CPY 0.710725817951885\n" +
                    "GJS 0.70295647752277\n" +
                    "EGL 0.6958344572995\n" +
                    "HOT 0.695351222899065\n" +
                    "CJK 0.68611825262221\n" +
                    "DLR 0.665994911915855\n" +
                    "BDZ 0.66003056035593\n" +
                    "JKZ 0.65787137909084\n" +
                    "SSV 0.647888963414585\n" +
                    "LPR 0.639653460290925\n" +
                    "NPZ 0.630955814962595\n" +
                    "DTZ 0.62313527991338\n" +
                    "DMZ 0.595159882058105\n" +
                    "GRZ 0.577425181690975\n" +
                    "ILM 0.570236458941423\n" +
                    "CIP 0.5699038307807\n" +
                    "FTZ 0.565682852592875\n" +
                    "HPT 0.554490881222685\n" +
                    "APY 0.55426064242297\n" +
                    "BEN 0.54838328366978\n" +
                    "GSX 0.53927666990447\n" +
                    "NTY 0.532556668337775\n" +
                    "TXY 0.530466419407075\n" +
                    "GHN 0.51763297475003\n" +
                    "BTZ 0.516359656792605\n" +
                    "OPX 0.51355001471485\n" +
                    "LMZ 0.507495973393055\n" +
                    "MTZ 0.50370158372331\n" +
                    "DFZ 0.500129283182735\n" +
                    "IKL 0.500075449238349\n" +
                    "CNX 0.493113413081035\n" +
                    "HKN 0.49219078769308\n" +
                    "IPX 0.485589690534165\n" +
                    "CCH 0.473074958440562\n" +
                    "IJZ 0.470788218074545\n" +
                    "AKX 0.46083057001264\n" +
                    "DHY 0.458547516371405\n" +
                    "BGS 0.454182170017835\n" +
                    "CLT 0.439295258145425\n" +
                    "DEI 0.438780046423501\n" +
                    "BET 0.425126049914456\n" +
                    "NPR 0.42391118545774\n" +
                    "PRX 0.41725243181127\n" +
                    "GNO 0.399392456161727\n" +
                    "BEK 0.39725521514448\n" +
                    "ATY 0.397250727216205\n" +
                    "BMZ 0.390453991350665\n" +
                    "IKR 0.390331274006998\n" +
                    "DIM 0.389314354442918\n" +
                    "HOP 0.386346013622714\n" +
                    "PQU 0.379896834404185\n" +
                    "HRX 0.36896655836027\n" +
                    "AFL 0.349624832589672\n" +
                    "LOX 0.33970126294911\n" +
                    "LOR 0.325496002634177\n" +
                    "MPR 0.310639227390704\n" +
                    "CMO 0.307435785531865\n" +
                    "RTX 0.29875539708943\n" +
                    "AMP 0.295401184576489\n" +
                    "AJN 0.293480730295655\n" +
                    "EOR 0.290237440395517\n" +
                    "CPZ 0.274311388255645\n" +
                    "ILP 0.269126369243601\n" +
                    "EJY 0.265636141856882\n" +
                    "BPS 0.264127844466555\n" +
                    "OTX 0.25637281719069\n" +
                    "IWZ 0.256238114494985\n" +
                    "AJR 0.256044327029531\n" +
                    "CTY 0.24159221474921\n" +
                    "DKZ 0.234609338828135\n" +
                    "IMT 0.23356115286449\n" +
                    "CLX 0.23305845547347\n" +
                    "DNY 0.222362988197435\n" +
                    "FKZ 0.20800091807646\n" +
                    "HIX 0.199555514697935\n" +
                    "HTX 0.194320538309155\n" +
                    "ABX 0.19285632631908\n" +
                    "FLX 0.18798671528994\n" +
                    "EFT 0.186657885198971\n" +
                    "MRX 0.17009150870612\n" +
                    "AGL 0.168836297875145\n" +
                    "BRY 0.15464330662752\n" +
                    "EJT 0.146107075899782\n" +
                    "EIX 0.142926531658605\n" +
                    "PTY 0.140636334674882\n" +
                    "MNX 0.13161445068673\n" +
                    "HMO 0.127585288767218\n" +
                    "ADY 0.126757549198057\n" +
                    "GNT 0.110524087029945\n" +
                    "DIT 0.0962171530835478\n" +
                    "JQU 0.09555724222492\n" +
                    "FRY 0.0749824590317765\n" +
                    "HLT 0.074677043495575\n" +
                    "MPY 0.0689745273472825\n" +
                    "FPS 0.0591865251909149\n" +
                    "LOY 0.056275206308517\n" +
                    "COP 0.046567981857055\n" +
                    "FSU 0.0439883583990895\n" +
                    "CDL 0.04230789497135\n" +
                    "AHJ 0.0420842484645175\n" +
                    "DEG 0.041794661612946\n" +
                    "DGN 0.041096677289052\n" +
                    "NPY 0.0395151953241699\n" +
                    "EFN 0.0317373742412925\n" +
                    "DNT 0.0226751681792799\n" +
                    "KWY 0.021152814347655\n" +
                    "NTX 0.0132820514924799\n" +
                    "MNT 0.00502890805601947\n" +
                    "CEI 0.00116757524561151\n" +
                    "HOY -0.00132804607254444\n" +
                    "MQU -0.00736678783309003\n" +
                    "FIR -0.0095181374098075\n" +
                    "ILY -0.011386785033652\n" +
                    "AKM -0.0138421193202\n" +
                    "CMZ -0.014046007545105\n" +
                    "EKM -0.0216177711542801\n" +
                    "DRX -0.05532931428441\n" +
                    "NSV -0.0574373280909649\n" +
                    "HNO -0.0611239760222694\n" +
                    "CLP -0.063979019639102\n" +
                    "ABM -0.088350179862619\n" +
                    "BBS -0.0901181141037951\n" +
                    "HRW -0.0906442254939326\n" +
                    "BDR -0.0911308882638154\n" +
                    "AJY -0.103066620726787\n" +
                    "EJL -0.104953584749425\n" +
                    "BOR -0.110736692450976\n" +
                    "BLR -0.111384417150257\n" +
                    "HMX -0.112127443696795\n" +
                    "KPZ -0.12167282191348\n" +
                    "DLO -0.128837315917485\n" +
                    "HLR -0.13317153335568\n" +
                    "DHO -0.146808960819719\n" +
                    "EHJ -0.1480133581755\n" +
                    "CDY -0.151371310908237\n" +
                    "HIK -0.155792723812989\n" +
                    "HNX -0.16756724271135\n" +
                    "DKY -0.169792047528047\n" +
                    "FRX -0.170349065320245\n" +
                    "EIL -0.17500180479214\n" +
                    "KLN -0.176929323621438\n" +
                    "CMN -0.18266319219358\n" +
                    "GSU -0.182680126600882\n" +
                    "FRT -0.183911904134766\n" +
                    "STV -0.186208139470053\n" +
                    "HKW -0.195033836979805\n" +
                    "DMN -0.196097392397196\n" +
                    "DNX -0.19733745385302\n" +
                    "FOX -0.20268980233881\n" +
                    "AEH -0.203722198917119\n" +
                    "GHZ -0.20453322209701\n" +
                    "ADP -0.210175641341095\n" +
                    "COY -0.21311931172943\n" +
                    "DMO -0.219015904530713\n" +
                    "LNT -0.223169032464299\n" +
                    "KOR -0.227528574038522\n" +
                    "FLR -0.22832493918734\n" +
                    "ABK -0.229237536760997\n" +
                    "BEM -0.230839861015861\n" +
                    "HKO -0.239614893668739\n" +
                    "HLP -0.25167127906263\n" +
                    "HJZ -0.26621952637311\n" +
                    "KTZ -0.270298153357585\n" +
                    "DEE -0.272037685912768\n" +
                    "HIM -0.272921509139529\n" +
                    "LPX -0.273970917200385\n" +
                    "ASU -0.27595542234614\n" +
                    "GIR -0.276282674499946\n" +
                    "DLN -0.277189825549463\n" +
                    "AAZ -0.279967495188233\n" +
                    "DPZ -0.291031281823605\n" +
                    "CHH -0.297398254472145\n" +
                    "IXY -0.301453553238245\n" +
                    "HMN -0.302440441831305\n" +
                    "DHN -0.30401285077031\n" +
                    "ABN -0.305653121098118\n" +
                    "WXZ -0.30846898684456\n" +
                    "KPR -0.30851838211869\n" +
                    "HIL -0.319154461475278\n" +
                    "AIN -0.32499766921012\n" +
                    "JKO -0.327144659484361\n" +
                    "CMY -0.342310012790427\n" +
                    "BKY -0.34653914436453\n" +
                    "IPT -0.349249844197917\n" +
                    "GLZ -0.35823521028025\n" +
                    "BEY -0.361450621568211\n" +
                    "EEX -0.366334971902309\n" +
                    "CHW -0.371986123592604\n" +
                    "INY -0.373805211212061\n" +
                    "ORY -0.374513158466482\n" +
                    "EKW -0.382791231359023\n" +
                    "ABD -0.383394728496555\n" +
                    "HKL -0.384525372966285\n" +
                    "AKP -0.391735767477263\n" +
                    "LOP -0.395171247162062\n" +
                    "JNZ -0.39657248468876\n" +
                    "LNO -0.403604207086272\n" +
                    "KMZ -0.40817712929704\n" +
                    "FIL -0.416358024690607\n" +
                    "CKM -0.421080367243336\n" +
                    "HIP -0.437650057479247\n" +
                    "BEH -0.441327968215156\n" +
                    "EOX -0.442056386997381\n" +
                    "LOT -0.444276571486937\n" +
                    "HMT -0.448337917017281\n" +
                    "FGS -0.450181246264445\n" +
                    "ABT -0.459225946120802\n" +
                    "DHI -0.475703806292122\n" +
                    "KMY -0.47702686122928\n" +
                    "BIL -0.481297897762956\n" +
                    "HKX -0.487824540992195\n" +
                    "BJZ -0.48857653937867\n" +
                    "HKP -0.49008266415215\n" +
                    "BCK -0.494327059740486\n" +
                    "EWX -0.502063270308585\n" +
                    "EIT -0.505211067257365\n" +
                    "MOY -0.505594758691773\n" +
                    "AQS -0.508786323036041\n" +
                    "HLX -0.509954692163045\n" +
                    "CSV -0.511984937365823\n" +
                    "KOY -0.516380724277311\n" +
                    "JKN -0.517696741992415\n" +
                    "BIR -0.519031500019104\n" +
                    "NRX -0.52336112264798\n" +
                    "SUY -0.523587297011983\n" +
                    "AKT -0.529027725117293\n" +
                    "ENW -0.53113150165641\n" +
                    "RWZ -0.53416552262462\n" +
                    "CMX -0.538232498710575\n" +
                    "KLR -0.539665995164222\n" +
                    "HLO -0.569755412289747\n" +
                    "JNY -0.570875549976306\n" +
                    "KNO -0.600883549172545\n" +
                    "JOX -0.6083958167922\n" +
                    "EGH -0.60890714975214\n" +
                    "DMY -0.61342084428605\n" +
                    "CCE -0.624655380292654\n" +
                    "BXY -0.629966537398665\n" +
                    "GIL -0.631007491223415\n" +
                    "BCZ -0.644871504253159\n" +
                    "HNP -0.645333085446225\n" +
                    "DHT -0.651290115989616\n" +
                    "MPZ -0.653661256004416\n" +
                    "CEO -0.664274321118145\n" +
                    "LMR -0.669600408053388\n" +
                    "KWZ -0.67626540414957\n" +
                    "LNR -0.67683742921657\n" +
                    "BRT -0.679045458016797\n" +
                    "CEJ -0.680225888708628\n" +
                    "CPT -0.690555370211716\n" +
                    "FQU -0.69151403354735\n" +
                    "BEJ -0.691520768832962\n" +
                    "FOR -0.695587809038511\n" +
                    "OSV -0.696326205534743\n" +
                    "AHW -0.701305005264026\n" +
                    "GMN -0.704134477605199\n" +
                    "BCE -0.704251154478534\n" +
                    "BSW -0.708908527113634\n" +
                    "FJS -0.709161208022635\n" +
                    "JRZ -0.71672319307668\n" +
                    "WXY -0.72289574767899\n" +
                    "FFZ -0.7243323517929\n" +
                    "AJL -0.724614309621511\n" +
                    "OPY -0.724667352136317\n" +
                    "LMO -0.727620276500214\n" +
                    "MOT -0.728419461435621\n" +
                    "BKR -0.732048193215415\n" +
                    "OPT -0.73250332191876\n" +
                    "ABY -0.733370855382067\n" +
                    "IJN -0.734376800956408\n" +
                    "GLY -0.739622272556943\n" +
                    "HKT -0.745048218671309\n" +
                    "KRT -0.751104630731577\n" +
                    "DKR -0.757682446735664\n" +
                    "ADK -0.762587079005542\n" +
                    "LPT -0.77983531453625\n" +
                    "CEF -0.794670785185809\n" +
                    "DHX -0.797563347126505\n" +
                    "ETT -0.797933037272428\n" +
                    "ACI -0.810034713649695\n" +
                    "ABC -0.821521810340391\n" +
                    "HJY -0.82538084011441\n" +
                    "ACC -0.825483148104096\n" +
                    "NPT -0.830183591540678\n" +
                    "BCH -0.832585695567617\n" +
                    "CFZ -0.833907324037866\n" +
                    "HJK -0.83395968445057\n" +
                    "GSW -0.836164003941769\n" +
                    "CPX -0.84649673405723\n" +
                    "ARR -0.848831833273685\n" +
                    "DEO -0.85012387081308\n" +
                    "ELV -0.853564755571825\n" +
                    "NOP -0.856332681809343\n" +
                    "FSW -0.856389498036734\n" +
                    "NOY -0.858457310816949\n" +
                    "CNP -0.869230381199331\n" +
                    "BOX -0.873311328079046\n" +
                    "ARW -0.873465674146857\n" +
                    "AJM -0.882784147166449\n" +
                    "BFS -0.886436190075905\n" +
                    "IIS -0.892912581396891\n" +
                    "ENN -0.894960595025168\n" +
                    "ADJ -0.89825806010659\n" +
                    "LWY -0.90220999861272\n" +
                    "CDX -0.903356548292935\n" +
                    "BLO -0.90716701442578\n" +
                    "ACJ -0.918146016325429\n" +
                    "DPY -0.921266540893403\n" +
                    "AJT -0.924603415173572\n" +
                    "ABJ -0.928915190602163\n" +
                    "IQU -0.92961587117558\n" +
                    "BKL -0.93294113918665\n" +
                    "EVX -0.944650995347029\n" +
                    "CDT -0.948478965276774\n" +
                    "MTY -0.954239533471111\n" +
                    "ELL -0.955600797069302\n" +
                    "OOZ -0.96515547227578\n" +
                    "DKN -0.984134559280666\n" +
                    "RWY -0.990644308962895\n" +
                    "RRZ -0.992907326203055\n" +
                    "JLY -0.996019196993107\n" +
                    "JOY -1.00496589748502\n" +
                    "CIY -1.00506145871134\n" +
                    "KTY -1.00855319542386\n" +
                    "SVZ -1.02134564642659\n" +
                    "LNX -1.02193571011453\n" +
                    "FMZ -1.03680668696918\n" +
                    "AIR -1.04362579535759\n" +
                    "HMP -1.04423965341762\n" +
                    "UXZ -1.0455228948793\n" +
                    "JKR -1.04581996944557\n" +
                    "DFR -1.04583080174721\n" +
                    "AEP -1.0482196472524\n" +
                    "EWY -1.05776139744375\n" +
                    "QSZ -1.06179885472037\n" +
                    "CHU -1.06709152700633\n" +
                    "OOS -1.07405341782455\n" +
                    "DOT -1.07746367550853\n" +
                    "ELW -1.07820870612144\n" +
                    "JRY -1.07974690034795\n" +
                    "JKX -1.08315974937463\n" +
                    "DMX -1.084406034705\n" +
                    "FKY -1.08670243955219\n" +
                    "IKX -1.09009120397825\n" +
                    "FIN -1.10183004228149\n" +
                    "EQS -1.10396823252196\n" +
                    "LTX -1.10783455290531\n" +
                    "SYY -1.10849567227289\n" +
                    "GXZ -1.11220704822893\n" +
                    "CFH -1.11376624629062\n" +
                    "AWX -1.13006936266171\n" +
                    "KNT -1.14077859219271\n" +
                    "EFH -1.14607340990735\n" +
                    "CHJ -1.14744552323395\n" +
                    "SVX -1.15847210093189\n" +
                    "HSV -1.16518870534691\n" +
                    "HHZ -1.16734168972501\n" +
                    "AIX -1.16769689918611\n" +
                    "NWZ -1.16886284272452\n" +
                    "BQU -1.16999953658876\n" +
                    "QST -1.1714870312271\n" +
                    "EFY -1.1803190882196\n" +
                    "IMP -1.18390266963196\n" +
                    "JNO -1.18709768172613\n" +
                    "EFK -1.19376990148313\n" +
                    "KNX -1.19823891004375\n" +
                    "EIM -1.20277268387718\n" +
                    "KOX -1.2048867372569\n" +
                    "LRX -1.20999081487237\n" +
                    "NPX -1.2113007602771\n" +
                    "FNX -1.2314299341698\n" +
                    "DHL -1.23545026607312\n" +
                    "AGH -1.23578564090983\n" +
                    "EEN -1.23843629538674\n" +
                    "PTX -1.25558499076791\n" +
                    "AQZ -1.2625097639005\n" +
                    "LMX -1.27358318203824\n" +
                    "IRY -1.28019422824379\n" +
                    "ABH -1.28455870527944\n" +
                    "EGT -1.32379828712339\n" +
                    "DLX -1.32611092812141\n" +
                    "KLO -1.32709789147828\n" +
                    "AIM -1.32825384381848\n" +
                    "LMN -1.34236671091733\n" +
                    "EPP -1.34389548961503\n" +
                    "IJX -1.3505219486754\n" +
                    "GRY -1.35234911322317\n" +
                    "CEE -1.35962953151436\n" +
                    "AIL -1.36626190909918\n" +
                    "DWZ -1.37303626346582\n" +
                    "JNX -1.38164492805429\n" +
                    "BIN -1.38530780642333\n" +
                    "AEK -1.38932025544094\n" +
                    "EMM -1.39534554696419\n" +
                    "FNR -1.39553713383876\n" +
                    "DIP -1.40517644756797\n" +
                    "BHR -1.40637226151144\n" +
                    "CLM -1.41077777293995\n" +
                    "SVY -1.41150364290501\n" +
                    "MTX -1.4143528625993\n" +
                    "EVZ -1.41499754390714\n" +
                    "KMR -1.42358047809321\n" +
                    "EEL -1.43210687805267\n" +
                    "DEV -1.43563475265966\n" +
                    "MPX -1.43686741829496\n" +
                    "EHH -1.45052758427437\n" +
                    "DHP -1.45309147299878\n" +
                    "IKY -1.45889581944996\n" +
                    "ENO -1.4591835732383\n" +
                    "DHK -1.4639402939278\n" +
                    "GHR -1.47514716930691\n" +
                    "BDL -1.47597390846502\n" +
                    "DSV -1.49404969628178\n" +
                    "BNR -1.49658515561137\n" +
                    "KLX -1.49721203082337\n" +
                    "FHX -1.49963423928759\n" +
                    "KLP -1.51265553520857\n" +
                    "AKW -1.5268875481445\n" +
                    "DOY -1.52962663511389\n" +
                    "IJK -1.53384418799852\n" +
                    "AGM -1.5430334611354\n" +
                    "CCK -1.54331581534888\n" +
                    "CFR -1.54777863724739\n" +
                    "LNP -1.54783360442081\n" +
                    "BCR -1.54901024267255\n" +
                    "DJZ -1.57524473307902\n" +
                    "HLN -1.57538957402226\n" +
                    "IQS -1.57683716899824\n" +
                    "CRU -1.57729485444148\n" +
                    "HTW -1.58023513501258\n" +
                    "GHY -1.58153616654743\n" +
                    "EET -1.58587798043661\n" +
                    "EGX -1.58672960085262\n" +
                    "DLT -1.5914172686139\n" +
                    "BMR -1.59202208679192\n" +
                    "LWZ -1.59453456534157\n" +
                    "DOP -1.60012318919892\n" +
                    "DTY -1.61602877109454\n" +
                    "DHM -1.62930768906276\n" +
                    "CGN -1.62991799319296\n" +
                    "FLO -1.63163006452405\n" +
                    "YYZ -1.6324906623938\n" +
                    "NWY -1.63751333409993\n" +
                    "IMY -1.63796047375296\n" +
                    "JLZ -1.64161131371464\n" +
                    "DLP -1.65023319245965\n" +
                    "AFN -1.65523857322017\n" +
                    "DFX -1.66751303597009\n" +
                    "AMM -1.67143802032042\n" +
                    "BFZ -1.67153783394258\n" +
                    "EJM -1.67463902223908\n" +
                    "FHR -1.70226277702501\n" +
                    "DPX -1.702637440171\n" +
                    "KNP -1.7046071703984\n" +
                    "AWY -1.70710408219318\n" +
                    "HJX -1.7084310524432\n" +
                    "JTY -1.71130668704538\n" +
                    "EJP -1.71461602549105\n" +
                    "DWY -1.71898001290169\n" +
                    "GNX -1.72670794573648\n" +
                    "DHW -1.72764609124776\n" +
                    "FTX -1.72976439691305\n" +
                    "DTX -1.7305426086662\n" +
                    "HOW -1.73448967020217\n" +
                    "JTZ -1.73572915396235\n" +
                    "DRW -1.74841415736992\n" +
                    "JOR -1.75302050309731\n" +
                    "ETW -1.75361748573319\n" +
                    "HLM -1.75530602785461\n" +
                    "AFT -1.76044513351121\n" +
                    "ELO -1.76264830224426\n" +
                    "FKR -1.76863711104043\n" +
                    "HWX -1.7713399067324\n" +
                    "DIK -1.77551221455386\n" +
                    "RTU -1.77937040976253\n" +
                    "ARV -1.8079880875417\n" +
                    "MOP -1.81210344063819\n" +
                    "DLM -1.82859289583054\n" +
                    "DGZ -1.83141787065635\n" +
                    "KRX -1.84737935409495\n" +
                    "HKM -1.85441176328436\n" +
                    "NNZ -1.86298025929672\n" +
                    "FKL -1.8738578624199\n" +
                    "EGY -1.89483748235018\n" +
                    "CMT -1.91250965478687\n" +
                    "FLT -1.91903185838092\n" +
                    "OTY -1.92876177779465\n" +
                    "GKN -1.92917904533219\n" +
                    "BHY -1.93468214092962\n" +
                    "KMN -1.9403990376239\n" +
                    "RUZ -1.94529578090174\n" +
                    "ABE -1.94578503263112\n" +
                    "EFF -1.95173693719018\n" +
                    "AGT -1.9563713661361\n" +
                    "IKP -1.95681441974648\n" +
                    "KRW -1.96254484076248\n" +
                    "PPZ -1.9637471309655\n" +
                    "ENV -1.96513532236969\n" +
                    "OSU -1.96860766635458\n" +
                    "HIY -1.97331710092212\n" +
                    "ACF -1.97925931159309\n" +
                    "CKW -1.98383372208778\n" +
                    "IPY -1.98461220390587\n" +
                    "BNY -1.98599427401707\n" +
                    "EFM -2.00678296218995\n" +
                    "CFK -2.01087403773371\n" +
                    "DNP -2.0122973603668\n" +
                    "CKU -2.02101503633767\n" +
                    "EOT -2.02380729073723\n" +
                    "BDY -2.02876307940574\n" +
                    "HNW -2.03301285483844\n" +
                    "DJY -2.04257310967967\n" +
                    "ALL -2.04299967198688\n" +
                    "IKT -2.04331604584004\n" +
                    "EMO -2.0588650486923\n" +
                    "EJW -2.07038522785364\n" +
                    "IKM -2.08159803622961\n" +
                    "INN -2.0917067004096\n" +
                    "AGY -2.0950359880141\n" +
                    "DIY -2.09518792266956\n" +
                    "GOR -2.09653343184392\n" +
                    "EGM -2.11156400548585\n" +
                    "BRX -2.11318083313873\n" +
                    "GHI -2.12506741401652\n" +
                    "BMY -2.12855404612853\n" +
                    "EEH -2.13682814932828\n" +
                    "FHY -2.13851961594353\n" +
                    "CCI -2.14333279485823\n" +
                    "AIT -2.14724638112052\n" +
                    "HJO -2.14756027311789\n" +
                    "AJP -2.15861634223929\n" +
                    "CDM -2.16658179546284\n" +
                    "GGS -2.16764354524729\n" +
                    "DDZ -2.17702554042\n" +
                    "AOX -2.1890481169213\n" +
                    "ANW -2.1935171596319\n" +
                    "KNW -2.19406362716646\n" +
                    "ITY -2.19493823342852\n" +
                    "BLX -2.19504026102516\n" +
                    "LLZ -2.20128677647346\n" +
                    "ANN -2.20294597354337\n" +
                    "BJY -2.21357987729262\n" +
                    "DKL -2.22170989747247\n" +
                    "KUZ -2.22379951804522\n" +
                    "GTZ -2.22444664644563\n" +
                    "CGH -2.22926483519763\n" +
                    "BIX -2.23942940725406\n" +
                    "ACO -2.24041294505801\n" +
                    "KPX -2.24046637775385\n" +
                    "DFL -2.24057220550455\n" +
                    "FGZ -2.24274605744739\n" +
                    "BLT -2.25330012950984\n" +
                    "BDO -2.25481552404671\n" +
                    "EHO -2.27118079884777\n" +
                    "BOY -2.28220748254313\n" +
                    "JMY -2.28331493960235\n" +
                    "ADG -2.28650990291448\n" +
                    "DGR -2.28972886786442\n" +
                    "BDI -2.29306211999567\n" +
                    "ADW -2.29826990320448\n" +
                    "CJO -2.29925593851894\n" +
                    "EHI -2.30129474098902\n" +
                    "AEJ -2.30144712214359\n" +
                    "FMR -2.30464871274466\n" +
                    "GRT -2.31696501671246\n" +
                    "BNO -2.31835930333571\n" +
                    "KLT -2.32041786333497\n" +
                    "AHH -2.32752425302669\n" +
                    "JPY -2.32885812251596\n" +
                    "FNY -2.3319580322347\n" +
                    "DJO -2.33218966597243\n" +
                    "CCR -2.33608304783586\n" +
                    "NUZ -2.33676355709995\n" +
                    "LMT -2.35423144867758\n" +
                    "LMP -2.37119267924237\n" +
                    "AGX -2.37835302723399\n" +
                    "DRU -2.38105032263095\n" +
                    "AFY -2.38280494930248\n" +
                    "GJN -2.38337657115463\n" +
                    "FLN -2.39362081817798\n" +
                    "BKO -2.39857763348529\n" +
                    "BMO -2.41174695308417\n" +
                    "CEV -2.41656340594045\n" +
                    "ATT -2.43607865757496\n" +
                    "ORW -2.43617352683362\n" +
                    "FKX -2.43668552345413\n" +
                    "LUZ -2.43695303927477\n" +
                    "FUZ -2.43857065611681\n" +
                    "GLR -2.4399460476015\n" +
                    "CFX -2.44010776235007\n" +
                    "CDP -2.44432966029916\n" +
                    "MMZ -2.4453212740754\n" +
                    "CMP -2.44780038560821\n" +
                    "BKN -2.44900248054992\n" +
                    "JKL -2.45337286757807\n" +
                    "BJK -2.45879098571895\n" +
                    "KOP -2.45951721613794\n" +
                    "CRR -2.45966312032045\n" +
                    "QSS -2.46029897793719\n" +
                    "EIP -2.46121982584651\n" +
                    "JOT -2.47439405390881\n" +
                    "BGZ -2.47531772347433\n" +
                    "FIT -2.48380087356934\n" +
                    "JNT -2.48745159787598\n" +
                    "CNU -2.49440674605419\n" +
                    "CJY -2.49533886027637\n" +
                    "AOR -2.50574789547454\n" +
                    "QRS -2.51381435576119\n" +
                    "GMZ -2.51563316005267\n" +
                    "EEM -2.52161032414496\n" +
                    "FJZ -2.52185561908822\n" +
                    "JWY -2.5244725935673\n" +
                    "CCZ -2.52805287939206\n" +
                    "BLN -2.53432213757899\n" +
                    "DGI -2.53672500930836\n" +
                    "RTT -2.5413554607101\n" +
                    "ISU -2.55215398127071\n" +
                    "ALW -2.55437208097442\n" +
                    "JMZ -2.55955942608723\n" +
                    "DKO -2.56353298532059\n" +
                    "BIK -2.56393212970734\n" +
                    "UYZ -2.56708465935143\n" +
                    "CCO -2.58304921801646\n" +
                    "MNP -2.58639616363615\n" +
                    "AFK -2.58755345628323\n" +
                    "ADF -2.58951572725439\n" +
                    "CFL -2.60170267587648\n" +
                    "BCL -2.60265514687213\n" +
                    "BLM -2.62153108783453\n" +
                    "NRU -2.62402534805619\n" +
                    "BJO -2.63129638703715\n" +
                    "FWZ -2.63638528780993\n" +
                    "HJR -2.63664450476157\n" +
                    "TWZ -2.6438082571297\n" +
                    "GQU -2.64693717366695\n" +
                    "EQZ -2.64713508971964\n" +
                    "FTY -2.64941931618282\n" +
                    "ADI -2.65518252020618\n" +
                    "HLW -2.65764196965854\n" +
                    "PPR -2.66820735300614\n" +
                    "AFH -2.6760016563712\n" +
                    "ALV -2.69060541651986\n" +
                    "BTY -2.6916167561068\n" +
                    "CFI -2.69342535280336\n" +
                    "BCO -2.69392056052072\n" +
                    "GNP -2.69395379654982\n" +
                    "CLU -2.70155147560268\n" +
                    "BDN -2.71162328322771\n" +
                    "JKU -2.71188106286501\n" +
                    "JLO -2.71590691084132\n" +
                    "NRW -2.72594488232293\n" +
                    "AFM -2.73176745887447\n" +
                    "ADD -2.73915080933844\n" +
                    "HPW -2.74873648372753\n" +
                    "FMX -2.74969477641813\n" +
                    "JRT -2.76211357705931\n" +
                    "FFX -2.764963615259\n" +
                    "IJR -2.76634352358565\n" +
                    "KMX -2.77031447058404\n" +
                    "BHK -2.7752038795609\n" +
                    "OQU -2.77902985198179\n" +
                    "EIK -2.78048990941723\n" +
                    "AMO -2.78352902400267\n" +
                    "AGJ -2.79288354747769\n" +
                    "DNU -2.79620659585966\n" +
                    "RRT -2.79773229132653\n" +
                    "DNW -2.80519311684577\n" +
                    "GIT -2.81253160191291\n" +
                    "JNR -2.82561056898893\n" +
                    "ETV -2.82857700396803\n" +
                    "HQS -2.83044072424412\n" +
                    "KMO -2.84762907141771\n" +
                    "DJK -2.8567082892955\n" +
                    "JWZ -2.85732696523384\n" +
                    "NTU -2.85760055302257\n" +
                    "BIM -2.87621119166138\n" +
                    "DFI -2.88048261323778\n" +
                    "DFY -2.90173528465055\n" +
                    "BUZ -2.91085599164851\n" +
                    "MPT -2.91380440202683\n" +
                    "MRU -2.91389793436401\n" +
                    "HJN -2.91821101441271\n" +
                    "LLY -2.92507068018884\n" +
                    "DJR -2.92747137056944\n" +
                    "INO -2.92856074990361\n" +
                    "CIO -2.92951919197741\n" +
                    "TUZ -2.93768125953113\n" +
                    "RTW -2.93854486536112\n" +
                    "DPT -2.94226361066911\n" +
                    "IRR -2.94250733699224\n" +
                    "CJZ -2.94353581565941\n" +
                    "DUZ -2.94795156316937\n" +
                    "BOT -2.95059164224075\n" +
                    "AJW -2.95185916878852\n" +
                    "FFR -2.9586936401591\n" +
                    "DJN -2.95949613152305\n" +
                    "DJX -2.95999230441555\n" +
                    "EEP -2.96035683414692\n" +
                    "HIW -2.96095056169785\n" +
                    "JKW -2.96609569399551\n" +
                    "BCI -2.96765549554331\n" +
                    "EOP -2.97282443353881\n" +
                    "JPZ -2.97455876023389\n" +
                    "BIT -2.98479803342887\n" +
                    "JMO -2.99469799188124\n" +
                    "APP -3.00028500452581\n" +
                    "GHT -3.0009225282054\n" +
                    "EUZ -3.00881643108711\n" +
                    "BHO -3.0110325502656\n" +
                    "FHT -3.01353632260307\n" +
                    "JUX -3.01419297108079\n" +
                    "GXY -3.01656039328517\n" +
                    "GIM -3.01707828236547\n" +
                    "FPZ -3.0232385534012\n" +
                    "EEK -3.0270491842619\n" +
                    "BMX -3.03363100411259\n" +
                    "DOW -3.04104598180458\n" +
                    "EGJ -3.04669444792007\n" +
                    "IRV -3.05349760329932\n" +
                    "DMT -3.06142916669929\n" +
                    "AVZ -3.06366230156572\n" +
                    "IQZ -3.06450903743042\n" +
                    "TTZ -3.06685334013354\n" +
                    "CFY -3.06835806782368\n" +
                    "HJT -3.07404035143284\n" +
                    "INW -3.08632362102279\n" +
                    "JRX -3.09002736891059\n" +
                    "BHL -3.10438452504566\n" +
                    "JTX -3.10841904910171\n" +
                    "PRU -3.12427595704696\n" +
                    "DIJ -3.125761614849\n" +
                    "BHT -3.16372752202725\n" +
                    "KLM -3.17862394445913\n" +
                    "JMX -3.18052190244871\n" +
                    "DKX -3.18326059596534\n" +
                    "GMR -3.18638149993919\n" +
                    "FNT -3.19490807918545\n" +
                    "HUZ -3.19550560567099\n" +
                    "GKZ -3.22576578660868\n" +
                    "ERU -3.23484154068223\n" +
                    "GLO -3.23516080428544\n" +
                    "NUX -3.23907597770269\n" +
                    "FHL -3.24051733783585\n" +
                    "GHO -3.24753836357707\n" +
                    "AFF -3.2494213626224\n" +
                    "BCY -3.25325332448035\n" +
                    "BJX -3.25447742408908\n" +
                    "DDR -3.25709604093009\n" +
                    "GHL -3.26123182262505\n" +
                    "KOT -3.2650625515233\n" +
                    "KLW -3.27008991364496\n" +
                    "IIZ -3.27262153151435\n" +
                    "CJN -3.27889926912129\n" +
                    "CCN -3.28758895715528\n" +
                    "CUX -3.29467599309804\n" +
                    "MUX -3.30721709966913\n" +
                    "OWX -3.30722308147897\n" +
                    "HRR -3.30882298918899\n" +
                    "CTU -3.30980654073893\n" +
                    "JLX -3.31323833647017\n" +
                    "PWY -3.32188945671298\n" +
                    "GJZ -3.33913945042065\n" +
                    "EMW -3.34944586797416\n" +
                    "BWZ -3.35012927916528\n" +
                    "AVX -3.35195585302343\n" +
                    "KOW -3.36166310782864\n" +
                    "ORR -3.36917968514578\n" +
                    "FMY -3.37380661665979\n" +
                    "FNO -3.37415438773808\n" +
                    "GIX -3.37627809109378\n" +
                    "GNW -3.38035002769074\n" +
                    "HHR -3.38530046427431\n" +
                    "AEG -3.38602915528705\n" +
                    "OWY -3.38606570800499\n" +
                    "MSV -3.38645997858765\n" +
                    "DHJ -3.3870224856712\n" +
                    "BHX -3.39354156325473\n" +
                    "BBZ -3.40288548600775\n" +
                    "BJR -3.40936121345696\n" +
                    "IOX -3.41180403574133\n" +
                    "ALO -3.4136645407848\n" +
                    "IJL -3.41995185733475\n" +
                    "EFJ -3.4252934508572\n" +
                    "BPZ -3.43272334796072\n" +
                    "BGN -3.43582355915586\n" +
                    "AQT -3.43627690147089\n" +
                    "BNT -3.4366302472803\n" +
                    "ANO -3.43905820164856\n" +
                    "IJT -3.43950934574532\n" +
                    "EPW -3.44053450306891\n" +
                    "FKN -3.44177710796235\n" +
                    "MUZ -3.4468216719327\n" +
                    "EGP -3.44749584132589\n" +
                    "GMY -3.44866692289779\n" +
                    "ATW -3.44910389698745\n" +
                    "TWY -3.45006621153548\n" +
                    "DRR -3.45537736088557\n" +
                    "RUX -3.45589465637554\n" +
                    "BKX -3.45861382382739\n" +
                    "ACG -3.46405931157237\n" +
                    "LUX -3.4704077815662\n" +
                    "CIJ -3.47399195049289\n" +
                    "IMM -3.47479499046594\n" +
                    "CJX -3.47795280215308\n" +
                    "MWZ -3.49376636441236\n" +
                    "FGN -3.49502323862354\n" +
                    "AEY -3.49809669438231\n" +
                    "CFN -3.50167420741428\n" +
                    "LRU -3.5055746868205\n" +
                    "DFN -3.50830693147965\n" +
                    "PPY -3.51535571052014\n" +
                    "CEG -3.51809711603728\n" +
                    "CUZ -3.52466354677582\n" +
                    "HIJ -3.53476847159609\n" +
                    "KSV -3.53592730289104\n" +
                    "PSV -3.54006941507627\n" +
                    "JPX -3.54237292597741\n" +
                    "NOW -3.5522582369895\n" +
                    "PRR -3.56090065996759\n" +
                    "BNX -3.56674636736583\n" +
                    "FOY -3.56750224644734\n" +
                    "AAX -3.57202063928631\n" +
                    "DDI -3.57740443054877\n" +
                    "FIK -3.57785509404838\n" +
                    "CEW -3.58102320316041\n" +
                    "KWX -3.58383098328083\n" +
                    "ILL -3.5840835456484\n" +
                    "BDX -3.58584110675035\n" +
                    "MMR -3.58718556933527\n" +
                    "CCL -3.59817747480934\n" +
                    "FHK -3.60058765094171\n" +
                    "GNU -3.60165697141184\n" +
                    "IVZ -3.61250465399239\n" +
                    "HJW -3.61574175742638\n" +
                    "KTX -3.62105288613979\n" +
                    "FOT -3.62343470573366\n" +
                    "JKT -3.62731778212295\n" +
                    "HMW -3.63347363183275\n" +
                    "FJX -3.63921786029974\n" +
                    "AAR -3.65026834134171\n" +
                    "JKP -3.65645499669009\n" +
                    "GKY -3.66768240116083\n" +
                    "DGY -3.66801389125091\n" +
                    "HRU -3.66909477812009\n" +
                    "CJR -3.67876549568232\n" +
                    "GPZ -3.68655252528246\n" +
                    "MWY -3.6881921670144\n" +
                    "IJY -3.68867901784487\n" +
                    "DUX -3.68921900396438\n" +
                    "JUZ -3.69047603593337\n" +
                    "DFO -3.69191284362114\n" +
                    "CFO -3.69346663952927\n" +
                    "DMP -3.69582263717976\n" +
                    "HHT -3.69916398673808\n" +
                    "GPY -3.71152285692974\n" +
                    "MNU -3.71433468640989\n" +
                    "AAC -3.71876692328396\n" +
                    "AHI -3.72030077459149\n" +
                    "DWX -3.72856274794077\n" +
                    "IRW -3.72999146040371\n" +
                    "GJY -3.75287951988608\n" +
                    "BEG -3.76031931988115\n" +
                    "GPR -3.76043530899913\n" +
                    "FUX -3.76637839538039\n" +
                    "AMW -3.77561883161999\n" +
                    "EGK -3.77739935517398\n" +
                    "CNN -3.78829210531456\n" +
                    "PUZ -3.79109460574815\n" +
                    "QYZ -3.79343456711567\n" +
                    "GOY -3.80080811554715\n" +
                    "RRY -3.81041209109441\n" +
                    "EKO -3.81567090094008\n" +
                    "DLU -3.82557315179691\n" +
                    "RWX -3.82862522920609\n" +
                    "DLW -3.84047173568066\n" +
                    "CDU -3.84169960392965\n" +
                    "BIJ -3.84306477059258\n" +
                    "HHX -3.84492225682834\n" +
                    "AEF -3.85951584840396\n" +
                    "NQS -3.8621238353642\n" +
                    "ACV -3.86740274299073\n" +
                    "FFI -3.87082456051621\n" +
                    "HPP -3.87976213775991\n" +
                    "MMX -3.88352558519663\n" +
                    "PWZ -3.89692746842569\n" +
                    "MRR -3.90470918439338\n" +
                    "BMN -3.90843801006102\n" +
                    "TUX -3.91672795608207\n" +
                    "BJL -3.92223596619213\n" +
                    "KPT -3.92299202059271\n" +
                    "HHY -3.93174237196118\n" +
                    "MMY -3.93605091047714\n" +
                    "FIM -3.94078022365362\n" +
                    "IOR -3.94579733896976\n" +
                    "NWX -3.95514556380015\n" +
                    "EVY -3.96042119428436\n" +
                    "DGL -3.96531188359943\n" +
                    "ITT -3.97071079878494\n" +
                    "LTU -3.97345721896122\n" +
                    "LNU -3.97632864705423\n" +
                    "FHO -3.98380403480588\n" +
                    "EJO -3.99456312405537\n" +
                    "EUX -4.00535091716154\n" +
                    "AOT -4.01628602370499\n" +
                    "FHI -4.02926350085799\n" +
                    "INV -4.04198819645918\n" +
                    "DKW -4.04305475482082\n" +
                    "GIY -4.0430682289876\n" +
                    "BBE -4.04568681768515\n" +
                    "FIY -4.04837815749045\n" +
                    "BCN -4.04996475284758\n" +
                    "GNN -4.0521697200818\n" +
                    "AAM -4.05858966402847\n" +
                    "JOW -4.06495595278183\n" +
                    "BCX -4.07214679676378\n" +
                    "BRU -4.07369830002078\n" +
                    "BDK -4.07693021079481\n" +
                    "ILV -4.08841217453315\n" +
                    "NRR -4.09140724296225\n" +
                    "BTX -4.09726418627105\n" +
                    "LRW -4.09899864705767\n" +
                    "CGI -4.09945931233009\n" +
                    "CGZ -4.10830964822772\n" +
                    "EHV -4.10924882392243\n" +
                    "HJP -4.11163776244207\n" +
                    "CCT -4.11919971546978\n" +
                    "DDN -4.12028301854949\n" +
                    "JMR -4.12336910753173\n" +
                    "GOX -4.12917795862008\n" +
                    "JSV -4.12969965343252\n" +
                    "BDH -4.13264081000823\n" +
                    "JKM -4.13680205903919\n" +
                    "FFL -4.13857604765423\n" +
                    "FLM -4.14372766990478\n" +
                    "CLL -4.14731057188756\n" +
                    "JWX -4.161525471086\n" +
                    "CCY -4.16197541314093\n" +
                    "EFG -4.16403037118273\n" +
                    "KNU -4.16633542296892\n" +
                    "AIP -4.166530675962\n" +
                    "HHK -4.17231475391692\n" +
                    "JPR -4.1761122624464\n" +
                    "IJM -4.17945110962268\n" +
                    "JMN -4.18038476548907\n" +
                    "BHI -4.18570938762252\n" +
                    "DIW -4.19072381445579\n" +
                    "HTT -4.19076118452283\n" +
                    "ANV -4.19181308198659\n" +
                    "IKW -4.19218302455848\n" +
                    "FGR -4.19426261009695\n" +
                    "DQS -4.21207459106502\n" +
                    "DKP -4.21283921657442\n" +
                    "JOP -4.21359564690523\n" +
                    "EEJ -4.21582528041688\n" +
                    "BEO -4.22760582366937\n" +
                    "KRU -4.23551136218266\n" +
                    "ABG -4.2407976126789\n" +
                    "AGP -4.24293106102261\n" +
                    "BHM -4.24518600159101\n" +
                    "QXZ -4.24736545616283\n" +
                    "LOW -4.24786817422553\n" +
                    "NTT -4.25215790861252\n" +
                    "AIK -4.25269881023886\n" +
                    "NNT -4.25625608901409\n" +
                    "AHO -4.25751973727298\n" +
                    "OVZ -4.27639084835305\n" +
                    "IPP -4.29446505641351\n" +
                    "CFT -4.29566180452291\n" +
                    "BEI -4.30331907083342\n" +
                    "NNO -4.30923643025712\n" +
                    "DEU -4.30969336796813\n" +
                    "GTY -4.30994991686687\n" +
                    "BIY -4.31318934928559\n" +
                    "BDM -4.31401583726829\n" +
                    "DGH -4.31547008803956\n" +
                    "BLU -4.31556250295829\n" +
                    "FJY -4.31654518659398\n" +
                    "FWY -4.3199483179987\n" +
                    "NTW -4.32211619757662\n" +
                    "BEF -4.32835298762081\n" +
                    "HTU -4.32929739677523\n" +
                    "CCX -4.32974972793111\n" +
                    "IMO -4.33081320024879\n" +
                    "PRW -4.33307694779053\n" +
                    "HMM -4.33317435218907\n" +
                    "GLT -4.33734875007744\n" +
                    "NNR -4.34408877492137\n" +
                    "GHX -4.3464432530787\n" +
                    "JNU -4.35503876630615\n" +
                    "CWY -4.35719352393946\n" +
                    "GSV -4.35869886450105\n" +
                    "CGR -4.36104353009331\n" +
                    "CWZ -4.36314872116404\n" +
                    "VYZ -4.37138844423891\n" +
                    "ADO -4.38682399265293\n" +
                    "EGI -4.38741964335508\n" +
                    "IVX -4.38991066075547\n" +
                    "JLN -4.38996994009644\n" +
                    "EFW -4.40091848738383\n" +
                    "AAL -4.40543301579583\n" +
                    "HUX -4.40958851529647\n" +
                    "DGO -4.41172772336962\n" +
                    "HHP -4.41489926856045\n" +
                    "IWX -4.41580493249551\n" +
                    "CJT -4.41648310689786\n" +
                    "AUZ -4.41693077009374\n" +
                    "AAN -4.42663959529329\n" +
                    "PUX -4.43381173425417\n" +
                    "MMO -4.43439256397194\n" +
                    "AEV -4.43593658056778\n" +
                    "JLR -4.43767709358418\n" +
                    "EFI -4.43982155428951\n" +
                    "UXY -4.44676520886938\n" +
                    "KMP -4.44694231638666\n" +
                    "FFY -4.45077343733264\n" +
                    "LPU -4.45853351727537\n" +
                    "FRU -4.45922866843911\n" +
                    "LMU -4.46589125952466\n" +
                    "FPX -4.47389530833452\n" +
                    "FLU -4.47575759048244\n" +
                    "BHN -4.48616868390217\n" +
                    "NNX -4.49773728230218\n" +
                    "BJN -4.51058902490902\n" +
                    "CRW -4.52305217391413\n" +
                    "FPR -4.52392048477727\n" +
                    "QSX -4.5247909602399\n" +
                    "HHO -4.53304042715891\n" +
                    "ORV -4.53944710120925\n" +
                    "XYY -4.55742829150639\n" +
                    "FMO -4.57062167544048\n" +
                    "CTT -4.57521538829504\n" +
                    "ACW -4.57536692569458\n" +
                    "ILO -4.57963118271914\n" +
                    "LVY -4.58288529602279\n" +
                    "KRR -4.58368909028988\n" +
                    "RVY -4.58491414259476\n" +
                    "JRU -4.58824155825112\n" +
                    "BGR -4.59062153217808\n" +
                    "AGK -4.59083928271066\n" +
                    "DDO -4.59900008907817\n" +
                    "BEE -4.59980574918611\n" +
                    "QUW -4.60730180717943\n" +
                    "VXZ -4.60749153424972\n" +
                    "FHN -4.60975692887614\n" +
                    "HJM -4.61373948565872\n" +
                    "HJL -4.61694867218244\n" +
                    "KQS -4.6174604790628\n" +
                    "DKM -4.6215919012087\n" +
                    "MRW -4.62260601719413\n" +
                    "DIO -4.62592067307598\n" +
                    "PPX -4.62871092656238\n" +
                    "BDJ -4.6294656947914\n" +
                    "EIJ -4.63682069585407\n" +
                    "BEW -4.64292026199332\n" +
                    "FKO -4.65229913729148\n" +
                    "BCD -4.65401429900911\n" +
                    "GMO -4.65488804083125\n" +
                    "JLT -4.66321298070556\n" +
                    "BHJ -4.68096278692439\n" +
                    "LPP -4.68501559446075\n" +
                    "LWX -4.69686742710782\n" +
                    "BDT -4.70267582515211\n" +
                    "BEP -4.71643677868942\n" +
                    "SVW -4.72264794179523\n" +
                    "CIV -4.72311244387054\n" +
                    "GHM -4.72701319069371\n" +
                    "DJL -4.73331602664826\n" +
                    "DNN -4.74095352274349\n" +
                    "DTU -4.74386147730833\n" +
                    "MMN -4.74463286070332\n" +
                    "SUW -4.74839663382592\n" +
                    "BFL -4.75009793826783\n" +
                    "DMU -4.76046586864894\n" +
                    "AQX -4.76120975286873\n" +
                    "RVZ -4.76195346571062\n" +
                    "NNY -4.76232239632005\n" +
                    "LLX -4.76682547506359\n" +
                    "PQS -4.78162649408951\n" +
                    "BWY -4.79336159493022\n" +
                    "EFP -4.79651456819114\n" +
                    "GIP -4.80106996877657\n" +
                    "GIJ -4.80657354192259\n" +
                    "LLO -4.80993782314497\n" +
                    "GHW -4.81209388689155\n" +
                    "DFT -4.81713238793307\n" +
                    "HHI -4.81929801794351\n" +
                    "GHP -4.82123787375546\n" +
                    "AEW -4.82359957908995\n" +
                    "CCD -4.83175988285933\n" +
                    "DFH -4.83178029604487\n" +
                    "DKT -4.84152680679292\n" +
                    "LUY -4.84280795801451\n" +
                    "BFR -4.84438013239936\n" +
                    "DDX -4.84582781551336\n" +
                    "GRX -4.84895230848098\n" +
                    "GLX -4.85232243016144\n" +
                    "GWZ -4.85334733884916\n" +
                    "RRX -4.85578811581949\n" +
                    "KLU -4.86058692904284\n" +
                    "CPU -4.86331023312099\n" +
                    "BUX -4.86367390411284\n" +
                    "DDY -4.86404167790463\n" +
                    "AFJ -4.86517799039621\n" +
                    "FRW -4.86592743897016\n" +
                    "OVX -4.87358816050127\n" +
                    "BGY -4.88115238735647\n" +
                    "CMU -4.88843832979229\n" +
                    "ELU -4.89064399953303\n" +
                    "FMN -4.89255732148566\n" +
                    "BKT -4.89380325513662\n" +
                    "LLR -4.89461644962324\n" +
                    "DJT -4.897659595109\n" +
                    "BCT -4.90539530160959\n" +
                    "BKM -4.90584793024308\n" +
                    "OPP -4.90643580380272\n" +
                    "CJL -4.90797294473515\n" +
                    "HHN -4.91839980741045\n" +
                    "ATV -4.92840215091035\n" +
                    "CDF -4.93121046714804\n" +
                    "ENU -4.93419289579131\n" +
                    "HKU -4.94595861803471\n" +
                    "OQZ -4.94737747292838\n" +
                    "AAT -4.94962519398356\n" +
                    "FGL -4.95692854192458\n" +
                    "QSY -4.97125209990655\n" +
                    "GHK -4.97132743585091\n" +
                    "SWW -4.97351751985438\n" +
                    "CQS -4.97774064460555\n" +
                    "EEY -4.97984722057195\n" +
                    "ABB -4.98534261347127\n" +
                    "CDD -4.98677528407204\n" +
                    "ILW -4.986879569395\n" +
                    "CEU -5.00137206359947\n" +
                    "OUZ -5.00326635821492\n" +
                    "BGL -5.00853361859382\n" +
                    "LQS -5.01600112388376\n" +
                    "DJW -5.02246699720461\n" +
                    "KYY -5.02407254016122\n" +
                    "MTU -5.02544956213958\n" +
                    "FWX -5.02911459072319\n" +
                    "DDL -5.02957987923765\n" +
                    "GOT -5.03126137041249\n" +
                    "VXY -5.03430762318234\n" +
                    "HMU -5.04408175840877\n" +
                    "BRR -5.04950130179717\n" +
                    "KNN -5.05914757310983\n" +
                    "QTZ -5.06597725212196\n" +
                    "GLM -5.07131256351487\n" +
                    "FRR -5.07267747117639\n" +
                    "GKR -5.07985138026936\n" +
                    "FPY -5.0848170839433\n" +
                    "HLL -5.08645477845563\n" +
                    "JNW -5.08996949168961\n" +
                    "LNW -5.0905804560525\n" +
                    "IOT -5.09830270712495\n" +
                    "EEF -5.09862610591589\n" +
                    "CDJ -5.10093734509985\n" +
                    "DHH -5.10415008762098\n" +
                    "CRV -5.10988610201686\n" +
                    "FJK -5.11540722749779\n" +
                    "KUY -5.12671279793584\n" +
                    "APW -5.13796236978724\n" +
                    "HHM -5.13861885493924\n" +
                    "TTX -5.14374302629675\n" +
                    "AOP -5.14448394199236\n" +
                    "FGI -5.14586199423662\n" +
                    "AGI -5.15048484898466\n" +
                    "CGY -5.15204076785268\n" +
                    "JRW -5.15328261675847\n" +
                    "ADV -5.1563007498866\n" +
                    "GJO -5.15892308312684\n" +
                    "HYY -5.16150431053964\n" +
                    "MOW -5.16789683208406\n" +
                    "OTT -5.17106310898806\n" +
                    "AVY -5.17174004671783\n" +
                    "GIK -5.18712500563714\n" +
                    "BPY -5.19000052688058\n" +
                    "FHW -5.19123677789854\n" +
                    "AAH -5.20835217297457\n" +
                    "ETU -5.2111738741102\n" +
                    "AJO -5.21193665510604\n" +
                    "DDH -5.21379315193454\n" +
                    "CMM -5.21665098025846\n" +
                    "FFO -5.2186276376287\n" +
                    "GJR -5.22196741041557\n" +
                    "BJM -5.22761231305426\n" +
                    "MWX -5.23006910319342\n" +
                    "HNU -5.23334974650083\n" +
                    "RUY -5.23382331435532\n" +
                    "LYY -5.23425616627033\n" +
                    "BFX -5.23847269477439\n" +
                    "EGW -5.23954504090711\n" +
                    "MNN -5.24356370400108\n" +
                    "PTU -5.25364557581856\n" +
                    "EOY -5.25486526729384\n" +
                    "IWY -5.2616532968548\n" +
                    "DJU -5.26754990062631\n" +
                    "CJU -5.26961089156547\n" +
                    "FGY -5.27511076749647\n" +
                    "KUX -5.27702807068407\n" +
                    "SUV -5.27785764626008\n" +
                    "DFF -5.27935008829061\n" +
                    "TWX -5.28249975991716\n" +
                    "CFF -5.28657219052775\n" +
                    "JTU -5.28727910262874\n" +
                    "EIV -5.28772695547033\n" +
                    "CPP -5.29367596618548\n" +
                    "HPU -5.29414450451043\n" +
                    "BCM -5.29539664005422\n" +
                    "LRR -5.29847824982749\n" +
                    "OTW -5.29958074416683\n" +
                    "JNP -5.3002512406705\n" +
                    "BJT -5.3134214033027\n" +
                    "BMT -5.32086847835236\n" +
                    "JLU -5.33120164522321\n" +
                    "ABI -5.33277544347445\n" +
                    "FFT -5.33708871357113\n" +
                    "KMT -5.34680770639441\n" +
                    "AFG -5.34945390074197\n" +
                    "DLL -5.37872428177642\n" +
                    "GWY -5.38804130237075\n" +
                    "BPR -5.39334429482899\n" +
                    "LRV -5.39502349575724\n" +
                    "IJP -5.40072701651502\n" +
                    "CGK -5.40553366802222\n" +
                    "GKL -5.40620445758563\n" +
                    "FFH -5.42072582055411\n" +
                    "FFN -5.42284656188856\n" +
                    "HNN -5.42396213498066\n" +
                    "MNW -5.44649654619155\n" +
                    "BGI -5.44783792207456\n" +
                    "EJV -5.45154445110055\n" +
                    "NPU -5.4579467351293\n" +
                    "JUY -5.46553277984663\n" +
                    "BJU -5.46773938906034\n" +
                    "RVX -5.47315708942124\n" +
                    "CGL -5.47408829765076\n" +
                    "DIV -5.47492078292342\n" +
                    "HHL -5.47737853863716\n" +
                    "RTV -5.47882541433859\n" +
                    "GLP -5.48443829325041\n" +
                    "BBR -5.50496545863122\n" +
                    "DTW -5.50783736836336\n" +
                    "DHU -5.51600814872985\n" +
                    "LTT -5.52315027473667\n" +
                    "EYY -5.52391849126696\n" +
                    "PPT -5.52765325909621\n" +
                    "CGO -5.5285200725096\n" +
                    "TTY -5.53286460261521\n" +
                    "PWX -5.53483948192897\n" +
                    "JLP -5.53514488816528\n" +
                    "AIJ -5.5420180594013\n" +
                    "KLL -5.56755790279185\n" +
                    "HLU -5.56969761698958\n" +
                    "EFO -5.56994093267193\n" +
                    "GUZ -5.57420549687988\n" +
                    "HQZ -5.57866155275707\n" +
                    "COW -5.58222674065694\n" +
                    "KTW -5.58786713399996\n" +
                    "BFY -5.59352612525583\n" +
                    "DRV -5.60894646546793\n" +
                    "DJM -5.61226079778823\n" +
                    "AQR -5.6126544773518\n" +
                    "DMM -5.61377273106943\n" +
                    "CHV -5.61528047979258\n" +
                    "ITW -5.61771091918363\n" +
                    "AKO -5.6230697235365\n" +
                    "NPP -5.62767097093814\n" +
                    "KPW -5.6360680606553\n" +
                    "GHJ -5.63807356928317\n" +
                    "LTW -5.66894275980237\n" +
                    "DFK -5.67524256459724\n" +
                    "CNW -5.68038008796167\n" +
                    "GGN -5.68091491088794\n" +
                    "AAD -5.69287890836095\n" +
                    "FHM -5.69656876773628\n" +
                    "ABO -5.70362294843327\n" +
                    "FLW -5.71790803112913\n" +
                    "BRW -5.71891699485827\n" +
                    "FLP -5.72065067027328\n" +
                    "OQS -5.72242276242349\n" +
                    "LLT -5.72585407451419\n" +
                    "MQS -5.74594599399738\n" +
                    "AUX -5.75353712429084\n" +
                    "CCM -5.75547905319051\n" +
                    "EQT -5.75959078754851\n" +
                    "RYY -5.76106871304199\n" +
                    "LMM -5.76421666971713\n" +
                    "HJU -5.77707231588098\n" +
                    "HIO -5.7825218179941\n" +
                    "BSV -5.78472240262573\n" +
                    "LLN -5.78818934360167\n" +
                    "CCP -5.79106122209857\n" +
                    "CUY -5.7950842118458\n" +
                    "EEG -5.80819358558311\n" +
                    "AYY -5.82051572403891\n" +
                    "ABP -5.82228923959635\n" +
                    "FJR -5.82759198445464\n" +
                    "EKV -5.82846443277768\n" +
                    "ABF -5.84509666294588\n" +
                    "GMX -5.8508338644208\n" +
                    "FKT -5.85918396261284\n" +
                    "NRV -5.86025517955482\n" +
                    "ARU -5.8687718051192\n" +
                    "ABW -5.87048260515239\n" +
                    "MMT -5.89281747713964\n" +
                    "LLP -5.89741378751779\n" +
                    "NUY -5.90207915516393\n" +
                    "GRU -5.90647924087904\n" +
                    "QRZ -5.90771977588715\n" +
                    "PUY -5.91000693621763\n" +
                    "CWX -5.91268925971837\n" +
                    "JPT -5.9160201819168\n" +
                    "BDU -5.92841687707984\n" +
                    "BLL -5.93341873262557\n" +
                    "FJO -5.9425125580112\n" +
                    "EQX -5.94758496579253\n" +
                    "IOP -5.96267598933988\n" +
                    "JMT -5.96982106780376\n" +
                    "KPP -5.96991415595964\n" +
                    "AFW -5.98245437571766\n" +
                    "AGW -5.98585876229131\n" +
                    "PYY -5.99927787326375\n" +
                    "EMV -5.99977085946323\n" +
                    "HHW -6.00795096971716\n" +
                    "DMW -6.01077427740784\n" +
                    "ITV -6.0271326330038\n" +
                    "GJL -6.04014245966196\n" +
                    "DDM -6.0425270885982\n" +
                    "DPU -6.04273336522628\n" +
                    "DPP -6.04321366997901\n" +
                    "GJK -6.06757290661117\n" +
                    "FMT -6.07271254946557\n" +
                    "MPU -6.07319786934619\n" +
                    "JLW -6.08201548772031\n" +
                    "DFM -6.08696042875888\n" +
                    "LOV -6.08701171279817\n" +
                    "GRW -6.09024586942073\n" +
                    "FGX -6.09145651839433\n" +
                    "BBL -6.09307606073807\n" +
                    "BLP -6.10408973096258\n" +
                    "LLM -6.11158988649274\n" +
                    "CDW -6.11325328524408\n" +
                    "GJX -6.11632624077827\n" +
                    "ANQ -6.12078892019135\n" +
                    "LNN -6.13956734158261\n" +
                    "AAK -6.15851853813845\n" +
                    "EQR -6.18741079923465\n" +
                    "ACU -6.19265237329619\n" +
                    "BKW -6.19751300338882\n" +
                    "CYY -6.20591588170943\n" +
                    "EMU -6.21049920131144\n" +
                    "PTT -6.21081585680103\n" +
                    "IIN -6.21090336431194\n" +
                    "DGX -6.21266044647003\n" +
                    "KPU -6.22349278821258\n" +
                    "LVX -6.23030017688564\n" +
                    "BKU -6.23882424274576\n" +
                    "MUY -6.2394556669396\n" +
                    "EEW -6.24146376650957\n" +
                    "JLM -6.247536983876\n" +
                    "BBY -6.25906428262442\n" +
                    "EIY -6.27371717465857\n" +
                    "FJL -6.27424302103591\n" +
                    "NYY -6.27473010490522\n" +
                    "AHV -6.27602383436834\n" +
                    "COV -6.27645156453312\n" +
                    "GGZ -6.27737466951181\n" +
                    "EJU -6.29362172011465\n" +
                    "MYY -6.29426357390494\n" +
                    "DKU -6.29706793943951\n" +
                    "NQZ -6.29783732438631\n" +
                    "JMU -6.30011822957822\n" +
                    "EEV -6.30269377064245\n" +
                    "OPW -6.30954734464454\n" +
                    "FLL -6.311763451711\n" +
                    "ALU -6.32012325529331\n" +
                    "BNU -6.323380909155\n" +
                    "CLW -6.32579341679553\n" +
                    "DJP -6.3282456168019\n" +
                    "BGO -6.33082227823308\n" +
                    "DTT -6.33243696122281\n" +
                    "BLW -6.3350659545491\n" +
                    "DDT -6.33629391959983\n" +
                    "EGO -6.33918009464647\n" +
                    "GOP -6.34155845359104\n" +
                    "JMP -6.34819692259252\n" +
                    "QUV -6.35472805256601\n" +
                    "OOX -6.35839516683367\n" +
                    "EVW -6.3681413294257\n" +
                    "ADQ -6.37607879219791\n" +
                    "HUY -6.38247115314272\n" +
                    "EGG -6.38325080662986\n" +
                    "LVZ -6.39165939362113\n" +
                    "AAP -6.396807534069\n" +
                    "AHQ -6.39700401520797\n" +
                    "EGV -6.40245090291284\n" +
                    "FFK -6.4063535973625\n" +
                    "KMW -6.40747550992839\n" +
                    "TUY -6.40751464189797\n" +
                    "FIJ -6.41288467584008\n" +
                    "DGM -6.41339050706692\n" +
                    "KQZ -6.41514194294532\n" +
                    "AFP -6.4566488743038\n" +
                    "FKW -6.45753826192107\n" +
                    "EOW -6.45772558673537\n" +
                    "BHW -6.46132375424993\n" +
                    "GTX -6.46374426041462\n" +
                    "EPV -6.47988596227626\n" +
                    "CJP -6.48177681927769\n" +
                    "DQZ -6.49026838441039\n" +
                    "AFI -6.49118705272105\n" +
                    "MPP -6.50037723095378\n" +
                    "AMU -6.51811593349915\n" +
                    "BCJ -6.51951576606197\n" +
                    "MTT -6.52689575525351\n" +
                    "JPU -6.53137611798457\n" +
                    "LPW -6.531613253277\n" +
                    "BTU -6.53328112845753\n" +
                    "DGT -6.56152783893511\n" +
                    "CJM -6.56768701932537\n" +
                    "IMW -6.57436593779972\n" +
                    "BBO -6.57601447800175\n" +
                    "AAJ -6.58300103258464\n" +
                    "FNU -6.58484872038847\n" +
                    "BMU -6.58714895656643\n" +
                    "GLU -6.60527880525658\n" +
                    "KMU -6.61198099223208\n" +
                    "GRR -6.62649112473026\n" +
                    "DUY -6.62825335134546\n" +
                    "AOY -6.63011238033953\n" +
                    "AJV -6.66204353515233\n" +
                    "FSV -6.66967070419598\n" +
                    "NVY -6.67587480192183\n" +
                    "IQT -6.69710319203559\n" +
                    "IKO -6.69783384354094\n" +
                    "NPW -6.69805584636439\n" +
                    "NVX -6.70238713323694\n" +
                    "CIW -6.7072221627536\n" +
                    "COO -6.70992165075657\n" +
                    "NNP -6.71021793387066\n" +
                    "BPX -6.7178325793753\n" +
                    "AKQ -6.72035800765167\n" +
                    "ANU -6.72178234147568\n" +
                    "IJW -6.72504730793739\n" +
                    "CVY -6.73940657392564\n" +
                    "AIY -6.77612793354233\n" +
                    "JTW -6.7927363014038\n" +
                    "FGO -6.80640891539833\n" +
                    "HRV -6.81540192292095\n" +
                    "EIW -6.81551013513062\n" +
                    "DGJ -6.81706649659228\n" +
                    "NVZ -6.82506559169524\n" +
                    "CFM -6.84041479783182\n" +
                    "DPW -6.84413503460845\n" +
                    "GPX -6.85360850801359\n" +
                    "FTU -6.85790895994976\n" +
                    "CLV -6.86156703938917\n" +
                    "DYY -6.8627599089726\n" +
                    "FOW -6.87747502752373\n" +
                    "GMT -6.8814420956986\n" +
                    "BOW -6.88709820143097\n" +
                    "KTU -6.88902778368485\n" +
                    "OOR -6.89734602766139\n" +
                    "DDK -6.90015725009218\n" +
                    "CVX -6.90960858865474\n" +
                    "BUY -6.92726176936173\n" +
                    "ATU -6.93723908679221\n" +
                    "IIX -6.93880409799537\n" +
                    "DEQ -6.94510663329495\n" +
                    "GHH -6.95207936698993\n" +
                    "GKO -6.95535556323051\n" +
                    "FHJ -6.95736343603575\n" +
                    "AMV -6.96011131420378\n" +
                    "HHJ -6.96120788403715\n" +
                    "EPU -6.96935203368414\n" +
                    "JNN -6.97780490672355\n" +
                    "FKM -6.99435327118689\n" +
                    "AQY -7.00984451802358\n" +
                    "FHP -7.01919763646577\n" +
                    "CII -7.02087218711393\n" +
                    "FJN -7.02408381338152\n" +
                    "FQZ -7.02815881999718\n" +
                    "FFM -7.02951813295061\n" +
                    "JVY -7.03084969942925\n" +
                    "BGH -7.03594256208901\n" +
                    "DFU -7.03895851687801\n" +
                    "BBK -7.04050831236099\n" +
                    "LMW -7.04107882108917\n" +
                    "AAB -7.04805356831132\n" +
                    "IQX -7.0492812821943\n" +
                    "FGH -7.05354686175913\n" +
                    "IUZ -7.06344804124346\n" +
                    "QXY -7.09247927423037\n" +
                    "INQ -7.09750217957868\n" +
                    "MMP -7.10326726580341\n" +
                    "BBI -7.10528199712263\n" +
                    "ACQ -7.11605305454636\n" +
                    "IVY -7.11640315160026\n" +
                    "RRW -7.13391703740093\n" +
                    "EHU -7.13877010588359\n" +
                    "BHP -7.14332603137724\n" +
                    "DDP -7.14566320471843\n" +
                    "BFI -7.15011499220417\n" +
                    "BDW -7.1502354829173\n" +
                    "EOV -7.16417968806378\n" +
                    "CNV -7.16631113819151\n" +
                    "BCU -7.16863033127369\n" +
                    "IJO -7.17208798123042\n" +
                    "DFW -7.17606915591601\n" +
                    "AGG -7.1844003742033\n" +
                    "ALQ -7.18850179488686\n" +
                    "RRU -7.19768457220902\n" +
                    "BWX -7.20482114425908\n" +
                    "PTW -7.23140790065409\n" +
                    "NOV -7.23197999476013\n" +
                    "AAY -7.241109204019\n" +
                    "BFO -7.24200923401276\n" +
                    "QUU -7.24513984143573\n" +
                    "HVY -7.24847864828341\n" +
                    "TYY -7.24935959353986\n" +
                    "BOP -7.24945123657489\n" +
                    "GIW -7.25851632116404\n" +
                    "FIP -7.27243542876144\n" +
                    "CTW -7.29916323346942\n" +
                    "QRT -7.33561323041146\n" +
                    "AJU -7.35477810654352\n" +
                    "DOV -7.36466779097979\n" +
                    "EKU -7.36527509181255\n" +
                    "FNW -7.36992703407436\n" +
                    "GLW -7.37228853535424\n" +
                    "IPW -7.3854999169041\n" +
                    "JQS -7.38680222997172\n" +
                    "DVZ -7.41661818066566\n" +
                    "FOP -7.42181589810017\n" +
                    "JYY -7.42386413113765\n" +
                    "GJT -7.42624573631748\n" +
                    "CGX -7.43196503983821\n" +
                    "CGT -7.43675164713283\n" +
                    "GHU -7.43962875768788\n" +
                    "AIV -7.44485814017173\n" +
                    "DFJ -7.44733226524134\n" +
                    "CFU -7.44952144704088\n" +
                    "KMM -7.4593855640356\n" +
                    "AMQ -7.46180610700292\n" +
                    "BIO -7.46295784902061\n" +
                    "JRR -7.4674094122004\n" +
                    "LNV -7.48244959198138\n" +
                    "CDG -7.48960759013269\n" +
                    "BDD -7.49434360338156\n" +
                    "AGO -7.50005035655829\n" +
                    "FJT -7.50338446719895\n" +
                    "BEV -7.50729101590692\n" +
                    "DVX -7.50952557054262\n" +
                    "GJU -7.52110476937699\n" +
                    "DLV -7.52998410008066\n" +
                    "BFK -7.53282432346574\n" +
                    "IIR -7.56184734219058\n" +
                    "MOO -7.56389754492231\n" +
                    "BKP -7.56433910717424\n" +
                    "FQS -7.56604305772573\n" +
                    "BDG -7.56944058777131\n" +
                    "HOO -7.57535970830439\n" +
                    "AKV -7.57749810555471\n" +
                    "BJW -7.60197817367875\n" +
                    "OUX -7.60556175999793\n" +
                    "FFU -7.60569801011097\n" +
                    "FKU -7.61441780105535\n" +
                    "BBD -7.62224070801838\n" +
                    "HVZ -7.6291409794776\n" +
                    "ADU -7.63289505233839\n" +
                    "DVY -7.63675244752352\n" +
                    "BBX -7.63959636097822\n" +
                    "TVX -7.6424669367582\n" +
                    "DGW -7.64839378086281\n" +
                    "LQZ -7.65081452708789\n" +
                    "CCU -7.65202874099861\n" +
                    "FFJ -7.65860897748948\n" +
                    "NOO -7.6660470449937\n" +
                    "GPT -7.66682417587949\n" +
                    "HVX -7.68791712323279\n" +
                    "GGR -7.69122543136276\n" +
                    "OVY -7.6921399652908\n" +
                    "GKX -7.69414382935989\n" +
                    "GLL -7.70100796670229\n" +
                    "BEU -7.71352941189575\n" +
                    "BHU -7.72779900872312\n" +
                    "ENQ -7.73892599101436\n" +
                    "LTV -7.75146751354481\n" +
                    "GGI -7.75313297888763\n" +
                    "GNV -7.78480699776715\n" +
                    "BQS -7.79525904239854\n" +
                    "GIO -7.79976943757392\n" +
                    "HQT -7.8012862731343\n" +
                    "AGV -7.80740097109561\n" +
                    "DNV -7.81159540645985\n" +
                    "CHQ -7.81656496301556\n" +
                    "IQR -7.82196106049009\n" +
                    "OYY -7.82662350936597\n" +
                    "BGJ -7.83345994356709\n" +
                    "JLL -7.84164731741208\n" +
                    "DOO -7.85601939691547\n" +
                    "DGP -7.85695974924108\n" +
                    "ORU -7.88344190124387\n" +
                    "EHQ -7.88860118415153\n" +
                    "CVZ -7.88937620804557\n" +
                    "DIQ -7.89790148352443\n" +
                    "FPT -7.89897707852893\n" +
                    "COU -7.91204697200009\n" +
                    "GOW -7.92482854099807\n" +
                    "FUY -7.9273115465815\n" +
                    "KVY -7.92826502473573\n" +
                    "FGT -7.9338930707538\n" +
                    "DFG -7.94118960989852\n" +
                    "MTW -7.9423490109425\n" +
                    "FIW -7.94827018041392\n" +
                    "KQY -7.9525094347088\n" +
                    "AFO -7.9586100117176\n" +
                    "QTX -7.96806407651574\n" +
                    "GMP -7.9741223538473\n" +
                    "BBJ -7.97604434677579\n" +
                    "BIP -7.99133764696979\n" +
                    "KTT -7.99816623135025\n" +
                    "MMU -8.00205002558225\n" +
                    "JTT -8.0049608449836\n" +
                    "BNW -8.01315694654822\n" +
                    "NTV -8.02236223483447\n" +
                    "DDJ -8.02973656383389\n" +
                    "GKW -8.03773086433874\n" +
                    "FKP -8.0384059887486\n" +
                    "HIV -8.04211941758453\n" +
                    "IIL -8.05076229992831\n" +
                    "PQZ -8.06699151704033\n" +
                    "EFV -8.07481479973852\n" +
                    "NQT -8.07808854452638\n" +
                    "NNU -8.07832843653756\n" +
                    "BDF -8.08054803971214\n" +
                    "PRV -8.08109461818964\n" +
                    "BQZ -8.09197453386962\n" +
                    "GIV -8.09217506749971\n" +
                    "DII -8.10073825220368\n" +
                    "TVZ -8.10376758942812\n" +
                    "TVY -8.105376548189\n" +
                    "FTW -8.10777537600726\n" +
                    "DDW -8.11538126703601\n" +
                    "BGX -8.1161490978881\n" +
                    "JMW -8.12905871540128\n" +
                    "FJU -8.12977090174473\n" +
                    "OTV -8.13090752679946\n" +
                    "LOO -8.13624802189005\n" +
                    "CKV -8.14512320384872\n" +
                    "BHH -8.14834297778683\n" +
                    "BMM -8.14861376316857\n" +
                    "CMW -8.15708056286975\n" +
                    "BYY -8.15775430487498\n" +
                    "BBN -8.18102040279566\n" +
                    "LLU -8.18284074862794\n" +
                    "FHH -8.19531176226931\n" +
                    "IIM -8.1965712991446\n" +
                    "WYY -8.20040933272194\n" +
                    "DGU -8.20542288590755\n" +
                    "DDU -8.20547259173102\n" +
                    "AAG -8.21278335026183\n" +
                    "JMM -8.2128593065976\n" +
                    "BGM -8.21979964680693\n" +
                    "IJV -8.22592989058639\n" +
                    "FMU -8.24837000360034\n" +
                    "WWZ -8.27237394968316\n" +
                    "QSW -8.27389087835845\n" +
                    "CTV -8.274068750062\n" +
                    "DGK -8.27627614066791\n" +
                    "JVX -8.28227230130016\n" +
                    "GJM -8.28981474537242\n" +
                    "IMV -8.29457539182803\n" +
                    "FNN -8.31412211448495\n" +
                    "DDF -8.3197879679889\n" +
                    "BNN -8.32232904391866\n" +
                    "FTT -8.33309907974601\n" +
                    "INU -8.33419820249172\n" +
                    "APQ -8.33532573820702\n" +
                    "MQZ -8.34282534909046\n" +
                    "CQZ -8.35264181226639\n" +
                    "FIO -8.35796834430495\n" +
                    "BCC -8.36119304046694\n" +
                    "BFF -8.36387621938225\n" +
                    "FNP -8.37660906672481\n" +
                    "OOT -8.37725176436361\n" +
                    "IIT -8.3815775528731\n" +
                    "FHU -8.38450589233056\n" +
                    "TTU -8.38763677818021\n" +
                    "CDV -8.39091765373538\n" +
                    "CFP -8.39574289464622\n" +
                    "CJW -8.40402119143269\n" +
                    "MRV -8.40644452659969\n" +
                    "MPW -8.41826790570575\n" +
                    "APV -8.41850958445924\n" +
                    "GUX -8.4262610859992\n" +
                    "GTU -8.42810190600704\n" +
                    "BGK -8.42891332462252\n" +
                    "EFU -8.43306632996636\n" +
                    "AKU -8.4389974955241\n" +
                    "GGY -8.44792265957332\n" +
                    "GJW -8.4542782546263\n" +
                    "FFG -8.45770705999\n" +
                    "CGM -8.45948333851588\n" +
                    "BBH -8.45965936137871\n" +
                    "JPW -8.48297445100382\n" +
                    "EKQ -8.48357428007902\n" +
                    "CPW -8.48912646407066\n" +
                    "FYY -8.49250499857726\n" +
                    "AVW -8.49345969775903\n" +
                    "BTT -8.49355636587244\n" +
                    "QTY -8.50849333392539\n" +
                    "HQY -8.51019513843248\n" +
                    "BGT -8.51224741784565\n" +
                    "BFN -8.53281454894454\n" +
                    "AJQ -8.53388252577198\n" +
                    "VWY -8.53786313959836\n" +
                    "BFT -8.54639833703704\n" +
                    "BBC -8.5474268646321\n" +
                    "AHU -8.56411787399153\n" +
                    "GWX -8.57902171871948\n" +
                    "HHU -8.58179453586567\n" +
                    "GRV -8.58381635609656\n" +
                    "BPT -8.59363689009169\n" +
                    "HQX -8.59463689716474\n" +
                    "GKP -8.6001321733205\n" +
                    "FFP -8.61172047559402\n" +
                    "BBM -8.63328107130472\n" +
                    "GUY -8.64807967275476\n" +
                    "BDP -8.66084623662911\n" +
                    "PPU -8.68017198478746\n" +
                    "NOU -8.70634621193163\n" +
                    "EQY -8.71239192760945\n" +
                    "AOW -8.71584921425347\n" +
                    "JOU -8.71683427594145\n" +
                    "DFP -8.74125218848342\n" +
                    "BFH -8.75204556973388\n" +
                    "HOV -8.75335271582651\n" +
                    "KRV -8.76997890776083\n" +
                    "CKQ -8.78342490358787\n" +
                    "NNW -8.79097041450975\n" +
                    "KVZ -8.79374986452562\n" +
                    "UWZ -8.79518009792091\n" +
                    "JQZ -8.79560452122865\n" +
                    "GQS -8.79801528968397\n" +
                    "MVX -8.79898725988161\n" +
                    "AIW -8.80483201417475\n" +
                    "GKM -8.8068420004564\n" +
                    "KOO -8.81143831459719\n" +
                    "BBT -8.81615610342042\n" +
                    "CCF -8.81911008761906\n" +
                    "BCP -8.8194928972732\n" +
                    "AAF -8.85129698009229\n" +
                    "GMM -8.85518896385631\n" +
                    "OOP -8.87011558509746\n" +
                    "IOY -8.87277708698773\n" +
                    "AEQ -8.87482403003763\n" +
                    "GMU -8.87663128774962\n" +
                    "FJW -8.88272677835016\n" +
                    "BNP -8.88964494104598\n" +
                    "EWW -8.89170220914778\n" +
                    "LLW -8.89239419700015\n" +
                    "ELQ -8.89487010329669\n" +
                    "GGL -8.91822281027942\n" +
                    "CIQ -8.92318276426496\n" +
                    "DOU -8.9331102746567\n" +
                    "ABU -8.9333483557958\n" +
                    "BMP -8.93698093065901\n" +
                    "DDG -8.93737091806091\n" +
                    "GKT -8.937497671242\n" +
                    "SUU -8.94579757898974\n" +
                    "AEI -8.95376998582805\n" +
                    "OTU -8.9675914634607\n" +
                    "PVY -8.97325908457809\n" +
                    "GYY -8.97425028570335\n" +
                    "APU -8.97555765142402\n" +
                    "CFJ -8.99081008795352\n" +
                    "IYY -8.99102403477471\n" +
                    "MOU -8.9918892211683\n" +
                    "HIQ -9.00173995154599\n" +
                    "CGP -9.01166789719704\n" +
                    "HLV -9.01230166894592\n" +
                    "CCJ -9.02023970890569\n" +
                    "IUX -9.02196303595058\n" +
                    "IKV -9.02468513156671\n" +
                    "LOU -9.0293763004946\n" +
                    "OQX -9.03330356702563\n" +
                    "CEQ -9.03986319971093\n" +
                    "GGH -9.04406207995152\n" +
                    "SVV -9.05016473654188\n" +
                    "HWW -9.05402469643882\n" +
                    "CIU -9.0797265119891\n" +
                    "ABV -9.10667908467214\n" +
                    "BIW -9.11291419331374\n" +
                    "RVW -9.1285857687274\n" +
                    "BFJ -9.13243868936761\n" +
                    "FFW -9.18363264960445\n" +
                    "KLV -9.20942433149789\n" +
                    "JPP -9.28845747703025\n" +
                    "ILQ -9.3005048591984\n" +
                    "GJP -9.31487110000748\n" +
                    "IRU -9.3170675488695\n" +
                    "FGK -9.32051010194116\n" +
                    "JOV -9.32085156568924\n" +
                    "BMW -9.33044708667087\n" +
                    "FJM -9.33193492164776\n" +
                    "RRR -9.33278240861652\n" +
                    "BJP -9.33980076985248\n" +
                    "IKQ -9.34519545426052\n" +
                    "BTW -9.34619624464008\n" +
                    "HPQ -9.34664639987793\n" +
                    "JRV -9.36355041290247\n" +
                    "QRY -9.37328240285343\n" +
                    "PVX -9.38952947908876\n" +
                    "JVZ -9.39468415054794\n" +
                    "MVY -9.3956899878205\n" +
                    "OOY -9.40462725153676\n" +
                    "AIQ -9.40594711532895\n" +
                    "EGU -9.4172915489509\n" +
                    "BRV -9.44569530057955\n" +
                    "CGJ -9.44611067609348\n" +
                    "KVX -9.44845473887039\n" +
                    "HII -9.46391229240652\n" +
                    "JOO -9.46433782263446\n" +
                    "DQX -9.47669748791605\n" +
                    "HTV -9.47981884261795\n" +
                    "MOV -9.48717190967075\n" +
                    "BFM -9.4884383145953\n" +
                    "AFQ -9.49797539056787\n" +
                    "GTW -9.50621585814733\n" +
                    "RRV -9.5134632854507\n" +
                    "FMM -9.51527681774145\n" +
                    "HQR -9.51564717730123\n" +
                    "NQX -9.52186850137184\n" +
                    "FVZ -9.5230724159094\n" +
                    "IIK -9.53932113471661\n" +
                    "HKQ -9.54324821593303\n" +
                    "FVX -9.54906383452774\n" +
                    "QWZ -9.55912268888254\n" +
                    "EPQ -9.55919972513404\n" +
                    "BVZ -9.59257995632411\n" +
                    "GGO -9.59467142547792\n" +
                    "GVY -9.60745767958394\n" +
                    "MVZ -9.61788739997886\n" +
                    "DQT -9.63862802463868\n" +
                    "DDD -9.64303082644516\n" +
                    "OQT -9.64811619955954\n" +
                    "GTT -9.65257407792025\n" +
                    "FMP -9.65450422988939\n" +
                    "HNV -9.66240710231488\n" +
                    "BCF -9.67359333064727\n" +
                    "DQR -9.67477501358795\n" +
                    "ILU -9.69484543134515\n" +
                    "IPV -9.69626747469395\n" +
                    "ABQ -9.70540547427682\n" +
                    "FQX -9.70541450574654\n" +
                    "DTV -9.7272043718044\n" +
                    "FGM -9.73299884676485\n" +
                    "AAW -9.74978536292909\n" +
                    "BOO -9.76052564134155\n" +
                    "FRV -9.76572185343479\n" +
                    "HUW -9.7757626344518\n" +
                    "KQX -9.79477616661927\n" +
                    "CQT -9.80267540052142\n" +
                    "AOV -9.8040150211078\n" +
                    "FGJ -9.81727741326312\n" +
                    "AWW -9.8410045608225\n" +
                    "IIP -9.85257140886154\n" +
                    "QRX -9.86444926555016\n" +
                    "VWZ -9.86896524430053\n" +
                    "FGU -9.8798540579446\n" +
                    "DHV -9.88855733073531\n" +
                    "LQY -9.89126122653817\n" +
                    "KNV -9.90118889244154\n" +
                    "NQY -9.91598537116007\n" +
                    "BGU -9.9209904893975\n" +
                    "HOU -9.93040139894835\n" +
                    "DQY -9.94701552314684\n" +
                    "EUY -9.95028481737645\n" +
                    "GVZ -9.95113259412663\n" +
                    "IMU -9.95540131833885\n" +
                    "PQY -9.95993227314945\n" +
                    "AEE -9.9613769330411\n" +
                    "DGG -9.98542793882623\n" +
                    "GLV -10.0055012160454\n" +
                    "ITU -10.0110287334041\n" +
                    "PQX -10.0265656777212\n" +
                    "GPP -10.0284358157965\n" +
                    "IMQ -10.0309155093578\n" +
                    "EMQ -10.0427160100763\n" +
                    "UWX -10.0505795262894\n" +
                    "AFV -10.0651078011074\n" +
                    "DIU -10.0971896724439\n" +
                    "RUV -10.1090899291928\n" +
                    "FMW -10.117459572222\n" +
                    "BBU -10.128615708746\n" +
                    "VWX -10.1301915565412\n" +
                    "TTW -10.1448741499647\n" +
                    "KQR -10.1466217846019\n" +
                    "KOV -10.1481222788048\n" +
                    "PVZ -10.1558780510908\n" +
                    "IPQ -10.1710155741804\n" +
                    "GPU -10.1872097355011\n" +
                    "IQY -10.1924615834521\n" +
                    "OPU -10.1969993184048\n" +
                    "GQZ -10.1974210978345\n" +
                    "CGU -10.2075356067317\n" +
                    "CFW -10.2475971318245\n" +
                    "NQR -10.2698596150405\n" +
                    "EJQ -10.3185005529532\n" +
                    "IOW -10.3637484883539\n" +
                    "UVX -10.3673003139452\n" +
                    "OVW -10.3932858944018\n" +
                    "PQT -10.3943240711593\n" +
                    "LQT -10.4123123506673\n" +
                    "IOV -10.424131743676\n" +
                    "AGU -10.4253438420148\n" +
                    "GMW -10.4292717968352\n" +
                    "MNV -10.4324018926663\n" +
                    "GKU -10.4327388386316\n" +
                    "KUW -10.4388524912259\n" +
                    "BFU -10.4420380014921\n" +
                    "DNQ -10.4427786720125\n" +
                    "BLV -10.4571855507973\n" +
                    "GGJ -10.4575620916364\n" +
                    "AAV -10.4610873214009\n" +
                    "WWY -10.461425084303\n" +
                    "CQX -10.4661737294041\n" +
                    "LPV -10.4780394261826\n" +
                    "BVY -10.4899343663475\n" +
                    "AFU -10.5091914036361\n" +
                    "LLL -10.5285325691185\n" +
                    "RUW -10.5321730909076\n" +
                    "JKV -10.5345876180583\n" +
                    "JNV -10.5382075482554\n" +
                    "JUW -10.5448576901509\n" +
                    "DHQ -10.5506857626105\n" +
                    "AQW -10.568625462232\n" +
                    "HKV -10.5738237412454\n" +
                    "KNQ -10.5758934326203\n" +
                    "AUY -10.5815693434017\n" +
                    "MMW -10.584446556265\n" +
                    "JLV -10.5903065514253\n" +
                    "HOQ -10.5969193200095\n" +
                    "AEO -10.6152798799446\n" +
                    "OPV -10.6180046105922\n" +
                    "CFG -10.6244050610613\n" +
                    "JQX -10.6251344433074\n" +
                    "JQY -10.6262443845033\n" +
                    "KQT -10.6310264426972\n" +
                    "CQR -10.635876813276\n" +
                    "EIQ -10.635898739226\n" +
                    "RWW -10.6390265861351\n" +
                    "MQX -10.6399601240565\n" +
                    "IVW -10.6408345416163\n" +
                    "PPW -10.6421328937365\n" +
                    "HVW -10.6497937381431\n" +
                    "NNN -10.6684888790229\n" +
                    "TTT -10.6744173553422\n" +
                    "DUW -10.6807395872727\n" +
                    "LUV -10.6905716484737\n" +
                    "LMV -10.7142766203755\n" +
                    "AGQ -10.7202857057629\n" +
                    "BOU -10.7242987588517\n" +
                    "CCW -10.7243739752686\n" +
                    "FOO -10.7263293828318\n" +
                    "PQR -10.7395158158626\n" +
                    "KOU -10.7417823664418\n" +
                    "EFQ -10.7427933605898\n" +
                    "JKQ -10.7640125519499\n" +
                    "HNQ -10.7792190441993\n" +
                    "LQX -10.7818075663487\n" +
                    "FPU -10.8196814395428\n" +
                    "GGX -10.8327789584718\n" +
                    "GII -10.8408637146087\n" +
                    "WWX -10.8614192798035\n" +
                    "NUW -10.8839202991679\n" +
                    "BCW -10.8916576956641\n" +
                    "GOV -10.9010718871737\n" +
                    "CQY -10.9014699504698\n" +
                    "BCG -10.918943218057\n" +
                    "EVV -10.9230370977678\n" +
                    "LLV -10.9341918126224\n" +
                    "FGW -10.9354047105483\n" +
                    "FLV -10.9671874110197\n" +
                    "OWW -10.9704117345532\n" +
                    "BIV -10.9889305046369\n" +
                    "MQY -10.998821295819\n" +
                    "KWW -10.99892791742\n" +
                    "DJV -11.0054767269001\n" +
                    "GGM -11.0356339721418\n" +
                    "DWW -11.044583652465\n" +
                    "BPU -11.0489083383454\n" +
                    "OQR -11.0717552964969\n" +
                    "GGT -11.0746549163925\n" +
                    "CCG -11.0763060303095\n" +
                    "LVW -11.0787019616685\n" +
                    "BVX -11.0996496335701\n" +
                    "FIV -11.1153737860632\n" +
                    "FVY -11.1310319334075\n" +
                    "FIQ -11.1369777688689\n" +
                    "GPW -11.1696428813532\n" +
                    "FII -11.1853980647438\n" +
                    "GVX -11.1922725939241\n" +
                    "BBG -11.2042598924028\n" +
                    "DVW -11.2180315282678\n" +
                    "CNQ -11.2226216919008\n" +
                    "QWY -11.2439959732874\n" +
                    "UWY -11.2465992991416\n" +
                    "DOQ -11.2598497097596\n" +
                    "EEI -11.2944176836551\n" +
                    "NVW -11.3121815351503\n" +
                    "IJU -11.3268506824289\n" +
                    "MQT -11.3404513027442\n" +
                    "FQY -11.3442100742703\n" +
                    "BOV -11.3450487709416\n" +
                    "FJP -11.3605090811594\n" +
                    "EIO -11.3755177312872\n" +
                    "GOO -11.4031257955524\n" +
                    "FQR -11.4407785670024\n" +
                    "HJV -11.4414647154974\n" +
                    "NOQ -11.454526129866\n" +
                    "NWW -11.4553503212307\n" +
                    "CMV -11.4558197390788\n" +
                    "BEQ -11.4661237992472\n" +
                    "BII -11.48292157712\n" +
                    "HQW -11.4997562418823\n" +
                    "FPP -11.5061507259161\n" +
                    "IPU -11.5133090707225\n" +
                    "OOW -11.5226781983074\n" +
                    "BGW -11.542130267513\n" +
                    "BFG -11.5569764943079\n" +
                    "DKV -11.5599950858649\n" +
                    "OQY -11.5659266201174\n" +
                    "LUW -11.5845872395698\n" +
                    "HPV -11.5875027552977\n" +
                    "IIJ -11.6026001036373\n" +
                    "FQT -11.6403249984532\n" +
                    "DMV -11.6480396939324\n" +
                    "IKU -11.6515260989158\n" +
                    "JQT -11.6587099715195\n" +
                    "EUV -11.6597788760374\n" +
                    "MQR -11.6690214960579\n" +
                    "CPV -11.6831655595157\n" +
                    "DKQ -11.692569289643\n" +
                    "CCV -11.6960867693856\n" +
                    "JTV -11.7253718457729\n" +
                    "AAE -11.753141161288\n" +
                    "HMV -11.7536753338025\n" +
                    "EQW -11.7952379125894\n" +
                    "UVZ -11.8177849866139\n" +
                    "IJQ -11.8268420229689\n" +
                    "FOU -11.8474206117614\n" +
                    "BPP -11.8587406656515\n" +
                    "QSV -11.9010752773693\n" +
                    "COQ -11.9094712117032\n" +
                    "CJV -11.9150751747055\n" +
                    "CUV -11.9224293109255\n" +
                    "CDQ -11.9239523271536\n" +
                    "IIY -11.9322275306122\n" +
                    "AAQ -11.9355734363606\n" +
                    "HMQ -11.9440396898166\n" +
                    "KLQ -11.9526818335582\n" +
                    "FPW -11.9639130528145\n" +
                    "GNQ -11.9641171554327\n" +
                    "NPV -11.9865071844033\n" +
                    "KOQ -12.0072283654987\n" +
                    "GHV -12.0130235029445\n" +
                    "DLQ -12.0152575426579\n" +
                    "LWW -12.0222434795957\n" +
                    "BFW -12.0248511091254\n" +
                    "MTV -12.0250235491931\n" +
                    "OUY -12.056161248478\n" +
                    "HLQ -12.0610405060477\n" +
                    "AVV -12.1246074632768\n" +
                    "KPQ -12.1285060704966\n" +
                    "NUV -12.1394587922975\n" +
                    "TUW -12.1450121015635\n" +
                    "EEQ -12.1481151074773\n" +
                    "HIU -12.1537350177415\n" +
                    "BQY -12.1818117719408\n" +
                    "PTV -12.2146356289482\n" +
                    "NNV -12.2166313648748\n" +
                    "FOV -12.2217985739081\n" +
                    "CGW -12.2465663220669\n" +
                    "DPV -12.24747310274\n" +
                    "LQR -12.255855925364\n" +
                    "QWX -12.319094766697\n" +
                    "HJQ -12.3373887953995\n" +
                    "MNQ -12.3381632282951\n" +
                    "DGV -12.3541621382913\n" +
                    "KVW -12.3604975320309\n" +
                    "BBF -12.3734842366074\n" +
                    "DDV -12.3906459355556\n" +
                    "KQW -12.4142961154212\n" +
                    "GIQ -12.4157750395179\n" +
                    "UYY -12.4163907779304\n" +
                    "CGG -12.436905444793\n" +
                    "BIQ -12.4554272588167\n" +
                    "DUV -12.4692458309719\n" +
                    "EEO -12.474813869867\n" +
                    "GGP -12.4837714320416\n" +
                    "FGP -12.4857713074471\n" +
                    "IWW -12.4865770527552\n" +
                    "OPQ -12.5106404817277\n" +
                    "QTT -12.5339168546209\n" +
                    "BGG -12.5382209097034\n" +
                    "GOU -12.5447813728432\n" +
                    "GGK -12.5459713313154\n" +
                    "EEE -12.5483750377528\n" +
                    "JVW -12.5690213423306\n" +
                    "BQX -12.5796594361114\n" +
                    "CLQ -12.5914013042552\n" +
                    "FIU -12.602632070954\n" +
                    "BBW -12.6264294135932\n" +
                    "JWW -12.6264629101751\n" +
                    "BBP -12.6459059591398\n" +
                    "BQT -12.653477197119\n" +
                    "FGG -12.6644997609788\n" +
                    "BIU -12.6680972122887\n" +
                    "VYY -12.6783923933531\n" +
                    "DPQ -12.7068018127713\n" +
                    "LNQ -12.7195920402877\n" +
                    "GGU -12.7235978520852\n" +
                    "BDV -12.7345259486773\n" +
                    "NPQ -12.7442074159802\n" +
                    "DMQ -12.7529854925604\n" +
                    "GIU -12.7614684009692\n" +
                    "JOQ -12.7932690914079\n" +
                    "BQR -12.7982206727946\n" +
                    "TWW -12.8380867878227\n" +
                    "TUV -12.8382858460308\n" +
                    "EGQ -12.8632400488356\n" +
                    "KTV -12.8633367494607\n" +
                    "GTV -12.8645126687832\n" +
                    "MOQ -12.8781912704807\n" +
                    "HHV -12.8845729117392\n" +
                    "BPW -12.8907326938065\n" +
                    "JNQ -12.8947670779075\n" +
                    "AEU -12.896129482197\n" +
                    "LOQ -12.9141589597507\n" +
                    "FKQ -12.9347121884437\n" +
                    "AOQ -12.955871482991\n" +
                    "BGP -12.9605195991845\n" +
                    "MUW -12.9863888218735\n" +
                    "AQV -12.9923145562605\n" +
                    "LPQ -13.0023532911619\n" +
                    "FHQ -13.0440475638416\n" +
                    "JUV -13.0555013526032\n" +
                    "QTW -13.0783271035203\n" +
                    "DJQ -13.0788473574128\n" +
                    "AIO -13.0840311916684\n" +
                    "TVW -13.1025292949201\n" +
                    "FLQ -13.1224239005907\n" +
                    "CPQ -13.1611353873056\n" +
                    "TTV -13.2146114432851\n" +
                    "PUW -13.2377454201043\n" +
                    "BKQ -13.2407310289854\n" +
                    "BNV -13.2418655432959\n" +
                    "IIV -13.2431531426978\n" +
                    "JQR -13.2894300100366\n" +
                    "GQY -13.2957048352732\n" +
                    "BJV -13.3009965867024\n" +
                    "DFQ -13.3154285235327\n" +
                    "FNV -13.3351104701829\n" +
                    "DQW -13.3432979024461\n" +
                    "CUW -13.3456063973312\n" +
                    "EUW -13.3562594359943\n" +
                    "GJV -13.3775592212056\n" +
                    "QRW -13.4187082919284\n" +
                    "UVY -13.4304692322959\n" +
                    "MWW -13.4522237533513\n" +
                    "FFQ -13.4705596887536\n" +
                    "HHQ -13.4774428245835\n" +
                    "FUW -13.545195932453\n" +
                    "CWW -13.5534189787031\n" +
                    "CVW -13.5626202641816\n" +
                    "FNQ -13.5745218476657\n" +
                    "GGW -13.5803212333859\n" +
                    "DFV -13.6098359604648\n" +
                    "IQW -13.6176057167118\n" +
                    "IVV -13.6656372689372\n" +
                    "KPV -13.6818757321548\n" +
                    "KMQ -13.7020637660832\n" +
                    "BCV -13.746275279611\n" +
                    "EQV -13.7673542181835\n" +
                    "QRR -13.8070957880674\n" +
                    "BTV -13.8117468656125\n" +
                    "RVV -13.83609567712\n" +
                    "FTV -13.8367409572414\n" +
                    "AUV -13.8404441639907\n" +
                    "BDQ -13.8427798898953\n" +
                    "IIW -13.8985625554354\n" +
                    "GHQ -13.9345810539972\n" +
                    "BLQ -13.9397358359901\n" +
                    "PWW -13.9501781358752\n" +
                    "JMV -13.9593375342872\n" +
                    "CFV -13.9616748491676\n" +
                    "UUZ -13.9690502989176\n" +
                    "BKV -13.9776456266334\n" +
                    "GQT -14.000297186933\n" +
                    "CGV -14.0035304783098\n" +
                    "EOQ -14.0166450309192\n" +
                    "FOQ -14.0350968377921\n" +
                    "KMV -14.0410711150366\n" +
                    "MPV -14.0415837258252\n" +
                    "AAI -14.0773969541341\n" +
                    "NQW -14.082788786122\n" +
                    "DDQ -14.1059139385947\n" +
                    "LMQ -14.1207846174103\n" +
                    "CMQ -14.1355510675223\n" +
                    "GQR -14.1378839175592\n" +
                    "BHV -14.165592594505\n" +
                    "BHQ -14.1808548667022\n" +
                    "BUW -14.2377794080126\n" +
                    "MPQ -14.2957908012778\n" +
                    "VVX -14.2989711909528\n" +
                    "FFV -14.3317216671061\n" +
                    "FHV -14.3593790881458\n" +
                    "BOQ -14.3765353831556\n" +
                    "VVZ -14.4172913081962\n" +
                    "OOV -14.418969198673\n" +
                    "HUV -14.4302126800435\n" +
                    "MMV -14.4452940516843\n" +
                    "JPV -14.4614887145138\n" +
                    "IOQ -14.4622327300011\n" +
                    "JQW -14.4751874118794\n" +
                    "BFP -14.5406896967712\n" +
                    "EOO -14.5462213111656\n" +
                    "FWW -14.5530643242341\n" +
                    "GQX -14.5565716391121\n" +
                    "BJQ -14.6149213237956\n" +
                    "BNQ -14.6319946735325\n" +
                    "MUV -14.6428769964505\n" +
                    "NNQ -14.6503843973971\n" +
                    "UUX -14.6747260217107\n" +
                    "IUY -14.6754263946197\n" +
                    "EII -14.7292022595911\n" +
                    "OQW -14.7375591032963\n" +
                    "LVV -14.7646910627921\n" +
                    "AUW -14.7693854084706\n" +
                    "FKV -14.8351504195662\n" +
                    "QVZ -14.8357601032441\n" +
                    "JLQ -14.8614036404365\n" +
                    "GUW -14.8717340669583\n" +
                    "GVW -14.8725964225299\n" +
                    "GMV -14.884326814328\n" +
                    "EOU -14.9205501925181\n" +
                    "JPQ -14.945018238817\n" +
                    "IQV -15.0068243129901\n" +
                    "QYY -15.0096439540988\n" +
                    "GUV -15.0197197451974\n" +
                    "PPQ -15.0292920823436\n" +
                    "PPV -15.0757600937326\n" +
                    "QVX -15.0948921853122\n" +
                    "CFQ -15.0959515928106\n" +
                    "GWW -15.1196013922637\n" +
                    "LLQ -15.1312568823713\n" +
                    "CCQ -15.1329958978701\n" +
                    "IIQ -15.1404574425703\n" +
                    "KUV -15.1591060301\n" +
                    "RUU -15.1605453360076\n" +
                    "MVW -15.1658642687687\n" +
                    "BWW -15.2033489444951\n" +
                    "GGG -15.2107528973473\n" +
                    "CJQ -15.259832245361\n" +
                    "EEU -15.2740679965389\n" +
                    "VVY -15.2938715816953\n" +
                    "GKV -15.3148851787759\n" +
                    "BMV -15.331253093249\n" +
                    "DGQ -15.3439155201229\n" +
                    "JMQ -15.4510911635626\n" +
                    "LQW -15.456655370897\n" +
                    "FJV -15.5181879602361\n" +
                    "PVW -15.5545445916879\n" +
                    "CUU -15.608752796964\n" +
                    "FJQ -15.6111073366209\n" +
                    "OVV -15.6353649561119\n" +
                    "OUW -15.6427560128147\n" +
                    "GLQ -15.6981359663545\n" +
                    "MMQ -15.7219647665286\n" +
                    "PUV -15.7316396897673\n" +
                    "AAO -15.7563478406394\n" +
                    "EIU -15.8131366496591\n" +
                    "FQW -15.8474490910035\n" +
                    "FMQ -15.8541066640837\n" +
                    "BMQ -15.8696272784076\n" +
                    "NUU -15.8741857016422\n" +
                    "CVV -15.9107921442361\n" +
                    "GOQ -15.9462428959181\n" +
                    "AII -15.9706266022672\n" +
                    "LUU -15.9726870212102\n" +
                    "NVV -15.9731869915869\n" +
                    "FVW -16.0001142131222\n" +
                    "JUU -16.0016176183871\n" +
                    "QRV -16.0227969998954\n" +
                    "OUV -16.040371735263\n" +
                    "GPV -16.0636895577989\n" +
                    "QVY -16.1730195884881\n" +
                    "PQW -16.1871336400323\n" +
                    "DVV -16.2105317157539\n" +
                    "BCQ -16.2201259328411\n" +
                    "MUU -16.2235726956135\n" +
                    "BGV -16.2688200902808\n" +
                    "TUU -16.2922166645644\n" +
                    "FGV -16.315025396259\n" +
                    "DUU -16.3528679337618\n" +
                    "GJQ -16.3682957518354\n" +
                    "GGV -16.3882805940435\n" +
                    "GKQ -16.4065924586738\n" +
                    "FMV -16.4311287379716\n" +
                    "BUV -16.466194770855\n" +
                    "BVW -16.4968277847613\n" +
                    "QTV -16.5156280607073\n" +
                    "AOO -16.5570463906532\n" +
                    "FUV -16.563378943552\n" +
                    "IUV -16.6775090318728\n" +
                    "KUU -16.7398185707699\n" +
                    "AOU -16.7459607227374\n" +
                    "MQW -16.7527832987662\n" +
                    "TVV -16.8532580044725\n" +
                    "BBV -16.8783770459883\n" +
                    "HUU -16.8802261109819\n" +
                    "FPQ -16.942010994476\n" +
                    "AAA -16.9755666149765\n" +
                    "CQW -17.0016636575758\n" +
                    "IOO -17.0217815656288\n" +
                    "GMQ -17.2060746855314\n" +
                    "HVV -17.2277315211734\n" +
                    "AIU -17.234347178016\n" +
                    "FGQ -17.237487274234\n" +
                    "PUU -17.2584902612953\n" +
                    "GPQ -17.3487041512543\n" +
                    "BFQ -17.5153793045077\n" +
                    "BBQ -17.5525429211713\n" +
                    "DQV -17.6407113501278\n" +
                    "BUU -17.7215935247045\n" +
                    "BQW -17.7860206842289\n" +
                    "NQV -17.7988466343074\n" +
                    "IIO -18.073403236787\n" +
                    "GQW -18.0780527565526\n" +
                    "LQV -18.0935707130448\n" +
                    "AAU -18.1193399889132\n" +
                    "VWW -18.134492771351\n" +
                    "FUU -18.1370280957071\n" +
                    "OOQ -18.1391976345575\n" +
                    "OQV -18.2854379068594\n" +
                    "HQV -18.2947563953302\n" +
                    "CGQ -18.3058255419744\n" +
                    "BFV -18.3099230243852\n" +
                    "UUY -18.3243844078424\n" +
                    "UWW -18.3249857656686\n" +
                    "BGQ -18.3268111172512\n" +
                    "UVW -18.3320645382324\n" +
                    "BPQ -18.3593578052318\n" +
                    "JVV -18.3805676282179\n" +
                    "IUW -18.4092472880485\n" +
                    "FPV -18.4793756113487\n" +
                    "KVV -18.5860739421556\n" +
                    "MVV -18.5875179563225\n" +
                    "CQV -18.7994614605243\n" +
                    "BPV -18.8130262643199\n" +
                    "GUU -18.9555617173257\n" +
                    "IOU -19.0391853018252\n" +
                    "KQV -19.2172389179192\n" +
                    "GVV -19.2671040020458\n" +
                    "PVV -19.3684093837975\n" +
                    "JQV -19.4172117283038\n" +
                    "GGQ -19.4971050035589\n" +
                    "VVW -19.6425448256688\n" +
                    "QWW -20.0638130592751\n" +
                    "UVV -20.1486714849771\n" +
                    "BVV -20.1913592725387\n" +
                    "QVW -20.2100582715429\n" +
                    "OOO -20.2128431258258\n" +
                    "OOU -20.5804886131736\n" +
                    "FVV -20.6077529363829\n" +
                    "PQV -20.7657852746372\n" +
                    "MQV -21.1442312360168\n" +
                    "EUU -21.3425323005168\n" +
                    "FQV -21.3684246164518\n" +
                    "III -21.4601415728014\n" +
                    "GQV -21.4603224109036\n" +
                    "BQV -22.2041089680942\n" +
                    "IIU -22.5212277525291\n" +
                    "AUU -22.563223348996\n" +
                    "UUW -22.756048082354\n" +
                    "UUV -22.93010456805\n" +
                    "OUU -23.9885188274969\n" +
                    "QVV -24.0469773136106\n" +
                    "IUU -25.1298871588053\n" +
                    "UUU -28.9508350221993\n";
            leaves[3] = "";
            leaves[4] = "";
            leaves[5] = "";
        }
    }
}
