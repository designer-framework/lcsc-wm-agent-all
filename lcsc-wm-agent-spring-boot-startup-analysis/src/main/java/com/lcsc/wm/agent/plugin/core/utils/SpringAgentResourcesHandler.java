package com.lcsc.wm.agent.plugin.core.utils;

import com.lcsc.wm.agent.core.properties.AgentOutputProperties;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-16 23:40
 */
@Slf4j
public class SpringAgentResourcesHandler implements BeanClassLoaderAware {

    private final AgentOutputProperties agentOutputProperties;

    @Setter
    private ClassLoader beanClassLoader;

    public SpringAgentResourcesHandler(AgentOutputProperties agentOutputProperties) {
        this.agentOutputProperties = agentOutputProperties;
    }

    @SneakyThrows
    public File getOutputFile(String fileName) {
        File outputFile = new File(agentOutputProperties.getOutputPath(), fileName);
        if (!outputFile.exists()) {
            FileUtils.touch(outputFile);
        }
        return outputFile;
    }

    @SneakyThrows
    public String readOutputResourrceToString(String fileName) {
        try (InputStream inputStream = Files.newInputStream(new File(agentOutputProperties.getOutputPath(), fileName).toPath())) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    public String resourrceToString(String fileName) {
        try (InputStream inputStream = new ClassPathResource(fileName, beanClassLoader).getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

}
