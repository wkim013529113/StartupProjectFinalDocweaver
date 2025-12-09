package com.docweaver.external.api;

/**
 * Represents integration errors with external systems.
 * One consistent exception type for all external integrations.
 * Makes it easy for higher layers to catch and handle integration errors.
 */
public class ExternalSystemException extends Exception {

    public ExternalSystemException(String message) {
        super(message);
    }

    public ExternalSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
