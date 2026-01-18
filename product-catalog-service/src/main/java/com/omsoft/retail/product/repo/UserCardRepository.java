package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.UserCard;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    @Query("SELECT o FROM UserCard o WHERE o.userId = :userId")
    List<UserCard> findUserOrders(@Param("userId") Long userId);

    @Query("SELECT o FROM UserCard o WHERE o.userId = :userId AND o.productId = :productId")
    UserCard findUserOrder(@Param("userId") Long userId,
                                  @Param("productId") Long productId);
}
