package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.LinkedList;
import java.util.List;

public class PublishingStatusConverter {

    private PublishingStatusConverter() {}

    /**
     * Converts the given {@link PublishingStatusItem} object into the corresponding PublishingStatusRecord object.
     *
     * @param publishingStatusItem PublishingStatusItem to convert
     * @return PublishingStatusRecord
     */
    public static PublishingStatusRecord toPublishingStatusRecord(PublishingStatusItem publishingStatusItem) {
        return PublishingStatusRecord.builder()
                .withBookId(publishingStatusItem.getBookId())
                .withStatus(publishingStatusItem.getStatus().name())
                .withStatusMessage(publishingStatusItem.getStatusMessage())
                .build();
    }

    /**
     * Converts the given List of {@link PublishingStatusItem} objects into a list of PublishingStatusRecords.
     *
     * @param publishingStatusItems List to convert
     * @return List<PublishingStatusRecord> A list of PublishingStatusRecord
     */
    public static List<PublishingStatusRecord> toPublishingStatusRecordList(
            List<PublishingStatusItem> publishingStatusItems) {

        List<PublishingStatusRecord> publishingStatusRecords = new LinkedList<>();
        for (PublishingStatusItem publishingStatusItem : publishingStatusItems) {
            publishingStatusRecords.add(toPublishingStatusRecord(publishingStatusItem));
        }
        return publishingStatusRecords;
    }
}
