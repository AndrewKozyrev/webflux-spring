package org.landsreyk.webfluxspring.repository;

import org.landsreyk.webfluxspring.model.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomBookRepository {
    Mono<Long> update(Book book);

    Flux<Book> findByAuthorAndYearRange(String author, int startYear, int endYear);
}
