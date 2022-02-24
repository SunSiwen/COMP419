import java.util.ArrayList;

/**
 * the constraint that shows the number of light bulbs horizontally or/and vertically adjacent to that wall, from 0 to 4
 */
public class WallConstrain implements Constrain {

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

