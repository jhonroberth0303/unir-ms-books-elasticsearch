package org.unir.books.services;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.unir.books.dtos.BookContext;
import org.unir.books.entities.BookElastic;
import org.unir.books.exceptions.DataSorceException;
import org.unir.books.exceptions.ServiceException;
import org.unir.books.repositories.BooksElasticsearchCRUDRepository;
import org.unir.books.repositories.BooksElasticsearchRepository;
import org.unir.books.repositories.BooksElasticsearchRepositoryImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class BooksElasticsearchService {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String HYPHEN = "-";
    private static final String SPACE = " ";

    private final BooksElasticsearchRepository booksElasticsearchRepository;
    private final BooksElasticsearchRepositoryImpl booksElasticsearchRepositoryImpl;
    private final BooksElasticsearchCRUDRepository booksElasticsearchCRUDRepository;

    public BooksElasticsearchService(BooksElasticsearchRepository booksElasticsearchRepository,
                                     BooksElasticsearchRepositoryImpl booksElasticsearchRepositoryImpl,
                                     BooksElasticsearchCRUDRepository booksElasticsearchCRUDRepository) {
        this.booksElasticsearchRepository = booksElasticsearchRepository;
        this.booksElasticsearchRepositoryImpl = booksElasticsearchRepositoryImpl;
        this.booksElasticsearchCRUDRepository = booksElasticsearchCRUDRepository;
    }

    public Optional<BookElastic> bookByIsbn(String isbn) throws ServiceException {
        try {
            Page<BookElastic> bookElasticPage = booksElasticsearchRepository.findByIsbn(isbn, PageRequest.of(PAGE_NUMBER, PAGE_SIZE));

            if(bookElasticPage.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(bookElasticPage.getContent().getFirst());

        } catch (DataSorceException e) {
            throw new ServiceException("Service error while searching booksByIsbn");
        }

    }

    public List<BookElastic> search(BookContext bookContext) throws ServiceException {
        try {
            return booksElasticsearchRepositoryImpl.search(
                    bookContext.getIsbn(),
                    Strings.isNotEmpty(bookContext.getTitle()) ? bookContext.getTitle().replace(HYPHEN, SPACE) : null,
                    Strings.isNotEmpty(bookContext.getAuthor()) ? bookContext.getAuthor().replace(HYPHEN, SPACE) : null,
                    bookContext.getGenre(),
                    bookContext.getYear(),
                    bookContext.getScore()
            );
        } catch (DataSorceException e) {
            throw new ServiceException("Service error while searching books");
        }
    }

    public BookElastic save(BookElastic bookElastic) throws ServiceException {
        try {
            if(Strings.isEmpty(bookElastic.getIsbn())) {
                bookElastic.setIsbn(String.valueOf(getSequence()));
            }
            return booksElasticsearchCRUDRepository.save(bookElastic);
        } catch (Exception e) {
            throw new ServiceException("Service error while saving book");
        }
    }

    public void delete(String isbn) throws ServiceException {
        try {
            booksElasticsearchCRUDRepository.deleteById(isbn);
        } catch (Exception e) {
            throw new ServiceException("Service error while deleting book");
        }
    }

    private Integer getSequence() throws ServiceException {
        try {
            return booksElasticsearchRepository.findBookElasticByOrderByIsbnDesc().stream()
                    .map(BookElastic::getIsbn)
                    .map(Integer::parseInt)
                    .findFirst().orElse(0) + 1;

        } catch (Exception e) {
            throw new ServiceException("Service error while updating book");
        }
    }

}
