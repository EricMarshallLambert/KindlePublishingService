package com.amazon.ata.kindlepublishingservice.publishing;


import com.amazon.ata.kindlepublishingservice.dagger.BookPublishRequestManager;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookPublishRequestManagerTest {

    private BookPublishRequestManager publishRequestManager = new BookPublishRequestManager(new ConcurrentLinkedQueue());

    @Test
    public void addBookPublishRequest_withBookPublishRequest_addedToQueue() {
        //GIVEN
        BookPublishRequest bookPublishRequest = bookPublishRequestHelper("abc");
        //WHEN
            publishRequestManager.addBookPublishRequest(bookPublishRequest);
        //THEN
            assertEquals(1, publishRequestManager.getBookPublishRequestQueue().size());
    }

    @Test
    void getBookPublishRequestToProcess_withBookPublishRequest_removedFromQueue() {
        //GIVEN
        BookPublishRequest bookPublishRequest = bookPublishRequestHelper("abc");
        publishRequestManager.addBookPublishRequest(bookPublishRequest);
        //WHEN
        BookPublishRequest result = publishRequestManager.getBookPublishRequestToProcess();
        //THEN
        assertEquals(0, publishRequestManager.getBookPublishRequestQueue().size());
        assertEquals(bookPublishRequest, result);
    }

    private BookPublishRequest bookPublishRequestHelper(String publishingRecordId) {
        BookPublishRequest bookPublishRequest =  BookPublishRequest.builder()
                .withAuthor("Eric")
                .withBookId("123")
                .withGenre(BookGenre.HORROR)
                .withPublishingRecordId(publishingRecordId)
                .withText("This is some Text")
                .withTitle("Autobiography of Eric")
                .build();
        return bookPublishRequest;
    }
}