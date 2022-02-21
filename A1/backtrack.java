import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

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

    @Override
    public String toString() {
        return "Node{" +
                "isWall=" + isWall +
                ", isBright=" + isBright +
                ", bulb=" + bulb +
                ", options=" + options.size() +
                '}';
    }

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

interface Constrain {
    boolean check();

    boolean partCheck();
}

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


    @Override
    public boolean check() {
        int cnt = 0;
        for (Node node : nodes) {
            cnt += node.getBulb();
        }
        return cnt == total;
    }

    @Override
    public boolean partCheck() {
        int cnt = getTotal();
        int down = 0;
        int up = 0;
        for (Node node : nodes) {
            if (node.getOptions().size() == 0) {
                return false;
            }
            int max = node.getOptions().stream().mapToInt(k -> k).max().getAsInt();
            int min = node.getOptions().stream().mapToInt(k -> k).min().getAsInt();
            down += min;
            up += max;
        }
        return cnt >= down && cnt <= up;
    }

    @Override
    public String toString() {
        return "NumConstrain{" +
                "total=" + total +
                ", nodes=" + nodes.size() +
                '}';
    }
}

class PlaceConstrain implements Constrain {

    Node head;
    Node tail;

    public PlaceConstrain(Node head, Node tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public boolean check() {
        return (head.getBulb() & tail.getBulb()) == 0 && head.isBright() && tail.isBright();
    }

    @Override
    public boolean partCheck() {
        return (head.getBulb() & tail.getBulb()) == 0;
    }

    @Override
    public String toString() {
        return "PlaceConstrain{" +
                "head=" + head.getBulb() +
                ", tail=" + tail.getBulb() +
                '}';
    }


}


public class backtrack {
    private static ArrayList<Constrain> wallConstrains;
    private static ArrayList<Constrain> placeConstrains;
    private static int brach = 0;
    private static Factory factory = null;

    public static void main(String[] args) {
        while (true) {
            brach = 0;
            factory = new MostConstrainedNodeFactory();
            System.out.println("\nplease give me a puzzle(File path): ");
            String file = new Scanner(System.in).nextLine();
            System.out.println();
            readFromFile(file);
        }

    }

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

    private static void solve(char[][] board) {
        Node[][] graph = new Node[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == '_') {
                    graph[i][j] = new Node(false, 0);
                } else {
                    graph[i][j] = new Node(true, Character.getNumericValue(board[i][j]));

                }
                System.out.print(board[i][j] + " ");
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

    private static boolean recursiveBackTracking(PriorityQueue<Node> undecided, Node[][] graph) {

        brach++;
        if (undecided.isEmpty()) {
            if (check(wallConstrains) && check(placeConstrains)) {
                System.out.println("\n# Solution");
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
                System.out.println("THERE ARE " + brach + " BRANCHES");
                return true;
            }
            return false;
        }

        Node poll = undecided.poll();
        ArrayList<Node> affected = new ArrayList<>();
        //选中light
        if (poll.getOptions().contains(1)) {
            poll.setBulb(1);
            poll.setBright(true);
            boolean remove = poll.getOptions().remove(0);
            if (AC3Check(poll)) {
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

                PriorityQueue<Node> p1 = factory.getInstance();
                p1.addAll(undecided);
                if (recursiveBackTracking(p1, graph)) {
                    return true;
                } else {
                    //back trace;
                    for (Node node : affected) {
                        node.setBright(false);
                        node.getOptions().add(1);
                    }
                    poll.setBulb(0);
                    poll.setBright(false);
                    if (remove) {
                        poll.getOptions().add(0);
                    }
                }
            } else {
                poll.setBulb(0);
                poll.setBright(false);
                if (remove) {
                    poll.getOptions().add(0);
                }
            }
        }

        if (poll.getOptions().contains(0)) {
            poll.setBulb(0);
            boolean remove = poll.getOptions().remove(1);
            if (AC3Check(poll)) {
                if (recursiveBackTracking(undecided, graph)) {
                    return true;
                } else {
                    if (remove) {
                        poll.getOptions().add(1);
                    }
                }
            } else {
                if (remove) {
                    poll.getOptions().add(1);
                }
            }
        }
        undecided.add(poll);
        return false;
    }

    private static boolean AC3Check(Node poll) {
        return partCheck(poll.getWallConstrains()) && partCheck(poll.getPlaceConstrains());
    }

    private static boolean partCheck(ArrayList<Constrain> cs) {
        for (Constrain c : cs) {
            if (!c.partCheck()) {
                return false;
            }
        }
        return true;
    }

    private static boolean check(ArrayList<Constrain> cs) {
        for (Constrain c : cs) {
            if (!c.check()) {
                return false;
            }
        }
        return true;
    }

    private static void preHandle(Node[][] graph, PriorityQueue<Node> undecided) {
        for (Constrain c : wallConstrains) {
            WallConstrain wallConstrain = (WallConstrain) c;
            if (wallConstrain.getTotal() != 0) {
                if (wallConstrain.getTotal() == wallConstrain.getNodes().size()) {
                    for (Node node : wallConstrain.getNodes()) {
                        node.getOptions().remove(0);
                    }
                }
            } else {
                for (Node node : wallConstrain.getNodes()) {
                    node.getOptions().remove(1);
                }
            }
        }

        for (Node[] nodes : graph) {
            for (Node n : nodes) {
                if (!n.isWall()) {
                    undecided.add(n);
                }
            }
        }
    }

    private static void generateConstrainGraph(Node[][] graph) {

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j].isWall()) {
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
                    for (int k = i + 1; k < graph.length && !graph[k][j].isWall(); k++) {
                        PlaceConstrain placeConstrain = new PlaceConstrain(graph[i][j], graph[k][j]);
                        graph[i][j].getPlaceConstrains().add(placeConstrain);
                        graph[k][j].getPlaceConstrains().add(placeConstrain);
                        placeConstrains.add(placeConstrain);
                    }

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

interface Factory {
    PriorityQueue<Node> getInstance();
}

class MostConstrainedNodeFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>(Comparator.comparingInt(o -> (o.getOptions().size())));
    }
}

class MostConstrainingNodeFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>((o1, o2) -> o2.getStrains() - o1.getStrains());
//        return new PriorityQueue<>((o1, o2) -> ((o2.getPlaceConstrains().size() + o2.getWallConstrains().size())-(o1.getPlaceConstrains().size() + o1.getWallConstrains().size())));
    }
}

class HybridFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>(Comparator.comparingInt(o -> (o.getOptions().size() + (o.getPlaceConstrains().size() + o.getWallConstrains().size()))));
//        return new PriorityQueue<>(Comparator.comparingInt(o -> (o.getOptions().size() - (o.getPlaceConstrains().size() + o.getWallConstrains().size()))));
    }
}
