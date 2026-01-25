package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.FileDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDetailRepository  extends JpaRepository<FileDetails, Long> {
}
