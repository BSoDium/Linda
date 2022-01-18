package linda.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.Collection;

import linda.Linda;
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
  private Linda linda;
  private LocalDateTime lastInteraction = LocalDateTime.now();
  private String url;
  private boolean doesTimeout = false;
  /** Inactivity time before the server is shut down (seconds). */
  private int timeoutDelay = 20;

  /** Maximum number of retries when trying to bind the server to the registry. */
  private static final int MAX_RETRIES = 20;
  /** Delay between scheduled timeout checks (seconds). */
  private static final int TIMEOUT_CHECK_DELAY = 1;

  public LindaServer(String host, int port, String path) throws RemoteException {
    linda = new CentralizedLinda();
    Boolean isServerRunning = false;
    int retries = 0;
    while (!isServerRunning && retries <= MAX_RETRIES) {
      try {
        LocateRegistry.createRegistry(port + retries);
        url = String.format("//%s:%d%s", host, port + retries, path);
        Logger.info(String.format("Registering Linda Server at %s", url));
        Naming.rebind(url, this);
        isServerRunning = true;
        Logger.info("Linda Server started");
      } catch (ExportException e) { // Port already in use
        Logger.warn(String.format("Port %d already in use, trying next one", port + retries));
        retries++;
      } catch (MalformedURLException e) { // Something went wrong
        Logger.fatal(e.getMessage());
        throw new RuntimeException(e);
      }
    }
    scheduleTimeoutCheck();
  }

  public void doesTimeout(boolean doTimeout) {
    this.doesTimeout = doTimeout;
  }

  public void stop() throws RemoteException {
    try {
      Logger.info("Unbinding Linda Server.");
      Naming.unbind(url);
      UnicastRemoteObject.unexportObject(this, true);
      Logger.info("Linda Server stopped.");
      // TODO: find a way to properly close all the rmi connections instead
      System.exit(0);

    } catch (RemoteException | MalformedURLException | NotBoundException e) {
      Logger.fatal(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public void setTimeoutDelay(int timeoutDelay) {
    this.timeoutDelay = timeoutDelay;
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
    PrintStream old = System.out;
    System.setOut(ps);

    linda.debug(prefix);

    System.out.flush();
    System.setOut(old);
    return baos.toString();
  }

  /**
   * Schedules a task to be executed after a
   * given delay, which checks for server activity.
   */
  private void scheduleTimeoutCheck() {
    new Thread(() -> {
      try {
        Thread.sleep(TIMEOUT_CHECK_DELAY * 1000);
        // stop the server if it has not been interacted with in the last 20 seconds
        if (doesTimeout && LocalDateTime.now().isAfter(lastInteraction.plusSeconds(timeoutDelay))) {
          Logger.log(
              "Linda Server has not been interacted with for " + timeoutDelay + " seconds, shutting down.",
              LogLevel.Warn);
          stop();
        } else {
          scheduleTimeoutCheck();
        }
      } catch (InterruptedException | RemoteException e) {
        Logger.fatal(e.getMessage());
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
