package com.docweaver.search;

import com.docweaver.domain.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Strategy that searches using metadata filters only (type, tags, dates).
 */
public class MetadataSearchStrategy implements SearchStrategy {

    private final DocumentSearchGateway searchGateway;

    public MetadataSearchStrategy(DocumentSearchGateway searchGateway) {
        if (searchGateway == null) {
            throw new IllegalArgumentException("searchGateway must not be null");
        }
        this.searchGateway = searchGateway;
    }

    @Override
    public List<SearchResult> search(SearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("criteria must not be null");
        }

        List<Document> documents = searchGateway.findCandidates(criteria);

        if (documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<SearchResult> results = new ArrayList<>();
        for (Document doc : documents) {
            results.add(new SearchResult(doc, 1.0, buildSnippet(doc)));
        }

        return results;
    }

    private String buildSnippet(Document doc) {
        // Only include information guaranteed to exist
        return "Type=" + doc.getDocumentType();
    }
}
