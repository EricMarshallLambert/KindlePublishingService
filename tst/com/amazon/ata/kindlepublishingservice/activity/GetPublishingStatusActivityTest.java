package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class GetPublishingStatusActivityTest {
    private static String PUBLISHING_STATUS_ID = "publishingStatus.123";
    private static PublishingRecordStatus STATUS = PublishingRecordStatus.SUCCESSFUL;
    private static String BOOK_ID = "book.123";
    private static String MESSAGE = "Cowabunga!";

    private GetPublishingStatusRequest request;
    private PublishingStatusItem item;
    private List<PublishingStatusItem> publishingStatusItems;

    @Mock
    private PublishingStatusDao publishingStatusDao;
    
    @InjectMocks
    private GetPublishingStatusActivity getPublishingStatusActivity;
    
    @BeforeEach
    public void setUp() {
        initMocks(this);
        request = GetPublishingStatusRequest
                .builder()
                .withPublishingRecordId(PUBLISHING_STATUS_ID)
                .build();

        item = new PublishingStatusItem();
        item.setPublishingRecordId(PUBLISHING_STATUS_ID);
        item.setStatus(STATUS);
        item.setStatusMessage(MESSAGE);
        item.setBookId(BOOK_ID);

        publishingStatusItems = new LinkedList<>();
        publishingStatusItems.add(item);
    }
    
    @Test
    public void execute_withPublishingStatusRequest_returnsPublishingStatusResponse() {
        //GIVEN
        when(publishingStatusDao.getPublishingStatuses(PUBLISHING_STATUS_ID)).thenReturn(publishingStatusItems);
        
        //WHEN
        GetPublishingStatusResponse response = getPublishingStatusActivity.execute(request);
        //THEN
        assertNotNull(response, "Expected request to return a non-null response.");
        assertNotNull(response.getPublishingStatusHistory(), "Expected a non null history in the response.");
        PublishingStatusRecord publishingStatusRecord  = response.getPublishingStatusHistory().get(0);
        assertEquals(BOOK_ID, publishingStatusRecord.getBookId(), "Expected publishing status in response to " +
                "contain book id.");
        assertEquals(STATUS.name(), publishingStatusRecord.getStatus(), "Expected publishing status in response to " +
                "contain status.");
        assertEquals(MESSAGE, publishingStatusRecord.getStatusMessage(), "Expected publishing status in response " +
                "to contain status message.");
    }

    @Test
    public void execute_withPublishingStatusRequest_throwsPublishingStatusNotFoundException() {
        //GIVEN
        when(publishingStatusDao.getPublishingStatusRecords(PUBLISHING_STATUS_ID)).thenReturn(null);
        when(publishingStatusDao.getPublishingStatuses(PUBLISHING_STATUS_ID))
                .thenThrow(PublishingStatusNotFoundException.class);

        //WHEN + THEN
        assertThrows(PublishingStatusNotFoundException.class, ()-> getPublishingStatusActivity.execute(request));
    }

    @Test
    public void execute_withPublishingStatusRequestMultiItem_returnsPublishingStatusResponse() {
        //GIVEN
        publishingStatusItems.add(item);
        when(publishingStatusDao.getPublishingStatuses(PUBLISHING_STATUS_ID)).thenReturn(publishingStatusItems);

        //WHEN
        GetPublishingStatusResponse response = getPublishingStatusActivity.execute(request);
        //THEN
        assertEquals(2,response.getPublishingStatusHistory().size());
    }
}