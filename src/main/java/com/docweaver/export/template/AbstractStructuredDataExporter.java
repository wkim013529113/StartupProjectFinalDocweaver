package com.docweaver.export.template;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.export.api.ExportException;
import com.docweaver.export.api.ExportTarget;
import com.docweaver.export.api.IStructuredDataExporter;

import java.util.Objects;

/**
 * Template Method base class that defines the overall export algorithm.
 *
 * Subclasses override the protected hook methods to implement format-specific behavior.
 */
public abstract class AbstractStructuredDataExporter implements IStructuredDataExporter {

    @Override
    public final void export(StructuredDocument document, ExportTarget target) throws ExportException {
        Objects.requireNonNull(document, "document must not be null");
        Objects.requireNonNull(target, "target must not be null");

        try {
            openTarget(target);
            writeHeader(document, target);
            writeBody(document, target);
            writeFooter(document, target);
        } catch (Exception e) {
            throw new ExportException("Failed to export document", e);
        } finally {
            try {
                closeTarget(target);
            } catch (Exception e) {
                // optionally log, but don't mask main error
            }
        }
    }

    /**
     * Open or initialize the target if necessary (e.g. create workbook, writer, etc.).
     * Default implementation does nothing.
     */
    protected void openTarget(ExportTarget target) throws Exception {
        // no-op by default
    }

    /**
     * Write header information (e.g., column names).
     * Default implementation does nothing.
     */
    protected void writeHeader(StructuredDocument document, ExportTarget target) throws Exception {
        // no-op by default
    }

    /**
     * Write the main body of the export. Subclasses MUST implement this.
     */
    protected abstract void writeBody(StructuredDocument document, ExportTarget target) throws Exception;

    /**
     * Write footer/trailer information.
     * Default implementation does nothing.
     */
    protected void writeFooter(StructuredDocument document, ExportTarget target) throws Exception {
        // no-op by default
    }

    /**
     * Close or flush the target if needed.
     * Default implementation does nothing.
     */
    protected void closeTarget(ExportTarget target) throws Exception {
        // no-op by default
    }
}
