package com.docweaver.export.api;

import com.docweaver.core.document.StructuredDocument;

/**
 * Abstraction for exporting a StructuredDocument to some target.
 */
public interface IStructuredDataExporter {

    /**
     * Export the given structured document to the provided target.
     *
     * @param document the document to export (never null)
     * @param target   where the exported representation is written (never null)
     * @throws ExportException if export fails for any reason
     */
    void export(StructuredDocument document, ExportTarget target) throws ExportException;
}
