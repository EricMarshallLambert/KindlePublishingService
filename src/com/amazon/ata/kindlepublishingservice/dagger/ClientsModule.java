package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.clients.RecommendationsServiceClient;
import com.amazon.ata.kindlepublishingservice.metrics.MetricsPublisher;
import com.amazon.ata.recommendationsservice.RecommendationsService;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ClientsModule {

    @Singleton
    @Provides
    public RecommendationsServiceClient provideRecommendationsServiceClient(
            RecommendationsService recommendationsService, MetricsPublisher metricsPublisher) {
        return new RecommendationsServiceClient(recommendationsService, metricsPublisher);
    }

//    @Singleton
//    @Provides
//    public CachingRecommendationsServiceClient provideCachingRecommendationsServiceClient(
//            RecommendationsServiceClient recommendationsServiceClient) {
//        return new CachingRecommendationsServiceClient(recommendationsServiceClient);
//    }
}
