package com.lavy.pixus.utils.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Pattern;

public class AssertionLoggerCapture extends AppenderBase<ILoggingEvent> implements Closeable {

    private final Deque<LogEntry> queue = new ConcurrentLinkedDeque<>();
    private final boolean shouldCaptureStackTrace;
    private static final String APPENDER_NAME = "ASSERTION_LOG_CAPTURE";

    public AssertionLoggerCapture() {
        this(false);
    }

    public AssertionLoggerCapture(boolean shouldCaptureStackTrace) {
        super();

        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        setName(APPENDER_NAME);
        rootLogger.addAppender(this);

        this.shouldCaptureStackTrace = shouldCaptureStackTrace;

        super.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        LogEntry logEntry = new LogEntry();
        logEntry.message = eventObject.getFormattedMessage();
        logEntry.level = eventObject.getLevel();
        logEntry.loggerName = eventObject.getLoggerName();

        if (shouldCaptureStackTrace && eventObject.getThrowableProxy() != null) {
            logEntry.stackTrace = eventObject.getThrowableProxy().getMessage();
        }

        queue.addFirst(logEntry);
    }

    public void clear() {
        queue.clear();
    }

    public List<LogEntry> getLogEntries() {
        return new ArrayList<>(queue);
    }

    public int getNumberOfMessages() {
        return queue.size();
    }

    public int countText(final String... texts) {
        int found = 0;
        List<Pattern> rs = Arrays.stream(texts).map(Pattern::compile).toList();
        for (LogEntry logEntry : queue) {
            for (Pattern r : rs) {
                if (r.matcher(logEntry.message).matches()) {
                    found++;
                }
            }
        }

        return found;
    }

    public boolean anyMatch(final String regex) {
        Pattern r = Pattern.compile(regex);
        for (LogEntry logEntry : queue) {
            if (r.matcher(logEntry.message).matches()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyLevel(LogLevel logLevel) {
        Level implLevel = logLevel.toImplLevel();
        for (LogEntry logEntry : queue) {
            if (logEntry.level.equals(implLevel)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void close() throws IOException {
        queue.clear();
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAppender(this);
    }

    public enum LogLevel {
        ERROR(Level.ERROR),
        WARN(Level.WARN),
        INFO(Level.INFO),
        DEBUG(Level.DEBUG),
        TRACE(Level.TRACE);

        private final Level implLevel;

        LogLevel(Level implLevel) {
            this.implLevel = implLevel;
        }

        Level toImplLevel() {
            return implLevel;
        }

        static LogLevel ofLevel(Level implLevel) {
            for (LogLevel logLevel : LogLevel.values()) {
                if (logLevel.implLevel.equals(implLevel)) {
                    return logLevel;
                }
            }
            throw new IllegalArgumentException("Unexpected log level: " + implLevel);
        }
    }

    public static class LogEntry {
        private String message;
        private String stackTrace;
        private String loggerName;
        private Level level;

        public String getMessage() {
            return message;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public String getLoggerName() {
            return loggerName;
        }

        public LogLevel getLogLevel() {
            return LogLevel.ofLevel(level);
        }
    }

}
