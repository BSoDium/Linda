package linda.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.Collection;

import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.ProxyCallback;
import linda.Tuple;
import linda.server.infrastructure.CallableRemote;
import linda.server.infrastructure.LindaRemote;
import linda.server.log.LogLevel;
import linda.server.log.Logger;
import linda.shm.CentralizedLinda;

public class LindaServer extends UnicastRemoteObject implements LindaRemote {
  private CentralizedLinda linda;
  private Registry registry;
  private LocalDateTime lastInteraction;
  private String url;

  private static final int MAX_RETRIES = 20;
  private static final int TIMEOUT_SHUTDOWN_DELAY = 20; // in seconds

  public LindaServer(String host, int port, String path) throws RemoteException {
    linda = new CentralizedLinda();
    Boolean isServerRunning = false;
    int retries = 0;
    while (!isServerRunning && retries <= MAX_RETRIES) {
      try {
        registry = LocateRegistry.createRegistry(port + retries);
        url = String.format("//%s:%d%s", host, port + retries, path);
        Logger.log(String.format("Registering Linda Server at %s", url), LogLevel.Info);
        Naming.rebind(url, this);
        isServerRunning = true;
      } catch (ExportException e) { // Port already in use
        Logger.log(String.format("Port %d already in use, trying next one", port + retries), LogLevel.Warn);
        retries++;
      } catch (MalformedURLException e) { // Something went wrong
        Logger.log(e.getMessage(), LogLevel.Fatal);
        throw new RuntimeException(e);
      }
    }
    scheduleTimeoutCheck();
  }

  public void stop() throws RemoteException {
    try {
      Logger.log("Unbinding Linda Server", LogLevel.Info);
      Naming.unbind(url);
      UnicastRemoteObject.unexportObject(registry, true);
      Logger.log("Linda Server unbound", LogLevel.Info);
      System.exit(0);

    } catch (RemoteException | MalformedURLException | NotBoundException e) {
      Logger.log(e.getMessage(), LogLevel.Fatal);
      throw new RuntimeException(e);
    }
  }

  public String getURL() {
    updateLastInteraction();
    return url;
  }

  public void write(Tuple t) throws RemoteException {
    updateLastInteraction();
    linda.write(t);
  }

  public Tuple take(Tuple template) throws RemoteException {
    updateLastInteraction();
    return linda.take(template);
  }

  public Tuple read(Tuple template) throws RemoteException {
    updateLastInteraction();
    return linda.read(template);
  }

  public Tuple tryTake(Tuple template) throws RemoteException {
    updateLastInteraction();
    return linda.tryTake(template);
  }

  public Tuple tryRead(Tuple template) throws RemoteException {
    updateLastInteraction();
    return linda.tryRead(template);
  }

  public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
    updateLastInteraction();
    return linda.takeAll(template);
  }

  public Collection<Tuple> readAll(Tuple template) throws RemoteException {
    updateLastInteraction();
    return linda.readAll(template);
  }

  public void eventRegister(eventMode mode, eventTiming timing, Tuple template, CallableRemote rcb)
      throws RemoteException {
    updateLastInteraction();
    linda.eventRegister(mode, timing, template, new ProxyCallback(rcb));
  }

  public String fetchDebug(String prefix) throws RemoteException {
    updateLastInteraction();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    linda.debug(prefix, ps);
    return baos.toString();
  }

  /**
   * Schedules a task to be executed after a
   * given delay, which checks for server activity.
   */
  private void scheduleTimeoutCheck() {
    new Thread(() -> {
      try {
        Thread.sleep(TIMEOUT_SHUTDOWN_DELAY * 1000);
        // stop the server if it has not been interacted with in the last 20 seconds
        if (LocalDateTime.now().isAfter(lastInteraction.plusSeconds(TIMEOUT_SHUTDOWN_DELAY))) {
          Logger.log(
              "Linda Server has not been interacted with for " + TIMEOUT_SHUTDOWN_DELAY + " seconds, stopping",
              LogLevel.Warn);
          stop();
        } else {
          scheduleTimeoutCheck();
        }
      } catch (InterruptedException | RemoteException e) {
        Logger.log(e.getMessage(), LogLevel.Fatal);
        throw new RuntimeException(e);
      }
    }).start();
  }

  /**
   * Save the last interaction date and time.
   */
  private void updateLastInteraction() {
    lastInteraction = LocalDateTime.now();
  }

}
