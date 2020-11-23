package com.fsteam.foodstyle.repository;

import com.fsteam.foodstyle.domain.Cart;
import com.fsteam.foodstyle.domain.CartDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartDetailsRepository extends JpaRepository<CartDetails, Long> {
    List<CartDetails> findAll();

    List<CartDetails> findByCartid(Long cid);
}
