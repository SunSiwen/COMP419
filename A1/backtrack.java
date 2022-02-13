import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class backtrack {

    public static void main(String[] args) {
        System.out.println("please give me a puzzle(File path): ");
        String file = new Scanner(System.in).nextLine();
        System.out.println();
        readFromFile(file);

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
                } else {

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

        generateConstrainGraph(graph);
        recursiveBackTracking(0, 0, board, graph);
    }

    private static void recursiveBackTracking(int i, int j, char[][] board, Node[][] graph) {
        if (j == board[i].length) {
            j = 0;
            i++;
        }
        if (i == board.length) {
            if (check(graph)) {
                System.out.println("\n# Solution");
                for (char[] chars : board) {
                    for (char aChar : chars) {
                        System.out.print(aChar + " ");
                    }
                    System.out.println();
                }
            }
            return;
        }

        if (!graph[i][j].isWall && !graph[i][j].isBright) {
            for (int k = 0; k < 2; k++) {
                graph[i][j].value = k;
                if (k == 1) {
                    board[i][j] = 'b';
                    switchOn(i, j, graph);
                }
                recursiveBackTracking(i, j + 1, board, graph);
                board[i][j] = '_';
                switchOff(i, j, graph);
            }
        } else {
            recursiveBackTracking(i, j + 1, board, graph);
        }

    }

    private static void switchOn(int i, int j, Node[][] graph) {
        graph[i][j].isBright = true;
        graph[i][j].value = 1;
        ArrayList<Constrain> constrains = graph[i][j].constrains;
        for (Constrain constrain : constrains) {
            if (constrain instanceof PlaceConstrain) {
                PlaceConstrain p = (PlaceConstrain) constrain;
                Node tail = p.tail;
                tail.isBright = true;
            }
        }
    }

    private static void switchOff(int i, int j, Node[][] graph) {
        graph[i][j].value = 0;
        graph[i][j].isBright = false;
        int cnt = 0;
        ArrayList<Constrain> constrains = graph[i][j].constrains;
        for (Constrain constrain : constrains) {
            if (constrain instanceof PlaceConstrain) {
                PlaceConstrain p = (PlaceConstrain) constrain;
                Node tail = p.tail;
                if(tail.value!=0) cnt++;
                for (Constrain c : tail.constrains) {
                    if (c instanceof PlaceConstrain) {
                        PlaceConstrain q = (PlaceConstrain) c;
                        if (q.tail.value != 0) {
                            tail.isBright = true;
                            break;
                        }
                        tail.isBright = false;
                    }
                }
            }
        }
        graph[i][j].isBright = cnt!=0;
    }

    private static boolean check(Node[][] graph) {
        for (Node[] nodes : graph) {
            for (Node node : nodes) {
                for (Constrain constrain : node.constrains) {
                    if (!constrain.check()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void generateConstrainGraph(Node[][] graph) {

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j].isWall) {
                    NumConstrain numConstrain = new NumConstrain(graph[i][j].value);
                    if (i > 0 && !graph[i - 1][j].isWall) {
                        numConstrain.nodes.add(graph[i - 1][j]);
                    }
                    if (i < graph.length - 1 && !graph[i + 1][j].isWall) {
                        numConstrain.nodes.add(graph[i + 1][j]);
                    }
                    if (j > 0 && !graph[i][j - 1].isWall) {
                        numConstrain.nodes.add(graph[i][j - 1]);
                    }
                    if (j < graph[0].length - 1 && !graph[i][j + 1].isWall) {
                        numConstrain.nodes.add(graph[i][j + 1]);
                    }

                    for (Node node : numConstrain.nodes) {
                        node.constrains.add(numConstrain);
                    }
                } else {
                    for (int k = i - 1; k >= 0 && !graph[k][j].isWall; k--) {
                        graph[i][j].constrains.add(new PlaceConstrain(graph[i][j], graph[k][j]));
                    }
                    for (int k = i + 1; k < graph.length && !graph[k][j].isWall; k++) {
                        graph[i][j].constrains.add(new PlaceConstrain(graph[i][j], graph[k][j]));
                    }
                    for (int k = j - 1; k >= 0 && !graph[i][k].isWall; k--) {
                        graph[i][j].constrains.add(new PlaceConstrain(graph[i][j], graph[i][k]));
                    }
                    for (int k = j + 1; k < graph[i].length && !graph[i][k].isWall; k++) {
                        graph[i][j].constrains.add(new PlaceConstrain(graph[i][j], graph[i][k]));
                    }
                }
            }
        }
    }
}

class Node {
    boolean isWall;
    int value;
    boolean isBright;
    //    HashSet<Integer> domain;
    ArrayList<Constrain> constrains;

    public Node(boolean isWall, int value) {
        this.isWall = isWall;
        this.value = value;
        this.isBright = false;
//        domain = new HashSet<>();
//        domain.add(0);
//        domain.add(1);
        constrains = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "isWall=" + isWall +
                ", value=" + value +
                ", isBright=" + isBright +
                ", constrains=" + constrains +
                '}';
    }
}

interface Constrain {
    boolean check();
}

class NumConstrain implements Constrain {

    int total;
    ArrayList<Node> nodes;

    public NumConstrain(int num) {
        total = num;
        nodes = new ArrayList<>();
    }

    @Override
    public boolean check() {
        int cnt = 0;
        for (Node node : nodes) {
            cnt += node.value;
        }
        return cnt == total;
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
        return (head.value & tail.value) == 0 && head.isBright && tail.isBright;
    }

    @Override
    public String toString() {
        return "PlaceConstrain{" +
                "head=" + head.value +
                ", tail=" + tail.value +
                '}';
    }
}
