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

public class ValueIterationAgent {

    private int rows, cols;
    private int[][] terminal, boulder;
    private int[] robotStart;
    private int depth, episodes;
    private double alpha, discount, noise, transCost;
    private double[][] vks;

    State[][] grid; // to hold list of states
    private boolean testMode = false; // for test

    /**
     * Constructor
     * Takes a grid and runs value iteration for the specified number of iterations
     */
    public ValueIterationAgent(){
        inputGridConf();
        generateGrid();
        if(testMode){
            printGridConf();
            printGrid(1);
        }

        // start value iteration
        valueIteration();

    }

    public ValueIterationAgent(int x, int y, int step, String line, int goal){
        inputGridConf();
        if(0 <= x && x <= rows && 0 <= y && y <= cols){ // check edges
            // reassigns the target variables
            depth = step;

            generateGrid();
            if(testMode){
                printGrid(1);
            }
            // start value iteration
            valueIteration();
            printResult(x, y, line, goal);
        }else{
            System.out.println(line + ": Invalid Input.");
        }

    }

    /**
     * Traverses the grid, runs value iteration for each state
     */
    private void valueIteration(){
        for(int m=2; m<depth+1; m++){
            for(int i=0; i<rows; i++){
                for(int j=0; j<cols; j++){
                    if(grid[i][j].canProcessVI()){
                        double invalid = vks[i][j]; // score for invalid move = stay
                        // east=0, north=1, west=2, south=3;
                        // check west
                        double[] vk = new double[4];
                        if(i>0 && !grid[i-1][j].isBoulder())
                            vk[2] = vks[i-1][j];
                        else
                            vk[2] = invalid;
                        // check east
                        if(i<rows-1 && !grid[i+1][j].isBoulder())
                            vk[0] = vks[i+1][j];
                        else
                            vk[0] = invalid;
                        // check north
                        if(j>0 && !grid[i][j-1].isBoulder())
                            vk[1] = vks[i][j-1];
                        else
                            vk[1] = invalid;
                        // check south
                        if(j<cols-1 && !grid[i][j+1].isBoulder())
                            vk[3] = vks[i][j+1];
                        else
                            vk[3] = invalid;
                        grid[i][j].valueIteration(vk, discount, noise, transCost);
                    }
                }
            }
            if(testMode){
                System.out.println();
                printGrid(m);
            }
            updateVks();
        }
        /*
        // print output
        System.out.println();
        printGrid(depth);
        */
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
        grid = new State[rows][cols];
        vks = new double[rows][cols];
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                grid[i][j] = new State(i,j);
            }
        }
        for(int i=0; i<terminal.length; i++){
            int x = terminal[i][0];
            int y = convertY(terminal[i][1]);
            grid[x][y].setTerminal(terminal[i][2]);
            vks[x][y] = terminal[i][2];
        }
        for(int i=0; i<boulder.length; i++){
            int x = boulder[i][0];
            int y = convertY(boulder[i][1]);
            grid[x][y].setBoulder();
        }
    }

    private void printGrid(int k){
        System.out.println("k=" + k);
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
     * Updates the list of curr max value, prepares for next iteration
     */
    private void updateVks(){
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                vks[i][j] = grid[i][j].getMaxValue();
            }
        }
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
}

class State{

    private int x, y; // position
    private double[] value; // value for each direction
    private final String[] policy = {"East", "North", "West", "South"};
    private double maxValue;
    private int maxIndex; // policy
    private boolean boulder, terminal;

    public State(int x, int y){
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

    private void updateValue(int direction, double newValue){
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

    public String drawCurrState(){ // helper, to draw current state
        String str = "  ";
        if (isBoulder()) {
            str = str + " Boul ";
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

    /**
     * Runs value iteration for this state, implements the equation
     * Then update each value for this state
     *
     * @param vk : vk from 4 direction, where east=0, north=1, west=2, south=3;
     * @param discount : Discount
     * @param noise : Noise
     * @param transCost : Transition cost
     */
    public void valueIteration(double[] vk, double discount, double noise, double transCost){
        int left, right;
        for (int i=0; i<value.length; i++){
            double goStraight = (1-noise) * (transCost + discount * vk[i]);
            left = (i+1)%4;
            right = (i+3)%4;
            double goLeft = noise/2 *  (transCost + discount * vk[left]);
            double goRight = noise/2 *  (transCost + discount * vk[right]);
            updateValue(i, goStraight + goLeft + goRight);
        }
    }
}