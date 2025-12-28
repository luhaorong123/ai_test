// RequestLoggingFilter.java - 请求日志过滤器
package com.item.management.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;

@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Instant startTime = Instant.now();

        // 包装请求和响应以支持多次读取
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // 计算请求处理时间
            Duration duration = Duration.between(startTime, Instant.now());

            // 记录请求日志
            logRequest(wrappedRequest, wrappedResponse, duration);

            // 复制响应内容到原始响应
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request,
                            ContentCachingResponseWrapper response, Duration duration) {

        // 只记录API请求
        if (!request.getRequestURI().startsWith("/api/")) {
            return;
        }

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n=== HTTP请求日志 ===");
        logMessage.append("\n请求: ").append(request.getMethod()).append(" ").append(request.getRequestURI());
        logMessage.append("\n客户端IP: ").append(getClientIp(request));
        logMessage.append("\n用户代理: ").append(request.getHeader("User-Agent"));
        logMessage.append("\n状态码: ").append(response.getStatus());
        logMessage.append("\n处理时间: ").append(duration.toMillis()).append("ms");

        // 记录请求头（敏感信息不记录）
        logMessage.append("\n请求头:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("authorization") &&
                    !headerName.equalsIgnoreCase("cookie")) {
                logMessage.append("\n  ").append(headerName).append(": ").append(request.getHeader(headerName));
            }
        }

        // 记录请求体（非敏感信息）
        String requestBody = getContentAsString(request.getContentAsByteArray(),
                request.getCharacterEncoding());
        if (requestBody != null && !requestBody.isEmpty() && requestBody.length() < 1000) {
            logMessage.append("\n请求体: ").append(requestBody);
        }

        // 记录响应体（非敏感信息）
        String responseBody = getContentAsString(response.getContentAsByteArray(),
                response.getCharacterEncoding());
        if (responseBody != null && !responseBody.isEmpty() && responseBody.length() < 1000) {
            logMessage.append("\n响应体: ").append(responseBody);
        }

        logMessage.append("\n===================");

        log.info(logMessage.toString());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getContentAsString(byte[] content, String charset) {
        if (content == null || content.length == 0) {
            return null;
        }

        try {
            return new String(content, charset != null ? charset : "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "[不可解码的内容]";
        }
    }
}