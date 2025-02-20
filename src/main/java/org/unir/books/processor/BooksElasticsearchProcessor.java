package org.unir.books.processor;

import org.springframework.stereotype.Component;
import org.unir.books.dtos.BookContext;
import org.unir.books.entities.BookElastic;
import org.unir.books.exceptions.ProcessorException;
import org.unir.books.exceptions.ServiceException;
import org.unir.books.services.BooksElasticsearchService;

import java.util.List;
import java.util.Optional;

@Component
public class BooksElasticsearchProcessor {

    private final BooksElasticsearchService booksElasticsearchService;

    public BooksElasticsearchProcessor(BooksElasticsearchService booksElasticsearchService) {
        this.booksElasticsearchService = booksElasticsearchService;
    }

    public Optional<BookElastic> bookByIsbn(String isbn) throws ProcessorException {
        try {
            return booksElasticsearchService.bookByIsbn(isbn);
        } catch (ServiceException e) {
            throw new ProcessorException("Processor error while searching booksByIsbn");
        }
    }

    public List<BookElastic> search(BookContext bookContext) throws ProcessorException {
        try {
            return booksElasticsearchService.search(bookContext);
        } catch (ServiceException e) {
            throw new ProcessorException("Processor error while searching books");
        }
    }

    public BookElastic save(BookElastic bookElastic) throws ProcessorException {
        try {
            return booksElasticsearchService.save(bookElastic);
        } catch (ServiceException e) {
            throw new ProcessorException("Processor error while saving book");
        }
    }

    public void delete(String isbn) throws ProcessorException {
        try {
            booksElasticsearchService.delete(isbn);
        } catch (ServiceException e) {
            throw new ProcessorException("Processor error while deleting book");
        }
    }

}
