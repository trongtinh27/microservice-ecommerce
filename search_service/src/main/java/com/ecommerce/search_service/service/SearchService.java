package com.ecommerce.search_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.event.dto.ShopEvent;
import com.ecommerce.search_service.dto.response.SearchResponseDto;
import com.ecommerce.search_service.model.SearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchClient client;

    public SearchResponseDto search(String query, String sortField, String sortOrder, int pageNumber, int pageSize) {
        try {
            log.info("Executing search with query: '{}' | sortField: '{}' | sortOrder: '{}' | pageNumber: {} | pageSize: {}", query, sortField, sortOrder, pageNumber, pageSize);
            // 🔹 Tạo truy vấn tìm kiếm sản phẩm
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("products")
                    .query(q -> q
                            .bool(b -> b.must(m -> m
                                    .multiMatch(mm -> mm
                                            .fields(List.of("name", "description"))
                                            .query(query)
                                    )
                            ))
                    )
                    .from(pageNumber * pageSize)
                    .size(pageSize)
                    .sort(sort -> {
                        if (sortField != null) {
                            sort.field(f -> f
                                    .field(sortField + ".keyword")
                                    .order("desc".equalsIgnoreCase(sortOrder) ? SortOrder.Desc : SortOrder.Asc));
                        }
                        return sort;
                    })
            );

            SearchResponse<SearchDocument> response = client.search(searchRequest, SearchDocument.class);

            List<SearchDocument> documents = response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
            log.info("Found {} results", documents.size());
            List<SearchResponseDto.Product> products = new ArrayList<>();
            Map<String, Long> shopFrequency = new HashMap<>();
            List<SearchResponseDto.Shop> shops = new ArrayList<>();

            for (SearchDocument doc : documents) {
                if ("product".equalsIgnoreCase(doc.getType())) {
                    products.add(SearchResponseDto.Product.builder()
                            .id(doc.getId())
                            .name(doc.getName())
                            .shopId(doc.getShopId())
                            .description(doc.getDescription())
                            .price(doc.getPrice())
                            .images(doc.getImages())
                            .categories(doc.getCategories())
                            .build());

                    // Đếm số lần xuất hiện của shopId
                    if (doc.getShopId() != null) {
                        shopFrequency.put(doc.getShopId(), shopFrequency.getOrDefault(doc.getShopId(), 0L) + 1);
                    }
                }
                shops.add(SearchResponseDto.Shop.builder()
                        .id(doc.getId())
                                .name(doc.getName())
                                .description(doc.getDescription())
                                .location(doc.getLocation())
                                .rating(doc.getRating() != null ? doc.getRating() : 0.0)
                        .build());
            }

            // 🔹 Lấy danh sách shopId phổ biến nhất (tối đa 5 shop)
            List<String> topShopIds = shopFrequency.entrySet().stream()
                    .sorted((a, b) -> Long.compare(b.getValue(), a.getValue())) // Sắp xếp giảm dần theo số sản phẩm
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .toList();

            // 🔹 Query lại Elasticsearch để lấy thông tin chi tiết của shop từ shopId
            if (!topShopIds.isEmpty()) {
                SearchRequest shopSearchRequest = SearchRequest.of(s -> s
                        .index("products") // Chỉ định index "shops"
                        .query(q -> q
                                .terms(t -> t
                                        .field("id.keyword") // Trường chứa shopId
                                        .terms(tt -> tt.value(topShopIds.stream()
                                                .map(FieldValue::of)
                                                .toList()))
                                )
                        )
                );

                SearchResponse<SearchDocument> shopResponse = client.search(shopSearchRequest, SearchDocument.class);

                shops = shopResponse.hits().hits().stream()
                        .map(Hit::source).filter(Objects::nonNull)
                        .map(doc -> SearchResponseDto.Shop.builder()
                                .id(doc.getId())
                                .name(doc.getName())
                                .description(doc.getDescription())
                                .location(doc.getLocation())
                                .rating(doc.getRating() != null ? doc.getRating() : 0.0)
                                .build())
                        .toList();
            }

            assert response.hits().total() != null;
            int totalPages = (int) Math.ceil((double) response.hits().total().value() / pageSize);

            return SearchResponseDto.builder()
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .totalPages(totalPages)
                    .shops(shops)
                    .products(products)
                    .build();

        } catch (IOException e) {
            log.error("Error executing search", e);
            throw new RuntimeException("Error searching in Elasticsearch", e);
        }
    }


    public void delete(String id) {
        try {
            log.info("Deleting document with ID: {}", id);
            client.delete(DeleteRequest.of(d -> d.index("products").id(id)));
        } catch (IOException e) {
            log.error("Error deleting document with ID: {}", id, e);
        }
    }


    public void productEvent(ProductEvent productEvent) {
        try {
            log.info("Indexing product event: {}", productEvent);
            IndexResponse response = client.index(IndexRequest.of(i -> i.index("products").id(productEvent.getId()).document(mapToSearchDocument(productEvent))));
            log.info("Product indexed with ID: {}", response.id());
        } catch (IOException e) {
            log.error("Error indexing product event: {}", productEvent, e);
        }
    }

    public void shopEvent(ShopEvent shopEvent) {
        try {
            log.info("Indexing shop event: {}", shopEvent);
            IndexResponse response = client.index(IndexRequest.of(i -> i.index("products").id(shopEvent.getId()).document(mapToSearchDocument(shopEvent))));
            log.info("Shop indexed with ID: {}", response.id());
        } catch (IOException e) {
            log.error("Error indexing shop event: {}", shopEvent, e);
        }
    }

    private SearchDocument mapToSearchDocument(ProductEvent event) {
        return new SearchDocument(event.getId(), "product", event.getName(), event.getDescription(), event.getPrice(), event.getShopId(), event.getImages(), event.getTags());
    }

    private SearchDocument mapToSearchDocument(ShopEvent event) {
        return new SearchDocument(event.getId(), "shop", event.getName(), event.getDescription(), null, null, null, null, event.getLocation(), event.getRating());
    }

}
