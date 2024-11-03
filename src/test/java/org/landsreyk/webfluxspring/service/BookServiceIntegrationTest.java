package org.landsreyk.webfluxspring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.exception.BookNotFoundException;
import org.landsreyk.webfluxspring.model.Book;
import org.landsreyk.webfluxspring.repository.ReactiveDatabaseBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReactiveDatabaseBookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll().block();
    }

    @Test
    @Order(1)
    void testCreateBook() {
        // given
        var bookDTO = new BookDTO(UUID.randomUUID(), "Title", "Author", 2022);

        // when & then
        StepVerifier.create(bookService.create(bookDTO))
                .expectNextMatches(dto -> dto.getTitle().equals("Title"))
                .verifyComplete();
    }

    @Test
    @Order(2)
    void testGetAllBooks() {
        // given
        for (int i = 1; i <= 100; i++) {
            bookRepository.save(new Book("Title" + i, "Author" + i, 2022)).block();
        }

        // when & then
        StepVerifier.create(bookService.getAll(2, 10))
                .expectNextMatches(dto -> dto.getTitle().equals("Title21"))
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    @Order(3)
    void testGetBookById() {
        // given
        bookRepository.save(new Book("TitleA", "AuthorA", 2022)).block();
        var saved = bookRepository.save(new Book("TitleB", "AuthorB", 2022));
        var id = Objects.requireNonNull(saved.block()).getId();

        // when & then
        StepVerifier.create(bookService.getById(id))
                .expectNextMatches(dto -> dto.getTitle().equals("TitleB"))
                .verifyComplete();
    }

    @Test
    @Order(4)
    void testUpdateBook() {
        // given
        var id = bookRepository.save(new Book("TitleA", "AuthorA", 2022)).block().getId();
        bookRepository.save(new Book("TitleB", "AuthorB", 2022)).block();
        bookRepository.save(new Book("TitleC", "AuthorC", 2022)).block();
        var bookDTO = new BookDTO(null, "NewTitle", "NewAuthor", 2022);

        // when
        var countAffected = bookService.update(id, bookDTO).block();

        // then
        assertEquals(1, countAffected);
        StepVerifier.create(bookRepository.findById(id))
                .expectNextMatches(dto -> dto.getTitle().equals("NewTitle") && dto.getAuthor().equals("NewAuthor"))
                .verifyComplete();
    }

    @Test
    @Order(5)
    void testDeleteBook() {
        // given
        var existingBookId = bookRepository.save(new Book("TitleA", "AuthorA", 2022)).block().getId();
        bookRepository.save(new Book("TitleB", "AuthorB", 2022)).block();
        bookRepository.save(new Book("TitleC", "AuthorC", 2022)).block();

        // when & then
        StepVerifier.create(bookService.delete(existingBookId))
                .expectNext()
                .verifyComplete();

        StepVerifier.create(bookRepository.findById(existingBookId))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @Order(6)
    void testDeleteBook_notFound() {
        // given
        bookRepository.save(new Book("TitleA", "AuthorA", 2022));
        bookRepository.save(new Book("TitleB", "AuthorB", 2022));
        bookRepository.save(new Book("TitleC", "AuthorC", 2022));
        var nonExistentId = UUID.randomUUID();

        // when & then
        StepVerifier.create(bookService.delete(nonExistentId))
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    @Order(7)
    void testCountBooks() {
        // given
        for (int i = 1; i <= 5; i++) {
            bookRepository.save(new Book("Title" + i, "Author" + i, 2022)).block();
        }

        // when & then
        StepVerifier.create(bookService.countBooks())
                .assertNext(count -> assertEquals(5, count))
                .verifyComplete();
    }

    @Test
    @Order(8)
    void testStreamAllBooks() {
        // given
        for (int i = 1; i <= 100; i++) {
            bookRepository.save(new Book("Title" + i, "Author" + i, 2022)).block();
        }

        // when & then
        var step = StepVerifier.withVirtualTime(() -> bookService.streamAllBooks())
                .expectSubscription();
        for (int i = 1; i <= 100; i++) {
            var cnt = i;
            step = step.thenAwait(Duration.ofSeconds(1)).expectNextMatches(book -> book.getTitle().equals("Title" + cnt));
        }
        step.verifyComplete();
    }

}