
/**
 * an interface to help check constrains
 */
public interface Constrain {
    /**
     * @return global check
     */
    boolean check();

    /**
     * @return local check
     */
    boolean partCheck();
}