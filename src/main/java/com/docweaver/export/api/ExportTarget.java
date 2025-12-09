package com.docweaver.export.api;

import java.io.OutputStream;
import java.util.Objects;

/**
 * Describes where the exported data should be written.
 */
public final class ExportTarget {

    private final OutputStream outputStream;
    private final String fileName;
    private final ExportFormat format;

    public ExportTarget(OutputStream outputStream,
                        String fileName,
                        ExportFormat format) {
        this.outputStream = Objects.requireNonNull(outputStream, "outputStream must not be null");
        this.fileName = fileName;
        this.format = Objects.requireNonNull(format, "format must not be null");
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public ExportFormat getFormat() {
        return format;
    }
}
