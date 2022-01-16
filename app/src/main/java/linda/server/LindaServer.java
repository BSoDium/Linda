package linda.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import linda.Callback;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.server.infrastructure.LindaRemote;
import linda.server.log.LogLevel;
import linda.server.log.Logger;
import linda.shm.CentralizedLinda;

public class LindaServer extends UnicastRemoteObject implements LindaRemote {
  private CentralizedLinda linda;
  private String url;
  static final int MAX_RETRIES = 20;

  public LindaServer(String host, int port, String path) throws RemoteException {
    linda = new CentralizedLinda();
    Boolean isServerRunning = false;
    int retries = 0;
    while (!isServerRunning && retries <= MAX_RETRIES) {
      try {
        LocateRegistry.createRegistry(port + retries);
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
  }

  public String getURL() {
    return url;
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

  public Tuple eventWait(eventMode mode, eventTiming timing, Tuple template, Semaphore runEndSem)
      throws RemoteException {
    // register a server-side callback which will trigger the client-side one
    Semaphore runStartSem = new Semaphore(0);
    linda.eventRegister(eventMode.READ, timing, template, new linda.Callback() {
      public void call(Tuple t) {
        runStartSem.release();
        try {
          runEndSem.acquire();
          Logger.log("Semaphore acquired", LogLevel.Debug);
        } catch (InterruptedException e) {
          Logger.log(e.getMessage(), LogLevel.Fatal);
          throw new RuntimeException(e);
        }
      }
    });

    // wait for the callback to be triggered
    try {
      runStartSem.acquire();
    } catch (InterruptedException e) {
      Logger.log(e.getMessage(), LogLevel.Fatal);
      throw new RuntimeException(e);
    }

    if (mode == eventMode.READ) {
      return linda.read(template);
    } else {
      return linda.take(template);
    }
  }

  public String fetchDebug(String prefix) throws RemoteException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    linda.debug(prefix, ps);
    return baos.toString();
  }

}
