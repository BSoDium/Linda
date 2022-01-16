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
    // this synchonized block isn't necessary, and also makes it harder to debug,
    // but I'm keeping it for safety reasons
    // synchronized (lock) {
    super.forEach(action);
    // }
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

  /**
   * Retrieves the method-synchronizing lock object; this method can be used to
   * synchronize big chunks of code that are not implemented in the aforementioned
   * API.
   * 
   * @return the lock object
   */
  public Object getLock() {
    return lock;
  }
}
