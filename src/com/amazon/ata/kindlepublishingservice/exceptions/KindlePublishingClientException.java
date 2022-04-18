package com.amazon.ata.kindlepublishingservice.exceptions;

public class KindlePublishingClientException extends RuntimeException {
    private static final long serialVersionUID = -5380856276437617051L;

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public KindlePublishingClientException(String message) {
        super(message);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public KindlePublishingClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
