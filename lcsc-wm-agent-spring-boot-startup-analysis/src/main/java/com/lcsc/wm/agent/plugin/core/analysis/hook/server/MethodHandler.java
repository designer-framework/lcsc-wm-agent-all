package com.lcsc.wm.agent.plugin.core.analysis.hook.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:29
 */
@Data
public class MethodHandler implements Handler {

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CACHE_CONTROL = "Cache-Control";

    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    private Object bean;

    private Method method;

    private String contentType;

    public MethodHandler(Object bean, Method method, String contentType) {
        this.bean = bean;
        this.method = method;
        this.contentType = contentType;
    }

    @Override
    public void handler(HttpExchange exchange) throws Exception {
        String result = handleResult(exchange);

        if (result == null) {
            sendResponse(exchange, 200, "{}");
        } else {
            sendResponse(exchange, 200, result);
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(CONTENT_TYPE, contentType);
        responseHeaders.add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        responseHeaders.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        responseHeaders.add(CACHE_CONTROL, "private, max-age=180");

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bodyBytes.length);
        exchange.getResponseBody().write(bodyBytes);
    }

    private String handleResult(HttpExchange exchange) throws Exception {
        //
        Object result = method.invoke(bean, resolveMethodArgs(exchange));
        if (result == null) {
            return null;
        } else if (String.class == method.getReturnType()) {
            return (String) result;
        } else {

            if (result instanceof String) {
                return (String) result;
            } else {
                return JSON.toJSONString(result);
            }
        }
    }

    private Object[] resolveMethodArgs(HttpExchange exchange) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return new Object[0];
        }

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (HttpExchange.class.isAssignableFrom(parameterType)) {
                args[i] = exchange;
            } else {
                args[i] = resolveMethodArg(exchange, parameterType);
            }
        }
        return args;
    }

    private <T> T resolveMethodArg(HttpExchange exchange, Class<T> parameterType) {
        return new JSONObject(getParam(exchange.getRequestURI())).toJavaObject(parameterType);
    }

    private Map<String, Object> getParam(URI requestURI) {
        if (StringUtils.isEmpty(requestURI.getQuery())) {
            return Collections.emptyMap();
        }

        String[] params = StringUtils.split(requestURI.getQuery(), "&");
        if (params.length == 0) {
            return Collections.emptyMap();
        }

        return Arrays.stream(params)
                .map(param -> StringUtils.split(param, "="))
                .filter(strings -> strings.length > 1)
                .collect(Collectors.toMap(
                        string -> StringUtils.stripToNull(string[0])
                        , string -> StringUtils.stripToNull(string[1])
                ));
    }

}
