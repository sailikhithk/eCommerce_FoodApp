package com.fsteam.foodstyle.domain;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userid;

    private Integer state;

    private Instant ordertime;

    private String sessionid;

    private Double totalbill;

    @Transient
    private List<CartDetails> cartDetails = new ArrayList<>();

    public List<CartDetails> getCartDetails() {
        return cartDetails;
    }

    public void setCartDetails(List<CartDetails> cartDetails) {
        this.cartDetails = cartDetails;
    }

    public Double getTotalbill() {
        return totalbill;
    }

    public void setTotalbill(Double totalbill) {
        this.totalbill = totalbill;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Instant getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(Instant ordertime) {
        this.ordertime = ordertime;
    }
}
