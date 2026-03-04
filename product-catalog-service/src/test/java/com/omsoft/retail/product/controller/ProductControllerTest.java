package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.dto.ProductResponse;
import com.omsoft.retail.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
    }

    @Test
    void getAllProduct_returnsListFromService() {
        List<ProductResponse> list = Collections.emptyList();
        when(productService.getAllProducts()).thenReturn(list);

        List<ProductResponse> result = productController.getAllProduct();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductById_returnsProductFromService() {
        ProductResponse response = new ProductResponse(
                1L, "Product A", "desc", BigDecimal.TEN, 10, null
        );
        when(productService.getProduct(1L)).thenReturn(response);

        ProductResponse result = productController.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Product A", result.name());
    }
}
