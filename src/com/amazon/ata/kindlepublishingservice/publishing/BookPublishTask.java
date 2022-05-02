package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.converters.KindleFormatConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * A runnable class for asynchronous processing of book publish tasks.
 */
public class BookPublishTask implements Runnable{
   private static final Logger LOGGER = LogManager.getLogger(BookPublishTask.class);
   private BookPublishRequestManager bookPublishRequestManager;
   private PublishingStatusDao publishingStatusDao;
   private CatalogDao catalogDao;

   @Inject
    public BookPublishTask(BookPublishRequestManager bookPublishRequestManager, PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    @Override
    /**
     * Adds an entry to the Publishing Status table with state IN_PROGRESS.
     * Performs formatting and conversion of the book.
     * Adds an item to the Publishing Status table with state SUCCESSFUL if all the processing steps succeed.
     * If an exception is caught while processing, adds an item into the Publishing Status table with state FAILED
     * and includes the exception message.
     */
    public void run() {
        LOGGER.info("BookPublishTask Task executed.");
        //get publish request from queue
        BookPublishRequest bookPublishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();
        LOGGER.info("REQUEST");

        if (bookPublishRequest == null) {
        LOGGER.info("Request Null");
            return;
        }
        LOGGER.info("REQUEST not Null");

        String publishingRecordId = bookPublishRequest.getPublishingRecordId();
        String requestedBookId = bookPublishRequest.getBookId();

        publishingStatusDao.setPublishingStatus(publishingRecordId, PublishingRecordStatus.IN_PROGRESS, requestedBookId);
        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(bookPublishRequest);

        CatalogItemVersion catalogItemVersion;
        try {
            //create or update book
           catalogItemVersion = catalogDao.createOrUpdateBook(kindleFormattedBook);
           if (catalogItemVersion == null) {
               throw new BookNotFoundException("Null");
           }
        } catch (BookNotFoundException e) {
            // Validation failed for book ID
            publishingStatusDao.setPublishingStatus(publishingRecordId,
                    PublishingRecordStatus.FAILED, requestedBookId,
                    e.getMessage());
            return;
        }
            publishingStatusDao.setPublishingStatus(publishingRecordId,
                PublishingRecordStatus.SUCCESSFUL, catalogItemVersion.getBookId());
    }
}
