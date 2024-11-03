package org.landsreyk.webfluxspring.repository;

import org.landsreyk.webfluxspring.model.Book;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReactiveDatabaseBookRepository extends ReactiveCrudRepository<Book, UUID>, CustomBookRepository {

    @NonNull
    @Query("SELECT * FROM book WHERE id = :id")
    Mono<Book> findById(@NonNull UUID id);

    @Query("DELETE FROM book WHERE id = :id")
    Mono<Void> deleteById(@NonNull UUID id);
}
