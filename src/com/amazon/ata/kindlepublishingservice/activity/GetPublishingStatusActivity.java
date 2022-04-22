package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.PublishingStatusConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.List;

public class GetPublishingStatusActivity {
    PublishingStatusDao publishingStatusDao;

    /**
     * Instantiates a new GetPublishingStatusActivity object.
     *
     * @param publishingStatusDao to access the PublishingStatus table.
     */
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    /**
     * Retrieves the PublishingStatus associated with the provided publishing record ID.
     *
     * @param publishingStatusRequest Request object containing the publishing record ID associated with the
     *                                publishing record to get from the PublishingStatus.
     * @return GetPublishingStatusResponse Response object containing the requested publishing status history.
     */
    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        String publishingRecordId = publishingStatusRequest.getPublishingRecordId();
        //publishingStatusDAO
        List<PublishingStatusItem> publishingStatusItems = publishingStatusDao.
                getPublishingStatuses(publishingRecordId);

        //convert publishingStatusItems to PublishingStatusRecords
        List<PublishingStatusRecord> publishingStatusRecords = PublishingStatusConverter
                .toPublishingStatusRecordList(publishingStatusItems);

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(publishingStatusRecords)
                .build();
    }
}
