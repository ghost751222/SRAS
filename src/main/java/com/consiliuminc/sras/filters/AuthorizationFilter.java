package com.consiliuminc.sras.filters;

import com.consiliuminc.sras.controllers.Error2Controller;
import com.consiliuminc.sras.entities.sqlserver.Function;
import com.consiliuminc.sras.entities.sqlserver.Menu;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
public class AuthorizationFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        boolean isAjax = Error2Controller.isAjax(req);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().replace(contextPath, Strings.EMPTY);

        if (IsExistInController(req) && !isAjax && !IsIgnoreRequestUrl(requestURI) && authentication instanceof UsernamePasswordAuthenticationToken) {
            HttpSession session = req.getSession(false);
            Map<Menu, List<Function>> menuListMap = (Map<Menu, List<Function>>) session.getAttribute("usermenu");
            if (menuListMap != null) {
                Boolean isPermission = false;
                for (Map.Entry<Menu, List<Function>> entry : menuListMap.entrySet()) {
                    List<Function> functions = entry.getValue();
                    isPermission = functions.stream().anyMatch(function -> function.getProgram_url().equals(requestURI));
                    if (isPermission) break;
                }
                if (!isPermission) {
                    res.sendError(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

            }
        }
        chain.doFilter(request, response);
    }


    private boolean IsIgnoreRequestUrl(String requestUrl) {
        List<String> reqUrls = new ArrayList<>();
        reqUrls.add("/");
        reqUrls.add("/login");
        reqUrls.add("/error");
        reqUrls.add("/recognize/recognizeUpload");
        reqUrls.add("/pqareport/report");
        return reqUrls.stream().anyMatch(s -> s.equalsIgnoreCase(requestUrl)) || requestUrl.contains("chat");
    }


    private boolean IsExistInController(HttpServletRequest request) {

        boolean IsExist = false;
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI().replace(contextPath, Strings.EMPTY);
        ServletContext servletContext = request.getSession().getServletContext();
        WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext, HandlerMapping.class, true, false);
        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
            if (handlerMapping instanceof RequestMappingHandlerMapping) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
                Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : handlerMethods.entrySet()) {

                    RequestMappingInfo mapping = requestMappingInfoHandlerMethodEntry.getKey();
                    HandlerMethod method = requestMappingInfoHandlerMethodEntry.getValue();
                    for (String urlPattern : mapping.getPatternsCondition().getPatterns()) {
                        //System.out.println(method.getBeanType().getName() + "#" + method.getMethod().getName() +" <-- " + urlPattern);
                        if (requestURI.equalsIgnoreCase(urlPattern)) {
                            IsExist = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return IsExist;
    }
}