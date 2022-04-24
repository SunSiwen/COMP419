import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * -----------------------------------------
 * NAME: Ran Shi, Siwen Sun
 * STUDENT NUMBER: 7814643, 7898970
 * COURSE: COMP 4190, SECTION: A01
 * INSTRUCTOR: Cuneyt Akcora
 * ASSIGNMENT: Assignment 3 - Reinforcement Learning
 * -----------------------------------------
 */

public class QLearningAgent {

    private double bound = 0;
    public double noise;
    private int rows, cols;
    private int[][] terminal, boulder;
    private int[] robotStart;
    private int depth, episodes;
    private double alpha, discount, transCost;

    QState[][] grid; // to hold list of states
    private boolean testMode = false; // for test

    /**
     * Constructor
     * Takes a grid and runs value iteration for the specified number of iterations
     */
    public QLearningAgent() {
        inputGridConf("GridConf.txt");
        generateGrid();
        if (testMode) {
            printGridConf();
            printGrid(0);
        }
    }


    public QLearningAgent(int x, int y, int step, String line, int goal, String file, double epsilon) {
        bound = epsilon;
        inputGridConf(file);
        if (0 <= x && x < rows && 0 <= y && y < cols) { // check edges
            // reassigns the target variables
            episodes = step;

            generateGrid();
            if (testMode) {
                printGrid(1);
            }
            // start Q-learning
            qLearning();
            printResult(x, y, line, goal);
        } else {
            System.out.println(line + ": Invalid Input.");
        }
    }


    /**
     * GET THE QVALUE OF A STATE BY AN ACTION
     *
     * @param actionIndex : ACTION
     * @param qState      : STATE
     * @return double : QVALUE
     * @author Siwen Sun
     * @date 2022/4/24 14:30
     */
    public double getQValue(int actionIndex, QState qState) {
        int i = qState.getX();
        int j = qState.getY();

        QState invalid = new QState(-1, -1);
        QState vk[] = new QState[4];

//    private final String[] policy = {"East", "North", "West", "South"};
        if (i > 0 && !grid[i - 1][j].isBoulder())
            vk[2] = grid[i - 1][j];
        else
            vk[2] = invalid;
        //east
        if (i < rows - 1 && !grid[i + 1][j].isBoulder())
            vk[0] = grid[i + 1][j];
        else
            vk[0] = invalid;
        //south
        if (j > 0 && !grid[i][j - 1].isBoulder())
            vk[3] = grid[i][j - 1];
        else
            vk[3] = invalid;
        //north
        if (j < cols - 1 && !grid[i][j + 1].isBoulder())
            vk[1] = grid[i][j + 1];
        else
            vk[1] = invalid;

        int left = (actionIndex + 1) % 4;
        int right = (actionIndex + 3) % 4;
        double oldValue = qState.getActions().get(qState.getPolicy()[actionIndex]).getValue();
        double goStraight = (1 - noise) * (discount * maxQ(vk[actionIndex]));

        double goLeft = noise / 2 * (discount * maxQ(vk[left]));
        double goRight = noise / 2 * (discount * maxQ(vk[right]));

        return (1 - alpha) * oldValue + alpha * (transCost + goStraight + goLeft + goRight);

    }

    /**
     * GET THE MAXQ OF A STATE
     *
     * @param qState : A STATE
     * @return double : MAXQ
     * @author Siwen Sun
     * @date 2022/4/24 14:30
     */
    public static double maxQ(QState qState) {
        double res = -9999.0;
        for (QAction value : qState.getActions().values()) {
            if (value.getValue() > res) {
                res = value.getValue();
            }
        }
        return res;
    }

    /**
     * DECIDE THE ACTION FORM A STATE, I USE EPSILON-GREEDY
     *
     * @param qState : A STATE
     * @return int : ACTION
     * @author Siwen Sun
     * @date 2022/4/24 14:31
     */
    public int getPolicy(QState qState) {
        Random random = new Random();
        int i = random.nextInt(100);
        if (i < bound * 100) {
            return random.nextInt(100) % 4;
        }

        double maxValue = -9999.0;

        ArrayList<Integer> res = new ArrayList<>();
        res.add(0);
        for (int j = 0; j < qState.getPolicy().length; j++) {
            double value = getQValue(j, qState);

            if (value > maxValue) {
                res.clear();
                res.add(j);
                maxValue = value;
            } else if (value == maxValue) {
                res.add(j);
            }
        }

        int direction = res.get(random.nextInt(100) % res.size());
//        System.out.println("I am here in " + qState.getX() + " " + qState.getY() + " " + qState.getPolicy()[direction] + " " + maxValue + " " + direction);
        return direction;
    }

    /**
     * Reads input from GridConf.txt, then loads variables
     */
    private void inputGridConf(String path) {
        File file = new File(path);
//        File file = new File("GridConf.txt");
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(file));
            // x=4
            String line = bf.readLine();
            String[] s = line.split("=");
            rows = Integer.parseInt(s[1]);
            // y=3
            line = bf.readLine();
            s = line.split("=");
            cols = Integer.parseInt(s[1]);


            //Terminal={1={3,1,-1},2={3,2,1}}
            line = bf.readLine();
            s = line.split("=\\{|},|\\{|}|=|,");
            int num = (s.length - 1) / 4;
            terminal = new int[num][3];
            for (int i = 0; i < num; i++) {
                terminal[i][0] = Integer.parseInt(s[i * 4 + 2]);
                terminal[i][1] = Integer.parseInt(s[i * 4 + 3]);
                terminal[i][2] = Integer.parseInt(s[i * 4 + 4]);
            }
            // Boulder={1={1,1}}
            line = bf.readLine();
            s = line.split("=\\{|},|\\{|}|=|,");
            num = (s.length - 1) / 3;
            boulder = new int[num][2];
            for (int i = 0; i < num; i++) {
                boulder[i][0] = Integer.parseInt(s[i * 3 + 2]);
                boulder[i][1] = Integer.parseInt(s[i * 3 + 3]);
            }
            // RobotStartState={0,0}
            line = bf.readLine();
            s = line.split("=\\{|},|\\{|}|=|,");
            robotStart = new int[2];
            robotStart[0] = Integer.parseInt(s[1]);
            robotStart[1] = Integer.parseInt(s[2]);


            // K=1000
            line = bf.readLine();
            s = line.split("=");
            depth = Integer.parseInt(s[1]);
            // Episodes=3500, not use in MDP
            line = bf.readLine();
            s = line.split("=");
            episodes = Integer.parseInt(s[1]);


            // alpha=0.2
            line = bf.readLine();
            s = line.split("=");
            alpha = Double.parseDouble(s[1]);
            // Discount=0.9
            line = bf.readLine();
            s = line.split("=");
            discount = Double.parseDouble(s[1]);
            //Noise=0.2
            line = bf.readLine();
            s = line.split("=");
            noise = Double.parseDouble(s[1]);
            //TransitionCost=0
            line = bf.readLine();
            s = line.split("=");
            transCost = Double.parseDouble(s[1]);

            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printGridConf() {  // helper, to check input
        System.out.println("=========================================");
        System.out.println("GridConf:");
        System.out.println("");
        System.out.println("x=" + rows);
        System.out.println("y=" + cols);
        String str = "Terminal=[";
        for (int i = 0; i < terminal.length; i++) {
            str = str + Arrays.toString(terminal[i]);
        }
        System.out.println(str + "]");
        str = "Boulder=[";
        for (int i = 0; i < boulder.length; i++) {
            str = str + Arrays.toString(boulder[i]);
        }
        System.out.println(str + "]");
        System.out.println("RobotStartState=" + Arrays.toString(robotStart));
        System.out.println("K=" + depth);
        System.out.println("Episodes=" + episodes);
        System.out.println("Alpha=" + alpha);
        System.out.println("Discount=" + discount);
        System.out.println("Noise=" + noise);
        System.out.println("TransitionCost=" + transCost);
        System.out.println("=========================================");
    }

    /**
     * Generates the grid
     */
    private void generateGrid() {
        grid = new QState[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new QState(i, j);
            }
        }
        for (int i = 0; i < terminal.length; i++) {
            int x = terminal[i][0];
            int y = terminal[i][1];
            grid[x][y].setTerminal(terminal[i][2]);
        }
        for (int i = 0; i < boulder.length; i++) {
            int x = boulder[i][0];
            int y = boulder[i][1];
            grid[x][y].setBoulder();
        }
    }

    private void printGrid(int e) {
        System.out.println("episode=" + e);
        String[] graph = new String[cols];
        for (int i = cols - 1; i >= 0; i--) {
            graph[i] = "";
            for (int j = 0; j < rows; j++) {
                graph[i] = graph[i] + grid[j][i].drawCurrState();
            }
        }
        for (int i = cols - 1; i >= 0; i--) {
            System.out.println(graph[i]);
        }
    }


    /**
     * Prints result of this query
     *
     * @param x    : x from results.txt
     * @param y    : y from results.txt
     * @param line : current line read from results.txt
     * @param goal : query from results.txt
     */
    private void printResult(int x, int y, String line, int goal) {
        // "stateValue"=0, "bestPolicy"=1, "bestQValue"=2;
        if (goal == 0) {
            double v = grid[x][y].getMaxValue() * (1 - bound) + grid[x][y].getAllValue() * bound / 4;
            System.out.println(line + ": " + v);
        } else if (goal == 1) {
            System.out.println(line + ": Go " + grid[x][y].getPolicy()[grid[x][y].getMaxIndex()]);
        } else if (goal == 2) {
            double v = Double.parseDouble(String.format("%.2f", grid[x][y].getMaxValue()));
            System.out.println(line + ": " + v);
        } else {
            System.out.println(line + ": Invalid Input.");
        }
    }

    public void qLearning() {
        for (int i = 0; i < episodes; i++) {
            // set initial position
            int currX;
            int currY;
            currX = robotStart[0];
            currY = robotStart[1];

            while (true) {
                // check if the move is valid
                int direction = getPolicy(grid[currX][currY]);

                int nextX = currX;
                int nextY = currY;
                switch (direction) {
                    case 0: // go east
                        nextX = currX + 1 == grid.length || grid[currX + 1][currY].isBoulder() ? currX : currX + 1;
                        break;
                    case 1: // go north
                        nextY = currY + 1 == grid[0].length || grid[currX][currY + 1].isBoulder() ? currY : currY + 1;
                        break;
                    case 2: // go west
                        nextX = currX == 0 || grid[currX - 1][currY].isBoulder() ? currX : currX - 1;
                        break;
                    case 3: // go south
                        nextY = currY == 0 || grid[currX][currY - 1].isBoulder() ? currY : currY - 1;
                        break;
                }


                update(currX, currY, direction, this);
                // update position
                currX = nextX;
                currY = nextY;

                if (grid[currX][currY].isTerminal()) {
                    // calculate q value for exit
//                    System.out.println("hello  i am " + currX + " " + currY);
                    break;
                }
            }
//            printGrid(i);
        }
    }

    private void update(int currX, int currY, int direction, QLearningAgent qLearningAgent) {
        grid[currX][currY].updateValue(direction, qLearningAgent);
    }

    private boolean checkValidMove(int currX, int currY, int direction) { // return true if this move is valid
        boolean result = false;
        switch (direction) {
            case 0: // go east
                if (currX < rows - 1 && !grid[currX + 1][currY].isBoulder())
                    result = true;
                break;

            case 1: // go north
                if (currY > 0 && !grid[currX][currY - 1].isBoulder())
                    result = true;
                break;

            case 2: // go west
                if (currX > 0 && !grid[currX - 1][currY].isBoulder())
                    result = true;
                break;

            case 3: // go south
                if (currY < cols - 1 && !grid[currX][currY + 1].isBoulder())
                    result = true;
                break;
        }
        return result;
    }


}

class QAction {
    private String policy;
    private QState state;
    private double value = 0.0;

    public QAction(String policy, QState state) {
        this.policy = policy;
        this.state = state;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

class QState {

    private int x, y; // position
    private final String[] policy = {"East", "North", "West", "South"};
    private double maxValue;
    private int maxIndex; // policy
    private boolean boulder, terminal;
    private HashMap<String, QAction> actions;

    public QState(int x, int y) {
        this.x = x;
        this.y = y;
        maxValue = 0; // default
        maxIndex = 1; // default
        boulder = false;
        terminal = false;
        actions = new HashMap<>();
        actions.put(this.policy[0], new QAction(this.policy[0], this));
        actions.put(this.policy[1], new QAction(this.policy[1], this));
        actions.put(this.policy[2], new QAction(this.policy[2], this));
        actions.put(this.policy[3], new QAction(this.policy[3], this));
    }

    public double getMaxValue() {
        return maxValue;
    }


    public void updateValue(int direction, QLearningAgent qLearningAgent) {
        this.actions.get(policy[direction]).setValue(qLearningAgent.getQValue(direction, this));
        maxValue = this.actions.get(policy[direction]).getValue();
//        System.out.println("max in update is " + maxValue + " direction is " + direction);
        maxIndex = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public String[] getPolicy() {
        return policy;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public HashMap<String, QAction> getActions() {
        return actions;
    }


    public boolean setBoulder() {
        boulder = true;
        maxValue = 0;
        maxIndex = -1;
        return true;
    }

    public boolean setTerminal(double v) {
        terminal = true;
        maxValue = v;
        maxIndex = -1;
        getActions().values().stream().forEach(k -> k.setValue(v));
        return true;
    }

    public boolean isBoulder() {
        return boulder;
    }

    public boolean isTerminal() {
        return terminal;
    }


    public String drawCurrState() {
        String str = "  ";
        if (isBoulder()) {
            str = str + " Wall ";
        } else {
            double v = Double.parseDouble(String.format("%.2f", maxValue));
            str = str + v + "[";
            if (isTerminal()) {
                str = str + "T]";
            } else
                str = str + getPolicy()[getMaxIndex()].charAt(0) + "]";
        }
        str = str + "  ";

        return str;
    }

    public double getAllValue() {
        double res = 0.0;
        for (QAction action : actions.values()) {
            res += action.getValue();
        }
        return res;
    }
}