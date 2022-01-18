package linda.shm;

import java.util.ArrayList;

/**
 * A Thread-safe implementation of ArrayList that uses a lock to synchronize
 * access.
 */
public class ArrayListSync<A> extends ArrayList<A> {
  private final Object lock = new Object();

  public ArrayListSync() {
    super();
  }

  public ArrayListSync(ArrayList<A> list) {
    super(list);
  }

  /**
   * Adds an element to the list.
   */
  @Override
  public boolean add(A a) {
    synchronized (lock) {
      return super.add(a);
    }
  }

  /**
   * Adds an element to the list.
   * 
   * @param index
   * @apiNote This method is not thread-safe.
   */
  public boolean defaultAdd(A a) {
    return super.add(a);
  }

  /**
   * Removes an element from the list.
   */
  @Override
  public boolean remove(Object o) {
    synchronized (lock) {
      return super.remove(o);
    }
  }

  /**
   * Removes an element from the list.
   * 
   * @param index
   * @apiNote This method is not thread-safe.
   */
  public boolean defaultRemove(Object o) {
    return super.remove(o);
  }

  /**
   * Returns a new ArrayList containing all elements in this list.
   */
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
