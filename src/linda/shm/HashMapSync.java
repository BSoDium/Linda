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

  public void forEachKey(Consumer<? super A> consumer) {
    synchronized (lock) {
      for (A key : keySet()) {
        consumer.accept(key);
      }
    }
  }
}
