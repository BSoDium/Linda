package src.linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import src.linda.Callback;
import src.linda.Linda;
import src.linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
  private static ArrayList<Tuple> database;
  // private static ArrayList<Callback> callbacks;
  private static HashMap<Tuple, Callback> callbacks;

  public CentralizedLinda() {
    database = new ArrayList<Tuple>();
  }

  @Override
  public void write(Tuple t) {
    database.add(t);
  }

  @Override
  public Tuple take(Tuple template) {
    double retryTime = 1; // seconds

    for (Tuple t : database) {
      if (t.matches(template)) {
        database.remove(t);
        return t;
      }
    }
    // block until a matching tuple is found
    System.out.println("No matching tuple found, waiting for a tuple to arrive...");
    while (true) {
      // wait retryTime seconds before retrying
      try {
        Thread.sleep((long) (retryTime * 1000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      retryTime = retryTime * 1.1;
      System.out.println("Retrying...");
      // retry
      for (Tuple t : database) {
        if (t.matches(template)) {
          database.remove(t);
          return t;
        }
      }
    }
  }

  @Override
  public Tuple read(Tuple template) {
    double retryTime = 1; // seconds

    for (Tuple t : database) {
      if (t.matches(template)) {
        return t;
      }
    }
    // block until a matching tuple is found
    System.out.println("No matching tuple found, waiting for a tuple to arrive...");
    while (true) {
      // wait retryTime seconds before retrying
      try {
        Thread.sleep((long) (retryTime * 1000));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      retryTime = retryTime * 1.1;
      System.out.println("Retrying...");
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
    callbacks.put(new Tuple(template, mode), callback);
    // if the timing is immediate, check for matches now
    // TODO : implement
  }

  @Override
  public void debug(String prefix) {
    System.out.println(prefix + ": " + database);
  }

  // TO BE COMPLETED

}
