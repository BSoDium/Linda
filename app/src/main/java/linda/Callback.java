package linda;

import java.io.Serializable;

/**
 * Callback when a tuple appears.
 * 
 * @author philippe.queinnec@enseeiht.fr
 */
public interface Callback extends Serializable {

    /**
     * Callback when a tuple appears.
     * See Linda.eventRegister for details.
     * 
     * @param t the new tuple
     */
    void call(Tuple t);
}
