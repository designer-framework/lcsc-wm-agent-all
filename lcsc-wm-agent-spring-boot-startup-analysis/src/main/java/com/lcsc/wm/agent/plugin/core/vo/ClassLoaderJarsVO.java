package com.lcsc.wm.agent.plugin.core.vo;

import lombok.Data;

import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-24 16:40
 */
@Data
public class ClassLoaderJarsVO {

    /**
     * 类加载器名称
     */
    private String name;

    private Set<ClassLoaderJarVO> children;

    private int allJarCount;

    private int loadedJarCount;

    private int unloadedJarCount;

    public ClassLoaderJarsVO(String name, Set<ClassLoaderJarVO> children, int unloadedJarCount, int allJarCount, int loadedJarCount) {
        this.name = name;
        this.children = children;
        this.unloadedJarCount = unloadedJarCount;
        this.allJarCount = allJarCount;
        this.loadedJarCount = loadedJarCount;
    }

    @Data
    public static class ClassLoaderJarVO {

        private String name;

        public ClassLoaderJarVO(String name) {
            this.name = name;
        }

    }

}
