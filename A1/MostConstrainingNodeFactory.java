import java.util.PriorityQueue;

/**
 * H2 : Most Constraining Node
 * This heuristic will select the next node that leads to the greatest reduction of choices on other nodes - i.e.,
 * the node that constraints the other nodes the most.
 * If more than one node leads to the maximum constraints to other nodes, then pick one of those randomly.
 */
public class MostConstrainingNodeFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>((o1, o2) -> o2.getStrains() - o1.getStrains());
    }
}