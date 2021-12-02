package src.linda.shm;

import java.util.ArrayList;

public class ArrayListSync<A> extends ArrayList<A> {
  private final Object lock = new Object();

  @Override
  public boolean add(A a) {
    synchronized (lock) {
      return super.add(a);
    }
  }

  @Override
  public boolean remove(Object o) {
    synchronized (lock) {
      return super.remove(o);
    }
  }
}
