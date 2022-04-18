package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.exceptions.KindlePublishingClientException;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {
    private CatalogDao catalogDao;

    /**
     * Instantiates a new RemoveBookFromCatalogActivity object.
     *
     * @param catalogDao CatalogDao to access the Catalog table.
     */
    @Inject
    RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    /**
     * "Soft Delete" the book associated with provided book id by
     * updating the inactive attribute to true.
     *
     * @param removeBookFromCatalogRequest Request object containing the book ID associated with the book to get
     *                                     from the Catalog.
     * @return RemoveBookFromCatalogResponse empty Response object.
     */
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        String bookId = removeBookFromCatalogRequest.getBookId();
//        try {
            CatalogItemVersion book = catalogDao.removeBookFromCatalog(bookId);
//        } catch (BookNotFoundException e) {
//            throw new KindlePublishingClientException(e.getMessage());
//        }

        return new RemoveBookFromCatalogResponse();
    }
}
