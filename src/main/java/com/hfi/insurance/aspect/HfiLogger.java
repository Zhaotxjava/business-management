package com.hfi.insurance.aspect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

public final class HfiLogger implements Logger {
    private static final String FQCN = HfiLogger.class.getName();
    private final LocationAwareLogger logger;
    private int keepOnsiteLevel;

    public static Logger create(Class<?> logClass) {
        return new HfiLogger(LoggerFactory.getLogger(logClass));
    }

    public static Logger create(Class<?> logClass, int keepOnsiteLevel) {
        return new HfiLogger(LoggerFactory.getLogger(logClass), keepOnsiteLevel);
    }

    private HfiLogger(Logger logger) {
        this(logger, 1);
    }

    private HfiLogger(Logger logger, int keepOnsiteLevel) {
        this.keepOnsiteLevel = 1;
        this.logger = (LocationAwareLogger)logger;
        this.keepOnsiteLevel = keepOnsiteLevel;
    }

    public String getName() {
        return this.logger.getName();
    }

    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    public void trace(String msg) {
        this.logger.log((Marker)null, FQCN, 0, msg, (Object[])null, (Throwable)null);
    }

    public void trace(String format, Object arg) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log((Marker)null, FQCN, 0, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void trace(String format, Object arg1, Object arg2) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log((Marker)null, FQCN, 0, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void trace(String format, Object... arguments) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 0, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void trace(String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.trace("Error: <{}> -- {} -- on site: {}, errors' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isTraceEnabled(Marker marker) {
        return this.logger.isTraceEnabled(marker);
    }

    public void trace(Marker marker, String msg) {
        this.logger.log(marker, FQCN, 0, msg, (Object[])null, (Throwable)null);
    }

    public void trace(Marker marker, String format, Object arg) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log(marker, FQCN, 0, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log(marker, FQCN, 0, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void trace(Marker marker, String format, Object... argArray) {
        if (this.logger.isTraceEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            this.logger.log(marker, FQCN, 0, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void trace(Marker marker, String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.trace(marker, "Exception: <{}> -- {} -- On-site: {}, details' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    public void debug(String msg) {
        this.logger.log((Marker)null, FQCN, 10, msg, (Object[])null, (Throwable)null);
    }

    public void debug(String format, Object arg) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log((Marker)null, FQCN, 10, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void debug(String format, Object arg1, Object arg2) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log((Marker)null, FQCN, 10, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void debug(String format, Object... arguments) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 10, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void debug(String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.debug("Error: <{}> -- {} -- On-site: {}, errors' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isDebugEnabled(Marker marker) {
        return this.logger.isDebugEnabled(marker);
    }

    public void debug(Marker marker, String msg) {
        this.logger.log(marker, FQCN, 10, msg, (Object[])null, (Throwable)null);
    }

    public void debug(Marker marker, String format, Object arg) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log(marker, FQCN, 10, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log(marker, FQCN, 10, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void debug(Marker marker, String format, Object... arguments) {
        if (this.logger.isDebugEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 10, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void debug(Marker marker, String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.debug(marker, "Exception: <{}> -- {} -- On-site: {}, details' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    public void info(String msg) {
        this.logger.log((Marker)null, FQCN, 20, msg, (Object[])null, (Throwable)null);
    }

    public void info(String format, Object arg) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log((Marker)null, FQCN, 20, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void info(String format, Object arg1, Object arg2) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log((Marker)null, FQCN, 20, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void info(String format, Object... arguments) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 20, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void info(String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.info("Error: <{}> -- {} -- On-site: {}, errors' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isInfoEnabled(Marker marker) {
        return this.logger.isInfoEnabled(marker);
    }

    public void info(Marker marker, String msg) {
        this.logger.log(marker, FQCN, 20, msg, (Object[])null, (Throwable)null);
    }

    public void info(Marker marker, String format, Object arg) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log(marker, FQCN, 20, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log(marker, FQCN, 20, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void info(Marker marker, String format, Object... arguments) {
        if (this.logger.isInfoEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 20, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void info(Marker marker, String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.info(marker, "Exception: <{}> -- {} -- On-site: {}, details' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    public void warn(String msg) {
        this.logger.log((Marker)null, FQCN, 30, msg, (Object[])null, (Throwable)null);
    }

    public void warn(String format, Object arg) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log((Marker)null, FQCN, 30, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void warn(String format, Object... arguments) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 30, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void warn(String format, Object arg1, Object arg2) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log((Marker)null, FQCN, 30, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void warn(String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.warn("Error: <{}> -- {} -- On-site: {}, errors' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isWarnEnabled(Marker marker) {
        return this.logger.isWarnEnabled(marker);
    }

    public void warn(Marker marker, String msg) {
        this.logger.log(marker, FQCN, 30, msg, (Object[])null, (Throwable)null);
    }

    public void warn(Marker marker, String format, Object arg) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log(marker, FQCN, 30, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log(marker, FQCN, 30, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void warn(Marker marker, String format, Object... arguments) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 30, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void warn(Marker marker, String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.warn(marker, "Exception: <{}> -- {} -- On-site: {}, details' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    public void error(String msg) {
        this.logger.log((Marker)null, FQCN, 40, msg, (Object[])null, (Throwable)null);
    }

    public void error(String format, Object arg) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log((Marker)null, FQCN, 40, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void error(String format, Object arg1, Object arg2) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log((Marker)null, FQCN, 40, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void error(String format, Object... arguments) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 40, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void error(String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.error("Error: <{}> -- {} -- On-site: {}, errors' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    public boolean isErrorEnabled(Marker marker) {
        return this.logger.isDebugEnabled(marker);
    }

    public void error(Marker marker, String msg) {
        this.logger.log(marker, FQCN, 40, msg, (Object[])null, (Throwable)null);
    }

    public void error(Marker marker, String format, Object arg) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg});
            this.logger.log(marker, FQCN, 40, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, new Object[]{arg1, arg2});
            this.logger.log(marker, FQCN, 40, ft.getMessage(), (Object[])null, (Throwable)null);
        }

    }

    public void error(Marker marker, String format, Object... arguments) {
        if (this.logger.isWarnEnabled()) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
            this.logger.log((Marker)null, FQCN, 40, ft.getMessage(), arguments, (Throwable)null);
        }

    }

    public void error(Marker marker, String msg, Throwable t) {
        String concatenatedErrorMessage = this.concatErrorMessage(t);
        this.error(marker, "Exception: <{}> -- {} -- On-site: {}, details' chain: {}", t.getClass().getName(), msg, this.getOnsite(t), concatenatedErrorMessage);
    }

    private String concatErrorMessage(Throwable t) {
        String errorMessage = null != t.getMessage() ? t.getMessage() : "N/A";
        if (null != t.getCause()) {
            errorMessage = String.format("%s->[%s]", errorMessage, this.concatErrorMessage(t.getCause()));
        }

        return String.format("<%s> -- %s -- On-site: %s", t.getClass().getName(), errorMessage, this.getOnsite(t));
    }

    private String getOnsite(Throwable t) {
        StackTraceElement[] stackTraces = t.getStackTrace();
        if (0 == stackTraces.length) {
            return "N/A";
        } else {
            StringBuilder builder = new StringBuilder();

            for(int index = 0; index < stackTraces.length && index < this.keepOnsiteLevel; ++index) {
                builder.append(String.format("Level(%s): %s;   ", index + 1, stackTraces[index].toString()));
            }

            return builder.toString();
        }
    }
}
