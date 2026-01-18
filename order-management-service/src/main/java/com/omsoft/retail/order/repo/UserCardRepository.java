package com.omsoft.retail.order.repo;

import com.omsoft.retail.order.entity.UserCard;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    @Query("SELECT u FROM UserCard u " +
            "WHERE u.userId = :userId")
    List<UserCard> findUserOrders(@Param("userId") String userId);

    @Query("SELECT o FROM UserCard o WHERE o.userId = :userId AND o.productId = :productId")
    UserCard findUserOrder(@Param("userId") String userId,
                                  @Param("productId") Long productId);
}
