package com.omsoft.retail.order.repo;

import com.omsoft.retail.order.entity.ShippingOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingOptionRepository extends JpaRepository<ShippingOption, Long> {
    List<ShippingOption> findAllByOrderByIdAsc();
    Optional<ShippingOption> findByIsDefaultTrue();
}
