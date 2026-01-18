package com.omsoft.retail.product.service;


import com.omsoft.retail.product.dto.*;
import com.omsoft.retail.product.entity.Category;
import com.omsoft.retail.product.entity.Product;
import com.omsoft.retail.product.entity.UserCard;
import com.omsoft.retail.product.mapper.ProductMapper;
import com.omsoft.retail.product.repo.CategoryRepository;
import com.omsoft.retail.product.repo.ProductRepository;
import com.omsoft.retail.product.repo.UserCardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final UserCardRepository cardRepo;
    private final CategoryRepository categoryRepo;
    private final ProductMapper mapper;
    private static final int MAX_PAGE_SIZE = 50;

    public ProductService(ProductRepository productRepo, UserCardRepository cardRepo,
                          CategoryRepository categoryRepo,
                          ProductMapper mapper) {
        this.productRepo = productRepo;
        this.cardRepo = cardRepo;
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

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.name());
        Category cat = categoryRepo.save(category);
        return new CategoryResponse(cat.getId(), cat.getName());
    }

    public boolean deleteProductById(Long id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<UserCard> getUserCardData(String userId) {
        return cardRepo.findUserOrders(userId);
    }

    public boolean addProductToCard(String userId, Long productId) {
        UserCard card = cardRepo.findUserOrder(userId, productId);
        Optional<Product> product = productRepo.findById(productId);
        if (product.isEmpty()) {
            return false;
        }
        if (card != null) {
            card.setQuantity(card.getQuantity() + 1);
            card.setAmount(card.getAmount().add(product.get().getPrice()));
            cardRepo.save(card);
            return true;
        } else {
            UserCard newCard = new UserCard();
            newCard.setProductId(productId);
            newCard.setUserId(userId);
            newCard.setQuantity(1L);
            newCard.setAmount(product.get().getPrice());
            cardRepo.save(newCard);
            return true;
        }
    }

        public ProductResponse updateProduct(Long id, ProductRequest dto) {
        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        Product product = mapper.toEntity(dto, category);

        Product response = productRepo.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setStock(product.getStock());
                    existingProduct.setCategory(product.getCategory());
                    return productRepo.save(existingProduct);
                })
                .orElse(null);
        assert response != null;
        return mapper.toDto(response);

    }
}
