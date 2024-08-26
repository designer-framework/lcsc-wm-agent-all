package com.lcsc.wm.agent.core.hook;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

class FlameGraphUtil {

    private static final byte FRAME_JIT_COMPILED = 0;

    private static final byte FRAME_NATIVE = 1;

    private final Frame root = new Frame(FRAME_NATIVE);

    private int depth;

    private final String[] highlightPackage;

    public FlameGraphUtil(String[] highlightPackage) {
        this.highlightPackage = highlightPackage;
    }

    /**
     * @param tail       html源码
     * @param outputFile
     * @param traceMap
     * @throws IOException
     */
    @SneakyThrows
    public void parse(String tail, File outputFile, Map<String, Integer> traceMap) {

        for (Map.Entry<String, Integer> entry : traceMap.entrySet()) {
            String[] trace = entry.getKey().split(";");
            addSample(trace, entry.getValue());
        }

        dump(tail, outputFile);

    }

    private void addSample(String[] trace, long ticks) {

        Frame frame = root;
        for (String s : trace) {
            frame = frame.addChild(s, ticks, highlightPackage);
        }
        frame.addLeaf(ticks);

        depth = Math.max(depth, trace.length);
    }

    private void dump(String tail, File outputFile) throws IOException {

        Path destPath = outputFile.toPath();
        Files.deleteIfExists(destPath);
        Files.createDirectories(destPath.getParent());
        Files.createFile(destPath);

        try (
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(destPath), 32768);
                PrintStream out = new PrintStream(bos, false, "UTF-8")
        ) {
            dump(tail, out);
        }
    }

    private void dump(String tail, PrintStream out) {
        int depth = this.depth + 1;

        tail = printTill(out, tail, "/*height:*/300");
        out.print(Math.min(depth * 16, 32767));

        tail = printTill(out, tail, "/*title:*/");
        out.print("Wall clock profile");

        tail = printTill(out, tail, "/*reverse:*/false");
        out.print(false);

        tail = printTill(out, tail, "/*depth:*/0");
        out.print(depth);

        tail = printTill(out, tail, "/*frames:*/");

        printFrame(out, "all", root, 0, 0);

        tail = printTill(out, tail, "/*highlight:*/");
        out.print("");

        out.print(tail);
    }

    private String printTill(PrintStream out, String data, String till) {
        int index = data.indexOf(till);
        out.print(data.substring(0, index));
        return data.substring(index + till.length());
    }

    private void printFrame(PrintStream out, String title, Frame frame, int level, long x) {
        int type = frame.getType();

        if ((frame.inlined | frame.c1 | frame.interpreted) != 0 && frame.inlined < frame.total && frame.interpreted < frame.total) {
            out.println("f(" + level + "," + x + "," + frame.total + "," + type + ",'" + escape(title) + "'," +
                    frame.inlined + "," + frame.c1 + "," + frame.interpreted + ")");
        } else {
            out.println("f(" + level + "," + x + "," + frame.total + "," + type + ",'" + escape(title) + "')");
        }

        x += frame.self;
        for (Map.Entry<String, Frame> e : frame.entrySet()) {
            Frame child = e.getValue();
            if (child.total >= 0) {
                printFrame(out, e.getKey(), child, level + 1, x);
            }
            x += child.total;
        }
    }

    private String escape(String s) {
        if (s.indexOf('\\') >= 0) {
            s = s.replace("\\", "\\\\");
        }
        if (s.indexOf('\'') >= 0) {
            s = s.replace("'", "\\'");
        }
        return s;
    }

    static class Frame extends TreeMap<String, Frame> {

        final byte type;

        long total;

        long self;

        long inlined, c1, interpreted;

        Frame(byte type) {
            this.type = type;
        }

        byte getType() {
            return type;
        }

        private Frame getChild(String title, byte type) {
            Frame child = super.get(title);
            if (child == null) {
                super.put(title, child = new Frame(type));
            }
            return child;
        }

        Frame addChild(String title, long ticks, String[] highlightPackage) {
            total += ticks;

            Frame child;

            if (StringUtils.containsAny(title, highlightPackage)) {
                child = getChild(title, FRAME_NATIVE);
            } else {
                child = getChild(title, FRAME_JIT_COMPILED);
            }

            return child;
        }

        void addLeaf(long ticks) {
            total += ticks;
            self += ticks;
        }

    }

}
