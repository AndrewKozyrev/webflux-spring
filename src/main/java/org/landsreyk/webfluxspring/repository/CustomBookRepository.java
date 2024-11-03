package org.landsreyk.webfluxspring.repository;

import org.landsreyk.webfluxspring.model.Book;
import reactor.core.publisher.Mono;

public interface CustomBookRepository {
    Mono<Long> update(Book book);
}
