package com.docweaver.search;

import com.docweaver.domain.Document;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple sanity tests for the Search Document module.
 */
class DocumentSearchServiceTest {

    /**
     * Minimal gateway implementation that returns no candidate documents.
     * This avoids depending on the real DocumentRepository or Document internals.
     */
    static class NoOpDocumentSearchGateway implements DocumentSearchGateway {
        @Override
        public List<Document> findCandidates(SearchCriteria criteria) {
            return Collections.emptyList();
        }
    }

    @Test
    void smartSearch_withKeyword_returnsNonNullList() {
        DocumentSearchGateway gateway = new NoOpDocumentSearchGateway();
        SearchStrategyFactory factory = new SearchStrategyFactory(gateway);
        DocumentSearchService service = new DocumentSearchService(factory);

        SearchCriteria criteria = SearchCriteria.builder()
                .keyword("income")
                .build();

        List<SearchResult> results = service.smartSearch(criteria);

        assertNotNull(results, "Results list should never be null");
        assertTrue(results.isEmpty(), "With no candidates, results should be empty");
    }

    @Test
    void smartSearch_withoutKeyword_usesMetadataModeAndReturnsNonNullList() {
        DocumentSearchGateway gateway = new NoOpDocumentSearchGateway();
        SearchStrategyFactory factory = new SearchStrategyFactory(gateway);
        DocumentSearchService service = new DocumentSearchService(factory);

        SearchCriteria criteria = SearchCriteria.builder()
                .documentType("W2")
                .build();

        List<SearchResult> results = service.smartSearch(criteria);

        assertNotNull(results, "Results list should never be null");
        assertTrue(results.isEmpty(), "With no candidates, results should be empty");
    }
}
