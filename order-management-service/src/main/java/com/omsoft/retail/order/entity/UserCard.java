package com.omsoft.retail.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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
}
