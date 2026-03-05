package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryName(String categoryName);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Product> searchByNameOrDescription(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) AND LOWER(c.name) = LOWER(:categoryName)")
    Page<Product> searchByNameAndCategory(@Param("q") String query, @Param("categoryName") String categoryName, Pageable pageable);
}
