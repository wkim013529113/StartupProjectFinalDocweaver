package com.docweaver.external.app;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.external.api.*;
import com.docweaver.external.adapters.CrmAdapter;
import com.docweaver.external.adapters.LosAdapter;
import com.docweaver.external.adapters.SalesforceAdapter;
import com.docweaver.external.sdk.CrmApiClient;
import com.docweaver.external.sdk.LosApiClient;
import com.docweaver.external.sdk.SalesforceApiClient;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Facade used by the rest of DocWeaver to talk to external systems.
 * Internally delegates to the correct ExternalSystemClient (Adapter).
 */
public class ExternalSystemGateway {

    private final Map<ExternalSystemType, ExternalSystemClient> clients =
            new EnumMap<>(ExternalSystemType.class);

    public ExternalSystemGateway() {
        // Default wiring: create adapters with default SDK clients.
        clients.put(ExternalSystemType.LOS,
                new LosAdapter(new LosApiClient()));
        clients.put(ExternalSystemType.CRM,
                new CrmAdapter(new CrmApiClient()));
        clients.put(ExternalSystemType.SALESFORCE,
                new SalesforceAdapter(new SalesforceApiClient()));
    }

    public ExternalSystemGateway(Map<ExternalSystemType, ExternalSystemClient> customClients) {
        Objects.requireNonNull(customClients, "customClients must not be null");
        clients.putAll(customClients);
    }

    public ExternalPushResult pushTo(ExternalSystemType type,
                                     StructuredDocument document) throws ExternalSystemException {
        ExternalSystemClient client = getClient(type);
        return client.pushDocument(document);
    }

    public ExternalStatus checkStatus(ExternalSystemType type,
                                      String externalId) throws ExternalSystemException {
        ExternalSystemClient client = getClient(type);
        return client.checkStatus(externalId);
    }

    private ExternalSystemClient getClient(ExternalSystemType type) throws ExternalSystemException {
        ExternalSystemClient client = clients.get(type);
        if (client == null) {
            throw new ExternalSystemException("No ExternalSystemClient registered for type: " + type);
        }
        return client;
    }
}
