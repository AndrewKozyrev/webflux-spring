package org.landsreyk.webfluxspring.service;

import lombok.RequiredArgsConstructor;
import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.exception.BookNotFoundException;
import org.landsreyk.webfluxspring.mapper.BookMapper;
import org.landsreyk.webfluxspring.model.Book;
import org.landsreyk.webfluxspring.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Mono<BookDTO> create(BookDTO bookDTO) {
        Book book = bookMapper.mapToEntity(bookDTO);
        Mono<Book> mono = bookRepository.save(book);
        return mono.map(bookMapper::mapToDTO);
    }

    public Flux<BookDTO> getAll(long page, long size) {
        Flux<Book> flux = bookRepository.findAll(page, size);
        return flux.map(bookMapper::mapToDTO);
    }

    public Mono<BookDTO> getById(UUID id) {
        Mono<Book> mono = bookRepository.findById(id);
        return mono.map(bookMapper::mapToDTO);
    }

    public Mono<BookDTO> update(UUID id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .doOnError(error -> Mono.error(new BookNotFoundException(id)))
                .map(book -> {
                    book.setAuthor(bookDTO.getAuthor());
                    book.setTitle(bookDTO.getTitle());
                    book.setPublishedYear(bookDTO.getPublishedYear());
                    return book;
                })
                .flatMap(bookRepository::save)
                .map(bookMapper::mapToDTO);
    }

    public Mono<Boolean> delete(UUID id) {
        return bookRepository.deleteById(id);
    }
}
