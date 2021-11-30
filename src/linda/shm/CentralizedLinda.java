package src.linda.shm;

import java.util.concurrent.Semaphore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import src.linda.Callback;
import src.linda.Linda;
import src.linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
  private ArrayList<Tuple> database;
  private HashMap<Tuple, Callback> readCallbacks;
  private HashMap<Tuple, Callback> takeCallbacks;
  private Semaphore mutex;

  public CentralizedLinda() {
    database = new ArrayList<Tuple>();
    readCallbacks = new HashMap<Tuple, Callback>();
    takeCallbacks = new HashMap<Tuple, Callback>();
    mutex = new Semaphore(1);
  }

  @Override
  public void write(Tuple t) {
    mutex.acquireUninterruptibly();
    database.add(t);
    runCallBacks();
    mutex.release();
  }

  @Override
  public Tuple take(Tuple template) {
    mutex.acquireUninterruptibly();
    double retryTime = 1; // seconds

    for (Tuple t : database) {
      if (t.matches(template)) {
        database.remove(t);
        runCallBacks();
        mutex.release();
        return t;
      }
    }
    // block until a matching tuple is found
    while (true) {
      // wait retryTime seconds before retrying
      try {
        Thread.sleep((long) (retryTime * 1000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      retryTime = retryTime * 1.1;
      // retry
      for (Tuple t : database) {
        if (t.matches(template)) {
          database.remove(t);
          runCallBacks();
          mutex.release();
          return t;
        }
      }
    }
  }

  @Override
  public Tuple read(Tuple template) {
    mutex.acquireUninterruptibly();
    double retryTime = 1; // seconds

    for (Tuple t : database) {
      if (t.matches(template)) {
        mutex.release();
        return t;
      }
    }

    // block until a matching tuple is found
    while (true) {
      // wait retryTime seconds before retrying
      try {
        Thread.sleep((long) (retryTime * 1000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      retryTime = retryTime * 2;
      // retry
      for (Tuple t : database) {
        if (t.matches(template)) {
          mutex.release();
          return t;
        }
      }
    }
  }

  @Override
  public Tuple tryTake(Tuple template) {
    mutex.acquireUninterruptibly();
    for (Tuple t : database) {
      if (t.matches(template)) {
        database.remove(t);
        runCallBacks();
        mutex.release();
        return t;
      }
    }
    mutex.release();
    return null;
  }

  @Override
  public Tuple tryRead(Tuple template) {
    mutex.acquireUninterruptibly();
    for (Tuple t : database) {
      if (t.matches(template)) {
        mutex.release();
        return t;
      }
    }
    mutex.release();
    return null;
  }

  @Override
  public Collection<Tuple> takeAll(Tuple template) {
    mutex.acquireUninterruptibly();
    ArrayList<Tuple> ret = new ArrayList<Tuple>();
    for (Tuple t : database) {
      if (t.matches(template)) {
        ret.add(t);
        database.remove(t);
      }
    }
    runCallBacks();
    mutex.release();
    return ret;
  }

  @Override
  public Collection<Tuple> readAll(Tuple template) {
    mutex.acquireUninterruptibly();
    ArrayList<Tuple> ret = new ArrayList<Tuple>();
    for (Tuple t : database) {
      if (t.matches(template)) {
        ret.add(t);
      }
    }
    mutex.release();
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
          callback.call(t);
          if (mode == eventMode.TAKE) {
            takeCallbacks.remove(template);
            database.remove(t);
          } else if (mode == eventMode.READ) {
            readCallbacks.remove(template);
          }
        }
      }
    }
  }

  @Override
  public void debug(String prefix) {
    System.out.println(prefix + " Database:");
    for (Tuple t : database) {
      System.out.println(prefix + "  | " + t);
    }
    System.out.println(prefix + " Read callbacks:");
    for (Tuple t : readCallbacks.keySet()) {
      System.out.println(prefix + "  | " + t);
    }
    System.out.println(prefix + " Take callbacks:");
    for (Tuple t : takeCallbacks.keySet()) {
      System.out.println(prefix + "  | " + t);
    }
  }

  /**
   * Run all active callbacks.
   */
  private void runCallBacks() {
    for (Tuple t : database) {
      readCallbacks.keySet().forEach(key -> {
        if (t.matches(key)) {
          readCallbacks.get(key).call(t);
          readCallbacks.remove(key);
        }
      });
      takeCallbacks.keySet().forEach(key -> {
        if (t.matches(key)) {
          takeCallbacks.get(key).call(t);
          takeCallbacks.remove(key);
        }
      });
    }
  }

  /**
   * A simple routine to run a task and block until it is complete.
   * <p>
   * Note: this is currently not used.
   * </p>
   * 
   * @param r
   */
  private void runWithRetry(Runnable r) {
    double retryTime = 1; // seconds

    while (true) {
      // wait retryTime seconds before retrying
      try {
        Thread.sleep((long) (retryTime * 1000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      retryTime = retryTime * 2;
      System.out.println("Retrying...");
      // retry
      r.run();
    }
  }

}
