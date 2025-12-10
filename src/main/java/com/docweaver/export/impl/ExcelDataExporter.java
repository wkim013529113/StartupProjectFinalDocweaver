package com.docweaver.export.impl;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.export.api.ExportTarget;
import com.docweaver.export.template.AbstractStructuredDataExporter;

/**
 * Placeholder for an Excel exporter.
 * In a real implementation, use Apache POI or similar library.
 */
public class ExcelDataExporter extends AbstractStructuredDataExporter {

    @Override
    protected void writeBody(StructuredDocument document, ExportTarget target) throws Exception {
        // TODO: Implement using Apache POI (HSSF/XSSF) or other Excel library.
        // For now, just signal it's not implemented.
        throw new UnsupportedOperationException("Excel export not yet implemented");
    }
}
