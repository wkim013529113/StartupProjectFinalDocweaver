package com.docweaver.external.adapters;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.external.api.ExternalPushResult;
import com.docweaver.external.api.ExternalStatus;
import com.docweaver.external.api.ExternalSystemClient;
import com.docweaver.external.api.ExternalSystemException;
import com.docweaver.external.sdk.LosApiClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter that makes a LosApiClient look like an ExternalSystemClient.
 */
public class LosAdapter implements ExternalSystemClient {

    private final LosApiClient losApiClient;

    public LosAdapter(LosApiClient losApiClient) {
        this.losApiClient = losApiClient;
    }

    @Override
    public ExternalPushResult pushDocument(StructuredDocument document) throws ExternalSystemException {
        try {
            Map<String, Object> payload = mapDocumentToLosPayload(document);
            String externalId = losApiClient.uploadLoan(payload);
            return new ExternalPushResult(true, externalId, "Pushed to LOS successfully");
        } catch (Exception e) {
            throw new ExternalSystemException("Failed to push document to LOS", e);
        }
    }

    @Override
    public ExternalStatus checkStatus(String externalId) throws ExternalSystemException {
        try {
            String rawStatus = losApiClient.getLoanStatus(externalId);
            return new ExternalStatus(externalId, rawStatus,
                    "LOS status: " + rawStatus, Instant.now());
        } catch (Exception e) {
            throw new ExternalSystemException("Failed to check status in LOS", e);
        }
    }

    private Map<String, Object> mapDocumentToLosPayload(StructuredDocument document) {
        // Example mapping – in reality this would be more complex
        Map<String, Object> payload = new HashMap<>();
        payload.putAll(document.getFields());
        if (document.isTabular()) {
            payload.put("records", document.getRecords());
        }
        return payload;
    }
}
