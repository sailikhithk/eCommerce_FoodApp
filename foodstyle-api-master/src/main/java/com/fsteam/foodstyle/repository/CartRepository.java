package com.fsteam.foodstyle.repository;

import com.fsteam.foodstyle.domain.Cart;
import com.fsteam.foodstyle.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAll();

    Optional<Cart> findFirstByUseridAndState(Long uid, Integer state);

    Optional<Cart> findFirstBySessionid(String sessionId);
}
