package com.lcsc.wm.agent.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.lcsc.wm.agent.core.vo.DurationVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeanLifeCycleDuration extends DurationVO {

    private String stepName;

    private BeanLifeCycleDuration(String stepName) {
        this.stepName = stepName;
    }

    /**
     * @param stepName
     * @param durationVO Bean创建及创建完成的时间
     * @return
     */
    public static BeanLifeCycleDuration create(String stepName, DurationVO durationVO) {
        BeanLifeCycleDuration beanLifeCycleDuration = new BeanLifeCycleDuration(stepName);
        beanLifeCycleDuration.copyDuration(durationVO);
        return beanLifeCycleDuration;
    }

    @Override
    @JSONField(name = "duration")
    public BigDecimal getDuration() {
        return super.getDuration();
    }

}
