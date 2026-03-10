package com.omsoft.retail.product.event;

import com.omsoft.retail.product.entity.Product;
import com.omsoft.retail.product.repo.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class OrderEventConsumer {

    private final ProductRepository productRepo;

    public OrderEventConsumer(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

   // @KafkaListener(topics = "order-event", groupId = "ecommerce-group")
    public void getOrderEvent(OrderEvent event) {
        log.info("Received order event... Detail {}", event);
        event.getProducts().forEach( prod -> {
            Optional<Product> product = productRepo.findById(prod.getProductId());
            if (product.isPresent()) {
                Product p = product.get();
                if (OrderStatus.PAID == event.getType()) {
                    p.setStock(p.getStock() - prod.getQuantity());
                } else if (OrderStatus.CANCELLED == event.getType()) {
                    p.setStock(p.getStock() + prod.getQuantity());
                } else {
                    log.info("Type == {}", event.getType());
                }
                productRepo.save(p);
            }
        });

    }
}
