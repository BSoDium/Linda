package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Before;

import linda.server.log.LogLevel;
import linda.server.log.Logger;

public abstract class RemoteTest {
  static final String HOST = "localhost";
  static final int PORT = 4000;
  static final String ROUTEPATH = "/LindaServer";

  private LindaServer server;
  protected String serverUrl;

  @Before
  public void setUp() {
    try { // Try running the server
      server = new LindaServer(HOST, PORT, ROUTEPATH);
      serverUrl = server.getURL();
    } catch (RemoteException e) { // Something went wrong
      Logger.log(e.getMessage(), LogLevel.Fatal);
      throw new RuntimeException(e);
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
