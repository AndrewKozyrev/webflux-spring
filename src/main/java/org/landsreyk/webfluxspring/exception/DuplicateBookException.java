package org.landsreyk.webfluxspring.exception;

import org.landsreyk.webfluxspring.model.Book;

public class DuplicateBookException extends RuntimeException {

    public DuplicateBookException(String message) {
        super(message);
    }

    public DuplicateBookException(Book book) {
        this("A book [%s] already exists.".formatted(book));
    }
}
