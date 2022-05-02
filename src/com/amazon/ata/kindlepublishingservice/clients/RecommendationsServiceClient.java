package com.amazon.ata.kindlepublishingservice.clients;

import com.amazon.ata.kindlepublishingservice.metrics.MetricsConstants;
import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.kindlepublishingservice.models.requests.BookRecommendationsRequest;
import com.amazon.ata.recommendationsservice.types.BookGenre;
// import com.amazon.ata.kindlepublishingservice.metrics.MetricsConstants;
//import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.recommendationsservice.RecommendationsService;
import com.amazon.ata.recommendationsservice.types.BookRecommendation;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.dynamodbv2.xspec.S;

import java.util.List;
import javax.inject.Inject;


/**
 * Client used to call Recommendations Service.
 */

public class RecommendationsServiceClient implements RecommendationsServiceable{

    private final RecommendationsService recommendationsService;
    private final MetricsPublisher metricsPublisher;

    /**
     * Instantiates a new RecommendationsServiceClient.
     *
     * @param service RecommendationsService to call.
     */
    @Inject
    public RecommendationsServiceClient(RecommendationsService service, MetricsPublisher metricsPublisher) {
        this.recommendationsService = service;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Returns a list of book recommendations based on the passed in genre. An empty list will be returned
     * if no recommendations are found or cannot be generated.
     * @param genre genre to get recommendations for.
     * @return list of book recommendations.
     */
    public List<BookRecommendation> getBookRecommendations(BookGenre genre) {
        final double startTime = System.currentTimeMillis();

        List<BookRecommendation> recommendations = recommendationsService.getBookRecommendations(
            BookGenre.valueOf(genre.name()));

        final double latencyTime = System.currentTimeMillis() - startTime;
        metricsPublisher.addMetric(MetricsConstants.GET_BOOK_RECOMMENDATIONS_LATENCY,
                latencyTime, StandardUnit.Milliseconds);

        metricsPublisher.addMetric(MetricsConstants.RECOMMENDATIONS_SERVICE_CALL, 1, StandardUnit.Count);

        return recommendations;
    }

    public List<BookRecommendation> getBookRecommendations(BookRecommendationsRequest bookRecommendationsRequest) {
        return getBookRecommendations(bookRecommendationsRequest.getBookGenre());
    }

}
