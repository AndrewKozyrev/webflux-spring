package org.landsreyk.webfluxspring.controller;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.model.Book;
import org.landsreyk.webfluxspring.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    private static final EasyRandom EASY_RANDOM = new EasyRandom(new EasyRandomParameters().seed(System.currentTimeMillis()));

    @Test
    void testCreateBookValidation() {
        var invalidBook = new BookDTO(null, "", "", 1800);

        webTestClient.post()
                .uri("/books")
                .bodyValue(invalidBook)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Title cannot be blank")
                .jsonPath("$.author").isEqualTo("Author cannot be blank")
                .jsonPath("$.publishedYear").isEqualTo("Published year must be at least 1900")
                .consumeWith(System.out::println);
    }

    @Test
    void testDeleteBook_notFoundException() {
        var nonExistentId = UUID.randomUUID();

        webTestClient.delete()
                .uri("/books/" + nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.status").isEqualTo("NOT_FOUND")
                .jsonPath("$.error").isEqualTo("Book Not Found.")
                .jsonPath("$.message").isEqualTo("Book with id = [%s] not found".formatted(nonExistentId))
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void testGetAllBooks_validPagination() {
        // given
        Stream.iterate(0, x -> x < 10, x -> x + 1).map(x -> EASY_RANDOM.nextObject(Book.class)).forEach(book -> bookRepository.save(book));

        // when & then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/books").queryParam("page", 0).queryParam("size", 10).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDTO.class)
                .consumeWith(System.out::println)
                .hasSize(10);
    }

    @Test
    void testGetAllBooks_invalidPagination() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/books").queryParam("page", -1).queryParam("size", 0).build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.size").isEqualTo("Page size can't be less than 1.")
                .jsonPath("$.page").isEqualTo("Page number can't be negative.");
    }
}