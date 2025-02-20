package org.unir.books.controllers;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unir.books.dtos.BookContext;
import org.unir.books.entities.BookElastic;
import org.unir.books.exceptions.ProcessorException;
import org.unir.books.exceptions.UNIRException;
import org.unir.books.processor.BooksElasticsearchProcessor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("v1/")
public class BooksElasticSearchController {

    private final BooksElasticsearchProcessor booksElasticsearchProcessor;

    public BooksElasticSearchController(BooksElasticsearchProcessor booksElasticsearchProcessor) {
        this.booksElasticsearchProcessor = booksElasticsearchProcessor;
    }

    @GetMapping("books/search")
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity search(@RequestParam(value = "isbn", required = false) String isbn,
                                    @RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "author", required = false) String author,
                                    @RequestParam(value = "genre", required = false) String genre,
                                    @RequestParam(value = "year", required = false) Integer year,
                                    @RequestParam(value = "score", required = false) Integer score) {
        BookContext bookContext = new BookContext(isbn, title, author, genre, year, score);

        try {

            List<BookElastic> bookElastics = booksElasticsearchProcessor.search(bookContext);

            if(Objects.isNull(bookElastics) || bookElastics.isEmpty()) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity(bookElastics, HttpStatus.OK);

        } catch (ProcessorException e) {
            throw new UNIRException("There was an exception searching books");
        }

    }

    @GetMapping("books/{isbn}")
    public ResponseEntity getBookByIsbn(@PathVariable String isbn) {

        try {
            Optional<BookElastic> bookElastic  = booksElasticsearchProcessor.bookByIsbn(isbn);

            return bookElastic.map(elastic -> new ResponseEntity(elastic, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));

        } catch (Exception ex) {
            throw new UNIRException("There was an exception getting the book by isbn");
        }

    }

    @PostMapping("books")
    public ResponseEntity saveBook(@RequestBody BookElastic bookElastic) {

        try {
            bookElastic.setIsbn(null);
            bookElastic.setCreateAt(LocalDate.now());
            bookElastic.setStatus(1);
            BookElastic bookElasticSaved = booksElasticsearchProcessor.save(bookElastic);

            return new ResponseEntity(bookElasticSaved, HttpStatus.CREATED);

        } catch (ProcessorException e) {
            throw new UNIRException("There was an exception saving the book");
        }

    }

    @PutMapping("books/{isbn}")
    public ResponseEntity updateBook(@PathVariable String isbn, @RequestBody BookElastic bookElastic) {

        try {
            bookElastic.setIsbn(isbn);
            BookElastic bookElasticSaved = booksElasticsearchProcessor.save(bookElastic);

            return new ResponseEntity(bookElasticSaved, HttpStatus.OK);

        } catch (ProcessorException e) {
            throw new UNIRException("There was an exception updating the book");
        }

    }

    @DeleteMapping("books/{isbn}")
    public ResponseEntity deleteBook(@PathVariable String isbn) {

        try {
            booksElasticsearchProcessor.delete(isbn);

            return new ResponseEntity(HttpStatus.NO_CONTENT);

        } catch (ProcessorException e) {
            throw new UNIRException("There was an exception deleting the book");
        }

    }

}
