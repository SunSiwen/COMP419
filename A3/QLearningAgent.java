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

    private int rows, cols;
    private int[][] terminal, boulder;
    private int[] robotStart;
    private int depth, episodes;
    private double alpha, discount, noise, transCost;

    // State[][] grid; // to hold list of states
    QState[][] grid; // to hold list of states
    private boolean testMode = true; // for test

    /**
     * Constructor
     * Takes a grid and runs value iteration for the specified number of iterations
     */
    public QLearningAgent(){
        inputGridConf();
        generateGrid();
        if(testMode){
            printGridConf();
            printGrid(0);
        }
    }


    public QLearningAgent(int x, int y, int step, String line, int goal){
        inputGridConf();
        if(0 <= x && x <= rows && 0 <= y && y <= cols){ // check edges
            // reassigns the target variables
            episodes = step;

            generateGrid();
            if(testMode){
                printGrid(1);
            }
            // start Q-learning
            qLearning();
            printResult(x, y, line, goal);
        }else{
            System.out.println(line + ": Invalid Input.");
        }

    }


    /**
     * Reads input from GridConf.txt, then loads variables
     */
    private void inputGridConf(){
        File file = new File("GridConf.txt");
        BufferedReader bf = null;
        try{
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
            int num = (s.length-1)/4;
            terminal = new int[num][3];
            for(int i=0; i<num; i++) {
                terminal[i][0] = Integer.parseInt(s[i*4+2]);
                terminal[i][1] = Integer.parseInt(s[i*4+3]);
                terminal[i][2] = Integer.parseInt(s[i*4+4]);
            }
            // Boulder={1={1,1}}
            line = bf.readLine();
            s = line.split("=\\{|},|\\{|}|=|,");
            num = (s.length-1)/3;
            boulder = new int[num][2];
            for(int i=0;i<num;i++) {
                boulder[i][0] = Integer.parseInt(s[i*3+2]);
                boulder[i][1] = Integer.parseInt(s[i*3+3]);
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
        }catch(Exception e) {
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
        for(int i=0; i<terminal.length; i++){
            str = str + Arrays.toString(terminal[i]);
        }
        System.out.println(str + "]");
        str = "Boulder=[";
        for(int i=0; i<boulder.length; i++){
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
    private void generateGrid(){
        grid = new QState[rows][cols];
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                grid[i][j] = new QState(i,j);
            }
        }
        for(int i=0; i<terminal.length; i++){
            int x = terminal[i][0];
            int y = convertY(terminal[i][1]);
            grid[x][y].setTerminal(terminal[i][2]);
        }
        for(int i=0; i<boulder.length; i++){
            int x = boulder[i][0];
            int y = convertY(boulder[i][1]);
            grid[x][y].setBoulder();
        }
    }

    private void printGrid(int e){
        System.out.println("episode=" + e);
        String[] graph = new String[cols];
        for(int i=0; i<cols; i++){
            graph[i] = "";
            for(int j=0; j<rows; j++){
                graph[i] = graph[i] + grid[j][i].drawCurrState();
            }
        }
        for(int i=0; i<cols; i++){
            System.out.println(graph[i]);
        }
    }

    private int convertY(int y){ // since graph[0] is the bottom row
        int newY = cols-1-y;
        return newY;
    }

    /**
     * Prints result of this query
     * @param x : x from results.txt
     * @param y : y from results.txt
     * @param line : current line read from results.txt
     * @param goal : query from results.txt
     */
    private void printResult(int x, int y, String line, int goal){
        y = convertY(y);
        // "stateValue"=0, "bestPolicy"=1, "bestQValue"=2;
        if(goal == 0) {
            double v = Double.parseDouble(String.format("%.2f", grid[x][y].getMaxValue()));
            System.out.println(line + ": " + v);
        }else if(goal == 1)
            System.out.println(line + ": Go " + grid[x][y].getPolicy());
        else
            System.out.println(line + ": Invalid Input.");
    }

    public void qLearning(){
        for(int i=0; i<episodes;i++){
            // set initial position
            int currX, currY;
            currX = robotStart[0];
            currY = robotStart[1];

            while(true){
                // check if the move is valid
                int direction = getPolicy();
                while(!checkValidMove(currX, currY, direction))
                    direction = getPolicy();

                // set next position
                int nextX = currX, nextY = currY;
                switch (direction){
                    case 0: // go east
                        nextX = currX+1;
                        break;
                    case 1: // go north
                        nextY = currY-1;
                        break;
                    case 2: // go west
                        nextX = currX-1;
                        break;
                    case 3: // go south
                        nextY = currY+1;
                        break;
                }

                // calculate q value
                double qValue = grid[currX][currY].getValue(direction); // Q(s,a)
                double qValuePrime = grid[nextX][nextY].getMaxValue(); // max Q(s',a')
                qValue = (1-alpha) * qValue + alpha * (transCost + discount * qValuePrime);
                grid[currX][currY].updateValue(direction, qValue);

                // update position
                currX = nextX;
                currY = nextY;

                if(grid[currX][currY].isTerminal()){
                    // calculate q value for exit
                    break;
                }
            }
        }
    }

    /**
     * Gets random move
     * @return direction, where east=0, north=1, west=2, south=3
     */
    private int getPolicy(){

        Random ra =new Random();
        int p = ra.nextInt(4);
        return p;
    }

    private boolean checkValidMove(int currX, int currY, int direction){ // return true if this move is valid
        boolean result = false;
        switch (direction){
            case 0: // go east
                if(currX<rows-1 && !grid[currX+1][currY].isBoulder())
                    result = true;
                break;

            case 1: // go north
                if(currY>0 && !grid[currX][currY-1].isBoulder())
                    result = true;
                break;

            case 2: // go west
                if(currX>0 && !grid[currX-1][currY].isBoulder())
                    result = true;
                break;

            case 3: // go south
                if(currY<cols-1 && !grid[currX][currY+1].isBoulder())
                    result = true;
                break;
        }
        return result;
    }


}


class QState{

    private int x, y; // position
    private double[] value; // value for each direction
    private final String[] policy = {"East", "North", "West", "South"};
    private double maxValue;
    private int maxIndex; // policy
    private boolean boulder, terminal;

    public QState(int x, int y){
        this.x = x;
        this.y = y;
        value = new double[4];
        maxValue = 0; // default
        maxIndex = 1; // default
        boulder = false;
        terminal = false;
    }

    public double getMaxValue(){
        return maxValue;
    }

    public double getValue(int direction){
        return value[direction];
    }

    public void updateValue(int direction, double newValue){
        value[direction] = newValue;
        if(newValue > maxValue){ // update max value
            maxValue = newValue;
            maxIndex = direction;
        }
    }

    public String getPolicy(){
        if(maxIndex >= 0 && canProcessVI())
            return policy[maxIndex];
        else
            return "-";
    }

    public boolean setBoulder(){
        boulder = true;
        maxValue = 0;
        maxIndex = -1;
        return true;
    }

    public boolean setTerminal(double v){
        terminal = true;
        maxValue = v;
        maxIndex = -1;
        return true;
    }

    public boolean isBoulder(){
        return boulder;
    }

    public boolean isTerminal(){
        return terminal;
    }

    public boolean canProcessVI(){
        return !terminal && !boulder;
    }

    public String drawCurrState(){
        String str = "  ";
        if (isBoulder()) {
            str = str + " Wall ";
        } else {
            double v = Double.parseDouble(String.format("%.2f", maxValue));
            str = str + v + "[";
            if(isTerminal()) {
                str = str + "T]";
            } else
                str = str + getPolicy().charAt(0) + "]";
        }
        str = str + "  ";

        return str;
    }
}