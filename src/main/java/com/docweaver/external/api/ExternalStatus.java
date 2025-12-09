package com.docweaver.external.api;

import java.time.Instant;

/**
 * Status of a document or entity in an external system.
 * Gives a standard DocWeaver view of status across different systems.
 * The adapters convert system-specific responses → this generic model.
 */
public final class ExternalStatus {

    private final String externalId;
    private final String status;         // e.g. "PENDING", "APPROVED", "ERROR"
    private final String details;        // human-readable detail
    private final Instant lastUpdated;

    public ExternalStatus(String externalId, String status, String details, Instant lastUpdated) {
        this.externalId = externalId;
        this.status = status;
        this.details = details;
        this.lastUpdated = lastUpdated;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return "ExternalStatus{" +
                "externalId='" + externalId + '\'' +
                ", status='" + status + '\'' +
                ", details='" + details + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
