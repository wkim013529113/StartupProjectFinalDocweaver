package com.docweaver.export.app;

import com.docweaver.export.api.ExportException;
import com.docweaver.export.api.ExportFormat;
import com.docweaver.export.api.ExportTarget;
import com.docweaver.export.api.IStructuredDataExporter;
import com.docweaver.export.impl.CsvDataExporter;
import com.docweaver.export.impl.ExcelDataExporter;
import com.docweaver.export.impl.JsonDataExporter;
import com.docweaver.domain.StructuredDocument;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Facade for export usage inside the application.
 * Chooses the correct exporter based on ExportFormat.
 */
public class ExportService {

    private final Map<ExportFormat, IStructuredDataExporter> exporters =
            new EnumMap<>(ExportFormat.class);

    public ExportService() {
        // Default registrations; you can inject instead if using DI.
        exporters.put(ExportFormat.JSON, new JsonDataExporter());
        exporters.put(ExportFormat.CSV, new CsvDataExporter());
        exporters.put(ExportFormat.EXCEL, new ExcelDataExporter());
    }

    public ExportService(Map<ExportFormat, IStructuredDataExporter> customExporters) {
        Objects.requireNonNull(customExporters, "customExporters must not be null");
        exporters.putAll(customExporters);
    }

    public void export(StructuredDocument document, ExportTarget target) throws ExportException {
        Objects.requireNonNull(document, "document must not be null");
        Objects.requireNonNull(target, "target must not be null");

        IStructuredDataExporter exporter = exporters.get(target.getFormat());
        if (exporter == null) {
            throw new ExportException("No exporter registered for format: " + target.getFormat());
        }
        exporter.export(document, target);
    }
}
