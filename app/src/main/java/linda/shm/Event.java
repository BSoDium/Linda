package linda.shm;

import linda.Callback;
import linda.Tuple;
import linda.Linda.eventMode;

public class Event {
  private Tuple triggerTemplate;
  private Callback callback;
  private eventMode mode;

  public Event(Tuple template, Callback callback, eventMode mode) {
    this.triggerTemplate = template;
    this.callback = callback;
    this.mode = mode;
  }

  public Tuple getTriggerTemplate() {
    return triggerTemplate;
  }

  public Callback getCallback() {
    return callback;
  }

  public void setTrigger(Tuple trigger) {
    this.triggerTemplate = trigger;
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public eventMode getMode() {
    return mode;
  }

  public void setMode(eventMode mode) {
    this.mode = mode;
  }

}
