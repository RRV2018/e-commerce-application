package com.omsoft.retail.product.mapper;

import com.omsoft.retail.product.dto.CategoryResponse;
import com.omsoft.retail.product.dto.ProductRequest;
import com.omsoft.retail.product.dto.ProductResponse;
import com.omsoft.retail.product.entity.Category;
import com.omsoft.retail.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest dto, Category category) {
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setCategory(category);
        return product;
    }

    public ProductResponse toDto(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                toCategoryDto(product.getCategory())
        );
    }

    private CategoryResponse toCategoryDto(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
