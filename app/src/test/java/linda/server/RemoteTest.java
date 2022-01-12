package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import org.junit.After;
import org.junit.Before;

import linda.server.log.LogLevel;
import linda.server.log.Logger;

public abstract class RemoteTest {
  static final String HOST = "localhost";
  static final int PORT = 4000;
  static final String ROUTEPATH = "/LindaServer";
  static final int MAX_RETRIES = 20;

  private LindaServer server;
  protected String serverUrl;

  @Before
  public void setUp() {
    Boolean isServerRunning = false;
    int retries = 0;
    while (!isServerRunning && retries <= MAX_RETRIES) { // Retry until server is up
      try { // Try running the server
        LocateRegistry.createRegistry(PORT + retries);
        this.server = new LindaServer();
        this.serverUrl = String.format("//%s:%d%s", HOST, PORT + retries, ROUTEPATH);
        Logger.log(String.format("Registering Linda Server at %s", serverUrl), LogLevel.Info);
        Naming.rebind(serverUrl, this.server);
        Logger.log("Linda Server ready", LogLevel.Info);
        isServerRunning = true;

      } catch (ExportException e) { // Port already in use
        Logger.log(String.format("Port %d already in use, trying next one", PORT + retries), LogLevel.Warn);
        retries++;

      } catch (RemoteException | MalformedURLException e) { // Something went wrong
        Logger.log(e.getMessage(), LogLevel.Fatal);
        throw new RuntimeException(e);
      }
    }
  }

  @After
  public void tearDown() {
    try {
      Logger.log("Unbinding Linda Server", LogLevel.Info);
      Naming.unbind(serverUrl);
      Logger.log("Linda Server unbound", LogLevel.Info);

    } catch (RemoteException | MalformedURLException | NotBoundException e) {
      Logger.log(e.getMessage(), LogLevel.Fatal);
      throw new RuntimeException(e);
    }
  }
}
