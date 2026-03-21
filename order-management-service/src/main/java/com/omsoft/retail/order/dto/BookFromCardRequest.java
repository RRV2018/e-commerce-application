package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.PaymentMethod;

/**
 * Optional body for checkout-from-cart. Null or missing {@code paymentMethod} defaults to {@link PaymentMethod#ONLINE}.
 */
public record BookFromCardRequest(PaymentMethod paymentMethod) {}
