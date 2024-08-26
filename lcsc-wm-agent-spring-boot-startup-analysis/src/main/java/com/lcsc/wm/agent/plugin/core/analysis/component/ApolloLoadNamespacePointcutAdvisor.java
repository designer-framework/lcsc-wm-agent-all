package com.lcsc.wm.agent.plugin.core.analysis.component;

import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import com.lcsc.wm.agent.framework.vo.ClassMethodInfo;
import com.lcsc.wm.agent.framework.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ApolloLoadNamespacePointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor {

    /**
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    public ApolloLoadNamespacePointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[0]);
    }

}
