package net.osomahe.esk.eventstore.entity;

/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class EventStoreException extends RuntimeException {
    public EventStoreException() {
    }

    public EventStoreException(String message) {
        super(message);
    }

    public EventStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventStoreException(Throwable cause) {
        super(cause);
    }

    public EventStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
