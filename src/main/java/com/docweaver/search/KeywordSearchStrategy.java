package com.docweaver.search;

import com.docweaver.domain.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Strategy that searches by keyword using Document.toString() as the searchable text.
 */
public class KeywordSearchStrategy implements SearchStrategy {

    private final DocumentSearchGateway searchGateway;

    public KeywordSearchStrategy(DocumentSearchGateway searchGateway) {
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
        if (!criteria.hasKeyword()) {
            return Collections.emptyList();
        }

        String keyword = criteria.getKeyword().toLowerCase(Locale.ROOT);

        // Get candidate docs (implementation decides how to filter)
        List<Document> candidates = searchGateway.findCandidates(criteria);

        List<SearchResult> results = new ArrayList<>();

        for (Document doc : candidates) {
            double score = computeScore(doc, keyword);
            if (score > 0) {
                String snippet = buildSnippet(doc, keyword);
                results.add(new SearchResult(doc, score, snippet));
            }
        }

        Collections.sort(results); // by score desc
        return results;
    }

    private double computeScore(Document doc, String keyword) {
        // Use the full string representation of the document as searchable text
        String fullText = safeLower(doc.toString());

        int hitCount = 0;
        int index = fullText.indexOf(keyword);
        while (index >= 0) {
            hitCount++;
            index = fullText.indexOf(keyword, index + keyword.length());
        }

        return hitCount;
    }

    private String buildSnippet(Document doc, String keyword) {
        String fullTextLower = safeLower(doc.toString());
        int idx = fullTextLower.indexOf(keyword);
        if (idx < 0) {
            // No match found (shouldn't happen if score > 0, but be safe)
            return "";
        }

        String fullText = doc.toString();
        int start = Math.max(0, idx - 30);
        int end = Math.min(fullText.length(), idx + keyword.length() + 30);
        String snippet = fullText.substring(start, end);

        return snippet.replace("\n", " ").trim();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
