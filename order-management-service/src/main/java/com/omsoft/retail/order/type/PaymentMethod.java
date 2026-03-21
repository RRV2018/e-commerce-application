package com.omsoft.retail.order.type;

/**
 * How the customer pays. {@link #ONLINE} triggers immediate payment; {@link #CASH_ON_DELIVERY} skips payment and leaves the order in CREATED until cash is collected.
 */
public enum PaymentMethod {
    ONLINE,
    CASH_ON_DELIVERY
}
