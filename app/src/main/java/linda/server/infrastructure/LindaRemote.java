package linda.server.infrastructure;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;

public interface LindaRemote extends Remote {

  /**
   * Stop the server.
   * This should technically require a password, but who cares, we're
   * running both the client and the server on the same machine anyways.
   * 
   * @throws RemoteException
   */
  public void stop() throws RemoteException;

  /**
   * Set the time after which the server will shut down if not interacted with.
   * Default is 20 seconds.
   * 
   * @param timeoutDelay time in seconds
   */
  public void setTimeoutDelay(int timeoutDelay) throws RemoteException;

  /**
   * Retrieve the URL at which the Linda server is available.
   * 
   * @return the URL at which the Linda server is available.
   * @throws RemoteException
   */
  public String getURL() throws RemoteException;

  /**
   * Write a tuple to the server.
   * 
   * @param t the tuple to write
   * @throws RemoteException
   */
  public void write(Tuple t) throws RemoteException;

  /**
   * Take a tuple matching the template from the server, blocking if necessary.
   * 
   * @param template the template to match
   * @return the tuple taken
   * @throws RemoteException
   */
  public Tuple take(Tuple template) throws RemoteException;

  /**
   * Read a tuple matching the template from the server, blocking if necessary.
   * 
   * @param template the template to match
   * @return the tuple read
   * @throws RemoteException
   */
  public Tuple read(Tuple template) throws RemoteException;

  /**
   * Try to take a tuple matching the template from the server.
   * 
   * @param template the template to match
   * @return the tuple taken
   * @throws RemoteException
   */
  public Tuple tryTake(Tuple template) throws RemoteException;

  /**
   * Try to read a tuple matching the template from the server.
   * 
   * @param template the template to match
   * @return the tuple read
   * @throws RemoteException
   */
  public Tuple tryRead(Tuple template) throws RemoteException;

  /**
   * Take all tuples matching the template from the server, blocking if necessary.
   * 
   * @param template the template to match
   * @return the tuples taken
   * @throws RemoteException
   */
  public Collection<Tuple> takeAll(Tuple template) throws RemoteException;

  /**
   * Read all tuples matching the template from the server, blocking if necessary.
   * 
   * @param template the template to match
   * @return the tuples read
   * @throws RemoteException
   */
  public Collection<Tuple> readAll(Tuple template) throws RemoteException;

  /**
   * Register a callback to be called when a tuple matching the template is
   * written to the server.
   * 
   * @param mode     the mode of the event
   * @param timing   the timing of the event
   * @param template the template to match
   * @param rcb      the callback to run
   * @throws RemoteException
   */
  public void eventRegister(eventMode mode, eventTiming timing, Tuple template, CallableRemote rcb)
      throws RemoteException;

  /**
   * Retrieve a debug string containing the current state of the server.
   * 
   * @param prefix the prefix to use
   * @return the debug string
   * @throws RemoteException
   */
  public String fetchDebug(String prefix) throws RemoteException;

}
