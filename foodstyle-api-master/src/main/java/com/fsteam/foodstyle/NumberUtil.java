package com.fsteam.foodstyle;

import java.math.BigDecimal;

public class NumberUtil {
    public static Double getDecimal(Double value, Integer fixed){
        BigDecimal b = new BigDecimal(value);
        Double newValue = b.setScale(fixed, BigDecimal.ROUND_HALF_UP).doubleValue();
        return newValue;
    }
}
