package org.unir.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.unir.books.entities.BookElastic;
import org.unir.books.exceptions.DataSorceException;

import java.util.List;

@EnableElasticsearchRepositories
public interface BooksElasticsearchRepository extends ElasticsearchRepository<BookElastic, String> {

    Page<BookElastic> findByIsbn(String name, Pageable pageable) throws DataSorceException;

    BookElastic save(BookElastic bookElastic) throws DataSorceException;

    List<BookElastic> findBookElasticByOrderByIsbnDesc() throws DataSorceException;

}
