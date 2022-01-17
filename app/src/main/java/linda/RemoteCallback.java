package linda;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.server.infrastructure.CallableRemote;

public class RemoteCallback extends UnicastRemoteObject implements CallableRemote {

  private Callback cb;

  public RemoteCallback(Callback cb) throws RemoteException {
    this.cb = cb;
  }

  @Override
  public void call(Tuple t) {
    cb.call(t);
  }

  @Override
  public Callback getCallback() {
    return this.cb;
  }

}
