package com.lcsc.wm.agent.spring;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ReproducibleResourceTransformer;
import org.apache.maven.plugins.shade.resource.properties.SortedProperties;
import org.apache.maven.plugins.shade.resource.properties.io.NoCloseOutputStream;
import org.apache.maven.plugins.shade.resource.properties.io.SkipPropertiesDateLineWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class SpringFactoriesAppendingTransformer implements ReproducibleResourceTransformer {

    private final List<Properties> properties = new ArrayList<>();

    private final String resource = "META-INF/spring.factories";

    private String alreadyMergedKey;

    private String ordinalKey;

    private int defaultOrdinal;

    private boolean reverseOrder;

    private long time = Long.MIN_VALUE;

    private static Properties mergeProperties(List<Properties> sortedProperties) {
        Properties mergedProperties = new SortedProperties();
        for (Properties originProperties : sortedProperties) {

            for (Map.Entry<Object, Object> originProperty : originProperties.entrySet()) {

                mergedProperties.compute(originProperty.getKey(), (existKey, existValue) -> {

                    //首次
                    if (existValue == null) {
                        return originProperty.getValue();
                        //已存在
                    } else {
                        return MessageFormat.format("{0},{1}", existValue, originProperty.getValue());
                    }

                });

            }

        }

        return mergedProperties;
    }

    @Override
    public boolean canTransformResource(String resource) {
        return Objects.equals(resource, this.resource);
    }

    @Override
    public final void processResource(String resource, InputStream is, List<Relocator> relocators)
            throws IOException {
        processResource(resource, is, relocators, 0);
    }

    @Override
    public void processResource(String resource, InputStream is, List<Relocator> relocators, long time) throws IOException {
        Properties p = new Properties();
        p.load(is);
        properties.add(p);
        if (time > this.time) {
            this.time = time;
        }
    }

    @Override
    public boolean hasTransformedResource() {
        return !properties.isEmpty();
    }

    @Override
    public void modifyOutputStream(JarOutputStream os)
            throws IOException {
        if (properties.isEmpty()) {
            return;
        }

        Properties out = mergeProperties(sortProperties());
        if (ordinalKey != null) {
            out.remove(ordinalKey);
        }
        if (alreadyMergedKey != null) {
            out.remove(alreadyMergedKey);
        }
        JarEntry jarEntry = new JarEntry(resource);
        jarEntry.setTime(time);
        os.putNextEntry(jarEntry);
        BufferedWriter writer = new SkipPropertiesDateLineWriter(
                new OutputStreamWriter(new NoCloseOutputStream(os), StandardCharsets.ISO_8859_1));
        out.store(writer, " Merged by maven-shade-plugin (" + getClass().getName() + ")");
        writer.close();
        os.closeEntry();
    }

    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    public void setOrdinalKey(String ordinalKey) {
        this.ordinalKey = ordinalKey;
    }

    public void setDefaultOrdinal(int defaultOrdinal) {
        this.defaultOrdinal = defaultOrdinal;
    }

    public void setAlreadyMergedKey(String alreadyMergedKey) {
        this.alreadyMergedKey = alreadyMergedKey;
    }

    private List<Properties> sortProperties() {
        List<Properties> sortedProperties = new ArrayList<>();
        boolean foundMaster = false;
        for (Properties current : properties) {
            if (alreadyMergedKey != null) {
                String master = current.getProperty(alreadyMergedKey);
                if (Boolean.parseBoolean(master)) {
                    if (foundMaster) {
                        throw new IllegalStateException(
                                "Ambiguous merged values: " + sortedProperties + ", " + current);
                    }
                    foundMaster = true;
                    sortedProperties.clear();
                    sortedProperties.add(current);
                }
            }
            if (!foundMaster) {
                int configOrder = getConfigurationOrdinal(current);

                int i;
                for (i = 0; i < sortedProperties.size(); i++) {
                    int listConfigOrder = getConfigurationOrdinal(sortedProperties.get(i));
                    if ((!reverseOrder && listConfigOrder > configOrder)
                            || (reverseOrder && listConfigOrder < configOrder)) {
                        break;
                    }
                }
                sortedProperties.add(i, current);
            }
        }
        return sortedProperties;
    }

    private int getConfigurationOrdinal(Properties p) {
        if (ordinalKey == null) {
            return defaultOrdinal;
        }
        String configOrderString = p.getProperty(ordinalKey);
        if (configOrderString != null && configOrderString.length() > 0) {
            return Integer.parseInt(configOrderString);
        }
        return defaultOrdinal;
    }

}


