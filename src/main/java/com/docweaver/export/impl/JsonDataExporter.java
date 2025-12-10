package com.docweaver.export.impl;

import com.docweaver.core.document.StructuredDocument;
import com.docweaver.export.api.ExportTarget;
import com.docweaver.export.template.AbstractStructuredDataExporter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Exports a StructuredDocument as a very simple JSON structure.
 * This is intentionally minimal; replace with Jackson/Gson in a real system.
 */
public class JsonDataExporter extends AbstractStructuredDataExporter {

    private Writer writer;

    @Override
    protected void openTarget(ExportTarget target) throws Exception {
        OutputStream os = target.getOutputStream();
        this.writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
    }

    @Override
    protected void writeBody(StructuredDocument document, ExportTarget target) throws Exception {
        if (document.isTabular()) {
            writer.write("[\n");
            for (int i = 0; i < document.getRecords().size(); i++) {
                Map<String, Object> record = document.getRecords().get(i);
                writeJsonObject(record);
                if (i < document.getRecords().size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write("]");
        } else {
            writeJsonObject(document.getFields());
        }
        writer.flush();
    }

    @Override
    protected void closeTarget(ExportTarget target) throws Exception {
        if (writer != null) {
            writer.close();
        }
    }

    private void writeJsonObject(Map<String, Object> fields) throws Exception {
        writer.write("{");
        Iterator<Map.Entry<String, Object>> it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            writer.write("\"");
            writer.write(escapeJson(entry.getKey()));
            writer.write("\": ");
            writeJsonValue(entry.getValue());
            if (it.hasNext()) {
                writer.write(", ");
            }
        }
        writer.write("}");
    }

    private void writeJsonValue(Object value) throws Exception {
        if (value == null) {
            writer.write("null");
        } else if (value instanceof Number || value instanceof Boolean) {
            writer.write(value.toString());
        } else {
            writer.write("\"");
            writer.write(escapeJson(value.toString()));
            writer.write("\"");
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
