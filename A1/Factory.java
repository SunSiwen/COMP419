import java.util.PriorityQueue;

 /**
 * factory interface
 */
public interface Factory {
    PriorityQueue<Node> getInstance();
}