package com.jtestim.tomcatssl.util;

/**
 * Created by bensende on 04/11/2018.
 */
public class SchedulingException extends Exception {
    public SchedulingException() {
    }

    public SchedulingException(String message) {
        super(message);
    }

    public SchedulingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulingException(Throwable cause) {
        super(cause);
    }

    public SchedulingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
