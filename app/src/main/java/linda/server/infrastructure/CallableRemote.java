package linda.server.infrastructure;

import java.rmi.Remote;
import java.rmi.RemoteException;

import linda.Callback;
import linda.Tuple;

public interface CallableRemote extends Remote {

  /**
   * Call the associated callback with the given tuple.
   * 
   * @param t the tuple to pass to the callback
   * @throws RemoteException
   */
  public void call(Tuple t) throws RemoteException;

  /**
   * Retrieves the internal callback.
   * 
   * @return the callback
   */
  public Callback getCallback() throws RemoteException;
}