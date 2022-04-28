package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Instantiates a BookPublishRequestManager to manage the queue of publish requests.
 */
@Singleton
public class BookPublishRequestManager {
    private final Queue<BookPublishRequest> bookPublishRequestQueue;

    /**
     *Instantiates a BookPublishRequestManager to manage the queue of publish requests.
     *
     * @param queue A thread safe queue
     */
    @Inject
    public BookPublishRequestManager(ConcurrentLinkedQueue queue) {
        this.bookPublishRequestQueue = new ConcurrentLinkedQueue<>(queue);
    }

    /**
     * Adds a BookPublishRequest to the concurrent linked queue.
     *
     * @param bookPublishRequest
     */
    public void addBookPublishRequest(final BookPublishRequest bookPublishRequest) {
        bookPublishRequestQueue.add(bookPublishRequest);
    }

    /**
     * Gets a book and removes the head of this queue.
     *
     * @return BookPublishRequest or null if this queue is empty.
     */
    public BookPublishRequest getBookPublishRequestToProcess() {
           return bookPublishRequestQueue.poll();
    }

    public Queue<BookPublishRequest> getBookPublishRequestQueue() {
        return bookPublishRequestQueue;
    }
}
