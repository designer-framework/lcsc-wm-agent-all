package com.lcsc.wm.agent.core.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-27 00:47
 */
public class DurationUtils {

    public static BigDecimal nowMillis() {
        return toMillis(System.nanoTime());
    }

    public static BigDecimal toMillis(long nanoDuration) {
        return BigDecimal.valueOf(nanoDuration).divide(BigDecimal.valueOf(1000_000L), 3, RoundingMode.HALF_UP);
    }

}
