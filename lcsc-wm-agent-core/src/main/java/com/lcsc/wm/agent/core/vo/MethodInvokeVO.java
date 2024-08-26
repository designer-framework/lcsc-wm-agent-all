package com.lcsc.wm.agent.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-01 21:05
 */
@Getter
@Setter
public class MethodInvokeVO extends DurationVO {

    private final String methodQualifier;

    private Object[] args;

    public MethodInvokeVO(String methodQualifier, Object[] args) {
        this.methodQualifier = methodQualifier;

        if (args == null) {
            return;
        }

        Object[] argStrList = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null) {
                argStrList[i] = arg.toString();
            }
        }
        this.args = argStrList;
    }

    @Override
    @JSONField(name = "duration")
    public BigDecimal getDuration() {
        return super.getDuration();
    }

}
