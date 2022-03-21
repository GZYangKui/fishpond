package cn.navclub.fishpond.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumUtil {
    public static double lDiv(long a, long b, int scale, RoundingMode mode) {
        return BigDecimal
                .valueOf(a)
                .setScale(scale, mode)
                .divide(BigDecimal.valueOf(b), mode)
                .doubleValue();
    }
}
