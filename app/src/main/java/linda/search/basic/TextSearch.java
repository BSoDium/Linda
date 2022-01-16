package linda.search.basic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

import linda.Linda;
import linda.server.LindaClient;
import linda.server.LindaServer;
import linda.server.log.LogLevel;
import linda.server.log.Logger;

public class TextSearch {
    static final String HOST = "localhost";
    static final int PORT = 4000;
    static final String ROUTEPATH = "/LindaServer";
    static final int MAX_RETRIES = 20;

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("linda.search.basic.Main search file.");
            return;
        }

        // initialize server
        LindaServer linda;
        String serverUrl = "";
        Boolean isServerRunning = false;
        int retries = 0;
        while (!isServerRunning && retries <= MAX_RETRIES) { // Retry until server is up
            try {
                LocateRegistry.createRegistry(PORT + retries);
                linda = new LindaServer();
                serverUrl = String.format("//%s:%d%s", "localhost", PORT + retries, "/LindaServer");
                Logger.log(String.format("Registering Linda Server at %s", serverUrl), LogLevel.Info);
                Naming.rebind(serverUrl, linda);
                isServerRunning = true;
            } catch (ExportException e) { // Port already in use
                Logger.log(String.format("Port %d already in use, trying next one", PORT + retries), LogLevel.Warn);
                retries++;
            } catch (RemoteException | MalformedURLException e) { // Something went wrong
                Logger.log(e.getMessage(), LogLevel.Fatal);
                throw new RuntimeException(e);
            }
        }

        Manager manager1 = new Manager(new LindaClient(serverUrl), args[1], args[0]);
        Manager manager2 = new Manager(new LindaClient(serverUrl), args[1], args[0]);
        Searcher searcher1 = new Searcher(new LindaClient(serverUrl));
        Searcher searcher2 = new Searcher(new LindaClient(serverUrl));
        (new Thread(manager1)).start();
        (new Thread(manager2)).start();
        (new Thread(searcher1)).start();
        (new Thread(searcher2)).start();
    }
}
