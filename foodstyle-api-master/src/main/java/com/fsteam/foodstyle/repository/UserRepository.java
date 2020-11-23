package com.fsteam.foodstyle.repository;

import com.fsteam.foodstyle.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findAllByUserType(Integer userType);

    public User findFirstByEmailAndPassword(String email, String password);

    List<User> findAllByUserTypeAndLogintimes(Integer userType, Integer loginTimes);
}
