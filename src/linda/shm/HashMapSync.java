package src.linda.shm;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

public class HashMapSync<A, B> extends HashMap<A, B> {
  private final Object lock = new Object();

  @Override
  public B put(A key, B value) {
    synchronized (lock) {
      return super.put(key, value);
    }
  }

  @Override
  public B remove(Object key) {
    synchronized (lock) {
      return super.remove(key);
    }
  }

  @Override
  public B get(Object key) {
    synchronized (lock) {
      return super.get(key);
    }
  }

  @Override
  public Set<A> keySet() {
    synchronized (lock) {
      return super.keySet();
    }
  }

  /**
   * Calls the given consumer for each key in the map.
   * @param consumer
   */
  public void forEachKey(Consumer<? super A> consumer) {
    synchronized (lock) {
      for (A key : keySet()) {
        consumer.accept(key);
      }
    }
  }

  /**
   * Calls the given consumer for each value in the map.
   * @param consumer
   */
  public void forEachValue(Consumer<? super B> consumer) {
    synchronized (lock) {
      for (B value : values()) {
        consumer.accept(value);
      }
    }
  }
}
