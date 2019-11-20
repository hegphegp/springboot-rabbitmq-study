package com.hegp.views;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/** 每个页面的URL单独配置一个@GetMapping接口，显得很傻逼，用通配符的方式映射 */
@Controller
public class ViewController {

    /**
     * 一个星号通配符只能匹配一层URL，两个星号通配符可以匹配多层URL
     * @param request
     * @return
     */
    @GetMapping(value={"/views/**"})
    public ModelAndView page(HttpServletRequest request) {
//        getCurrentIpAddress();
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith(".html")) {
            requestURI = requestURI.substring(0, requestURI.length()-5);
        } else if (requestURI.endsWith(".htm")) {
            requestURI = requestURI.substring(0, requestURI.length()-4);
        }
        ModelAndView view = new ModelAndView(requestURI);
        return view;
    }

    // 经过转发后，获取不到请求路径
    public String getCurrentIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        System.out.println(request.getRequestURI());
//        System.out.println(request.getRequestURL().toString());
//        System.out.println();
//        System.out.println("x-forwarded-uri  ===>>>  "+request.getHeader("x-forwarded-uri"));
//        System.out.println("x-real-ip  ===>>>  "+request.getHeader("x-real-ip"));
//        System.out.println("x-forwarded-host  ===>>>  "+request.getHeader("x-forwarded-host"));
//        System.out.println("x-forwarded-proto  ===>>>  "+request.getHeader("x-forwarded-proto"));
//        System.out.println("x-forwarded-prefix  ===>>>  "+request.getHeader("x-forwarded-prefix"));
//        System.out.println("x-forwarded-port  ===>>>  "+request.getHeader("x-forwarded-port"));
//        System.out.println("x-forwarded-for  ===>>>  "+request.getHeader("x-forwarded-for"));
        System.out.println(request.getScheme());
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            System.out.println(headerName+"  ===>>>  "+request.getHeader(headerName));
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }
}
