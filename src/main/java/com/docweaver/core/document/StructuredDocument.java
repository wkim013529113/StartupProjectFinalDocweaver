package com.docweaver.core.document;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal stub for a structured document.
 * Replace with your real domain model in DocWeaver.
 */
public class StructuredDocument {

    /** Ordered field names -> values for a single-record document */
    private final Map<String, Object> fields = new LinkedHashMap<>();

    /** Multi-record tabular representation (e.g., rows of data) */
    private final List<Map<String, Object>> records;

    public StructuredDocument() {
        this.records = Collections.emptyList();
    }

    public StructuredDocument(List<Map<String, Object>> records) {
        this.records = records == null ? Collections.emptyList() : records;
    }

    public Map<String, Object> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public void putField(String name, Object value) {
        fields.put(name, value);
    }

    public List<Map<String, Object>> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public boolean isTabular() {
        return !records.isEmpty();
    }
}
