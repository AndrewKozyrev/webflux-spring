package org.landsreyk.webfluxspring.service;

import lombok.RequiredArgsConstructor;
import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.exception.BookNotFoundException;
import org.landsreyk.webfluxspring.exception.DuplicateBookException;
import org.landsreyk.webfluxspring.mapper.BookMapper;
import org.landsreyk.webfluxspring.model.Book;
import org.landsreyk.webfluxspring.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Mono<BookDTO> create(BookDTO bookDTO) {
        Book book = bookMapper.mapToEntity(bookDTO);
        Mono<Book> mono = validateAndCreateBook(book);
        return mono.map(bookMapper::mapToDTO);
    }

    public Flux<BookDTO> getAll(long page, long size) {
        Flux<Book> flux = bookRepository.findAll(page, size);
        return flux.map(bookMapper::mapToDTO);
    }

    public Mono<BookDTO> getById(UUID id) {
        return bookRepository.findById(id)
                .retry(3)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .onErrorResume(e -> Mono.error(new RuntimeException("Error while fetching a book with id = " + id, e)))
                .map(bookMapper::mapToDTO);
    }

    public Mono<BookDTO> update(UUID id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
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

    /**
     * Retrieves the total count of books in the repository.
     *
     * @return Mono<Long> representing the total number of books.
     */
    public Mono<Long> countBooks() {
        return bookRepository.countBooks();
    }


    /**
     * Validates and creates a new book if it does not already exist in the system.
     *
     * @param book The book data to create.
     * @return Mono<Book> of the created book if validation passes; emits error if duplicate is found.
     * @throws DuplicateBookException if a book with the same title and author exists.
     */
    private Mono<Book> validateAndCreateBook(Book book) {
        return bookRepository.findAll().any(x -> x.getTitle().equals(book.getTitle()) && x.getAuthor().equals(book.getAuthor()))
                .flatMap(isFound -> {
                    if (!isFound) {
                        return bookRepository.save(book);
                    } else {
                        return Mono.error(new DuplicateBookException(book));
                    }
                });
    }

    /**
     * Streams all books as a continuous Flux of BookDTOs.
     *
     * @return Flux<BookDTO> that emits each book with a delay of 1 second.
     */
    public Flux<BookDTO> streamAllBooks() {
        return bookRepository.findAll()
                .map(bookMapper::mapToDTO)
                .delayElements(Duration.ofSeconds(1));
    }
}
