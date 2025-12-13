package com.docweaver.external.adapters;

import com.docweaver.external.api.ExternalPushResult;
import com.docweaver.external.api.ExternalStatus;
import com.docweaver.external.api.ExternalSystemClient;
import com.docweaver.external.api.ExternalSystemException;
import com.docweaver.external.sdk.SalesforceApiClient;
import com.docweaver.domain.StructuredDocument;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter that makes a SalesforceApiClient look like an ExternalSystemClient.
 */
public class SalesforceAdapter implements ExternalSystemClient {

    private final SalesforceApiClient salesforceApiClient;

    public SalesforceAdapter(SalesforceApiClient salesforceApiClient) {
        this.salesforceApiClient = salesforceApiClient;
    }

    @Override
    public ExternalPushResult pushDocument(StructuredDocument document) throws ExternalSystemException {
        try {
            Map<String, Object> payload = mapDocumentToSalesforcePayload(document);
            String externalId = salesforceApiClient.upsertOpportunity(payload);
            return new ExternalPushResult(true, externalId, "Pushed to Salesforce successfully");
        } catch (Exception e) {
            throw new ExternalSystemException("Failed to push document to Salesforce", e);
        }
    }

    @Override
    public ExternalStatus checkStatus(String externalId) throws ExternalSystemException {
        try {
            String stage = salesforceApiClient.getOpportunityStage(externalId);
            return new ExternalStatus(externalId, stage,
                    "Salesforce opportunity stage: " + stage, Instant.now());
        } catch (Exception e) {
            throw new ExternalSystemException("Failed to check Salesforce opportunity status", e);
        }
    }

    private Map<String, Object> mapDocumentToSalesforcePayload(StructuredDocument document) {
        // Example mapping: map loan amount and borrower to opportunity fields
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", document.getFields().getOrDefault("loanName", "Loan Opportunity"));
        payload.put("amount", document.getFields().getOrDefault("loanAmount", 0));
        payload.put("stage", "QUALIFICATION");
        payload.put("sourceSystem", "DocWeaver");
        return payload;
    }
}
