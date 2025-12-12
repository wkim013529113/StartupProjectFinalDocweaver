package com.docweaver.external.sdk;

import java.util.Map;
import java.util.UUID;

/**
 * Stub for a Salesforce integration client.
 */
public class SalesforceApiClient {

    public String upsertOpportunity(Map<String, Object> payload) {
        // Simulate returning an opportunity ID
        return "SF-" + UUID.randomUUID();
    }

    public String getOpportunityStage(String externalId) {
        // Simulate opportunity stage
        return "NEGOTIATION";
    }
}
