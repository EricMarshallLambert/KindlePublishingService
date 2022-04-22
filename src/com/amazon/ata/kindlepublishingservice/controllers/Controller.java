package com.amazon.ata.kindlepublishingservice.controllers;

import com.amazon.ata.kindlepublishingservice.*;
import com.amazon.ata.kindlepublishingservice.activity.GetBookActivity;
import com.amazon.ata.kindlepublishingservice.activity.GetPublishingStatusActivity;
import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.activity.SubmitBookForPublishingActivity;
import com.amazon.ata.kindlepublishingservice.dagger.ApplicationComponent;
import com.amazon.ata.kindlepublishingservice.models.*;
import com.amazon.ata.kindlepublishingservice.models.requests.GetBookRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.requests.SubmitBookForPublishingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class Controller {
    private static final ApplicationComponent component = App.component;

    @GetMapping(value = "/books/{id}", produces = {"application/json"})
    public ResponseEntity<?> getBook(@PathVariable String id) {
        GetBookActivity bookActivity = component.provideGetBookActivity();
        GetBookRequest getBookRequest = GetBookRequest.builder().withBookId(id).build();
        return new ResponseEntity<>(bookActivity.execute(getBookRequest), HttpStatus.OK);
    }

    @GetMapping(value = "/publishingStatus/{id}", produces = {"application/json"})
    public  ResponseEntity<?> getPublishingStatus(@PathVariable String id){
        GetPublishingStatusActivity publishingStatusActivity = component.provideGetPublishingStatusActivity();
        GetPublishingStatusRequest getPublishingStatusRequest = GetPublishingStatusRequest.builder()
                .withPublishingRecordId(id).build();
        return new ResponseEntity<>(publishingStatusActivity.execute(getPublishingStatusRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "/books/{id}")
    public ResponseEntity<?> removeBook(@PathVariable String id) {
        RemoveBookFromCatalogActivity removeActivity = component.provideRemoveBookFromCatalogActivity();
        RemoveBookFromCatalogRequest removeBookRequest = RemoveBookFromCatalogRequest.builder()
                .withBookId(id).build();

        return new ResponseEntity<>(removeActivity.execute(removeBookRequest), HttpStatus.OK);
    }

    @PostMapping(value = "/books", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> submitBookForPublishing(@Valid @RequestBody Book book) {
        SubmitBookForPublishingActivity submitBookForPublishingActivity = component.provideSubmitBookForPublishingActivity();
        SubmitBookForPublishingRequest submitBookForPublishingRequest = SubmitBookForPublishingRequest.builder()
                .withAuthor(book.getAuthor())
                .withBookId(book.getBookId())
                .withGenre(book.getGenre())
                .withText(book.getText())
                .withTitle(book.getTitle())
                .build();
        return new ResponseEntity<>(submitBookForPublishingActivity.execute(submitBookForPublishingRequest), HttpStatus.OK);
    }
}
