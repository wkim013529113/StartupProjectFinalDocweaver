package com.docweaver.external.api;

/**
 * Identifies which external system we are integrating with.
 * type-safe way to choose which external system (LOS / CRM / Salesforce).
 * Used by the gateway to select the correct adapter.
 */
public enum ExternalSystemType {
    LOS,        // Loan Origination System
    CRM,        // Customer Relationship Management
    SALESFORCE  // Example of another external system
}
