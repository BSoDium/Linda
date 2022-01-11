package linda.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.net.UnknownHostException;
import java.util.Collection;

import linda.Callback;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.server.infrastructure.LindaRemote;
import linda.server.log.LogLevel;
import linda.server.log.Logger;
import linda.shm.CentralizedLinda;

public class LindaServer implements LindaRemote {
  CentralizedLinda linda;

  public LindaServer() {
    linda = new CentralizedLinda();
  }

  public void write(Tuple t) throws RemoteException {
    linda.write(t);
  }

  public Tuple take(Tuple template) throws RemoteException {
    return linda.take(template);
  }

  public Tuple read(Tuple template) throws RemoteException {
    return linda.read(template);
  }

  public Tuple tryTake(Tuple template) throws RemoteException {
    return linda.tryTake(template);
  }

  public Tuple tryRead(Tuple template) throws RemoteException {
    return linda.tryRead(template);
  }

  public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
    return linda.takeAll(template);
  }

  public Collection<Tuple> readAll(Tuple template) throws RemoteException {
    return linda.readAll(template);
  }

  public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback)
      throws RemoteException {
    linda.eventRegister(mode, timing, template, callback);
  }

  public String fetchDebug(String prefix) throws RemoteException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    linda.debug(prefix, ps);
    return baos.toString();
  }

  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(4000);

      // setup security manager
      Logger.log("Setting up security manager");
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new SecurityManager());
      }
      Logger.log("Done.");

      // setup RMI
      LindaServer server = new LindaServer();
      String url = String.format("rmi://%s:4000/LindaServer", InetAddress.getLocalHost().getHostAddress());
      Logger.log(String.format("Registering LindaServer at %s", url), LogLevel.Info);
      Naming.rebind(url, server);
      Logger.log("LindaServer ready", LogLevel.Info);
    } catch (RemoteException | MalformedURLException | UnknownHostException e) {
      Logger.log(e.getMessage(), LogLevel.Error);
      e.printStackTrace();
    }
  }
}
