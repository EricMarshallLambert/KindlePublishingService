package com.amazon.ata.kindlepublishingservice.clients;

import com.amazon.ata.kindlepublishingservice.models.requests.BookRecommendationsRequest;
import com.amazon.ata.recommendationsservice.types.BookRecommendation;

import java.util.List;

public interface RecommendationsServiceable {
    List<BookRecommendation> getBookRecommendations(BookRecommendationsRequest bookRecommendationsRequest);

    }
