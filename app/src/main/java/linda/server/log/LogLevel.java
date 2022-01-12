package linda.server.log;

interface LogLevelInterface {
  String toString();

  String getColor();
}

public enum LogLevel implements LogLevelInterface {
  Info {
    public String toString() {
      return "INFO";
    }

    public String getColor() {
      return Logger.ANSI_CYAN;
    }
  },
  Log {
    public String toString() {
      return "LOG";
    }

    public String getColor() {
      return Logger.ANSI_BLUE;
    }
  },
  Warn {
    public String toString() {
      return "WARNING";
    }

    public String getColor() {
      return Logger.ANSI_YELLOW;
    }
  },
  Error {
    public String toString() {
      return "ERROR";
    }

    public String getColor() {
      return Logger.ANSI_RED;
    }
  },
  Fatal {
    public String toString() {
      return "FATAL";
    }

    public String getColor() {
      return Logger.ANSI_RED;
    }
  }
}
