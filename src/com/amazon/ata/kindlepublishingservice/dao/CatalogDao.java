package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     * Removes a book from the catalog with a given book id.
     * This deactivates the latest version of the book in the
     * CatalogItemVersions table by changing its inactive attribute to true.
     *
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getBookFromCatalog(bookId);
        book.setInactive(true);
        dynamoDbMapper.save(book);
        return book;
    }

    public void validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null){
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
    }

    /**
     * Adds the new book to the CatalogItemVersion table.
     * If this request is updating an existing book:
     * The entry in CatalogItemVersion will use the same bookId but with the version incremented by 1.
     * The previously active version of the book will be marked inactive.
     * Otherwise, a new bookId is generated for the book and the book will be stored in CatalogItemVersion as version 1.
     *
     * @param kindleFormattedBook A book to create or update.
     * @return CatalogItemVersion
     */
    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook kindleFormattedBook) {
            String bookId = kindleFormattedBook.getBookId();

            CatalogItemVersion book = new CatalogItemVersion();
            book.setAuthor(kindleFormattedBook.getAuthor());
            book.setGenre(kindleFormattedBook.getGenre());
            book.setInactive(false);
            book.setText(kindleFormattedBook.getText());
            book.setTitle(kindleFormattedBook.getTitle());

        if (bookId == null) {
            String generatedBookId = KindlePublishingUtils.generateBookId();
            book.setBookId(generatedBookId);
            book.setVersion(1);
            dynamoDbMapper.save(book);
            return book;
        }

        try {
            CatalogItemVersion previousVersion = getBookFromCatalog(bookId);
            book.setBookId(bookId);
            book.setVersion(previousVersion.getVersion() + 1);
            previousVersion.setInactive(true);
            dynamoDbMapper.save(previousVersion);
            dynamoDbMapper.save(book);
        } catch (BookNotFoundException e) {
            throw new BookNotFoundException("Previous version not found or inactive.");
        }

        return book;
    }
}
