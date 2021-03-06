package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CatalogDaoTest {

    @Mock
    private PaginatedQueryList<CatalogItemVersion> list;

    @Mock
    private DynamoDBMapper dynamoDbMapper;

    @InjectMocks
    private CatalogDao catalogDao;

    @BeforeEach
    public void setup(){
        initMocks(this);
    }

    @Test
    public void getBookFromCatalog_bookDoesNotExist_throwsException() {
        // GIVEN
        String invalidBookId = "notABookID";
        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(true);

        // WHEN && THEN
        assertThrows(BookNotFoundException.class, () -> catalogDao.getBookFromCatalog(invalidBookId),
                "Expected BookNotFoundException to be thrown for an invalid bookId.");
    }

    @Test
    public void getBookFromCatalog_bookInactive_throwsException() {
        // GIVEN
        String bookId = "book.123";
        CatalogItemVersion item = new CatalogItemVersion();
        item.setInactive(true);
        item.setBookId(bookId);
        item.setVersion(1);

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(item);

        // WHEN && THEN
        assertThrows(BookNotFoundException.class, () -> catalogDao.getBookFromCatalog(bookId),
                "Expected BookNotFoundException to be thrown for an invalid bookId.");
    }

    @Test
    public void getBookFromCatalog_oneVersion_returnVersion1() {
        // GIVEN
        String bookId = "book.123";
        CatalogItemVersion item = new CatalogItemVersion();
        item.setInactive(false);
        item.setBookId(bookId);
        item.setVersion(1);
        ArgumentCaptor<DynamoDBQueryExpression> requestCaptor = ArgumentCaptor.forClass(DynamoDBQueryExpression.class);

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(item);

        // WHEN
        CatalogItemVersion book = catalogDao.getBookFromCatalog(bookId);

        // THEN
        assertEquals(bookId, book.getBookId());
        assertEquals(1, book.getVersion(), "Expected version 1 of book to be returned");
        assertFalse(book.isInactive(), "Expected book to be active.");

        verify(dynamoDbMapper).query(eq(CatalogItemVersion.class), requestCaptor.capture());
        CatalogItemVersion queriedItem = (CatalogItemVersion) requestCaptor.getValue().getHashKeyValues();
        assertEquals(bookId, queriedItem.getBookId(), "Expected query to look for provided bookId");
        assertEquals(1, requestCaptor.getValue().getLimit(), "Expected query to have a limit set");
    }

    @Test
    public void getBookFromCatalog_twoVersions_returnsVersion2() {
        // GIVEN
        String bookId = "book.123";
        CatalogItemVersion item = new CatalogItemVersion();
        item.setInactive(false);
        item.setBookId(bookId);
        item.setVersion(2);
        ArgumentCaptor<DynamoDBQueryExpression> requestCaptor = ArgumentCaptor.forClass(DynamoDBQueryExpression.class);

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(item);

        // WHEN
        CatalogItemVersion book = catalogDao.getBookFromCatalog(bookId);

        // THEN
        assertEquals(bookId, book.getBookId());
        assertEquals(2, book.getVersion(), "Expected version 2 of book to be returned");
        assertFalse(book.isInactive(), "Expected book to be active.");

        verify(dynamoDbMapper).query(eq(CatalogItemVersion.class), requestCaptor.capture());
        CatalogItemVersion queriedItem = (CatalogItemVersion) requestCaptor.getValue().getHashKeyValues();
        assertEquals(bookId, queriedItem.getBookId(), "Expected query to look for provided bookId");
        assertEquals(1, requestCaptor.getValue().getLimit(), "Expected query to have a limit set");
    }

    @Test
    public void removeBookFromCatalog_bookActive_updatesBookInactive() {
        // GIVEN
        String bookId = "book.123";
        CatalogItemVersion item = new CatalogItemVersion();
        item.setInactive(false);
        item.setBookId(bookId);
        item.setVersion(1);
        ArgumentCaptor<DynamoDBQueryExpression> requestCaptor = ArgumentCaptor.forClass(DynamoDBQueryExpression.class);

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(item);

        // WHEN
        CatalogItemVersion book = catalogDao.removeBookFromCatalog(bookId);

        // THEN
        assertEquals(bookId, book.getBookId());
        assertEquals(1, book.getVersion(), "Expected version 1 of book to be returned");
        assertTrue(book.isInactive(), "Expected book to be inactive.");

        verify(dynamoDbMapper).query(eq(CatalogItemVersion.class), requestCaptor.capture());
        verify(dynamoDbMapper, times(1)).save(item);
        CatalogItemVersion queriedItem = (CatalogItemVersion) requestCaptor.getValue().getHashKeyValues();
        assertEquals(bookId, queriedItem.getBookId(), "Expected query to look for provided bookId");
        assertEquals(1, requestCaptor.getValue().getLimit(), "Expected query to have a limit set");
    }

    @Test
    public void validateBookExists_withNonExistantBook_throwsBookNotFoundException() {
        String bookId = "book.123";
        CatalogItemVersion item = new CatalogItemVersion();
        item.setInactive(false);
        item.setBookId(bookId);
        item.setVersion(1);

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(true);

        // WHEN + THEN
        assertThrows(BookNotFoundException.class, () -> catalogDao.validateBookExists(bookId));
    }

    @Test
    public void createOrUpdateBook_withKindleFormattedBookNewBook_returnsCatalogItemVersion() {
        //GIVEN
        int version = 1;
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setAuthor("Ray Kurzweil");
        catalogItemVersion.setGenre(BookGenre.COOKING);
        catalogItemVersion.setText("text");
        catalogItemVersion.setTitle("The Age of Intelligent Machines");
        catalogItemVersion.setVersion(version);
        catalogItemVersion.setInactive(false);

        KindleFormattedBook kindleFormattedBook = KindleFormattedBook.builder()
                .withAuthor("Ray Kurzweil")
                .withGenre(BookGenre.COOKING)
                .withText("text")
                .withTitle("The Age of Intelligent Machines")
                .build();
        //WHEN
        CatalogItemVersion resultBook = catalogDao.createOrUpdateBook(kindleFormattedBook);
        //THEN
        verify(dynamoDbMapper, times(1)).save(any(CatalogItemVersion.class));
        assertEquals(catalogItemVersion.getVersion(), resultBook.getVersion());
        assertFalse(resultBook.isInactive());
        assertNotNull(resultBook.getBookId());
    }

    @Test
    public void createOrUpdateBook_withKindleFormattedBookOldBook_returnsCatalogItemVersion() {
        //GIVEN
        int version = 1;
        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId("1234");
        catalogItemVersion.setAuthor("Ray Kurzweil");
        catalogItemVersion.setGenre(BookGenre.COOKING);
        catalogItemVersion.setText("text");
        catalogItemVersion.setTitle("The Age of Intelligent Machines");
        catalogItemVersion.setVersion(version);
        catalogItemVersion.setInactive(false);

        KindleFormattedBook kindleFormattedBook = KindleFormattedBook.builder()
                .withBookId("1234")
                .withAuthor("Ray Kurzweil")
                .withGenre(BookGenre.COOKING)
                .withText("text")
                .withTitle("The Age of Intelligent Machines")
                .build();
        ArgumentCaptor<DynamoDBQueryExpression> requestCaptor = ArgumentCaptor.forClass(DynamoDBQueryExpression.class);

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(list.get(0)).thenReturn(catalogItemVersion);
        //WHEN
        CatalogItemVersion resultBook = catalogDao.createOrUpdateBook(kindleFormattedBook);
        //THEN
        verify(dynamoDbMapper).query(eq(CatalogItemVersion.class), requestCaptor.capture());
        verify(dynamoDbMapper, times(2)).save(any(CatalogItemVersion.class));
        CatalogItemVersion queriedItem = (CatalogItemVersion) requestCaptor.getValue().getHashKeyValues();
        assertEquals(queriedItem.getBookId(), resultBook.getBookId());
        assertEquals(catalogItemVersion.getVersion() + 1, resultBook.getVersion());
        assertFalse(resultBook.isInactive());
    }

    @Test
    public void createOrUpdateBook_withKindleFormattedBookNoBook_throwsBookNotFound() {
        //GIVEN
        KindleFormattedBook kindleFormattedBook = KindleFormattedBook.builder()
                .withBookId("1234")
                .withAuthor("Ray Kurzweil")
                .withGenre(BookGenre.COOKING)
                .withText("text")
                .withTitle("The Age of Intelligent Machines")
                .build();

        when(dynamoDbMapper.query(eq(CatalogItemVersion.class), any(DynamoDBQueryExpression.class))).thenReturn(list);
        when(list.isEmpty()).thenReturn(true);

        //WHEN + THEN
        assertThrows(BookNotFoundException.class, ()->catalogDao.createOrUpdateBook(kindleFormattedBook));
    }
}
