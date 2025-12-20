package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.dto.PageResponse;
import com.omsoft.retail.product.dto.ProductRequest;
import com.omsoft.retail.product.dto.ProductResponse;
import com.omsoft.retail.product.service.ProductService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ProductResponse create(@RequestBody @Valid ProductRequest dto) {
        return service.create(dto);
    }

    @GetMapping("/filter")
    public PageResponse<ProductResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {

        return service.getProducts(page, size, sort, dir);
    }
}

