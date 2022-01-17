package linda.shm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.log.Logger;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
  private ArrayListSync<Tuple> database;
  private ArrayListSync<Event> callbacks;
  private final Object lock = new Object();

  /**
   * Creates a new shared memory implementation of Linda.
   */
  public CentralizedLinda() {
    database = new ArrayListSync<>();
    callbacks = new ArrayListSync<>();
  }

  @Override
  public void write(Tuple t) {
    database.add(t);
    // run callbacks
    runCallBacks();
    // wake up all waiting threads after a write
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  @Override
  public Tuple take(Tuple template) {
    // try to find a tuple matching the template
    synchronized (database.getLock()) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          database.remove(t);
          runCallBacks();
          return t;
        }
      }
    }

    // block until a matching tuple is found (multithreaded version)
    while (true) {
      try {
        // wait for a matching tuple to be added to the database
        synchronized (lock) {
          lock.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // retry
      synchronized (database.getLock()) {
        for (Tuple t : database) {
          if (t.matches(template)) {
            database.remove(t);
            runCallBacks();
            return t;
          }
        }
      }
    }
  }

  @Override
  public Tuple read(Tuple template) {
    // try to find a tuple matching the template
    synchronized (database.getLock()) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          return t;
        }
      }
    }

    // block until a matching tuple is found
    while (true) {
      try {
        // wait for a matching tuple to be added to the database
        synchronized (lock) {
          lock.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // retry
      synchronized (database.getLock()) {
        for (Tuple t : database) {
          if (t.matches(template)) {
            return t;
          }
        }
      }
    }
  }

  @Override
  public Tuple tryTake(Tuple template) {
    synchronized (database.getLock()) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          database.remove(t);
          runCallBacks();
          return t;
        }
      }
    }
    return null;
  }

  @Override
  public Tuple tryRead(Tuple template) {
    synchronized (database.getLock()) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          return t;
        }
      }
    }
    return null;
  }

  @Override
  public Collection<Tuple> takeAll(Tuple template) {
    ArrayList<Tuple> ret = new ArrayList<Tuple>();
    synchronized (database.getLock()) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          ret.add(t);
          database.remove(t);
        }
      }
    }
    runCallBacks();
    return ret;
  }

  @Override
  public Collection<Tuple> readAll(Tuple template) {
    ArrayList<Tuple> ret = new ArrayList<Tuple>();
    synchronized (database.getLock()) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          ret.add(t);
        }
      }
    }
    return ret;
  }

  @Override
  public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
    // store the callback
    Event e = new Event(template, callback, mode);
    callbacks.add(e);

    // if the event is immediate, remove the callback, then run it
    if (timing == eventTiming.IMMEDIATE) {
      for (Tuple t : database.clone().reversed()) { // clone instead of synchronized to avoid deadlock
        if (t != null && t.matches(template)) {
          callbacks.remove(e);
          if (mode == eventMode.TAKE) {
            database.remove(t);
          }
          callback.call(t);
        }
      }
    }
  }

  @Override
  public void debug(String prefix) {
    Logger.log(prefix + " Database:");
    for (Tuple t : database) {
      Logger.log(prefix + "  | " + t);
    }
    Logger.log(prefix + " Callbacks:");
    for (Event e : callbacks) {
      Logger.log(String.format("%s  | %s : %s -> %s", prefix, e.getMode(), e.getTriggerTemplate(), e.getCallback()));
    }
  }

  /**
   * Run all active callbacks.
   */
  private void runCallBacks() {
    callbacks.clone().forEach(e -> { // clone the list to avoid concurrent modification
      for (Tuple t : database.clone().reversed()) { // clone instead of synchronized to avoid deadlock
        if (t.matches(e.getTriggerTemplate())) {
          callbacks.remove(e);
          e.getCallback().call(t);
          if (e.getMode() == eventMode.TAKE) {
            database.remove(t);
          }
          break;
        }
      }
    });
  }
}
