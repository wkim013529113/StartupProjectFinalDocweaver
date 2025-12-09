package com.docweaver.search;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Immutable value object representing the user's search request.
 */
public final class SearchCriteria {

    private final String keyword;
    private final String documentType;
    private final LocalDate createdFrom;
    private final LocalDate createdTo;
    private final List<String> tags;

    private SearchCriteria(Builder builder) {
        this.keyword = builder.keyword;
        this.documentType = builder.documentType;
        this.createdFrom = builder.createdFrom;
        this.createdTo = builder.createdTo;
        this.tags = builder.tags == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(builder.tags);
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDocumentType() {
        return documentType;
    }

    public LocalDate getCreatedFrom() {
        return createdFrom;
    }

    public LocalDate getCreatedTo() {
        return createdTo;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }

    public boolean hasDocumentType() {
        return documentType != null && !documentType.isBlank();
    }

    public boolean hasDateRange() {
        return createdFrom != null || createdTo != null;
    }

    public boolean hasTags() {
        return !tags.isEmpty();
    }

    @Override
    public String toString() {
        return "SearchCriteria{" +
                "keyword='" + keyword + '\'' +
                ", documentType='" + documentType + '\'' +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", tags=" + tags +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for SearchCriteria to keep call sites clean and readable.
     */
    public static final class Builder {
        private String keyword;
        private String documentType;
        private LocalDate createdFrom;
        private LocalDate createdTo;
        private List<String> tags;

        private Builder() {
        }

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder documentType(String documentType) {
            this.documentType = documentType;
            return this;
        }

        public Builder createdFrom(LocalDate createdFrom) {
            this.createdFrom = createdFrom;
            return this;
        }

        public Builder createdTo(LocalDate createdTo) {
            this.createdTo = createdTo;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public SearchCriteria build() {
            if (createdFrom != null && createdTo != null && createdFrom.isAfter(createdTo)) {
                throw new IllegalArgumentException("createdFrom cannot be after createdTo");
            }
            return new SearchCriteria(this);
        }
    }
}
