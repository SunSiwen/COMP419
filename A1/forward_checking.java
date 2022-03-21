import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * -----------------------------------------
 * NAME: Ran Shi, Siwen Sun
 * STUDENT NUMBER: 7814643, 7898970
 * COURSE: COMP 4190, SECTION: A01
 * INSTRUCTOR: Cuneyt Akcora
 * ASSIGNMENT: Assignment 1 -CSP, QUESTION: part2-forward_checking
 *
 * REMARKS: What is the purpose of this program?
 * Use the constraint propagation algorithm as described in class.
 * -----------------------------------------
 */

public class forward_checking {
    private static ArrayList<Constrain> wallConstrains;
    private static ArrayList<Constrain> placeConstrains;
    private static int branch = 0;
    private static Factory factory = null;

    public static void main(String[] args) {
        //read Heuristic from paras
        if (args.length==0 || args[0] == null) {
            System.out.println("Wrong Heuristic option");
            return;
        }

        //initial the priority factory
        switch (args[0]) {
            case "H1":
                factory = new MostConstrainedNodeFactory();
                break;
            case "H2":
                factory = new MostConstrainingNodeFactory();
                break;
            default:
                factory = new HybridFactory();
        }

        //read user input which is the file path or quit
        while (true) {
            branch = 0;
            System.out.println("\nPlease give me a puzzle(File path): ");
            String file = new Scanner(System.in).nextLine();

            System.out.println();
            System.out.println("Received input. Start processing");

            if ("quit".equals(file)) {
                System.out.println("\nProgram completed normally.");
                return;
            }
            System.out.println();
            readFromFile(file);
        }
    }

    /**
     * @param file : the path of file
     */
    private static void readFromFile(String file) {
        BufferedReader bf = null;
        boolean flag = false;
        try {
            bf = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bf.readLine()) != null) {
                if (line.startsWith("#")) {
                    flag = !flag;
                } else if (flag) {
                    String[] s = line.split(" ");
                    char[][] board = new char[Integer.parseInt(s[0])][Integer.parseInt((s[1]))];
                    for (int i = 0; i < board.length; i++) {
                        board[i] = bf.readLine().toCharArray();
                    }
                    solve(board);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @param board : read from file and start to solve
     */
    private static void solve(char[][] board) {
        Node[][] graph = new Node[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == '_') {
                    graph[i][j] = new Node(false, 0);
                } else {
                    graph[i][j] = new Node(true, Character.getNumericValue(board[i][j]));

                }
            }
        }
        branch = 0;
        long startTime = System.currentTimeMillis();
        wallConstrains = new ArrayList<>();
        placeConstrains = new ArrayList<>();
        generateConstrainGraph(graph);
        PriorityQueue<Node> undecided = factory.getInstance();
        preHandle(graph, undecided);
        recursiveForwardCheck(undecided, graph);

        long endTime = System.currentTimeMillis();
        int counter = wallCounter(graph);
        long spend = endTime - startTime;
        System.out.println("SPEND " + spend + "MS WITH " + counter + " WALLS");
    }

    /**
     * the core method to do back tracking by DFS
     * @param undecided : the priority queue contains the undecided nodes
     * @param graph : the entire nodes
     * @return : whether this try is valid
     */
    private static boolean recursiveForwardCheck(PriorityQueue<Node> undecided, Node[][] graph) {

        branch++; // increment for every selection
        if (undecided.isEmpty()) {// if there is no undecided node, global check
            if (check(wallConstrains) && check(placeConstrains)) {//if check passes
                System.out.println("\n# Solution");//print the solution out
                for (Node[] chars : graph) {
                    for (Node aChar : chars) {
                        if (aChar.isWall()) {
                            System.out.print(aChar.getBulb() + "");
                        } else {
                            if (aChar.getBulb() == 1) {
                                System.out.print("b");
                            } else {
                                System.out.print("_");
                            }
                        }
                    }
                    System.out.println();
                }

                //print the total number of nodes generated by the algorithm on the following line.
                System.out.println("THERE ARE " + branch + " TREE NODES GENERATED");
                return true;
            }
            return false;
        }

        //get the first node from priority queue
        Node poll = undecided.poll();
        ArrayList<Node> affected = new ArrayList<>();
        //light it out
        if (poll.getOptions().contains(1)) {
            poll.setBulb(1);
            poll.setBright(true);
            //remove the other option
            boolean remove = poll.getOptions().remove(0);
            if (AC3Check(poll)) { //check constraints
                //update relative nodes
                for (Constrain c : poll.getPlaceConstrains()) {
                    PlaceConstrain placeConstrain = (PlaceConstrain) c;
                    if (placeConstrain.head != poll) {
                        placeConstrain.head.setBright(true);
                        if (placeConstrain.head.getOptions().contains(1)) {
                            affected.add(placeConstrain.head);
                            placeConstrain.head.getOptions().remove(1);
                        }
                    }
                    if (placeConstrain.tail != poll) {
                        placeConstrain.tail.setBright(true);
                        if (placeConstrain.tail.getOptions().contains(1)) {
                            affected.add(placeConstrain.tail);
                            placeConstrain.tail.getOptions().remove(1);
                        }
                    }
                }

                //generate a new priority queue
                PriorityQueue<Node> p1 = factory.getInstance();
                p1.addAll(undecided);
                if (recursiveForwardCheck(p1, graph)) {//try the following node
                    return true;
                } else {
                    //back track
                    for (Node node : affected) {
                        node.setBright(false);
                        node.getOptions().add(1);
                    }
                    poll.setBulb(0);
                    poll.setBright(false);
                    //put the option back
                    if (remove) {
                        poll.getOptions().add(0);
                    }
                }
            } else {
                //go back
                poll.setBulb(0);
                poll.setBright(false);
                if (remove) {
                    poll.getOptions().add(0);
                }
            }
        }

        //let this cell be an empty space
        if (poll.getOptions().contains(0)) {
            poll.setBulb(0);
            //remove the other option
            boolean remove = poll.getOptions().remove(1);
            if (AC3Check(poll)) {//local check
                if (recursiveForwardCheck(undecided, graph)) {//try the following node
                    return true;
                } else {
                    //put back
                    if (remove) {
                        poll.getOptions().add(1);
                    }
                }
            } else {
                // put back
                if (remove) {
                    poll.getOptions().add(1);
                }
            }
        }
        undecided.add(poll);
        return false;
    }

    /**
     * used for checking every selection
     * @param poll: selected node
     * @return : whether meets the minimum constraints
     */
    private static boolean AC3Check(Node poll) {
        return partCheck(poll.getWallConstrains()) && partCheck(poll.getPlaceConstrains());
    }

    /**
     * local check for a node's constraints
     * @param cs : a list of constraints of this node
     * @return : whether these constraints meet
     */
    private static boolean partCheck(ArrayList<Constrain> cs) {
        for (Constrain c : cs) {
            if (!c.partCheck()) {
                return false;
            }
        }
        return true;
    }

    /**
     * global check for all constraints
     * @param cs : a list of constraints
     * @return : whether all constraints meet
     */
    private static boolean check(ArrayList<Constrain> cs) {
        for (Constrain c : cs) {
            if (!c.check()) {
                return false;
            }
        }
        return true;
    }

    /**
     * do some pre handle before calculating to accelerate
     * @param graph : the nodes' info
     * @param undecided : the priority queue which contains undecided nodes
     */
    private static void preHandle(Node[][] graph, PriorityQueue<Node> undecided) {
        for (Constrain c : wallConstrains) {
            WallConstrain wallConstrain = (WallConstrain) c;
            if (wallConstrain.getTotal() != 0) {
                //for those non-zero walls, check the neighbour number, if equals the wall value, light out all
                if (wallConstrain.getTotal() == wallConstrain.getNodes().size()) {
                    for (Node node : wallConstrain.getNodes()) {
                        node.getOptions().remove(0);
                    }
                }
            } else {
                //for those zero walls, let all neighbour dark
                for (Node node : wallConstrain.getNodes()) {
                    node.getOptions().remove(1);
                }
            }
        }

        //add all nodes to the priority queue
        for (Node[] nodes : graph) {
            for (Node n : nodes) {
                if (!n.isWall()) {
                    undecided.add(n);
                }
            }
        }
    }

    /**
     * @param graph : the nodes' info and generate te constrain graph
     */
    private static void generateConstrainGraph(Node[][] graph) {

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j].isWall()) {
                    //create WallConstrain, add those cells to this constrain
                    WallConstrain wallConstrain = new WallConstrain(graph[i][j].getBulb());
                    if (i > 0 && !graph[i - 1][j].isWall()) {
                        wallConstrain.getNodes().add(graph[i - 1][j]);
                    }
                    if (i < graph.length - 1 && !graph[i + 1][j].isWall()) {
                        wallConstrain.getNodes().add(graph[i + 1][j]);
                    }
                    if (j > 0 && !graph[i][j - 1].isWall()) {
                        wallConstrain.getNodes().add(graph[i][j - 1]);
                    }
                    if (j < graph[0].length - 1 && !graph[i][j + 1].isWall()) {
                        wallConstrain.getNodes().add(graph[i][j + 1]);
                    }

                    for (Node node : wallConstrain.getNodes()) {
                        node.getWallConstrains().add(wallConstrain);
                    }
                    wallConstrains.add(wallConstrain);
                } else {
                    //create PlaceConstrain, add those vertical cells to this constrain util meeting the wall
                    for (int k = i + 1; k < graph.length && !graph[k][j].isWall(); k++) {
                        PlaceConstrain placeConstrain = new PlaceConstrain(graph[i][j], graph[k][j]);
                        graph[i][j].getPlaceConstrains().add(placeConstrain);
                        graph[k][j].getPlaceConstrains().add(placeConstrain);
                        placeConstrains.add(placeConstrain);
                    }

                    //create PlaceConstrain, add those horizontal cells to this constrain util meeting the wall
                    for (int k = j + 1; k < graph[i].length && !graph[i][k].isWall(); k++) {
                        PlaceConstrain placeConstrain = new PlaceConstrain(graph[i][j], graph[i][k]);
                        graph[i][j].getPlaceConstrains().add(placeConstrain);
                        graph[i][k].getPlaceConstrains().add(placeConstrain);
                        placeConstrains.add(placeConstrain);
                    }
                }
            }
        }
    }

    /**
     * helper, to count # of walls
     * @param graph : the entire nodes
     * @return : # of walls
     */
    private static int wallCounter(Node[][] graph){
        int counter = 0;
        for (Node[] nodes : graph) {
            for (Node n : nodes) {
                if (n.isWall()) {
                    counter++;
                }
            }
        }
        return counter;
    }
}
