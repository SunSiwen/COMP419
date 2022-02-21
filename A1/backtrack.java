import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * -----------------------------------------
 * NAME: to be added, Siwen Sun
 * STUDENT NUMBER: to be added, 7898970
 * COURSE: COMP 4190, SECTION: A01
 * INSTRUCTOR: Cuneyt Akcora
 * ASSIGNMENT: Assignment 1 -CSP, QUESTION: part1-backtrace
 *
 * REMARKS: What is the purpose of this program?
 * Use the standard backtrack search algorithm. Read the input as shown above from standard input
 * and convert it into the corresponding constraint graph. Then apply backtracking search to solve the puzzle.
 * Output one possible solution to the puzzle.
 * Print the total number of nodes generated by your algorithm on the following line.
 * After printing the solution and the number of nodes your program, go back to the beginning
 * and start reading the next puzzle from standard input.
 * -----------------------------------------
 */


//the info of every cell
class Node {
    private final boolean isWall;
    private boolean isBright;
    private int bulb;
    private HashSet<Integer> options;
    private ArrayList<Constrain> wallConstrains;
    private ArrayList<Constrain> placeConstrains;

    public Node(boolean isWall, int value) {
        this.isWall = isWall;
        this.bulb = value;
        this.isBright = false;
        if (!isWall) {
            options = new HashSet<>();
            options.add(0);
            options.add(1);
            wallConstrains = new ArrayList<>();
            placeConstrains = new ArrayList<>();
        }
    }

    public void setBright(boolean bright) {
        isBright = bright;
    }

    public void setBulb(int bulb) {
        this.bulb = bulb;
    }

    public boolean isWall() {
        return isWall;
    }

    public boolean isBright() {
        return isBright;
    }

    public int getBulb() {
        return bulb;
    }

    public HashSet<Integer> getOptions() {
        return options;
    }

    public ArrayList<Constrain> getWallConstrains() {
        return wallConstrains;
    }

    public ArrayList<Constrain> getPlaceConstrains() {
        return placeConstrains;
    }

    /**
     * @return the number of nodes which are constrained by this
     */
    public int getStrains() {
        int cnt = 0;
        for (Constrain placeConstrain : placeConstrains) {
            PlaceConstrain p = (PlaceConstrain) placeConstrain;
            cnt += p.head.getOptions().size() / 2;
            cnt += p.tail.getOptions().size() / 2;
        }
        cnt -= this.getOptions().size() / 2 * placeConstrains.size();

        return cnt + wallConstrains.size();
    }
}

/**
 * an interface to help check constrains
 */
interface Constrain {
    /**
     * @return global check
     */
    boolean check();

    /**
     * @return local check
     */
    boolean partCheck();
}

/**
 * the constraint that shows the number of light bulbs horizontally or/and vertically adjacent to that wall, from 0 to 4
 */
class WallConstrain implements Constrain {

    private final int total;
    private final ArrayList<Node> nodes;

    public WallConstrain(int num) {
        total = num;
        nodes = new ArrayList<>();
    }

    public int getTotal() {
        return total;
    }


    public ArrayList<Node> getNodes() {
        return nodes;
    }


    /**
     * @return whether the number of light bulbs adjacent to that wall equals the wall's value
     */
    @Override
    public boolean check() {
        int cnt = 0;
        for (Node node : nodes) {
            cnt += node.getBulb();
        }
        return cnt == total;
    }

    /**
     * @return whether the number of light bulbs adjacent to that wall could equal the wall's value
     */
    @Override
    public boolean partCheck() {
        int cnt = getTotal();
        int down = 0;
        int up = 0;
        for (Node node : nodes) {
            if (node.getOptions().size() == 0) {
                return false;
            }
            int max = node.getOptions().stream().mapToInt(k -> k).max().orElse(-1);
            int min = node.getOptions().stream().mapToInt(k -> k).min().orElse(-1);
            down += min;
            up += max;
        }
        return cnt >= down && cnt <= up;
    }
}

/**
 * the constraint that shows no light bulb may illuminate another light bulb if there is no wall between them
 */
class PlaceConstrain implements Constrain {

    Node head;
    Node tail;

    public PlaceConstrain(Node head, Node tail) {
        this.head = head;
        this.tail = tail;
    }

    /**
     * @return check no light bulb may illuminate another light bulb if there is no wall between them and all bright
     */
    @Override
    public boolean check() {
        return (head.getBulb() & tail.getBulb()) == 0 && head.isBright() && tail.isBright();
    }

    /**
     * @return check no light bulb may illuminate another light bulb if there is no wall
     */
    @Override
    public boolean partCheck() {
        return (head.getBulb() & tail.getBulb()) == 0;
    }

}

/**
 * the assignment
 */
public class backtrack {
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
            System.out.println("\nplease give me a puzzle(File path): ");
            String file = new Scanner(System.in).nextLine();
            if ("quit".equals(file)) {
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
            System.out.println();
        }

        wallConstrains = new ArrayList<>();
        placeConstrains = new ArrayList<>();
        generateConstrainGraph(graph);
        PriorityQueue<Node> undecided = factory.getInstance();
        preHandle(graph, undecided);
        recursiveBackTracking(undecided, graph);
    }

    /**
     * the core method to do back tracking by DFS
     * @param undecided : the priority queue contains the undecided nodes
     * @param graph : the entire nodes
     * @return : whether this try is valid
     */
    private static boolean recursiveBackTracking(PriorityQueue<Node> undecided, Node[][] graph) {

        branch++; // increment for every selection
        if (undecided.isEmpty()) {// if there is no undecided node, global check
            if (check(wallConstrains) && check(placeConstrains)) {//if check passes
                System.out.println("\n# Solution");//print the solution out
                for (Node[] chars : graph) {
                    for (Node aChar : chars) {
                        if (aChar.isWall()) {
                            System.out.print(aChar.getBulb() + " ");
                        } else {
                            if (aChar.getBulb() == 1) {
                                System.out.print("b ");
                            } else {
                                System.out.print("_ ");
                            }
                        }
                    }
                    System.out.println();
                }

                //print the total number of nodes generated by the algorithm on the following line.
                System.out.println("THERE ARE " + branch + " TREE NODES GENERATED\n");
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
                if (recursiveBackTracking(p1, graph)) {//try the following node
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
                if (recursiveBackTracking(undecided, graph)) {//try the following node
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
}

/**
 * factory interface
 */
interface Factory {
    PriorityQueue<Node> getInstance();
}

/**
 * H1 : Most Constrained Node
 * This heuristic will select the next node that has the fewest possible options left.
 * The intuition is that by choosing these nodes first,
 * then the system will be forced to backtrack sooner.
 * If more than one node has the minimum number of options left,
 * then pick one of those nodes at random.
 */
class MostConstrainedNodeFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>(Comparator.comparingInt(o -> (o.getOptions().size())));
    }
}

/**
 * H2 : Most Constraining Node
 * This heuristic will select the next node that leads to the greatest reduction of choices on other nodes - i.e.,
 * the node that constraints the other nodes the most.
 * If more than one node leads to the maximum constraints to other nodes, then pick one of those randomly.
 */
class MostConstrainingNodeFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>((o1, o2) -> o2.getStrains() - o1.getStrains());
    }
}

/**
 * H3 : Hybrid
 * This heuristic will combine both H1 and H2.
 */
class HybridFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>((o1, o2) -> {
            if (o1.getOptions().size() == o2.getOptions().size()) {
                return o2.getStrains() - o1.getStrains();
            }
            return o1.getOptions().size() - o2.getOptions().size();
        });
    }
}
