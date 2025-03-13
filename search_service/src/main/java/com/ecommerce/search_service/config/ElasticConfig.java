package com.ecommerce.search_service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

@Configuration
public class ElasticConfig {

    @Value("${app.elastic_host}")
    private String host;
    @Value("${app.elastic_api}")
    private String apiKey;

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        RestClientBuilder builder = RestClient.builder(HttpHost.create(host))
                .setDefaultHeaders(new BasicHeader[]{
                        new BasicHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + apiKey)
                });

        RestClient restClient = builder.build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }

}
