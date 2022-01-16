 public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
    // store the callback
    Event e = new Event(template, callback, mode);
    callbacks.add(e);

    // if the event is immediate, remove the callback, then run it
    if (timing == eventTiming.IMMEDIATE) {
      for (Tuple t : database.clone()) {
        if (t.matches(template)) {
          callbacks.remove(e);
          if (mode == eventMode.TAKE) {
            database.remove(t);
          }
          callback.call(t);
        }
      }
    }
  }