import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * H1 : Most Constrained Node
 * This heuristic will select the next node that has the fewest possible options left.
 * The intuition is that by choosing these nodes first,
 * then the system will be forced to backtrack sooner.
 * If more than one node has the minimum number of options left,
 * then pick one of those nodes at random.
 */
public class MostConstrainedNodeFactory implements Factory {

    @Override
    public PriorityQueue<Node> getInstance() {
        return new PriorityQueue<>(Comparator.comparingInt(o -> (o.getOptions().size())));
    }
}