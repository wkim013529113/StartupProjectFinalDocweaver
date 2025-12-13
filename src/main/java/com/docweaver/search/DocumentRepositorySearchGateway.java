package com.docweaver.search;

import com.docweaver.domain.Document;
import com.docweaver.service.DocumentRepository;

import java.util.List;

/**
 * Adapter that connects Search module to the existing DocumentRepository.
 * You must implement this to call the real repository methods.
 */
public class DocumentRepositorySearchGateway implements DocumentSearchGateway {

    private final DocumentRepository documentRepository;

    public DocumentRepositorySearchGateway(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<Document> findCandidates(SearchCriteria criteria) {
        // TODO: IMPLEMENT using your existing DocumentRepository API.
        // Examples (you choose based on what methods you have):
        //
        // 1) If you have some filtered methods:
        // return documentRepository.findByTypeAndDateAndTags(
        //         criteria.getDocumentType(),
        //         criteria.getCreatedFrom(),
        //         criteria.getCreatedTo(),
        //         criteria.getTags()
        // );
        //
        // 2) If you only have findAll(), you can filter in memory:
        // return documentRepository.findAll().stream()
        //         .filter(doc -> /* apply criteria */)
        //         .toList();
        //
        throw new UnsupportedOperationException("Implement findCandidates based on DocumentRepository");
    }
}
