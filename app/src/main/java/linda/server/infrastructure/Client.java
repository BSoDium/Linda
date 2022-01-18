package linda.server.infrastructure;

import java.io.Serializable;

import linda.server.log.Logger;

public abstract class Client implements Serializable {
  protected LindaRemote server;

  /**
   * Initializes the client implementation.
   * 
   * @param serverURI the URI of the server, e.g.
   *                  "rmi://localhost:8080/Index" or
   *                  "//localhost:8080/Login".
   */
  public Client(String serverURI) {
    try {
      this.server = (LindaRemote) java.rmi.Naming.lookup(serverURI);
    } catch (Exception e) {
      Logger.fatal("Failed to lookup provided server URI");
      throw new RuntimeException("Client failed to lookup provided server URI");
    }
  }
}
