package com.fsteam.foodstyle.repository;

import com.fsteam.foodstyle.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByRestaurantidOrderByReviewtimeDesc(Long restaurantid);

    @Query(nativeQuery = true, value = "SELECT avg(rating) from review where restaurantid=:rid")
    Double findAvgRatingByRestaurantId(@Param("rid") Long rid);
}
