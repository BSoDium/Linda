package linda.server.infrastructure;

import linda.server.LindaServer;

public abstract class Client {
  protected LindaServer server;

  /**
   * Initializes the client implementation.
   * 
   * @param serverURI the URI of the server, e.g.
   *                  "rmi://localhost:8080/Index" or
   *                  "//localhost:8080/Login".
   */
  public Client(String serverURI) {
    try {
      this.server = (LindaServer) java.rmi.Naming.lookup(serverURI);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
