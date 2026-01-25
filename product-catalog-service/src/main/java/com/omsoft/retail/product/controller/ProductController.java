package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.dto.*;
import com.omsoft.retail.product.entity.UserCard;
import com.omsoft.retail.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "Product APIs",
        description = "APIs for managing products, categories and user cart"
)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ===================== GET ALL PRODUCTS =====================
    @Operation(
            summary = "Get all products",
            description = "Returns all available products",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ProductResponse.class)
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public List<ProductResponse> getAllProduct() {
        return service.getAllProducts();
    }

    // ===================== GET PRODUCT BY ID =====================
    @Operation(
            summary = "Get product by ID",
            description = "Returns product details for given product ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return service.getProduct(id);
    }

    // ===================== CREATE CATEGORY =====================
    @Operation(
            summary = "Create category",
            description = "Creates a new product category",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product category created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid category request")
            }
    )
    @PostMapping("/category")
    public CategoryResponse createProductCategory(@RequestBody @Valid CategoryRequest dto) {
        return service.createCategory(dto);
    }
    // ===================== FILTER / PAGINATE PRODUCTS =====================
    @Operation(
            summary = "Filter products with pagination",
            description = "Returns paginated and sorted products",
            parameters = {
                    @Parameter(name = "page", example = "0"),
                    @Parameter(name = "size", example = "10"),
                    @Parameter(name = "sort", example = "id"),
                    @Parameter(name = "dir", example = "asc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/filter")
    public PageResponse<ProductResponse> getFilterProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir) {

        return service.getProducts(page, size, sort, dir);
    }

    // ===================== CREATE PRODUCT =====================
    @Operation(
            summary = "Create product",
            description = "Creates a new product",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid product data")
            }
    )
    @PostMapping
    public ProductResponse createProduct(@RequestBody @Valid ProductRequest dto) {
        return service.create(dto);
    }
    // ===================== GET USER CART =====================
    @Operation(
            summary = "Get user cart products",
            description = "Returns all products added to user's cart",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cart fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserCard.class)
                                    )
                            )
                    )
            }
    )
    @GetMapping("/card")
    public List<UserCard> getUserCardProducts(@RequestHeader("X-User-Id") String userId) {
        return service.getUserCardData(userId);
    }

    // ===================== ADD PRODUCT TO CART =====================
    @Operation(
            summary = "Add product to cart",
            description = "Adds a product to user's cart",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product added to cart"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @PostMapping("/card/{id}")
    public ResponseEntity<Void> addProductToCard(@RequestHeader("X-User-Id") String userId, @PathVariable("id") Long id) {
        boolean added = service.addProductToCard(userId, id);
        if (added) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    // ===================== DELETE CART ITEM =====================
    @Operation(
            summary = "Delete cart item",
            description = "Removes item from user's cart",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Item removed from cart"),
                    @ApiResponse(responseCode = "404", description = "Cart item not found")
            }
    )
    @DeleteMapping("/card/{cardItemId}")
    public ResponseEntity<Void> deleteItemFromCard(@PathVariable("cardItemId") Long cardItemId) {
        boolean added = service.deleteCardItem(cardItemId);
        if (added) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // ===================== INCREASE QUANTITY =====================
    @Operation(
            summary = "Increase cart item quantity",
            description = "Increases quantity of cart item by 1",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Quantity increased"),
                    @ApiResponse(responseCode = "404", description = "Cart item not found")
            }
    )
    @PostMapping("/card/{cardItemId}/increase")
    public ResponseEntity<Void> increaseItemQuantity(@PathVariable("cardItemId") Long cardItemId) {
        boolean increased = service.increaseCardItemQuantity(cardItemId);
        if (increased) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    // ===================== DECREASE QUANTITY =====================
    @Operation(
            summary = "Decrease cart item quantity",
            description = "Decreases quantity of cart item by 1",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Quantity decreased"),
                    @ApiResponse(responseCode = "404", description = "Cart item not found")
            }
    )
    @PostMapping("/card/{cardItemId}/decrease")
    public ResponseEntity<Void> decreaseItemQuantity(@PathVariable("cardItemId") Long cardItemId) {
        boolean decreased = service.decreaseCardItemQuantity(cardItemId);
        if (decreased) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // ===================== DELETE PRODUCT =====================
    @Operation(
            summary = "Delete product",
            description = "Deletes product by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = service.deleteProductById(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // ===================== UPDATE PRODUCT =====================
    @Operation(
            summary = "Update product",
            description = "Updates product details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )
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

