package linda;

public class SynchronousCallback {

  private Callback cb;

  public SynchronousCallback(Callback cb) {
    this.cb = cb;
  }

  /**
   * Synchronous call: the associated callback is run and this one waits for it to
   * finish.
   * 
   * @param t the tuple to pass to the callback
   */
  public void call(final Tuple t) {
    cb.call(t);
  }
}
