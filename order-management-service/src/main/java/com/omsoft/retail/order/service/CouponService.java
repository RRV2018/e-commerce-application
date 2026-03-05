package com.omsoft.retail.order.service;

import com.omsoft.retail.order.dto.CouponValidationResponse;
import com.omsoft.retail.order.entity.Coupon;
import com.omsoft.retail.order.repo.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponValidationResponse validate(String code, BigDecimal orderSubtotal) {
        if (code == null || code.isBlank()) {
            return new CouponValidationResponse(false, "No coupon code", BigDecimal.ZERO, null);
        }
        Optional<Coupon> opt = couponRepository.findByCodeIgnoreCase(code.trim());
        if (opt.isEmpty()) {
            return new CouponValidationResponse(false, "Invalid coupon code", BigDecimal.ZERO, code);
        }
        Coupon c = opt.get();
        LocalDateTime now = LocalDateTime.now();
        if (c.getValidFrom() != null && now.isBefore(c.getValidFrom())) {
            return new CouponValidationResponse(false, "Coupon not yet valid", BigDecimal.ZERO, code);
        }
        if (c.getValidTo() != null && now.isAfter(c.getValidTo())) {
            return new CouponValidationResponse(false, "Coupon expired", BigDecimal.ZERO, code);
        }
        if (c.getMaxUses() != null && c.getUsedCount() != null && c.getUsedCount() >= c.getMaxUses()) {
            return new CouponValidationResponse(false, "Coupon usage limit reached", BigDecimal.ZERO, code);
        }
        if (c.getMinOrderAmount() != null && orderSubtotal.compareTo(c.getMinOrderAmount()) < 0) {
            return new CouponValidationResponse(false, "Minimum order amount not met", BigDecimal.ZERO, code);
        }
        BigDecimal discount = computeDiscount(c, orderSubtotal);
        return new CouponValidationResponse(true, "Valid", discount, code);
    }

    public BigDecimal computeDiscount(Coupon coupon, BigDecimal orderSubtotal) {
        if ("PERCENT".equalsIgnoreCase(coupon.getType())) {
            return orderSubtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        if ("FIXED".equalsIgnoreCase(coupon.getType())) {
            return coupon.getValue().min(orderSubtotal);
        }
        return BigDecimal.ZERO;
    }

    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCodeIgnoreCase(code);
    }

    public void incrementUsedCount(Coupon coupon) {
        coupon.setUsedCount(coupon.getUsedCount() == null ? 1 : coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }
}
