//package com.ecommerce.api_gateway.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@Slf4j
//public class AuthenticationRequestInterceptor implements RequestInterceptor {
//    @Override
//    public void apply(RequestTemplate template) {
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        var token = servletRequestAttributes.getRequest().getHeader("Authorization");
//
//        log.info("Token: {}", token);
//
//        if(StringUtils.hasText(token)) {
//            template.header("Authorization", token);
//        }
//
//    }
//}
