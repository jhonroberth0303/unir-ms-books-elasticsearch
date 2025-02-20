package org.unir.books.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BookContext {

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private Integer year;
    private Integer score;

}
