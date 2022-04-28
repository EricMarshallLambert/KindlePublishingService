package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

/**
 * Accesses the Publishing Status table.
 */
public class PublishingStatusDao {

    private static final String ADDITIONAL_NOTES_PREFIX = " Additional Notes: ";
    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new PublishingStatusDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the publishing status table.
     */
    @Inject
    public PublishingStatusDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Updates the publishing status table for the given publishingRecordId with the provided
     * publishingRecordStatus. If the bookId is provided it will also be stored in the record.
     *
     * @param publishingRecordId The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId The id of the book associated with the request, may be null
     * @return The stored PublishingStatusItem.
     */
    public PublishingStatusItem setPublishingStatus(String publishingRecordId,
                                                    PublishingRecordStatus publishingRecordStatus,
                                                    String bookId) {
        return setPublishingStatus(publishingRecordId, publishingRecordStatus, bookId, null);
    }

    /**
     * Updates the publishing status table for the given publishingRecordId with the provided
     * publishingRecordStatus. If the bookId is provided it will also be stored in the record. If
     * a message is provided, it will be appended to the publishing status message in the datastore.
     *
     * @param publishingRecordId The id of the record to update
     * @param publishingRecordStatus The PublishingRecordStatus to save into the table.
     * @param bookId The id of the book associated with the request, may be null
     * @param message additional notes stored with the status
     * @return The stored PublishingStatusItem.
     */
    public PublishingStatusItem setPublishingStatus(String publishingRecordId,
                                                    PublishingRecordStatus publishingRecordStatus,
                                                    String bookId,
                                                    String message) {
        String statusMessage = KindlePublishingUtils.generatePublishingStatusMessage(publishingRecordStatus);
        if (StringUtils.isNotBlank(message)) {
            statusMessage = new StringBuffer()
                .append(statusMessage)
                .append(ADDITIONAL_NOTES_PREFIX)
                .append(message)
                .toString();
        }

        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId(publishingRecordId);
        item.setStatus(publishingRecordStatus);
        item.setStatusMessage(statusMessage);
        item.setBookId(bookId);
        dynamoDbMapper.save(item);
        return item;
    }

    /**
     * Returns a list of publishing status items corresponding to the specified publishing record ID.
     *      * Throws a PublishingStatusNotFoundException if the record is not found.
     *
     * @param publishingRecordId ID associated with the publishing record.
     * @return The list of corresponding PublishingStatusItems from the publishing status table.
     */
    public List<PublishingStatusItem> getPublishingStatuses(String publishingRecordId) {
        List<PublishingStatusItem> publishingStatusItems = getPublishingStatusRecords(publishingRecordId);

        if (publishingStatusItems == null) {
            throw new PublishingStatusNotFoundException(String.format("Publishing record not found for ID: %s",
                    publishingRecordId));
        }
        return publishingStatusItems;
    }

    // Returns null if no record exists for the provided publishingRecordId
    public List<PublishingStatusItem> getPublishingStatusRecords(String publishingRecordId) {
        PublishingStatusItem publishingStatusItem = new PublishingStatusItem();
        publishingStatusItem.setPublishingRecordId(publishingRecordId);

        DynamoDBQueryExpression<PublishingStatusItem> dbQueryExpression =
                new DynamoDBQueryExpression<PublishingStatusItem>()
                .withHashKeyValues(publishingStatusItem);

        List<PublishingStatusItem> publishingStatusItems = dynamoDbMapper.query(PublishingStatusItem.class,
                dbQueryExpression);

        if (publishingStatusItems.isEmpty()) {
            return null;
        }
        return publishingStatusItems;
    }
}
