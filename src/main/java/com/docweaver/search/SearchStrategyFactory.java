package com.docweaver.search;

import java.util.List;
import java.util.Objects;

/**
 * Factory that builds SearchStrategy instances based on SearchMode.
 */
public class SearchStrategyFactory {

    private final DocumentSearchGateway searchGateway;

    public SearchStrategyFactory(DocumentSearchGateway searchGateway) {
        this.searchGateway = Objects.requireNonNull(searchGateway);
    }

    public SearchStrategy create(SearchMode mode) {
        Objects.requireNonNull(mode, "mode must not be null");

        switch (mode) {
            case KEYWORD:
                return new KeywordSearchStrategy(searchGateway);
            case METADATA:
                return new MetadataSearchStrategy(searchGateway);
            case HYBRID:
                return new CompositeSearchStrategy(
                        List.of(
                                new KeywordSearchStrategy(searchGateway),
                                new MetadataSearchStrategy(searchGateway)
                        )
                );
            default:
                throw new IllegalArgumentException("Unsupported search mode: " + mode);
        }
    }
}
