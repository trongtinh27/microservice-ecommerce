package com.ecommerce.product_service.service.impl;

import com.ecommerce.event.dto.ProductEvent;
import com.ecommerce.product_service.dto.request.DeleteProductRequest;
import com.ecommerce.product_service.dto.request.EditProductRequest;
import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.grpc.ProductItem;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return ProductResponse.builder()
                    .shopId(product.getShopId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .categories(product.getCategories())
                    .images(product.getImages())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .rating(product.getRating())
                    .soldCount(product.getSoldCount())
                    .build();
        }
        return null;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        boolean isExists = productRepository.existsProductByNameAndShopId(request.getName(), request.getShopId());

        if(isExists) {
           throw new RuntimeException("Product is existed!");
        }

        Product product = Product.builder()
                .shopId(request.getShopId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .images(request.getImages())
                .categories(request.getCategories())
                .rating(0.0)
                .soldCount(0)
                .build();
        product = productRepository.save(product);

        sendEvent(product, "CREATE");

        return ProductResponse.builder()
                .id(product.get_id())
                .shopId(product.getShopId())
                .name(product.getName())
                .description(product.getDescription())
                .categories(product.getCategories())
                .images(product.getImages())
                .price(product.getPrice())
                .stock(product.getStock())
                .rating(product.getRating())
                .soldCount(product.getSoldCount())
                .build();
    }

    @Override
    @Transactional
    public ProductResponse editProduct(EditProductRequest request) {
        Query query = new Query(Criteria.where("_id").is(request.getId()).and("shopId").is(request.getShopId()));
        Update update = new Update();

        if (request.getName() != null) update.set("name", request.getName());
        if (request.getDescription() != null) update.set("description", request.getDescription());
        if (request.getCategories() != null) update.set("categories", request.getCategories());
        if (request.getImages() != null) update.set("images", request.getImages());
        if (request.getPrice() != null) update.set("price", request.getPrice());
        if (request.getStock() != 0) update.set("stock", request.getStock());

        Product updatedProduct = mongoTemplate.findAndModify(query, update, Product.class);
        if(updatedProduct != null) {
            sendEvent(updatedProduct, "UPDATE");
            return convertProductToProductResponse(updatedProduct);
        }
        return null;
    }

    @Override
    public void deleteProductForSeller(DeleteProductRequest request) {
        productRepository.deleteBy_idAndShopId(request.getProductId(), request.getShopId());
        sendEvent(Product.builder()._id(request.getProductId()).build(), "DELETE");
    }

    @Override
    public void deleteProductForAdmin(String id) {
        productRepository.deleteById(id);
        sendEvent(Product.builder()._id(id).build(), "DELETE");

    }


    /**
    * MongoDB cung cấp **các thao tác atomics** trên một tài liệu thông qua truy vấn `findAndModify`.
     * Với cách này, bạn có thể:
     * 1. Kiểm tra xem `stock >= 1`.
     * 2. Nếu `stock >= 1`, giảm **`stock`** xuống và trả về sản phẩm đã cập nhật.
     * 3. Nếu `stock < 1`, không thực hiện gì và trả về `null`.
     * Do đây là một lệnh atomic, bạn không cần phải lo lắng về việc nhiều yêu cầu (threads/users) cập nhật cùng lúc,
     * vì MongoDB đảm bảo xử lý tuần tự trên từng thao tác.
    * */
    @Override
    @Transactional
    public boolean decreaseStock(List<ProductItem> items) {
        List<Product> updatedProducts = new ArrayList<>();

        for (ProductItem item : items) {
            Query query = new Query(Criteria.where("_id").is(item.getProductId()).and("stock").gte(item.getQuantity()));
            Update update = new Update().inc("stock", -item.getQuantity());

            Product updatedProduct = mongoTemplate.findAndModify(query, update, Product.class);

            if (updatedProduct == null) {
                // Nếu một sản phẩm không đủ stock, rollback các sản phẩm trước đó
                for (Product prevProduct : updatedProducts) {
                    mongoTemplate.updateFirst(
                            Query.query(Criteria.where("_id").is(prevProduct.get_id())),
                            new Update().inc("stock", prevProduct.getStock()),
                            Product.class
                    );
                }
                return false;
            }

            updatedProducts.add(updatedProduct);
        }

        return true;
    }

    @Override
    @Transactional
    public void increaseStock(List<ProductItem> items) {
        for (ProductItem item : items) {
            Query query = new Query(Criteria.where("_id").is(item.getProductId()).and("stock").gte(item.getQuantity()));
            Update update = new Update().inc("stock", +item.getQuantity());

            mongoTemplate.findAndModify(query, update, Product.class);
        }
    }

    private ProductResponse convertProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .shopId(product.getShopId())
                .name(product.getName())
                .description(product.getDescription())
                .categories(product.getCategories())
                .images(product.getCategories())
                .price(product.getPrice())
                .stock(product.getStock())
                .rating(product.getRating())
                .soldCount(product.getSoldCount())
                .build();
    }

    private void sendEvent(Product product, String operation) {
        kafkaTemplate.send("product-event", ProductEvent.builder()
                .id(product.get_id())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .shopId(product.getShopId())
                .images(product.getImages())
                .tags(product.getCategories())
                .operation(operation)
                .build());
    }
}
