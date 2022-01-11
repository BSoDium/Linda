package linda.shm;

import java.io.ByteArrayOutputStream;
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
  private HashMapSync<Tuple, Callback> readCallbacks;
  private HashMapSync<Tuple, Callback> takeCallbacks;
  private final Object lock = new Object();

  public CentralizedLinda() {
    database = new ArrayListSync<Tuple>();
    readCallbacks = new HashMapSync<Tuple, Callback>();
    takeCallbacks = new HashMapSync<Tuple, Callback>();
  }

  @Override
  public void write(Tuple t) {
    database.add(t);
    runCallBacks();
    // wake up all waiting threads after a write
    synchronized (lock) {
      lock.notifyAll();
    }
  }

  @Override
  public Tuple take(Tuple template) {

    for (Tuple t : database) {
      if (t.matches(template)) {
        database.remove(t);
        runCallBacks();
        return t;
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
      for (Tuple t : database) {
        if (t.matches(template)) {
          database.remove(t);
          runCallBacks();
          return t;
        }
      }
    }
  }

  @Override
  public Tuple read(Tuple template) {

    for (Tuple t : database) {
      if (t.matches(template)) {
        return t;
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
      for (Tuple t : database) {
        if (t.matches(template)) {
          return t;
        }
      }
    }
  }

  @Override
  public Tuple tryTake(Tuple template) {
    for (Tuple t : database) {
      if (t.matches(template)) {
        database.remove(t);
        runCallBacks();
        return t;
      }
    }
    return null;
  }

  @Override
  public Tuple tryRead(Tuple template) {
    for (Tuple t : database) {
      if (t.matches(template)) {
        return t;
      }
    }
    return null;
  }

  @Override
  public Collection<Tuple> takeAll(Tuple template) {
    ArrayList<Tuple> ret = new ArrayList<Tuple>();
    for (Tuple t : database) {
      if (t.matches(template)) {
        ret.add(t);
        database.remove(t);
      }
    }
    runCallBacks();
    return ret;
  }

  @Override
  public Collection<Tuple> readAll(Tuple template) {
    ArrayList<Tuple> ret = new ArrayList<Tuple>();
    for (Tuple t : database) {
      if (t.matches(template)) {
        ret.add(t);
      }
    }
    return ret;
  }

  @Override
  public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
    // store the callback
    if (mode == eventMode.READ) {
      readCallbacks.put(template, callback);
    } else if (mode == eventMode.TAKE) {
      takeCallbacks.put(template, callback);
    } else {
      throw new IllegalArgumentException("Invalid event mode");
    }

    // if the event is immediate, call the callback, then remove it
    if (timing == eventTiming.IMMEDIATE) {
      for (Tuple t : database) {
        if (t.matches(template)) {
          if (mode == eventMode.TAKE) {
            takeCallbacks.remove(template);
            database.remove(t);
          } else if (mode == eventMode.READ) {
            readCallbacks.remove(template);
          }
          callback.call(t);
        }
      }
    }
  }

  @Override
  public void debug(String prefix) {
    debug(prefix, System.out);
  }

  public void debug(String prefix, PrintStream ps) {
    PrintStream old = System.out;
    System.setOut(ps);
    Logger.log(prefix + " Database:");
    for (Tuple t : database) {
      Logger.log(prefix + "  | " + t);
    }
    Logger.log(prefix + " Read callbacks:");
    for (Tuple t : readCallbacks.keySet()) {
      Logger.log(prefix + "  | " + t + " -> " + readCallbacks.get(t));
    }
    Logger.log(prefix + " Take callbacks:");
    for (Tuple t : takeCallbacks.keySet()) {
      Logger.log(prefix + "  | " + t + " -> " + takeCallbacks.get(t));
    }

    System.out.flush();
    System.setOut(old);
  }

  /**
   * Run all active callbacks.
   */
  private void runCallBacks() {
    readCallbacks.forEachKey(key -> {
      for (Tuple t : database.clone()) {
        if (t.matches(key)) {
          readCallbacks.get(key).call(t);
          readCallbacks.remove(key);
        }
      }
    });
    takeCallbacks.forEachKey(key -> {
      for (Tuple t : database.clone()) {
        if (t.matches(key)) {
          database.remove(t);
          takeCallbacks.get(key).call(t);
          takeCallbacks.remove(key);
        }
      }
    });
  }

}
