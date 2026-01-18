package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory_Name(String categoryName);


}
