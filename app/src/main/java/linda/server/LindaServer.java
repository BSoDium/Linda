package linda.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Callback;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.server.infrastructure.LindaRemote;
import linda.shm.CentralizedLinda;

public class LindaServer extends UnicastRemoteObject implements LindaRemote {
  CentralizedLinda linda;

  public LindaServer() throws RemoteException {
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

}
