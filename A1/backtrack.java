import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class backtrack {

    public static void main(String[] args) {
        System.out.println("please give me a puzzle(File path): ");
        String file = new Scanner(System.in).nextLine();
        System.out.println("");
        readFromFile(file);

    }

    private static void readFromFile(String file) {
        BufferedReader bf = null;
        boolean flag = false;
        try {
            bf = new BufferedReader(new FileReader(file));
            String line = null;
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
    }

    private static void generateConstrainGraph(Node[][] graph) {

    }
}

class Node {
    boolean isWall;
    int value;
    boolean isBright;
    HashSet<Integer> domain;

    public Node(boolean isWall, int value) {
        this.isWall = isWall;
        this.value = value;
        isBright = false;
        domain = new HashSet<>();
        domain.add(0);
        domain.add(1);
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

    public void addNode(Node e) {
        nodes.add(e);
    }

    @Override
    public boolean check() {
        int cnt = 0;
        for (Node node : nodes) {
            cnt += node.value;
        }
        return cnt == total;
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
        return (head.value & tail.value) == 0;
    }
}

class ConstrainGraph {
    HashMap<Node, ArrayList<Constrain>> constrains;

}