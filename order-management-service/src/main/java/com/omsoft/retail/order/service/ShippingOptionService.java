package com.omsoft.retail.order.service;

import com.omsoft.retail.order.dto.ShippingOptionResponse;
import com.omsoft.retail.order.entity.ShippingOption;
import com.omsoft.retail.order.repo.ShippingOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingOptionService {

    private final ShippingOptionRepository shippingOptionRepository;

    public List<ShippingOptionResponse> getAll() {
        return shippingOptionRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ShippingOptionResponse toResponse(ShippingOption o) {
        return new ShippingOptionResponse(
                o.getId(),
                o.getName(),
                o.getCost(),
                o.getEstimatedDays(),
                Boolean.TRUE.equals(o.getIsDefault())
        );
    }
}
