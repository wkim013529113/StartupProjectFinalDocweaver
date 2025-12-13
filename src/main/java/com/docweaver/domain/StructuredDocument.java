package com.docweaver.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Structured representation of an extracted document.
 * Acts as the canonical payload for export and external-system mapping.
 */
public class StructuredDocument {

    /** Ordered field names -> values for a single-record document */
    private final Map<String, Object> fields = new LinkedHashMap<>();

    /** Optional tabular records (e.g., invoice line items) */
    private final List<Map<String, Object>> records;

    /**
     * Default: no tabular records, just top-level fields.
     */
    public StructuredDocument() {
        this.records = new ArrayList<>();
    }

    /**
     * Document with already-known tabular records.
     */
    public StructuredDocument(List<Map<String, Object>> records) {
        this.records = new ArrayList<>(records);
    }

    /**
     * Immutable view of scalar fields.
     */
    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    /**
     * Add or overwrite a scalar field.
     */
    public void putField(String name, Object value) {
        fields.put(name, value);
    }

    /**
     * Immutable view of tabular records.
     */
    public List<Map<String, Object>> getRecords() {
        return Collections.unmodifiableList(records);
    }

    /**
     * Convenience: does this document have tabular data?
     */
    public boolean isTabular() {
        return !records.isEmpty();
    }
}
