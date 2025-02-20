package org.unir.books.repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.unir.books.entities.BookElastic;
import org.unir.books.exceptions.DataSorceException;
import org.unir.books.utils.LocalDateTypeAdapter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.unir.books.config.ElasticSearchConfiguration.ELASTICSEARCH_BEAN_NAME;

@Repository
public class BooksElasticsearchRepositoryImpl {

    private final RestHighLevelClient client;

    public BooksElasticsearchRepositoryImpl(@Qualifier(ELASTICSEARCH_BEAN_NAME) RestHighLevelClient client) {
        this.client = client;
    }

    public List<BookElastic> search(String isbn, String title, String author, String genre, Integer year, Integer score) throws DataSorceException {

        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            buildQuery(boolQuery, "isbn", isbn);
            buildQuery(boolQuery, "title", title);
            buildQuery(boolQuery, "author", author);
            buildQuery(boolQuery, "genre", genre);
            buildQuery(boolQuery, "year", year != null? year.toString() : null);
            buildQuery(boolQuery, "score", score != null? score.toString() : null);
            buildQuery(boolQuery, "status", "1");

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQuery);
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            return Arrays.stream(searchResponse.getHits().getHits())
                    .map(hit ->  mapToBookElastic(hit)).collect(Collectors.toList());

        } catch (IOException e) {
            throw new DataSorceException("DataSource error while searching books", e.getCause());
        }

    }

    private void buildQuery(BoolQueryBuilder boolQuery, String key, String value) {
        if(Strings.isNotEmpty(value)){
            boolQuery.must(QueryBuilders.matchQuery(key, value));
        }
    }

    private BookElastic mapToBookElastic(SearchHit hit) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create();
        return gson.fromJson(hit.getSourceAsString(), BookElastic.class);
    }
}
