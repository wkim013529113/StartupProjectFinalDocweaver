package com.docweaver.external.api;

import com.docweaver.core.document.StructuredDocument;

/**
 * DocWeaver's abstraction over an external system.
 * This is the "target" interface for the Adapter pattern.
 * Key interface that all adapters implement.
 * DocWeaver only talks to ExternalSystemClient, not to LOS/CRM SDKs directly.
 */
public interface ExternalSystemClient {

    /**
     * Push a document into the external system.
     */
    ExternalPushResult pushDocument(StructuredDocument document) throws ExternalSystemException;

    /**
     * Check the status of a previously pushed document.
     */
    ExternalStatus checkStatus(String externalId) throws ExternalSystemException;
}
