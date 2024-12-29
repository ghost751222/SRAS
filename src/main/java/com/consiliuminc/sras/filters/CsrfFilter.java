package com.consiliuminc.sras.filters;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsrfFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().replace(contextPath, Strings.EMPTY);
        if (!IsIgnoreRefererUrl(requestURI) && authentication instanceof UsernamePasswordAuthenticationToken) {
            String referer = req.getHeader("Referer");
            String evnUrl = System.getProperty("env.url");
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            String parameterCSRF = request.getParameter(csrfToken.getParameterName());


            if (referer == null || !referer.contains(evnUrl) || (parameterCSRF!= null && !csrfToken.getToken().equals(parameterCSRF))) {
                res.sendError(HttpStatus.BAD_REQUEST.value());
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean IsIgnoreRefererUrl(String requestUrl) {
        List<String> reqUrls = new ArrayList<>();
        reqUrls.add("/error");
        reqUrls.add("/");
        reqUrls.add("/login");
        return reqUrls.stream().anyMatch(s -> s.equalsIgnoreCase(requestUrl)) || requestUrl.contains("chat");
    }
}
