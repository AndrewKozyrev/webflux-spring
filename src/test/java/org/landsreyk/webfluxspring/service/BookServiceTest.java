package org.landsreyk.webfluxspring.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.landsreyk.webfluxspring.mapper.BookMapper;
import org.landsreyk.webfluxspring.repository.ReactiveDatabaseBookRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private ReactiveDatabaseBookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void testGetById_onException() {
        // given
        var someId = UUID.randomUUID();
        when(bookRepository.findById(someId)).thenReturn(Mono.error(new IllegalStateException("some random error")));

        // when & then
        StepVerifier.create(bookService.getById(someId))
                .expectErrorMatches(ex -> ex.getMessage().equals("Error while fetching a book with id = " + someId))
                .verify();
    }
}