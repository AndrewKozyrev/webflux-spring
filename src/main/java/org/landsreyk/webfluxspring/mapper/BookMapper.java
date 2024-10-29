package org.landsreyk.webfluxspring.mapper;

import org.landsreyk.webfluxspring.dto.BookDTO;
import org.landsreyk.webfluxspring.model.Book;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "spring", nullValueCheckStrategy = ALWAYS)
public interface BookMapper {
    Book mapToEntity(BookDTO bookDTO);

    BookDTO mapToDTO(Book book);
}
