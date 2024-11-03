package org.landsreyk.webfluxspring.repository;

import lombok.RequiredArgsConstructor;
import org.landsreyk.webfluxspring.model.Book;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

    @Override
    public Flux<Book> findByAuthorAndYearRange(String author, int startYear, int endYear) {
        return databaseClient.sql("""
                        SELECT * FROM book
                        WHERE author = :author
                        AND published_year BETWEEN :startYear AND :endYear
                        """)
                .bind("author", author)
                .bind("startYear", startYear)
                .bind("endYear", endYear)
                .map(((row, metadata) -> new Book(
                        row.get("id", UUID.class),
                        row.get("title", String.class),
                        row.get("author", String.class),
                        row.get("published_year", Integer.class)
                )))
                .all();
    }
}