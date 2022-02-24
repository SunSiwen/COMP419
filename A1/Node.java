import java.util.ArrayList;
import java.util.HashSet;

public class Node {
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
