import java.util.PriorityQueue;

/**
 * H3 : Hybrid
 * This heuristic will combine both H1 and H2.
 */
public class HybridFactory implements Factory {

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