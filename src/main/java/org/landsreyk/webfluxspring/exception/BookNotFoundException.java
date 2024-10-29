package org.landsreyk.webfluxspring.exception;

import java.util.UUID;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String bookId) {
        super("Book with id = [%s] not found".formatted(bookId));
    }

    public BookNotFoundException(UUID bookId) {
        this(bookId.toString());
    }
}
