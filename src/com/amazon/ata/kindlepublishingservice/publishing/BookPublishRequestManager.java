package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;

//@Singleton
public class BookPublishRequestManager {
    private Queue<BookPublishRequest> bookPublishRequestQueue;

    @Inject
//    do I need any dependencies
    public BookPublishRequestManager() {
        this.bookPublishRequestQueue = new LinkedList<>();
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        bookPublishRequestQueue.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        if (!bookPublishRequestQueue.isEmpty()) {
            return bookPublishRequestQueue.remove();
        }
        return null;
    }

    public Queue<BookPublishRequest> getBookPublishRequestQueue() {
        return bookPublishRequestQueue;
    }
}
