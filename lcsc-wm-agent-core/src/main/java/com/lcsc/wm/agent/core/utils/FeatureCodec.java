package com.lcsc.wm.agent.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static com.lcsc.wm.agent.core.utils.AgentCheckUtils.isEquals;
import static com.lcsc.wm.agent.core.utils.AgentCheckUtils.isIn;

/**
 * Feature编解器(线程安全)<br/>
 * <p/>
 * 用于封装系统内部features/attribute等扩展字段的管理
 * Created by dukun on 15/3/31.
 */
public class FeatureCodec {

    // 对象的编码解码器
    public final static FeatureCodec DEFAULT_COMMANDLINE_CODEC = new FeatureCodec(';', '=');

    /**
     * 转义前缀符
     */
    private static final char ESCAPE_PREFIX_CHAR = '\\';

    /**
     * KV片段分割符<br/>
     * KV片段定义为一个完整的KV对，例如字符串<span>;k1=v1;k2=v2;</span>
     * 其中<b>;</b>即为KV片段分隔符
     */
    private final char kvSegmentSeparator;

    /**
     * KV分割符<br/>
     * KV定义为一个KV对区分K和V的分割符号，例如字符串<span>k1=v1</span>
     * 其中<b>=</b>即为KV分隔符
     */
    private final char kvSeparator;


    /**
     * 使用指定的KV分割符构造FeatureParser<br/>
     *
     * @param kvSegmentSeparator KV对之间的分隔符
     * @param kvSeparator        K与V之间的分隔符
     */
    public FeatureCodec(char kvSegmentSeparator, char kvSeparator) {

        // 分隔符禁止与转义前缀符相等
        if (isIn(ESCAPE_PREFIX_CHAR, kvSegmentSeparator, kvSeparator)) {
            throw new IllegalArgumentException("separator can not init to '" + ESCAPE_PREFIX_CHAR + "'.");
        }

        this.kvSegmentSeparator = kvSegmentSeparator;
        this.kvSeparator = kvSeparator;
    }

    /**
     * map集合转换到feature字符串
     *
     * @param map map集合
     * @return feature字符串
     */
    public String toString(Map<String, String> map) {

        StringBuilder featureSB = new StringBuilder().append(kvSegmentSeparator);

        if (null == map
                || map.isEmpty()) {
            return featureSB.toString();
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {

            featureSB
                    .append(escapeEncode(entry.getKey()))
                    .append(kvSeparator)
                    .append(escapeEncode(entry.getValue()))
                    .append(kvSegmentSeparator)
            ;

        }

        return featureSB.toString();
    }


    /**
     * feature字符串转换到map集合
     *
     * @param featureString the feature string
     * @return the map
     */
    public Map<String, String> toMap(String featureString) {

        Map<String, String> map = new HashMap<String, String>();

        if (StringUtils.isBlank(featureString)) {
            return map;
        }

        for (String kv : escapeSplit(featureString, kvSegmentSeparator)) {

            if (StringUtils.isBlank(kv)) {
                // 过滤掉为空的字符串片段
                continue;
            }

            String[] ar = escapeSplit(kv, kvSeparator);
            if (ar.length != 2) {
                // 过滤掉不符合K:V单目的情况
                continue;
            }

            String k = ar[0];
            String v = ar[1];
            if (!StringUtils.isBlank(k)
                    && !StringUtils.isBlank(v)) {
                map.put(escapeDecode(k), escapeDecode(v));
            }

        }

        return map;
    }

    /**
     * 转义编码
     *
     * @param string 原始字符串
     * @return 转义编码后的字符串
     */
    private String escapeEncode(String string) {
        StringBuilder returnSB = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (isIn(c, kvSegmentSeparator, kvSeparator, ESCAPE_PREFIX_CHAR)) {
                returnSB.append(ESCAPE_PREFIX_CHAR);
            }
            returnSB.append(c);
        }

        return returnSB.toString();
    }

    /**
     * 转义解码
     *
     * @param string 编码字符串
     * @return 转义解码后的字符串
     */
    private String escapeDecode(String string) {

        StringBuilder segmentSB = new StringBuilder();
        int stringLength = string.length();

        for (int index = 0; index < stringLength; index++) {

            char c = string.charAt(index);

            if (isEquals(c, ESCAPE_PREFIX_CHAR)
                    && index < stringLength - 1) {

                char nextChar = string.charAt(++index);

                // 下一个字符是转义符
                if (isIn(nextChar, kvSegmentSeparator, kvSeparator, ESCAPE_PREFIX_CHAR)) {
                    segmentSB.append(nextChar);
                }

                // 如果不是转义字符，则需要两个都放入
                else {
                    segmentSB.append(c);
                    segmentSB.append(nextChar);
                }
            } else {
                segmentSB.append(c);
            }

        }

        return segmentSB.toString();
    }

    /**
     * 编码字符串拆分
     *
     * @param string          编码字符串
     * @param splitEscapeChar 分割符
     * @return 拆分后的字符串数组
     */
    private String[] escapeSplit(String string, char splitEscapeChar) {

        ArrayList<String> segmentArrayList = new ArrayList<String>();
        Stack<Character> decodeStack = new Stack<Character>();
        int stringLength = string.length();

        for (int index = 0; index < stringLength; index++) {

            boolean isArchive = false;

            char c = string.charAt(index);

            // 匹配到转义前缀符
            if (isEquals(c, ESCAPE_PREFIX_CHAR)) {

                decodeStack.push(c);
                if (index < stringLength - 1) {
                    char nextChar = string.charAt(++index);
                    decodeStack.push(nextChar);
                }

            }

            // 匹配到分割符
            else if (isEquals(c, splitEscapeChar)) {
                isArchive = true;
            }

            // 匹配到其他字符
            else {
                decodeStack.push(c);
            }

            if (isArchive
                    || index == stringLength - 1) {
                StringBuilder segmentSB = new StringBuilder(decodeStack.size());
                while (!decodeStack.isEmpty()) {
                    segmentSB.append(decodeStack.pop());
                }

                segmentArrayList.add(
                        segmentSB
                                .reverse()  // 因为堆栈中是逆序的,所以需要对逆序的字符串再次逆序
                                .toString() // toString
                                .trim()     // 考虑到字符串片段可能会出现首尾空格的场景，这里做一个过滤
                );
            }

        }

        return segmentArrayList.toArray(new String[0]);
    }


}
