package com.fsteam.foodstyle.controller;

import com.fsteam.foodstyle.NumberUtil;
import com.fsteam.foodstyle.domain.Restaurant;
import com.fsteam.foodstyle.domain.User;
import com.fsteam.foodstyle.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fsteam.foodstyle.domain.Review; // Added
import com.fsteam.foodstyle.repository.ReviewRepository; // Added

import java.time.Instant;
import java.util.List;

@Controller
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepository; // Added
    @Autowired
    private RestaurantRepository restaurantRepository;

    @PostMapping("/post-review")
    @ResponseBody
    public Review postReview(@RequestBody Review rev){
        rev.setReviewtime(Instant.now());
        Review review = reviewRepository.save(rev);

        Restaurant restaurant = restaurantRepository.findById(rev.getRestaurantid()).get();
        restaurant.setRating(NumberUtil.getDecimal(reviewRepository.findAvgRatingByRestaurantId(rev.getRestaurantid()), 1));
        restaurantRepository.save(restaurant);
        return review;
    }

    @GetMapping("/reviews/restaurant/{rid}")
    @ResponseBody
    public List<Review> findByRestaurantId(@PathVariable Long rid){
        return reviewRepository.findAllByRestaurantidOrderByReviewtimeDesc(rid);
    }

    @GetMapping("/reviews/count")
    @ResponseBody
    public Integer getReviewsCount(){
        return restaurantRepository.findAll().size();
    }
}
