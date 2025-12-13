package com.docweaver.demo;

import com.docweaver.command.CommandDispatcher;
import com.docweaver.command.UploadDocumentCommand;
import com.docweaver.domain.*;
import com.docweaver.export.api.ExportException;
import com.docweaver.export.api.ExportFormat;
import com.docweaver.export.api.ExportTarget;
import com.docweaver.export.app.ExportService;
import com.docweaver.external.api.ExternalPushResult;
import com.docweaver.external.api.ExternalSystemException;
import com.docweaver.external.api.ExternalSystemType;
import com.docweaver.external.app.ExternalSystemGateway;
import com.docweaver.search.DocumentSearchGateway;
import com.docweaver.search.DocumentSearchService;
import com.docweaver.search.SearchCriteria;
import com.docweaver.search.SearchResult;
import com.docweaver.search.SearchStrategyFactory;
import com.docweaver.service.DocumentRecordRepository;
import com.docweaver.service.DocumentRepository;
import com.docweaver.service.ExtractionJobProcessor;
import com.docweaver.service.ProcessingJobQueue;
import com.docweaver.service.RecordApprovalService;
import com.docweaver.strategy.ExtractionEngine;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Full-story demo that ties together:
 *  - Upload (Command + Repository)
 *  - Extraction (Strategy + Job Processor)
 *  - Approval lifecycle (State + Service)
 *  - Export Structured Data (Strategy)
 *  - Map to External System (Adapter + Gateway)
 *  - Search Document (Strategy + Template / Facade)
 *  Full-story demo for DocWeaver with pause-and-go
 */
public class DocWeaverStoryDemo {

    public static void main(String[] args) throws Exception {
        new DocWeaverStoryDemo().run();
    }

    public void run() throws Exception {
        System.out.println("=== DOCWEAVER FULL STORY DEMO ===");

        // --------------------------------------------------------------------
        // INITIALIZE SYSTEM
        // --------------------------------------------------------------------
        DocumentRepository documentRepository = new DocumentRepository();
        DocumentRecordRepository recordRepository = new DocumentRecordRepository();
        ProcessingJobQueue jobQueue = new ProcessingJobQueue();

        ExtractionEngine extractionEngine = new ExtractionEngine();
        ExtractionJobProcessor extractionJobProcessor = new ExtractionJobProcessor(
                documentRepository,
                recordRepository,
                extractionEngine
        );

        RecordApprovalService approvalService = new RecordApprovalService(
                documentRepository,
                recordRepository
        );

        ExportService exportService = new ExportService();
        ExternalSystemGateway externalGateway = new ExternalSystemGateway();
        DocumentSearchService searchService = createSearchService(documentRepository);

        // --------------------------------------------------------------------
        // STEP 1 — Upload Document (Command Pattern)
        // --------------------------------------------------------------------
        Workspace workspace = new Workspace("ws-demo", "Demo Workspace");
        CommandDispatcher dispatcher = new CommandDispatcher();

        UploadDocumentCommand uploadCommand = new UploadDocumentCommand(
                workspace,
                List.of("invoice-demo.pdf"),
                documentRepository,
                jobQueue
        );

        dispatcher.submit(uploadCommand);
        dispatcher.runAll();

        Document uploadedDoc = documentRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No document uploaded"));

        System.out.println("\n[Step 1] Upload completed");
        System.out.println("  Document ID: " + uploadedDoc.getId());
        System.out.println("  Workspace:   " + uploadedDoc.getWorkspaceId());
        System.out.println("  Filename:    " + uploadedDoc.getFilename());
        System.out.println("  Status:      " + uploadedDoc.getStatus());
        waitForEnter();

        uploadedDoc.setDocumentType(DocumentType.INVOICE);
        documentRepository.save(uploadedDoc);

        // --------------------------------------------------------------------
        // STEP 2 — Extraction (Strategy + Queue + Processor)
        // --------------------------------------------------------------------
        System.out.println("\n[Step 2] Processing extraction jobs...");
        ProcessingJob job;
        while ((job = jobQueue.dequeue()) != null) {
            extractionJobProcessor.process(job);
        }

        Document extractedDoc = documentRepository.findById(uploadedDoc.getId());
        DocumentRecord record = recordRepository.findByDocumentId(uploadedDoc.getId());

        System.out.println("  Document status: " + extractedDoc.getStatus());
        System.out.println("  Record ID:       " + record.getId());
        System.out.println("  Fields extracted:");
        for (Field f : record.getFields()) {
            System.out.println("    - " + f.getKey() + " = " + f.getValue());
        }
        waitForEnter();

        // --------------------------------------------------------------------
        // STEP 3 — Approval Lifecycle (State Pattern)
        // --------------------------------------------------------------------
        String recordId = record.getId();

        System.out.println("\n[Step 3] Approval lifecycle...");
        approvalService.startReview(recordId);

        record = recordRepository.findById(recordId);
        System.out.println("  After startReview → Record status: " + record.getApprovalStatus());

        approvalService.editField(recordId, "total_amount", "1500.00");
        record = recordRepository.findById(recordId);
        System.out.println("  Edited total_amount → 1500.00");

        approvalService.approveRecord(recordId);
        record = recordRepository.findById(recordId);
        System.out.println("  After approveRecord → Record status: " + record.getApprovalStatus());
        waitForEnter();

        // --------------------------------------------------------------------
        // STEP 4 — External Mapping + Export Structured Data
        // --------------------------------------------------------------------
        System.out.println("\n[Step 4] Mapping + Export");
        StructuredDocument structured = toStructuredDocument(record);

        try {
            ExternalPushResult pushResult = externalGateway.pushTo(ExternalSystemType.CRM, structured);
            System.out.println("  External system push success: " + pushResult);
        } catch (ExternalSystemException e) {
            System.out.println("  External push error: " + e.getMessage());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExportTarget target = new ExportTarget(out, "invoice.json", ExportFormat.JSON);

        try {
            exportService.export(structured, target);
            System.out.println("  Exported JSON:");
            System.out.println(out.toString(StandardCharsets.UTF_8));
        } catch (ExportException e) {
            System.out.println("  Export error: " + e.getMessage());
        }

        approvalService.markExported(recordId);
        record = recordRepository.findById(recordId);
        System.out.println("  After markExported → " + record.getApprovalStatus());
        waitForEnter();

        // --------------------------------------------------------------------
        // STEP 5 — Search Module
        // --------------------------------------------------------------------
        System.out.println("\n[Step 5] Searching for keyword: 'invoice'");

        SearchCriteria criteria = SearchCriteria.builder()
                .keyword("invoice")
                .build();

        List<SearchResult> results = searchService.smartSearch(criteria);

        System.out.println("  Search results: " + results.size());
        for (SearchResult r : results) {
            System.out.println("    - DocId = " + r.getDocument().getId()
                    + " | score=" + r.getScore()
                    + " | snippet=" + r.getSnippet());
        }
        waitForEnter();

        System.out.println("\n=== DEMO COMPLETE ===");
    }

    // Mapping DocumentRecord → StructuredDocument
    private static StructuredDocument toStructuredDocument(DocumentRecord record) {
        StructuredDocument doc = new StructuredDocument();
        for (Field f : record.getFields()) {
            doc.putField(f.getKey(), f.getValue());
        }
        return doc;
    }

    // Search service adapter
    private static DocumentSearchService createSearchService(DocumentRepository repo) {
        DocumentSearchGateway gateway = criteria ->
                new ArrayList<>(repo.findAll());

        return new DocumentSearchService(new SearchStrategyFactory(gateway));
    }

    // ---------------------------------------------------------
    // PAUSE METHOD (Option 1: Press Enter to continue)
    // ---------------------------------------------------------
    private void waitForEnter() {
        System.out.println("\nPress ENTER to continue...");
        try {
            System.in.read();
        } catch (Exception ignored) {}
    }
}
