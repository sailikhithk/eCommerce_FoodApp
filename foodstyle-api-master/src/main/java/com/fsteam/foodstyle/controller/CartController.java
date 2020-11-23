package com.fsteam.foodstyle.controller;

import com.fsteam.foodstyle.domain.Cart;
import com.fsteam.foodstyle.domain.CartDetails;
import com.fsteam.foodstyle.domain.Food;
import com.fsteam.foodstyle.domain.Restaurant;
import com.fsteam.foodstyle.repository.CartDetailsRepository;
import com.fsteam.foodstyle.repository.CartRepository;
import com.fsteam.foodstyle.repository.FoodRepository;
import com.fsteam.foodstyle.repository.RestaurantRepository;
import com.fsteam.foodstyle.vm.FoodVM;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@Controller
public class CartController {
    private static final String publicKey = "pk_test_51H28gbINIrThnVruHZ9Ia5cGvzUqy99lSrdlrxVCPPMFKNwUm5fGSm7fU9rnGl5YF0LXFT9hwphbSusOQ9jVGK0A00MndJIdz4";
    private static final String apiKey = "sk_test_51H28gbINIrThnVruwPReavMpb4oOjSAaCTRyIyp7WTyhS5QxFBJMlYegUBxOvPr2MpjgDfmk58Eq2avGsB0XOBCc00dIA68pJe";

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartDetailsRepository cartDetailsRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;



    @PostMapping("/carts")
    @ResponseBody
    public Cart createCart(@RequestBody Cart cart){
        return cartRepository.save(cart);
    }

    @PostMapping("/carts/details")
    @ResponseBody
    public Integer createCart(@RequestBody CartDetails cartDetails){
        Optional<Cart> optCart = cartRepository.findFirstByUseridAndState(cartDetails.getUserid(), 0);
        Cart cart = null;
        if (optCart != null && optCart.isPresent()){
            cart = optCart.get();
        }else{
            cart = new Cart();
        }
        cart.setOrdertime(Instant.now());
        cart.setState(0);
        cart.setUserid(cartDetails.getUserid());

        Food food = foodRepository.findById(cartDetails.getFoodid()).get();
        Double totalbill = cart.getTotalbill() == null ? 0d : cart.getTotalbill();
        cart.setTotalbill(totalbill + food.getPrice());
        cart = cartRepository.save(cart);

        List<CartDetails> cartDetailsList = cartDetailsRepository.findByCartid(cart.getId());
        Map<Long, CartDetails> detailsMap = new HashMap<>();
        for (CartDetails cd: cartDetailsList){
            detailsMap.put(cd.getFoodid(), cd);
        }

        if (detailsMap.containsKey(cartDetails.getFoodid())){
            cartDetails = detailsMap.get(cartDetails.getFoodid());
            cartDetails.setQuantity(cartDetails.getQuantity() + 1);

        }else{
            cartDetails.setFoodName(food.getName());
            cartDetails.setDescription(food.getDescription());
            cartDetails.setPhoto(food.getPhoto());
            cartDetails.setPrice(food.getPrice());
            cartDetails.setQuantity(1);
            cartDetails.setCartid(cart.getId());
        }

        CartDetails detail = cartDetailsRepository.save(cartDetails);
        //cartDetailsList.add(detail);
        //cart.setCartDetails(cartDetailsList);
        return cartDetails.getQuantity();
    }

    @GetMapping("/carts/count")
    @ResponseBody
    public Integer getCartsCount(){
        return cartRepository.findAll().size();
    }

    @GetMapping("/carts/{id}")
    @ResponseBody
    public Cart findOneFood(@PathVariable Long id){
        return cartRepository.findById(id).get();
    }

    @GetMapping("/carts/unpaid")
    @ResponseBody
    public Cart findCurentUserUnpaidCart(){
        Optional<Cart> optCart = cartRepository.findFirstByUseridAndState(1L, 0);
        if (optCart != null && optCart.isPresent()){
            Cart cart = optCart.get();
            cart.setCartDetails(cartDetailsRepository.findByCartid(cart.getId()));
            return cart;
        }
        return new Cart();
    }

    @DeleteMapping("/carts/{id}")
    @ResponseBody
    public Long deleteCart(@PathVariable Long id){
        cartRepository.deleteById(id);
        return id;
    }

    @PutMapping("/carts")
    @ResponseBody
    public Cart updateCart(@RequestBody Cart cart){
        if (cart.getId() == null){
            System.out.println("Cart id cannot be null.");
            return null;
        }
        return cartRepository.save(cart);
    }

    @GetMapping("/carts")
    @ResponseBody
    public List<Cart> findAllCarts(){
        return cartRepository.findAll();
    }

    @GetMapping("/carts/details/{id}")
    @ResponseBody
    public List<CartDetails> findByCartId(@PathVariable Long id){
        return cartDetailsRepository.findByCartid(id);
    }

    @PostMapping("/carts/checkout")
    @ResponseBody
    public Map<String, String> checkout(@RequestBody CartDetails cartDetails){
        Long userId = cartDetails.getUserid();
        Optional<Cart> optCart = cartRepository.findFirstByUseridAndState(userId, 0);
        if (optCart != null && optCart.isPresent()){
            Cart cart = optCart.get();
            List<CartDetails> cartDetailsList = cartDetailsRepository.findByCartid(cart.getId());
            //cart.setCartDetails(cartDetailsList);
            String sessionId = this.getCheckoutSession(cartDetailsList);
            cart.setSessionid(sessionId);
            cartRepository.save(cart);
            Map<String, String> responseData = new HashMap<>();

            responseData.put("sessionId", sessionId);
            responseData.put("publicKey", publicKey);
            print(sessionId);
            return responseData;
        }
        return null;
    }

    private String getCheckoutSession(List<CartDetails> cartDetailsList){
        print("getCheckoutSession");
        String domainUrl = "http://localhost:8080";
        String successUrl = domainUrl + "/payment/success.html?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = domainUrl + "/payment/canceled.html";

        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl).addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT);

        // Add a line item for the sticker the Customer is purchasing

        for (CartDetails cartDetails: cartDetailsList) {
            Integer quantity =cartDetails.getQuantity();
            Long foodId = cartDetails.getFoodid();
            if (foodId == null || quantity == null) continue;
            String productId = this.getProductId(cartDetails);
            builder.addLineItem(new SessionCreateParams.LineItem.Builder().setQuantity(quantity.longValue()).setPrice(this.getPriceId(productId, cartDetails.getPrice())).build());
        }

        SessionCreateParams createParams = builder.build();
        Session session = null;
        try {
            session = Session.create(createParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return session.getId();
    }

    private String getProductId(CartDetails cartDetails){
        print("getProductId");
        Stripe.apiKey = apiKey;
        Map<String, Object> product_params = new HashMap<>();
        product_params.put("name", cartDetails.getFoodName());
        product_params.put("images", new String[]{cartDetails.getPhoto()});
        Product product = null;
        try {
            product = Product.create(product_params);
            return product.getId();
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPriceId(String productCode, Double foodPrice){
        print("getPriceId");
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("product", productCode);
            PriceCollection prices = Price.list(params);
            List<Price> price_list = prices.getData();
            int unit_amount = Double.valueOf(foodPrice*100).intValue();
            for (Price price : price_list){
                if (price.getUnitAmount() == unit_amount){
                    return price.getId();
                }
            }
            Map<String, Object> price_params = new HashMap<>();
            price_params.put("unit_amount", unit_amount);
            price_params.put("currency", "usd");
            price_params.put("product", productCode);
            Price price = Price.create(price_params);
            return price.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(path = "/carts/checkout-session/{sessionId}")
    @ResponseBody
    public String checkoutSession(@PathVariable("sessionId") String sessionId){
        Stripe.apiKey = apiKey;
        HashMap<String, Object> createdParams = new HashMap<>();
        createdParams.put("gte", (int) ((System.currentTimeMillis() / 1000) -  10 * 60 * 60));

        HashMap<String, Object> params = new HashMap<>();
        params.put("type", "checkout.session.completed");
        params.put("created", createdParams);

        Iterable<Event> events = null;
        try {
            events = Event.list(params).autoPagingIterable();
        } catch (StripeException e) {

        }
        for (Event event : events) {
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            if (deserializer.getObject().isPresent()) {
                Session session = (Session) deserializer.getObject().get();
                if (session.getId().equals(sessionId)){
                    Optional<Cart> cartOptional = cartRepository.findFirstBySessionid(session.getId());
                    if (cartOptional != null
                            && cartOptional.isPresent()){
                        Cart cart = cartOptional.get();
                        cart.setState(1);
                        List<CartDetails> detailsList = cartDetailsRepository.findByCartid(cart.getId());
                        int restSold = 0;
                        Restaurant restaurant = null;
                        for (CartDetails details: detailsList){
                            Food food = foodRepository.findById(details.getFoodid()).get();
                            int sold = food.getSold() == null ? 0 : food.getSold();
                            food.setSold(sold + details.getQuantity());
                            foodRepository.save(food);
                            if (restaurant == null){
                                restaurant = restaurantRepository.findById(food.getRestaurantid()).get();
                                restSold = restaurant.getSold() == null ? 0 :restaurant.getSold();
                            }
                            restSold += details.getQuantity();
                        }
                        restaurant.setSold(restSold);
                        cartRepository.save(cart);
                        restaurantRepository.save(restaurant);
                        return sessionId;
                    }
                }
            }
        }
        return "";
    }
    private void print(String msg){
        System.out.println(msg);
    }
}
