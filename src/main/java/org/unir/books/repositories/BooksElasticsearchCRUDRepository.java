package org.unir.books.repositories;

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.CrudRepository;
import org.unir.books.entities.BookElastic;

@EnableElasticsearchRepositories
public interface BooksElasticsearchCRUDRepository extends CrudRepository<BookElastic, String> {

}
