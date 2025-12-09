package com.docweaver.external.api;

/**
 * Result of pushing a document to an external system.
 * Normalizes the push result across all systems
 * DocWeaver doesn’t have to know LOS-specific or CRM-specific details; it just sees success, externalId, message.
 */
public final class ExternalPushResult {

    private final boolean success;
    private final String externalId;
    private final String message;

    public ExternalPushResult(boolean success, String externalId, String message) {
        this.success = success;
        this.externalId = externalId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ExternalPushResult{" +
                "success=" + success +
                ", externalId='" + externalId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
