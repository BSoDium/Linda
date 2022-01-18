package linda.search.improved;

import java.rmi.RemoteException;

import linda.server.LindaClient;
import linda.server.LindaServer;
import linda.server.log.LogLevel;
import linda.server.log.Logger;

public class TextSearch {
    static final String HOST = "localhost";
    static final int PORT = 4000;
    static final String ROUTEPATH = "/LindaServer";

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("linda.search.basic.Main search file.");
            return;
        }

        // initialize server
        LindaServer linda;
        String serverUrl;
        try {
            linda = new LindaServer(HOST, PORT, ROUTEPATH);
        } catch (RemoteException e) {
            Logger.log("Error while initializing Linda server.", LogLevel.Fatal);
            return;
        }
        linda.doesTimeout(true);
        linda.setTimeoutDelay(5);
        serverUrl = linda.getURL();

        Manager manager1 = new Manager(new LindaClient(serverUrl), args[1], args[0]);
        (new Thread(manager1)).start();

        Searcher searcher1 = new Searcher(new LindaClient(serverUrl));
        // Searcher searcher2 = new Searcher(new LindaClient(serverUrl));
        (new Thread(searcher1)).start();
        // (new Thread(searcher2)).start();
    }
}
