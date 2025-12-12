package com.docweaver.external.adapters;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.external.api.ExternalPushResult;
import com.docweaver.external.api.ExternalStatus;
import com.docweaver.external.api.ExternalSystemClient;
import com.docweaver.external.api.ExternalSystemException;
import com.docweaver.external.sdk.CrmApiClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter that makes a CrmApiClient look like an ExternalSystemClient.
 */
public class CrmAdapter implements ExternalSystemClient {

    private final CrmApiClient crmApiClient;

    public CrmAdapter(CrmApiClient crmApiClient) {
        this.crmApiClient = crmApiClient;
    }

    @Override
    public ExternalPushResult pushDocument(StructuredDocument document) throws ExternalSystemException {
        try {
            Map<String, Object> payload = mapDocumentToCrmPayload(document);
            String externalId = crmApiClient.createOrUpdateContact(payload);
            return new ExternalPushResult(true, externalId, "Pushed to CRM successfully");
        } catch (Exception e) {
            throw new ExternalSystemException("Failed to push document to CRM", e);
        }
    }

    @Override
    public ExternalStatus checkStatus(String externalId) throws ExternalSystemException {
        try {
            String rawStatus = crmApiClient.getContactStatus(externalId);
            return new ExternalStatus(externalId, rawStatus,
                    "CRM contact status: " + rawStatus, Instant.now());
        } catch (Exception e) {
            throw new ExternalSystemException("Failed to check CRM contact status", e);
        }
    }

    private Map<String, Object> mapDocumentToCrmPayload(StructuredDocument document) {
        // Example mapping: map borrower info to CRM contact fields
        Map<String, Object> payload = new HashMap<>();
        payload.put("fullName", document.getFields().getOrDefault("borrowerName", "Unknown"));
        payload.put("email", document.getFields().getOrDefault("borrowerEmail", null));
        payload.put("source", "DocWeaver");
        return payload;
    }
}
