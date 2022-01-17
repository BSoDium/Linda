package linda.server.infrastructure;

import java.rmi.Remote;
import java.rmi.RemoteException;

import linda.Callback;
import linda.Tuple;

public interface CallableRemote extends Remote {

  public void call(Tuple t) throws RemoteException;

  /**
   * Retrieves the callback.
   * 
   * @return the callback
   */
  public Callback getCallback() throws RemoteException;
}