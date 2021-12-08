package linda.shm;


import java.util.ArrayList;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class PrimitiveCentralizedLinda implements Linda {
  private ArrayListSync<Tuple> database;
  private HashMapSync<Tuple, Callback> readCallbacks;
  private HashMapSync<Tuple, Callback> takeCallbacks;

  public PrimitiveCentralizedLinda() {
    database = new ArrayListSync<Tuple>();
    readCallbacks = new HashMapSync<Tuple, Callback>();
    takeCallbacks = new HashMapSync<Tuple, Callback>();
  }

  @Override
  public void write(Tuple t) {
    database.add(t);
    runCallBacks();
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

    // block until a matching tuple is found (cpu-intensive version)
    double retryTime = 1; // seconds
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

    // block until a matching tuple is found (cpu-intensive version)
    double retryTime = 1; // seconds
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
    System.out.println(prefix + " Database:");
    for (Tuple t : database) {
      System.out.println(prefix + "  | " + t);
    }
    System.out.println(prefix + " Read callbacks:");
    for (Tuple t : readCallbacks.keySet()) {
      System.out.println(prefix + "  | " + t + " -> " + readCallbacks.get(t));
    }
    System.out.println(prefix + " Take callbacks:");
    for (Tuple t : takeCallbacks.keySet()) {
      System.out.println(prefix + "  | " + t + " -> " + takeCallbacks.get(t));
    }
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
