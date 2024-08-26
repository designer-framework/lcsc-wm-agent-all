package com.lcsc.wm.agent.plugin.core.vo;

import com.lcsc.wm.agent.core.vo.MethodInvokeVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MethodInvokeMetrics {

    private final String method;

    private final long invokeCount;

    private final BigDecimal totalCost;

    private final BigDecimal averageCost;

    private final List<MethodInvokeVO> invokeDetails;

    public MethodInvokeMetrics(String method, long invokeCount, BigDecimal totalCost, BigDecimal averageCost, List<MethodInvokeVO> invokeDetails) {
        this.method = method;
        this.invokeCount = invokeCount;
        this.totalCost = totalCost;
        this.averageCost = averageCost;
        this.invokeDetails = invokeDetails;
    }

}
