package org.landsreyk.webfluxspring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.exception.BookNotFoundException;
import org.landsreyk.webfluxspring.model.Book;
import org.landsreyk.webfluxspring.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.getBooks().clear();
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
            bookRepository.save(new Book("Title" + i, "Author" + i, 2022));
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
        bookRepository.save(new Book("TitleA", "AuthorA", 2022));
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
        bookRepository.save(new Book("TitleB", "AuthorB", 2022));
        bookRepository.save(new Book("TitleC", "AuthorC", 2022));
        var bookDTO = new BookDTO(null, "NewTitle", "NewAuthor", 2022);

        // when
        bookService.update(id, bookDTO).block();

        // then
        StepVerifier.create(bookService.getById(id))
                .expectNextMatches(dto -> dto.getTitle().equals("NewTitle") && dto.getAuthor().equals("NewAuthor"))
                .verifyComplete();
    }

    @Test
    @Order(5)
    void testDeleteBook() {
        // given
        var existingBookId = bookRepository.save(new Book("TitleA", "AuthorA", 2022)).block().getId();
        bookRepository.save(new Book("TitleB", "AuthorB", 2022));
        bookRepository.save(new Book("TitleC", "AuthorC", 2022));

        // when & then
        StepVerifier.create(bookService.delete(existingBookId))
                .expectNext(true)
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
}