package com.docweaver.search;

import java.util.List;
import java.util.Objects;

/**
 * Facade service for document search.
 *
 * Other modules should depend on this class instead of concrete strategies.
 */
public class DocumentSearchService {

    private final SearchStrategyFactory strategyFactory;

    public DocumentSearchService(SearchStrategyFactory strategyFactory) {
        this.strategyFactory = Objects.requireNonNull(strategyFactory);
    }

    /**
     * Performs a search using the given criteria and mode.
     */
    public List<SearchResult> search(SearchCriteria criteria, SearchMode mode) {
        Objects.requireNonNull(criteria, "criteria must not be null");
        Objects.requireNonNull(mode, "mode must not be null");

        SearchStrategy strategy = strategyFactory.create(mode);
        try {
            return strategy.search(criteria);
        } catch (RuntimeException ex) {
            throw new SearchException("Search failed for criteria: " + criteria, ex);
        }
    }

    /**
     * Convenience method: chooses mode automatically based on criteria.
     */
    public List<SearchResult> smartSearch(SearchCriteria criteria) {
        Objects.requireNonNull(criteria, "criteria must not be null");

        SearchMode mode = criteria.hasKeyword()
                ? SearchMode.HYBRID
                : SearchMode.METADATA;

        return search(criteria, mode);
    }
}
