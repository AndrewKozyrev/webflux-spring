package org.landsreyk.webfluxspring.repository;

import lombok.RequiredArgsConstructor;
import org.landsreyk.webfluxspring.model.Book;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomBookRepositoryImpl implements CustomBookRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> update(Book book) {
        return databaseClient.sql("""
                        UPDATE book
                        SET title = :title,
                            author = :author,
                            published_year = :publishedYear
                        WHERE id = :id
                        """)
                .bind("title", book.getTitle())
                .bind("author", book.getAuthor())
                .bind("publishedYear", book.getPublishedYear())
                .bind("id", book.getId())
                .fetch()
                .rowsUpdated();
    }
}