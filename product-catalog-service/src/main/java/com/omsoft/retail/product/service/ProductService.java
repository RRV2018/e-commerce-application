package com.omsoft.retail.product.service;


import com.omsoft.retail.product.dto.PageResponse;
import com.omsoft.retail.product.dto.ProductRequest;
import com.omsoft.retail.product.dto.ProductResponse;
import com.omsoft.retail.product.entity.Category;
import com.omsoft.retail.product.entity.Product;
import com.omsoft.retail.product.mapper.ProductMapper;
import com.omsoft.retail.product.repo.CategoryRepository;
import com.omsoft.retail.product.repo.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductMapper mapper;
    private static final int MAX_PAGE_SIZE = 50;

    public ProductService(ProductRepository productRepo,
                          CategoryRepository categoryRepo,
                          ProductMapper mapper) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.mapper = mapper;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepo.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        return mapper.toDto(
                productRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found"))
        );
    }

    public ProductResponse create(ProductRequest dto) {

        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = mapper.toEntity(dto, category);

        return mapper.toDto(productRepo.save(product));
    }

    public PageResponse<ProductResponse> getProducts(
            int page, int size, String sortBy, String direction) {

        size = Math.min(size, MAX_PAGE_SIZE);

        Sort sort = Sort.by(
                Sort.Direction.fromString(direction),
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepo.findAll(pageable);

        List<ProductResponse> products = productPage.getContent()
                .stream()
                .map(mapper::toDto)
                .toList();

        return new PageResponse<>(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }
}
