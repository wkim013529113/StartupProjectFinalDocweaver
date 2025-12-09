package com.docweaver.export.impl;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.export.api.ExportTarget;
import com.docweaver.export.template.AbstractStructuredDataExporter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Exports a StructuredDocument as CSV.
 * Uses field names as headers, assuming a tabular document.
 */
public class CsvDataExporter extends AbstractStructuredDataExporter {

    private Writer writer;
    private String[] header;

    @Override
    protected void openTarget(ExportTarget target) throws Exception {
        OutputStream os = target.getOutputStream();
        this.writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
    }

    @Override
    protected void writeHeader(StructuredDocument document, ExportTarget target) throws Exception {
        Set<String> fieldNames = deriveFieldNames(document);
        this.header = fieldNames.toArray(new String[0]);

        writeCsvRow((Object[]) header);
        writer.write("\n");
        writer.flush();
    }

    @Override
    protected void writeBody(StructuredDocument document, ExportTarget target) throws Exception {
        if (header == null) {
            // Fallback if header was not generated
            Set<String> fieldNames = deriveFieldNames(document);
            header = fieldNames.toArray(new String[0]);
        }

        if (document.isTabular()) {
            for (Map<String, Object> record : document.getRecords()) {
                Object[] values = new Object[header.length];
                for (int i = 0; i < header.length; i++) {
                    values[i] = record.get(header[i]);
                }
                writeCsvRow(values);
                writer.write("\n");
            }
        } else {
            Object[] values = new Object[header.length];
            Map<String, Object> fields = document.getFields();
            for (int i = 0; i < header.length; i++) {
                values[i] = fields.get(header[i]);
            }
            writeCsvRow(values);
            writer.write("\n");
        }
        writer.flush();
    }

    @Override
    protected void closeTarget(ExportTarget target) throws Exception {
        if (writer != null) {
            writer.close();
        }
    }

    private Set<String> deriveFieldNames(StructuredDocument document) {
        Set<String> fieldNames = new LinkedHashSet<>();
        if (document.isTabular() && !document.getRecords().isEmpty()) {
            fieldNames.addAll(document.getRecords().get(0).keySet());
        } else {
            fieldNames.addAll(document.getFields().keySet());
        }
        return fieldNames;
    }

    private void writeCsvRow(Object[] values) throws Exception {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.write(",");
            }
            String text = values[i] == null ? "" : values[i].toString();
            writer.write(escapeCsv(text));
        }
    }

    private String escapeCsv(String value) {
        boolean mustQuote = value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        if (mustQuote) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
