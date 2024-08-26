package com.lcsc.wm.agent.plugin.core.analysis.hook.server;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:29
 */
public interface HandlerMapping {

    /**
     * 匹配API接口
     *
     * @param uri
     * @return
     */
    Handler getHandler(String uri);

}

