package com.docweaver.search;

import java.util.List;

/**
 * Strategy interface for different search algorithms.
 */
public interface SearchStrategy {

    /**
     * Executes search using the provided criteria.
     *
     * @param criteria search filters and keyword
     * @return ordered list of results (best match first)
     */
    List<SearchResult> search(SearchCriteria criteria);
}
