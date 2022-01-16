package linda.search.basic;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

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

    public Manager(Linda linda, String pathname, String search) {
        this.linda = linda;
        this.pathname = pathname;
        this.search = search;
    }

    private void addSearch(String search) {
        this.search = search;
        this.reqUUID = UUID.randomUUID();
        Logger.log("New request initiated " + this.reqUUID + " for \'" + this.search + "\'");
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

    private class CbGetResult implements linda.Callback {
        public void call(Tuple t) { // [ Result, ?UUID, ?String, ?Integer ]
            String s = (String) t.get(2);
            Integer v = (Integer) t.get(3);
            Logger.log("Received: " + s, LogLevel.Info);
            if (v < bestvalue) {
                bestvalue = v;
                bestresult = s;
                Logger.log("New best (" + bestvalue + "): \"" + bestresult + "\"", LogLevel.Debug);
            }
            linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
                    new Tuple(Code.Result, reqUUID, String.class, Integer.class), this);
        }
    }

    public void run() {
        this.loadData(pathname);
        this.addSearch(search);
        this.waitForEndSearch();
        Logger.log("Best result: \"" + bestresult + "\"");
    }
}
