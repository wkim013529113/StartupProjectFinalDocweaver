package com.docweaver.external.sdk;

import java.util.Map;
import java.util.UUID;

/**
 * Stub for an external Loan Origination System client.
 * In a real system, this would call HTTP APIs or a vendor SDK.
 */
public class LosApiClient {

    public String uploadLoan(Map<String, Object> payload) {
        // Simulate success and return a fake external ID
        return "LOS-" + UUID.randomUUID();
    }

    public String getLoanStatus(String externalId) {
        // Simulate a simple status response
        return "APPROVED";
    }
}
