package linda.server.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Logger class.
 */
public class Logger {
  public static class Message {
    public String message;
    public LogLevel level;

    public Message(String message, LogLevel level) {
      this.message = message;
      this.level = level;
    }
  }

  private static HashMap<Date, Message> history = new HashMap<Date, Message>();

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";

  /**
   * Logs a message with the given level.
   * 
   * @param message the message to log.
   * @param level   the level of the message.
   */
  public static void log(String message, LogLevel level) {
    Date date = new Date();
    history.put(date, new Message(message, level));
    System.out.printf("%s%s %s: %s\n", level.getColor(), level, ANSI_RESET, message);
  }

  /**
   * Logs a message with the default level {@code LogLevel.Log}.
   * 
   * @param message the message to log.
   */
  public static void log(String message) {
    log(message, LogLevel.Log);
  }

  /**
   * Prints the history of the logger.
   * 
   * @return The history of the logger.
   */
  public static ArrayList<Message> getHistory() {
    return new ArrayList<Message>(history.values());
  }

  /**
   * Retrieve the history of the messages in a given date range.
   * 
   * @param from the start of the range
   * @param to   the end of the range
   * @return the history of the messages in the given range
   */
  public static ArrayList<Message> getHistory(Date from, Date to) {
    ArrayList<Message> result = new ArrayList<Message>();
    for (Date d : history.keySet()) {
      if (d.compareTo(from) >= 0 && d.compareTo(to) <= 0) {
        result.add(history.get(d));
      }
    }
    return result;
  }
}
