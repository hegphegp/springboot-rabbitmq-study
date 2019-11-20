package com.hegp.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 给request配置用户浏览器访问路径，然后动态设置项目的css和js的全路径
 */
@Component
public class BasePathConfigFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String basePath = "";
        String contextPath = request.getContextPath();
        String scheme = StringUtils.hasText(request.getScheme())? request.getScheme():"http";
        // 如果用了 nginx作为请求入口，一定要配置
        /**
         *         proxy_set_header X-Real-IP $remote_addr;
         *         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
         *         proxy_set_header X-Forwarded-Proto $scheme;
         *         proxy_set_header Host $http_host;
         *         proxy_set_header X-Forwarded-Uri $uri;
         */
        String xForwardedUri = request.getHeader("x-forwarded-uri");
        String xForwardedHost = request.getHeader("x-forwarded-host");
        String requestURI = request.getRequestURI();
        if (StringUtils.hasText(xForwardedUri)) {
            if (StringUtils.hasText(xForwardedHost) && StringUtils.hasText(requestURI) && requestURI.equals("/") == false) {
                int index = xForwardedUri.lastIndexOf(requestURI);
                basePath = index>-1 ? scheme+"://"+xForwardedHost+xForwardedUri.substring(0, index) : basePath;
            }
        } else {
            basePath = StringUtils.hasText(xForwardedHost) ? scheme+"://"+xForwardedHost+request.getHeader("x-forwarded-prefix") : basePath;
        }
        basePath += StringUtils.hasText(contextPath)? contextPath:"";
        String host = request.getHeader("Host");
        if (StringUtils.hasText(basePath)) {
            request.setAttribute("basePath", basePath);
        } else if (StringUtils.hasText(host)) {
            request.setAttribute("basePath", scheme+"://"+host);
        }

        filterChain.doFilter(request, response);
    }
}
