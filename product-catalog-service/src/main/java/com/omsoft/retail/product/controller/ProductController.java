package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.dto.*;
import com.omsoft.retail.product.entity.Category;
import com.omsoft.retail.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return service.getProduct(id);
    }

    @PostMapping("/category")
    public CategoryResponse createCategory(@RequestBody @Valid CategoryRequest dto) {
        return service.createCategory(dto);
    }

    @GetMapping("/filter")
    public PageResponse<ProductResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {

        return service.getProducts(page, size, sort, dir);
    }

    @PostMapping
    public ProductResponse create(@RequestBody @Valid ProductRequest dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = service.deleteProductById(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = service.updateProduct(id, productRequest);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

}

