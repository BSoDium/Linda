package linda;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.server.infrastructure.CallableRemote;

public class RemoteCallback extends UnicastRemoteObject implements CallableRemote {

  private Callback cb;

  public RemoteCallback(Callback cb) throws RemoteException {
    this.cb = cb;
  }

  public void call(Tuple t) throws RemoteException {
    cb.call(t);
  }

  public Callback getCallback() throws RemoteException {
    return this.cb;
  }
}
