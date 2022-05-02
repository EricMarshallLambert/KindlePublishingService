package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class CloudWatchModule {
    @Singleton
    @Provides
    public AmazonCloudWatch provideRecommendationsServiceClient() {
                AmazonCloudWatch cloudWatch = AmazonCloudWatchClientBuilder
                        .standard()
                        .withRegion(Regions.US_WEST_2)
                        .build();
        return  cloudWatch;
    }
}
