package org.landsreyk.webfluxspring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private UUID id;

    private String title;

    private String author;

    private int publishedYear;

    public Book(String title, String author, int publishedYear) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
    }
}