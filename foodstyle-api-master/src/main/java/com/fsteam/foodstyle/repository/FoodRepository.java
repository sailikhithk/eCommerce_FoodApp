package com.fsteam.foodstyle.repository;

import com.fsteam.foodstyle.domain.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    public List<Food> findAllByRestaurantid(Long restaurantid);
    public Food findByName(String name);

    List<Food> findAllByRestaurantidAndNameLike(Long restaurantid, String name);

    @Query(nativeQuery = true, value = "SELECT  avg(price) from food where restaurantid=:rid")
    Double findAvgPriceByRestaurantId(@Param("rid") Long rid);

    List<Food> findTop5ByRestaurantidOrderBySoldDesc(Long restaurantid);

    List<Food> findTop5ByNameLikeOrderBySoldDesc(String name);
}
