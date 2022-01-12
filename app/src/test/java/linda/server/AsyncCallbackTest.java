
package linda.server;

import org.junit.Test;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.server.log.Logger;

public class AsyncCallbackTest extends RemoteTest {

    private static class OneTimeCallback implements Callback {
        public void call(Tuple t) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            Logger.log("Got " + t);
        }
    }

    @Test
    public void asyncCbTest() {
        Linda linda = new linda.server.LindaClient(serverUrl);

        Tuple motif = new Tuple(Integer.class, String.class);
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif,
                new AsynchronousCallback(new OneTimeCallback()));

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
