package linda.shm;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ArrayListSync<A> extends ArrayList<A> {
  private final Object lock = new Object();

  public ArrayListSync() {
    super();
  }

  public ArrayListSync(ArrayList<A> list) {
    super(list);
  }

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

  @Override
  public void forEach(Consumer<? super A> action) {
    synchronized (lock) {
      super.forEach(action);
    }
  }

  @Override
  public ArrayListSync<A> clone() {
    return new ArrayListSync<A>(this);
  }

  /**
   * Returns the same list, but reversed
   * 
   * @return
   */
  public ArrayListSync<A> reversed() {
    ArrayListSync<A> list = new ArrayListSync<A>();
    synchronized (lock) {
      for (int i = size() - 1; i >= 0; i--) {
        list.add(get(i));
      }
    }
    return list;
  }
}
