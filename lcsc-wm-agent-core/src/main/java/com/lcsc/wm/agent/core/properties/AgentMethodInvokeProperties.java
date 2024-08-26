package com.lcsc.wm.agent.core.properties;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class AgentMethodInvokeProperties {

    private List<MethodInvokeAdvisorProperties> advisors;

}
