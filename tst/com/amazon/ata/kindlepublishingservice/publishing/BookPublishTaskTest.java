package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dagger.BookPublishRequestManager;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class BookPublishTaskTest {
    @Mock
    private BookPublishRequestManager bookPublishRequestManager;

    @Mock
    private CatalogDao catalogDao;

    @Mock
    private PublishingStatusDao publishingStatusDao;

    @InjectMocks
    private BookPublishTask bookPublishTask;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void run_withNullBookPublishRequest_returns() {
        //GIVEN
        BookPublishRequest bookPublishRequest = null;
        when(bookPublishRequestManager.getBookPublishRequestToProcess()).thenReturn(bookPublishRequest);
        //WHEN
        bookPublishTask.run();
        //THEN
        verify(bookPublishRequestManager, times(1)).getBookPublishRequestToProcess();
        verifyNoMoreInteractions(bookPublishRequestManager);
        verifyNoInteractions(publishingStatusDao);
        verifyNoInteractions(catalogDao);
    }
    @Test
    public void run_withBookPublishRequestWithBookId_publishingStatusIN_SUCCESSFUL() {
        //GIVEN
        String author = "Frank";
        String bookId = "1234";
        String text = "text";
        String title = "title";
        String publishingRecordId = "publishingRecordId";

        BookPublishRequest bookPublishRequest = BookPublishRequest.builder()
                .withAuthor(author)
                .withBookId(bookId)
                .withGenre(BookGenre.ACTION)
                .withText(text)
                .withTitle(title)
                .withPublishingRecordId(publishingRecordId)
                .build();

        CatalogItemVersion catalogItemVersion = new CatalogItemVersion();
        catalogItemVersion.setBookId(bookId);
        catalogItemVersion.setAuthor(author);
        catalogItemVersion.setGenre(BookGenre.ACTION);
        catalogItemVersion.setText(text);
        catalogItemVersion.setVersion(1);
        catalogItemVersion.setTitle(title);
        catalogItemVersion.setInactive(false);

        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(bookPublishRequest);
        when(bookPublishRequestManager.getBookPublishRequestToProcess()).thenReturn(bookPublishRequest);
        when(catalogDao.createOrUpdateBook(kindleFormattedBook)).thenReturn(catalogItemVersion);
        //WHEN
        bookPublishTask.run();
        //THEN
        verify(bookPublishRequestManager, times(1)).getBookPublishRequestToProcess();
        verifyNoMoreInteractions(bookPublishRequestManager);
        verify(publishingStatusDao, times(1)).setPublishingStatus(publishingRecordId,
                        PublishingRecordStatus.IN_PROGRESS, bookId);
        verify(catalogDao, times(1)).createOrUpdateBook(any(KindleFormattedBook.class));
        verify(publishingStatusDao,times(1)).setPublishingStatus(publishingRecordId,
                PublishingRecordStatus.SUCCESSFUL, bookId);
    }


    @Test
    public void run_withBookPublishRequestNoBookId_PublishingStatusFAILED() {
        //GIVEN
        String author = "Frank";
        String bookId = "1234";
        String text = "text";
        String title = "title";
        String publishingRecordId = "publishingRecordId";

        BookPublishRequest bookPublishRequest = BookPublishRequest.builder()
                .withAuthor(author)
                .withBookId(bookId)
                .withGenre(BookGenre.ACTION)
                .withText(text)
                .withTitle(title)
                .withPublishingRecordId(publishingRecordId)
                .build();

        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(bookPublishRequest);
        when(bookPublishRequestManager.getBookPublishRequestToProcess()).thenReturn(bookPublishRequest);
        when(catalogDao.createOrUpdateBook(kindleFormattedBook)).thenThrow(BookNotFoundException.class);
        //WHEN
        bookPublishTask.run();
        // THEN
        verify(bookPublishRequestManager, times(1)).getBookPublishRequestToProcess();
        verifyNoMoreInteractions(bookPublishRequestManager);
        verify(publishingStatusDao, times(1)).setPublishingStatus(publishingRecordId,
                PublishingRecordStatus.IN_PROGRESS, bookId);
        verify(catalogDao, times(1)).createOrUpdateBook(any(KindleFormattedBook.class));
        verify(publishingStatusDao, times(2));
    }
}