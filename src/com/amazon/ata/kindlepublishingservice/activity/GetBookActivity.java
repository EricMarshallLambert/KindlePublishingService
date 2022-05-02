package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.clients.CachingRecommendationsServiceClient;
import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceable;
import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.metrics.MetricsConstants;
import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.kindlepublishingservice.models.requests.BookRecommendationsRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.GetBookRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetBookResponse;
import com.amazon.ata.kindlepublishingservice.converters.RecommendationsCoralConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.recommendationsservice.types.BookRecommendation;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import java.util.List;
import javax.inject.Inject;

/**
 * Implementation of the GetBookActivity for the ATACurriculumKindlePublishingService's
 * GetBook API.
 *
 * This API allows the client to retrieve a book.
 */
public class GetBookActivity {

    private final RecommendationsServiceable recommendationsServiceClient;
    private final MetricsPublisher metricsPublisher;

    private CatalogDao catalogDao;

    /**
     * Instantiates a new GetBookActivity object.
     *
     * @param catalogDao CatalogDao to access the Catalog table.
     * @param recommendationsServiceClient Returns recommendations based on genre.
     */
    @Inject
    public GetBookActivity(CatalogDao catalogDao, CachingRecommendationsServiceClient recommendationsServiceClient,
                           MetricsPublisher metricsPublisher) {
        this.catalogDao = catalogDao;
        this.recommendationsServiceClient = recommendationsServiceClient;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Retrieves the book associated with the provided book id.
     *
     * @param request Request object containing the book ID associated with the book to get from the Catalog.
     * @return GetBookResponse Response object containing the requested book.
     */

    public GetBookResponse execute(final GetBookRequest request) {
        metricsPublisher.addMetric(MetricsConstants.GET_BOOK_REQUEST, 1, StandardUnit.Count);

        CatalogItemVersion catalogItem = catalogDao.getBookFromCatalog(request.getBookId());
        //use cache here
        BookRecommendationsRequest bookRecommendationsRequest = new BookRecommendationsRequest(catalogItem.getGenre());


        List<BookRecommendation> recommendations = recommendationsServiceClient
                .getBookRecommendations(bookRecommendationsRequest);


        return GetBookResponse.builder()
            .withBook(CatalogItemConverter.toBook(catalogItem))
            .withRecommendations(RecommendationsCoralConverter.toCoral(recommendations))
            .build();
    }
}
