package linda.search.improved;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.log.LogLevel;
import linda.server.log.Logger;

public class Manager implements Runnable {

    private Linda linda;

    private UUID reqUUID;
    private String pathname;
    private String search;
    private int bestvalue = Integer.MAX_VALUE; // lower is better
    private String bestresult;

    private LocalDateTime lastSearcherInteraction = LocalDateTime.now();

    /** Inactivity time before the request is aborted (seconds). */
    private static final int TIMEOUT_DELAY = 5; // seconds
    /** Delay between scheduled timeout checks (seconds). */
    private static final int TIMEOUT_CHECK_DELAY = 1; // seconds

    public Manager(Linda linda, String pathname, String search) {
        this.linda = linda;
        this.pathname = pathname;
        this.search = search;
    }

    private void addSearch(String search) {
        this.search = search;
        this.reqUUID = UUID.randomUUID();
        Logger.log("New request initiated " + this.reqUUID + " for \'" + this.search + "\'", LogLevel.Debug);
        linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
                new Tuple(Code.Result, this.reqUUID, String.class, Integer.class), new CbGetResult());
        linda.write(new Tuple(Code.Request, this.reqUUID, this.search));
    }

    private void loadData(String pathname) {
        try (Stream<String> stream = Files.lines(Paths.get(pathname))) {
            stream.limit(10000).forEach(s -> linda.write(new Tuple(Code.Value, s.trim())));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForEndSearch() {
        linda.take(new Tuple(Code.Searcher, "done", this.reqUUID));
        linda.take(new Tuple(Code.Request, this.reqUUID, String.class)); // remove query
        Logger.log("Request " + this.reqUUID + " fulfilled.", LogLevel.Debug);
    }

    private class CbGetResult implements Callback {
        public void call(Tuple t) { // [ Result, ?UUID, ?String, ?Integer ]
            lastSearcherInteraction = LocalDateTime.now();
            String s = (String) t.get(2);
            Integer v = (Integer) t.get(3);
            if (v < bestvalue) {
                bestvalue = v;
                bestresult = s;
                Logger.log("New best (" + bestvalue + "): \"" + bestresult + "\"", LogLevel.Log);
            }
            linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
                    new Tuple(Code.Result, reqUUID, String.class, Integer.class), this);
        }
    }

    public void run() {
        this.loadData(pathname);
        this.scheduleTimeoutCheck();
        this.addSearch(search);
        this.waitForEndSearch();
        Logger.log("Best result: \"" + bestresult + "\"");
    }

    private void scheduleTimeoutCheck() {
        new Thread(() -> {
            try {
                Thread.sleep(TIMEOUT_CHECK_DELAY * 1000);
                // cancel the request and exit if no searcher have interacted recently
                if (LocalDateTime.now().isAfter(lastSearcherInteraction.plusSeconds(TIMEOUT_DELAY))) {
                    linda.take(new Tuple(Code.Request, this.reqUUID, String.class)); // remove query
                    Logger.log("Request " + this.reqUUID + " cancelled due to inactivity.", LogLevel.Debug);
                    Thread.currentThread().interrupt();
                } else {
                    scheduleTimeoutCheck();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
