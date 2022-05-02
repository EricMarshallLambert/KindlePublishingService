package com.amazon.ata.kindlepublishingservice.clients;

import com.amazon.ata.kindlepublishingservice.metrics.MetricsConstants;
import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.kindlepublishingservice.models.requests.BookRecommendationsRequest;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazon.ata.recommendationsservice.types.BookRecommendation;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class CachingRecommendationsServiceClient implements RecommendationsServiceable{

    private final LoadingCache<BookRecommendationsRequest, List<BookRecommendation>> bookRecommendationsCache;
    private final MetricsPublisher metricsPublisher;
    private static final long MAXIMUM_SIZE = 10;
    private static final long DURATION = 10;
    private static final TimeUnit TIME_UNITS = TimeUnit.MINUTES;

    @Inject
    public CachingRecommendationsServiceClient(RecommendationsServiceClient recommendationsServiceClient,
                                               MetricsPublisher metricsPublisher) {
        this.metricsPublisher = metricsPublisher;
        this.bookRecommendationsCache = CacheBuilder.newBuilder()
                .maximumSize(MAXIMUM_SIZE)
                .expireAfterWrite(DURATION, TIME_UNITS)
                .build(CacheLoader.from(recommendationsServiceClient::getBookRecommendations));
    }

    public List<BookRecommendation> getBookRecommendations(BookRecommendationsRequest bookRecommendationsRequest){
        metricsPublisher.addMetric(MetricsConstants.RECOMMENDATIONS_CACHE_CALL, 1, StandardUnit.Count);
        return bookRecommendationsCache.getUnchecked(bookRecommendationsRequest);
    }

}
