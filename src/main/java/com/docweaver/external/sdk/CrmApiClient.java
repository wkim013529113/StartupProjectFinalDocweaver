package com.docweaver.external.sdk;

import java.util.Map;
import java.util.UUID;

/**
 * Stub for a CRM system client.
 */
public class CrmApiClient {

    public String createOrUpdateContact(Map<String, Object> payload) {
        // Simulate returning a contact ID
        return "CRM-" + UUID.randomUUID();
    }

    public String getContactStatus(String externalId) {
        // Simulate status
        return "ACTIVE";
    }
}
