package com.docweaver.search;

import com.docweaver.domain.Document;

/**
 * Represents a single search hit with document, score, and snippet.
 */
public final class SearchResult implements Comparable<SearchResult> {

    private final Document document;
    private final double score;
    private final String snippet;

    public SearchResult(Document document, double score, String snippet) {
        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }
        this.document = document;
        this.score = score;
        this.snippet = snippet;
    }

    public Document getDocument() {
        return document;
    }

    /**
     * Higher score means more relevant.
     */
    public double getScore() {
        return score;
    }

    /**
     * Short excerpt showing why this document matched.
     */
    public String getSnippet() {
        return snippet;
    }

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score); // sort desc
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "document=" + document +
                ", score=" + score +
                ", snippet='" + snippet + '\'' +
                '}';
    }
}
