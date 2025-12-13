package com.docweaver.search;

import com.docweaver.domain.Document;

import java.util.*;

/**
 * Combines multiple strategies into a single search and merges results.
 */
public class CompositeSearchStrategy implements SearchStrategy {

    private final List<SearchStrategy> strategies;

    public CompositeSearchStrategy(List<SearchStrategy> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException("strategies must not be null or empty");
        }
        this.strategies = List.copyOf(strategies);
    }

    @Override
    public List<SearchResult> search(SearchCriteria criteria) {
        Map<String, SearchResult> merged = new HashMap<>();

        for (SearchStrategy strategy : strategies) {
            List<SearchResult> strategyResults = strategy.search(criteria);
            for (SearchResult result : strategyResults) {
                Document doc = result.getDocument();
                String key = getDocumentKey(doc); // adjust to your ID API

                merged.merge(key, result, CompositeSearchStrategy::mergeResult);
            }
        }

        List<SearchResult> finalResults = new ArrayList<>(merged.values());
        Collections.sort(finalResults);
        return finalResults;
    }

    /**
     * Adjust this method to match your Document identifier method.
     */
    private String getDocumentKey(Document doc) {
        return doc.getId(); // CHANGE HERE if your Document uses a different method
    }

    private static SearchResult mergeResult(SearchResult existing, SearchResult incoming) {
        double combinedScore = existing.getScore() + incoming.getScore();
        String snippet = incoming.getSnippet() != null && !incoming.getSnippet().isBlank()
                ? incoming.getSnippet()
                : existing.getSnippet();

        return new SearchResult(existing.getDocument(), combinedScore, snippet);
    }
}
