package linda.net;

import linda.server.LindaServer;

public class RunServer {

  public static void main(String[] args) {
    try {
      new LindaServer("localhost", 4000, "/LindaServer");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
