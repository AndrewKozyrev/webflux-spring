package org.landsreyk.webfluxspring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.landsreyk.webfluxspring.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.UUID;

@SpringBootTest
class CustomBookRepositoryImplTest {

    @Autowired
    private ReactiveDatabaseBookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll().block();
        addTestBooks();
    }

    private void addTestBooks() {
        bookRepository.save(new Book(UUID.randomUUID(), "Spring Guide", "John Doe", 2021)).block();
        bookRepository.save(new Book(UUID.randomUUID(), "Reactive Programming", "Jane Doe", 2020)).block();
        bookRepository.save(new Book(UUID.randomUUID(), "WebFlux in Action", "John Doe", 2019)).block();
        bookRepository.save(new Book(UUID.randomUUID(), "Modern Java", "Jane Doe", 2018)).block();
    }

    @Test
    @DisplayName("Should return books by author and year range")
    void testFindByAuthorAndYearRange() {
        // given
        var author = "John Doe";
        var startYear = 2019;
        var endYear = 2021;

        // when & then
        StepVerifier.create(bookRepository.findByAuthorAndYearRange(author, startYear, endYear))
                .expectNextMatches(book -> book.getTitle().equals("Spring Guide") && book.getPublishedYear() == 2021)
                .expectNextMatches(book -> book.getTitle().equals("WebFlux in Action") && book.getPublishedYear() == 2019)
                .verifyComplete();
    }
}
