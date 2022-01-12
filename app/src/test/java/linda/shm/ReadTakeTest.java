package linda.shm;

import org.junit.Test;

import linda.Linda;
import linda.Tuple;
import linda.server.log.Logger;

public class ReadTakeTest {

    @Test
    public void writeTakeTest() {

        final Linda linda = new linda.shm.CentralizedLinda();
        // final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.take(motif);
                Logger.log("(1) Resultat:" + res);
                linda.debug("(1)");
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                Logger.log("(2) write: " + t1);
                linda.write(t1);

                Tuple t11 = new Tuple(4, 5);
                Logger.log("(2) write: " + t11);
                linda.write(t11);

                Tuple t2 = new Tuple("hello", 15);
                Logger.log("(2) write: " + t2);
                linda.write(t2);

                Tuple t3 = new Tuple(4, "foo");
                Logger.log("(2) write: " + t3);
                linda.write(t3);

                linda.debug("(2)");

            }
        }.start();
    }

    @Test
    public void writeReadTest() {
        final Linda linda = new linda.shm.CentralizedLinda();

        for (int i = 1; i <= 3; i++) {
            final int j = i;
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda.read(motif);
                    Logger.log("(" + j + ") Resultat:" + res);
                    linda.debug("(" + j + ")");
                }
            }.start();
        }

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t1 = new Tuple(4, 5);
                Logger.log("(0) write: " + t1);
                linda.write(t1);

                Tuple t2 = new Tuple("hello", 15);
                Logger.log("(0) write: " + t2);
                linda.write(t2);

                linda.debug("(0)");

                Tuple t3 = new Tuple(4, "foo");
                Logger.log("(0) write: " + t3);
                linda.write(t3);

                linda.debug("(0)");

            }
        }.start();

    }
}
