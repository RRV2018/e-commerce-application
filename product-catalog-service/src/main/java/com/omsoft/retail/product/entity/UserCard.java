package com.omsoft.retail.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_card")
@Getter
@Setter
public class UserCard extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "amount")
    private BigDecimal amount;

    @Transient // This field is not persisted
    private String productName;

    @Transient // This field is not persisted
    private BigDecimal productPrice;
}
