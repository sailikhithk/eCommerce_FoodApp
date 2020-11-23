package com.fsteam.foodstyle.controller;

import com.fsteam.foodstyle.NumberUtil;
import com.fsteam.foodstyle.domain.Food;
import com.fsteam.foodstyle.domain.Restaurant;
import com.fsteam.foodstyle.domain.User;
import com.fsteam.foodstyle.repository.RestaurantRepository;
import com.fsteam.foodstyle.vm.FoodVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fsteam.foodstyle.domain.Food; // Added
import com.fsteam.foodstyle.repository.FoodRepository; // Added

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FoodController {
    @Autowired
    private FoodRepository foodRepository; // Added
    @Autowired
    private RestaurantRepository restaurantRepository;

    // Add function to retrieve food data
    @GetMapping("/food-list")
    @ResponseBody
    public List<Food> findAllFood(){
        return foodRepository.findAllByRestaurantid(1L);
    }

    @PostMapping("/food")
    @ResponseBody
    public Food createFood(@RequestBody Food food){
        if (food.getRestaurantid() == null){
            food.setRestaurantid(1L);
        }
        food.setRestaurantid(food.getRestaurantid());
        Food f = foodRepository.save(food);
        Double avgPrice = foodRepository.findAvgPriceByRestaurantId(food.getRestaurantid());
        Restaurant restaurant = restaurantRepository.findById(food.getRestaurantid()).get();
        restaurant.setAvgprice(NumberUtil.getDecimal(avgPrice,2));
        restaurantRepository.save(restaurant);
        return f;
    }

    @GetMapping("/food/{id}")
    @ResponseBody
    public Food findOneFood(@PathVariable Long id){
        return foodRepository.findById(id).get();
    }

    @DeleteMapping("/food/{id}")
    @ResponseBody
    public Long deleteFood(@PathVariable Long id){
        foodRepository.deleteById(id);
        return id;
    }

    @PutMapping("/food")
    @ResponseBody
    public Food updateFood(@RequestBody Food food){
        if (food.getId() == null){
            System.out.println("Food id cannot be null.");
            return null;
        }
        if (food.getRestaurantid() == null){
            food.setRestaurantid(1L);
        }
        Food f = foodRepository.save(food);
        Double avgPrice = foodRepository.findAvgPriceByRestaurantId(food.getRestaurantid());
        Restaurant restaurant = restaurantRepository.findById(food.getRestaurantid()).get();
        restaurant.setAvgprice(NumberUtil.getDecimal(avgPrice,2));
        restaurantRepository.save(restaurant);
        return f;
    }

    @GetMapping("/food")
    @ResponseBody
    public List<Food> findAllFoods(){
        return foodRepository.findAll();
    }

    @PostMapping("/food/search")
    @ResponseBody
    public List<Food> findFoodsByRestaurantAndNameLike(@RequestBody FoodVM foodVM){
        return foodRepository.findAllByRestaurantidAndNameLike(foodVM.getRestaurantId(),"%" + foodVM.getFoodName() + "%");
    }

    @GetMapping("/food/restaurant/{id}")
    @ResponseBody
    public List<Food> findAllFoodsByRestaurantId(@PathVariable Long id){
        return foodRepository.findAllByRestaurantid(id);
    }

    @GetMapping("/food/top/{restaurantId}")
    @ResponseBody
    public List<Food> findTop5FoodsByRestaurantId(@PathVariable Long restaurantId){
        return foodRepository.findTop5ByRestaurantidOrderBySoldDesc(restaurantId);
    }

    @GetMapping("/food/top")
    @ResponseBody
    public List<Food> findTop5Foods(){
        return foodRepository.findTop5ByNameLikeOrderBySoldDesc("%%");
    }
}
