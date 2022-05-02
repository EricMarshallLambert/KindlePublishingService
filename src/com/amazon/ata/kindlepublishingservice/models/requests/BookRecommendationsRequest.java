package com.amazon.ata.kindlepublishingservice.models.requests;

import com.amazon.ata.recommendationsservice.types.BookGenre;

import java.util.Objects;

public class BookRecommendationsRequest {
    private final BookGenre bookGenre;

    public BookRecommendationsRequest(BookGenre bookGenre) {
        this.bookGenre = bookGenre;
    }

    public BookGenre getBookGenre() {
        return bookGenre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRecommendationsRequest that = (BookRecommendationsRequest) o;
        return getBookGenre() == that.getBookGenre();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookGenre());
    }
}
