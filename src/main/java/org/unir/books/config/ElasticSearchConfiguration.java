package org.unir.books.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.unir.books.repositories")
public class ElasticSearchConfiguration extends AbstractElasticsearchConfiguration {

    public static final String ELASTICSEARCH_BEAN_NAME = "ElasticsearchBeanName";

    @Value("${elasticsearch.endpoint}")
    private String clusterEndpoint;

    @Value("${elasticsearch.credentials.user}")
    private String username;

    @Value("${elasticsearch.credentials.password}")
    private String password;

    @Bean(name = ELASTICSEARCH_BEAN_NAME)
    public RestHighLevelClient elasticsearchClient() {

        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(clusterEndpoint, 443, "https"))
                        .setHttpClientConfigCallback(
                                httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                        .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));

    }
}