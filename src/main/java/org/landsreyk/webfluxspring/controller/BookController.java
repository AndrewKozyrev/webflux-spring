package org.landsreyk.webfluxspring.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.service.BookService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public Mono<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        return bookService.create(bookDTO);
    }

    @GetMapping
    public Flux<BookDTO> getAllBooks(@RequestParam @Min(value = 0, message = "Page number can't be negative.") long page,
                                     @RequestParam @Min(value = 1, message = "Page size can't be less than 1.") @Max(value = 100, message = "Page size maximum value is 100.") long size) {
        return bookService.getAll(page, size);
    }

    @GetMapping("{id}")
    public Mono<BookDTO> getBookById(@PathVariable UUID id) {
        return bookService.getById(id);
    }

    @PutMapping("{id}")
    public Mono<BookDTO> updateBook(@PathVariable UUID id, @Valid @RequestBody BookDTO bookDTO) {
        return bookService.update(id, bookDTO);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteBook(@PathVariable UUID id) {
        return bookService.delete(id);
    }

    @GetMapping(value = "/stream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<BookDTO> streamAllBooks() {
        return bookService.streamAllBooks();
    }
}
