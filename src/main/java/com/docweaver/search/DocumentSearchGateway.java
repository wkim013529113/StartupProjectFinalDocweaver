// NEW
package com.docweaver.search;

import com.docweaver.domain.Document;

import java.util.List;

/**
 * Abstraction over however documents are loaded for searching.
 * You will implement this using your existing DocumentRepository.
 */
public interface DocumentSearchGateway {

    /**
     * Returns candidate documents that should be considered for search
     * based on high-level criteria (type, date, tags, etc.).
     *
     * Implementation can decide how much is pushed down to DB.
     */
    List<Document> findCandidates(SearchCriteria criteria);
}
