package com.fsteam.foodstyle.repository;

import com.fsteam.foodstyle.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findAllByCategory(Integer category);

    List<Restaurant> findAllByCategoryAndNameLike(Integer category, String name);

    List<Restaurant> findTop5ByNameLikeOrderBySoldDesc(String name);
}
