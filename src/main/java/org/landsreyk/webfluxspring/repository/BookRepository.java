package org.landsreyk.webfluxspring.repository;

import lombok.Getter;
import org.landsreyk.webfluxspring.exception.BookNotFoundException;
import org.landsreyk.webfluxspring.model.Book;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class BookRepository {

    @Getter
    private final Map<UUID, Book> books = new LinkedHashMap<>();

    public Mono<Book> save(Book book) {
        books.put(book.getId(), book);
        return Mono.just(book);
    }

    public Flux<Book> findAll(long page, long size) {
        return Flux.fromIterable(books.values()).skip(page * size).take(size);
    }

    public Mono<Book> findById(UUID id) {
        return Mono.justOrEmpty(books.get(id));
    }

    public Mono<Boolean> deleteById(UUID id) {
        return findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .map(book -> {
                    books.remove(id);
                    return true;
                });
    }
}