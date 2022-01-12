package linda.server.log;

interface LogLevelInterface {
  String toString();

  String getColor();

  int getPriority();

  String getIcon();
}

public enum LogLevel implements LogLevelInterface {
  Info {
    public String toString() {
      return "INFO";
    }

    public String getColor() {
      return Logger.ANSI_CYAN;
    }

    public int getPriority() {
      return 0;
    }

    public String getIcon() {
      return "💡";
    }
  },
  Log {
    public String toString() {
      return "LOG";
    }

    public String getColor() {
      return Logger.ANSI_BLUE;
    }

    public int getPriority() {
      return 1;
    }

    public String getIcon() {
      return "💬";
    }
  },
  Warn {
    public String toString() {
      return "WARNING";
    }

    public String getColor() {
      return Logger.ANSI_YELLOW;
    }

    public int getPriority() {
      return 2;
    }

    public String getIcon() {
      return "⚠️";
    }
  },
  Error {
    public String toString() {
      return "ERROR";
    }

    public String getColor() {
      return Logger.ANSI_RED;
    }

    public int getPriority() {
      return 3;
    }

    public String getIcon() {
      return "❌";
    }
  },
  Fatal {
    public String toString() {
      return "FATAL";
    }

    public String getColor() {
      return Logger.ANSI_RED;
    }

    public int getPriority() {
      return 4;
    }

    public String getIcon() {
      return "💀";
    }
  }
}
