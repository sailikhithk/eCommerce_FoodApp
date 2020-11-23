package com.fsteam.foodstyle.controller;

import com.fsteam.foodstyle.domain.Restaurant;
import com.fsteam.foodstyle.domain.User;
import com.fsteam.foodstyle.repository.RestaurantRepository;
import com.fsteam.foodstyle.repository.UserRepository;
import com.fsteam.foodstyle.vm.LoginVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/admin")
    public ModelAndView redirect(){
        return new ModelAndView(new RedirectView("/managerpages/index.html"));
    }

    @GetMapping("/managers")
    @ResponseBody
    public List<User> findAllManagers(){
        return userRepository.findAllByUserType(1);
    }

    @PostMapping("/managers")
    @ResponseBody
    public User createManager(@RequestBody User user){
        user.setUserType(1);
        if (user.getRestaurantid() != null) {
            Optional<Restaurant> optRes = restaurantRepository.findById(user.getRestaurantid());
            if (optRes != null && optRes.isPresent()){
                user.setRestaurantname(optRes.get().getName());
            }
        }
        return userRepository.save(user);
    }

    @PutMapping("/managers")
    @ResponseBody
    public User updateManager(@RequestBody User user){
        if (user.getId() == null){
            System.out.println("User id cannot be null.");
            return null;
        }
        if (user.getRestaurantid() != null) {
            Optional<Restaurant> optRes = restaurantRepository.findById(user.getRestaurantid());
            if (optRes != null && optRes.isPresent()){
                user.setRestaurantname(optRes.get().getName());
            }
        }
        user.setUserType(1);
        return userRepository.save(user);
    }

    @GetMapping("/managers/{id}")
    @ResponseBody
    public User createManager(@PathVariable Long id){
        return userRepository.findById(id).get();
    }

    @DeleteMapping("/managers/{id}")
    @ResponseBody
    public Long deleteManager(@PathVariable Long id){
        userRepository.deleteById(id);
        return id;
    }

    @PostMapping("/users-register")
    @ResponseBody
    public User registerUser(@RequestBody User user){
        user.setUserType(2);
        return userRepository.save(user);
    }

    @PostMapping("/users-login")
    @ResponseBody
    public Map<String, Object> userLogin(@RequestBody LoginVM loginVM){
        User user = userRepository.findFirstByEmailAndPassword(loginVM.getEmail(), loginVM.getPassword());
        Map<String, Object> map = new HashMap<>();
        if (user != null){
            map.put("success", 1);
            map.put("userid", user.getId());
            map.put("username", user.getFirstname());
            int loginTimes = user.getLogintimes() == null ? 0 :user.getLogintimes();
            user.setLogintimes(loginTimes + 1);
            userRepository.save(user);
        }else {
            map.put("success", 0);
            map.put("message", "Login failed, please check your email and password!");
        }
        return map;
    }

    @PostMapping("/managers-login")
    @ResponseBody
    public Map<String, Object> managerLogin(@RequestBody LoginVM loginVM){
        Map<String, Object> map = new HashMap<>();
        System.out.println(loginVM.getEmail());
        System.out.println(loginVM.getPassword());
        if (loginVM.getVerifyCode() == null || "".equals(loginVM.getVerifyCode().trim())
                || !loginVM.getVerifyCode().trim().equals("jgmxj")){
            map.put("success", 0);
            map.put("message", "Login failed, please check your verification code!");
            return map;
        }
        User user = userRepository.findFirstByEmailAndPassword(loginVM.getEmail(), loginVM.getPassword());

        if (user != null){
            if (user.getUserType() == 2){
                map.put("success", 0);
                map.put("message", "Login failed, please check your email and password!");
                return map;
            }
            map.put("success", 1);
            map.put("userid", user.getId());
            map.put("usertype", user.getUserType());
            map.put("username", user.getFirstname());
            map.put("restaurantid", user.getRestaurantid() == null ? "" : user.getRestaurantid());
        }else {
            map.put("success", 0);
            map.put("message", "Login failed, please check your email and password!");
        }
        return map;
    }

    @GetMapping("/users/count")
    @ResponseBody
    public Integer getUsersCount(){
        return userRepository.findAllByUserType(2).size();
    }

    @GetMapping("/users/unique")
    @ResponseBody
    public Integer getUniqueVisitorsCount(){
        return userRepository.findAllByUserTypeAndLogintimes(2, 1).size();
    }
}
