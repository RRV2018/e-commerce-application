package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
