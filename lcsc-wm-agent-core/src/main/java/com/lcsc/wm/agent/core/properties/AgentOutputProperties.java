package com.lcsc.wm.agent.core.properties;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class AgentOutputProperties implements InitializingBean {

    /**
     * 性能分析jar包所在文件夹
     */
    private File home;

    /**
     * 日志输出路径
     */
    private File outputPath;

    @Override
    public void afterPropertiesSet() throws Exception {
        FileUtils.forceMkdir(home);
        FileUtils.forceMkdir(outputPath);
    }

    public void setHome(File home) {
        this.home = home;
    }

    public void setOutputPath(File outputPath) {
        this.outputPath = outputPath;
    }

}
