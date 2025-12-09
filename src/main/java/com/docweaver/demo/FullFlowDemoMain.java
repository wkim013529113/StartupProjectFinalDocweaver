// src/main/java/com/docweaver/demo/FullFlowDemoMain.java
package com.docweaver.demo;

import com.docweaver.command.CommandDispatcher;
import com.docweaver.command.UploadDocumentCommand;
import com.docweaver.domain.*;
import com.docweaver.service.*;
import com.docweaver.strategy.ExtractionEngine;

import java.util.Collections;

public class FullFlowDemoMain {

    public static void main(String[] args) {
        // ---------- Setup shared infrastructure ----------
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

        CommandDispatcher dispatcher = new CommandDispatcher();

        Workspace workspace = new Workspace("ws-1", "Demo Workspace");

        System.out.println("=== DOCWEAVER FULL DEMO ===");
        System.out.println("Workspace: " + workspace.getName());
        System.out.println();

        // ---------- 1) Upload Document (Command Pattern) ----------
        System.out.println("[Step 1] Upload Document (Command)");
        UploadDocumentCommand uploadCmd = new UploadDocumentCommand(
                workspace,
                Collections.singletonList("invoice1.pdf"),
                documentRepository,
                jobQueue
        );
        dispatcher.submit(uploadCmd);
        dispatcher.runAll();

        System.out.println("Uploaded documents:");
        Document uploadedDoc = documentRepository.findAll().iterator().next();
        System.out.printf("  id=%s, file=%s, status=%s%n",
                uploadedDoc.getId(),
                uploadedDoc.getFilename(),
                uploadedDoc.getStatus());

        System.out.println("Jobs in processing queue: " + jobQueue.size());
        System.out.println();

        // Simulate classification result (in real system, a classifier would set this)
        uploadedDoc.setDocumentType(DocumentType.INVOICE);
        documentRepository.save(uploadedDoc);

        // ---------- 2) Extract Key Fields (Strategy Pattern) ----------
        System.out.println("[Step 2] Extract Key Fields (Strategy)");

        while (jobQueue.size() > 0) {
            ProcessingJob job = jobQueue.dequeue();
            extractionJobProcessor.process(job);
        }

        Document extractedDoc = documentRepository.findById(uploadedDoc.getId());
        DocumentRecord record = recordRepository.findById(extractedDoc.getRecordId());

        System.out.println("After extraction:");
        System.out.println("  Document status: " + extractedDoc.getStatus());
        System.out.println("  Record id: " + record.getId());
        System.out.println("  Record docType: " + record.getDocumentType());

        System.out.println("Extracted fields:");
        record.getFields().forEach(f ->
                System.out.printf("  %s = %s (conf=%.2f)%n",
                        f.getKey(), f.getValue(), f.getConfidence())
        );
        System.out.println();

        // ---------- 3) Approve Export (State Pattern) ----------
        System.out.println("[Step 3] Approve & Export (State)");

        // Start review
        approvalService.startReview(record.getId());
        record = recordRepository.findById(record.getId());
        extractedDoc = documentRepository.findById(extractedDoc.getId());
        System.out.println("After startReview:");
        System.out.println("  Record approvalStatus: " + record.getApprovalStatus());
        System.out.println("  Document status: " + extractedDoc.getStatus());

        // Edit field during review
        System.out.println("Editing total_amount during review...");
        approvalService.editField(record.getId(), "total_amount", "1300.00");
        record = recordRepository.findById(record.getId());
        String total = record.getFields().stream()
                .filter(f -> f.getKey().equals("total_amount"))
                .findFirst()
                .map(Field::getValue)
                .orElse("<missing>");
        System.out.println("  Updated total_amount: " + total);

        // Approve -> Ready for Export
        approvalService.approveRecord(record.getId());
        record = recordRepository.findById(record.getId());
        extractedDoc = documentRepository.findById(extractedDoc.getId());
        System.out.println("After approveRecord:");
        System.out.println("  Record approvalStatus: " + record.getApprovalStatus());
        System.out.println("  Document status: " + extractedDoc.getStatus());

        // Mark exported
        approvalService.markExported(record.getId());
        record = recordRepository.findById(record.getId());
        extractedDoc = documentRepository.findById(extractedDoc.getId());
        System.out.println("After markExported:");
        System.out.println("  Record approvalStatus: " + record.getApprovalStatus());
        System.out.println("  Document status: " + extractedDoc.getStatus());

        System.out.println();
        System.out.println("=== DEMO COMPLETE ===");
    }
}
