package com.fsteam.foodstyle.controller;

import com.fsteam.foodstyle.domain.Restaurant;
import com.fsteam.foodstyle.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RestaurantController {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @PostMapping("/restaurants")
    @ResponseBody
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant){
        return restaurantRepository.save(restaurant);
    }

    @GetMapping("/restaurants/{id}")
    @ResponseBody
    public Restaurant findOneRestaurant(@PathVariable Long id){
        return restaurantRepository.findById(id).get();
    }

    @DeleteMapping("/restaurants/{id}")
    @ResponseBody
    public Long deleteRestaurant(@PathVariable Long id){
        restaurantRepository.deleteById(id);
        return id;
    }

    @PutMapping("/restaurants")
    @ResponseBody
    public Restaurant updateRestaurant(@RequestBody Restaurant restaurant){
        if (restaurant.getId() == null){
            System.out.println("Restaurant id cannot be null.");
            return null;
        }
        if (restaurant.getAvgprice() == null){
            restaurant.setAvgprice(restaurantRepository.findById(restaurant.getId()).get().getAvgprice());
        }
        return restaurantRepository.save(restaurant);
    }

    @GetMapping("/restaurants")
    @ResponseBody
    public List<Restaurant> findAllRestaurants(){
        return restaurantRepository.findAll().stream().map(restaurant -> {
            if (restaurant.getAvgprice()==null){
                restaurant.setAvgprice(0D);
            }
            return restaurant;
        }).collect(Collectors.toList());
    }

    @GetMapping("/restaurants/category/{category}")
    @ResponseBody
    public List<Restaurant> findAllRestaurantsByCategory(@PathVariable Integer category){
        return restaurantRepository.findAllByCategory(category).stream().map(restaurant -> {
            if (restaurant.getAvgprice()==null){
                restaurant.setAvgprice(0D);
            }
            return restaurant;
        }).collect(Collectors.toList());
    }

    private List<Restaurant> getRestaurent(Integer category, String name){
        return restaurantRepository.findAllByCategoryAndNameLike(1, "%" + name + "%").stream().map(restaurant -> {
            if (restaurant.getAvgprice()==null){
                restaurant.setAvgprice(0D);
            }
            return restaurant;
        }).collect(Collectors.toList());
    }

    @GetMapping("/restaurants/1/{name}")
    @ResponseBody
    public List<Restaurant> findAmerianRestaurantsByNameLike(@PathVariable String name){
        return this.getRestaurent(1, name);
    }

    @GetMapping("/restaurants/2/{name}")
    @ResponseBody
    public List<Restaurant> findChineseRestaurantsByNameLike(@PathVariable String name){
        return this.getRestaurent(2, name);
    }

    @GetMapping("/restaurants/3/{name}")
    @ResponseBody
    public List<Restaurant> findIndianRestaurantsByNameLike(@PathVariable String name){
        return this.getRestaurent(3, name);
    }

    @GetMapping("/restaurants/top")
    @ResponseBody
    public List<Restaurant> findTop5(){
        return restaurantRepository.findTop5ByNameLikeOrderBySoldDesc("%%");
    }
}
