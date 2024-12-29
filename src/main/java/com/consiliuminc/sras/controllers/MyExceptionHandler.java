package com.consiliuminc.sras.controllers;


import com.consiliuminc.sras.util.JacksonUtils;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.MessageFormat;


//@ControllerAdvice
public class MyExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    public static final String ERROR_VIEW = "404";

    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest httpRequest,
                               HttpServletResponse response, Exception e) throws Exception {

        Integer httpErrorCode = (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) httpRequest.getAttribute("javax.servlet.error.exception");
        String exceptionMessage = getExceptionMessage(throwable, httpErrorCode);
        String requestUri = (String) httpRequest.getAttribute("javax.servlet.error.request_uri");
        String errorMsg = "";
        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        String message = MessageFormat.format("{0} returned  {1} with message {2}",
                httpErrorCode, requestUri, exceptionMessage
        );
        logger.error("Error Message ={} ", message);
        if (isAjax(httpRequest)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();


            writer.write(JacksonUtils.toJsonString(Result.errorOf(HttpStatus.NOT_FOUND.value(), e.getMessage())));

            writer.flush();
            writer.close();

            return Result.errorOf(HttpStatus.NOT_FOUND.value(), e.getMessage());
        } else {
            ModelAndView errorPage = new ModelAndView("error");
            errorPage.addObject("errorMsg", errorMsg);
            return errorPage;
        }
    }


    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return Throwables.getRootCause(throwable).getMessage();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }

    /**
     * 判断是否是ajax请求
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest"
                .equals(httpRequest.getHeader("X-Requested-With").toString()));
    }
}


