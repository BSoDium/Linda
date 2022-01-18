package linda;

import java.rmi.RemoteException;

import linda.server.infrastructure.CallableRemote;
import linda.server.log.Logger;

public class ProxyCallback implements Callback {
  private CallableRemote cb;

  public ProxyCallback(CallableRemote cb) {
    this.cb = cb;
  }

  public void call(Tuple t) {
    try {
      cb.call(t);
    } catch (RemoteException e) {
      Logger.err(e.getMessage().toString());
      throw new RuntimeException(e);
    }
  }
}