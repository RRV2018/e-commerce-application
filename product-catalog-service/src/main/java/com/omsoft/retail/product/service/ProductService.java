package com.omsoft.retail.product.service;


import com.omsoft.retail.product.dto.*;
import com.omsoft.retail.product.entity.Category;
import com.omsoft.retail.product.entity.FileDetails;
import com.omsoft.retail.product.entity.Product;
import com.omsoft.retail.product.entity.UserCard;
import com.omsoft.retail.product.mapper.ProductMapper;
import com.omsoft.retail.product.repo.CategoryRepository;
import com.omsoft.retail.product.repo.FileDetailRepository;
import com.omsoft.retail.product.repo.ProductRepository;
import com.omsoft.retail.product.repo.UserCardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
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
    private final FileDetailRepository fileDetailRepo;
    private final ProductMapper mapper;
    private static final int MAX_PAGE_SIZE = 50;

    public ProductService(ProductRepository productRepo, UserCardRepository cardRepo,
                          CategoryRepository categoryRepo, FileDetailRepository fileDetailRepo,
                          ProductMapper mapper) {
        this.productRepo = productRepo;
        this.cardRepo = cardRepo;
        this.categoryRepo = categoryRepo;
        this.fileDetailRepo = fileDetailRepo;
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

    public Page<Product> searchProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return productRepo.findAll(pageable);
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

    public boolean deleteCardItem(Long cardItemId) {
        if (cardRepo.existsById(cardItemId)) {
            cardRepo.deleteById(cardItemId);
        }
        return false;
    }

    public boolean increaseCardItemQuantity(Long cardItemId) {
        Optional<UserCard> cardItem = cardRepo.findById(cardItemId);
        if (cardItem.isPresent()) {
            UserCard card = cardItem.get();
            card.setQuantity(card.getQuantity()+1);
            cardRepo.save(card);
            return true;
        }
        return false;
    }
    public boolean decreaseCardItemQuantity(Long cardItemId) {
        Optional<UserCard> cardItem = cardRepo.findById(cardItemId);
        if (cardItem.isPresent()) {
            UserCard card = cardItem.get();
            if (card.getQuantity() == 1) {
                deleteCardItem(card.getId());
                return true;
            }
            card.setQuantity(card.getQuantity()-1);
            cardRepo.save(card);
            return true;
        }
        return false;
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

    public List<FileDetails> getFileRecords() {
        return fileDetailRepo.findAll();
    }

    public FileDetails saveFileDetails(String fineName, String type, String status) {
        FileDetails details = new FileDetails();
        details.setFileName(fineName);
        details.setFileType(type);
        details.setFileExtension(getFileExtension(fineName));
        details.setStatus(status);
        return fileDetailRepo.save(details);
    }

    public void updateFileDetails(FileDetails details) {
        fileDetailRepo.save(details);
    }

    public boolean runValidation(FileDetails details) {
        if (StringUtils.isEmpty(details.getFileName())) {
            details.setStatus("INVALID");
            details.setDetail("Invalid: File name is empty/null.");
            updateFileDetails(details);
            return false;
        }
        if (StringUtils.isEmpty(details.getFileExtension()) || !StringUtils.equalsIgnoreCase(details.getFileExtension(), "CSV")) {
            details.setStatus("INVALID");
            details.setDetail("Invalid: The requested file extension does not support to load. allowed only CSV file. actual "+ details.getFileExtension());
            updateFileDetails(details);
            return false;
        }
        if (StringUtils.isEmpty(details.getFileType()) || !StringUtils.equalsIgnoreCase(details.getFileType(), "PRODUCT")) {
            details.setStatus("INVALID");
            details.setDetail("Invalid: Request must be for product type, seems to be invalid type. Type : "+details.getFileType());
            updateFileDetails(details);
            return false;
        }
        return true;
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
