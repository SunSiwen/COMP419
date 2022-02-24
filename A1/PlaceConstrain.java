
/**
 * the constraint that shows no light bulb may illuminate another light bulb if there is no wall between them
 */
public class PlaceConstrain implements Constrain {

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
