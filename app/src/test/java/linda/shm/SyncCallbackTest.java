
package linda.shm;

import org.junit.Test;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.server.log.Logger;

public class SyncCallbackTest {

    private static Linda linda;
    private static Tuple cbmotif;

    private static class RenewingCallback implements Callback {
        public void call(Tuple t) {
            Logger.log("CB got " + t);
            linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, cbmotif, this);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            Logger.log("CB done with " + t);
        }
    }

    @Test
    public void syncCbTest() {
        linda = new linda.shm.CentralizedLinda();

        cbmotif = new Tuple(Integer.class, String.class);
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, cbmotif, new RenewingCallback());

        Tuple t1 = new Tuple(4, 5);
        Logger.log("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        Logger.log("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "foo");
        Logger.log("(2) write: " + t3);
        linda.write(t3);

        linda.debug("(2)");

    }

}
