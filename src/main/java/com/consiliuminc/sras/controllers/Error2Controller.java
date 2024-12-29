package com.consiliuminc.sras.controllers;

import com.consiliuminc.sras.util.JacksonUtils;
import com.consiliuminc.sras.vo.ResVo;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;



@Controller
public class Error2Controller implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(Error2Controller.class);

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "error", method = RequestMethod.GET)
    public Object renderErrorPage(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {

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
            default:
                errorMsg="UnKnown Error";
                break;
        }


        if (requestUri == null) {
            requestUri = "Unknown";
        }

        String message = MessageFormat.format("{0} returned  {1} with message {2}",
                httpErrorCode, requestUri, exceptionMessage
        );
        logger.error("Error Message ={} ", message);

        if (isAjax(httpRequest)) {
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            PrintWriter writer = httpResponse.getWriter();
            ResVo resVo = new ResVo(HttpStatus.valueOf(httpErrorCode),message);
            writer.write(JacksonUtils.toJsonString(resVo));
            writer.flush();
            writer.close();
            return Result.errorOf(HttpStatus.NOT_FOUND.value(), exceptionMessage);
        } else {
            ModelAndView errorPage = new ModelAndView("error");
            errorPage.addObject("errorMsg", errorMsg);
            return errorPage;
        }

    }

    /**
     * 判断是否是ajax请求
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-CSRF-TOKEN") != null);
    }
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return Throwables.getRootCause(throwable).getMessage();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }

}

class Result<T> {

    public static final int ERROR_CODE_UN_SUPPORT_ARGUMENTS_VALUE = -3;
    public static final int ERROR_CODE_UN_SUPPORT_ACTION = -4;
    public static Result SYSTEM_ERR = errorOf(-1, "系统错误");
    int resCode;
    String resMessage;
    T data;

    public static <O> Result<O> sucessOf(O object) {
        Result<O> result = new Result<O>();
        result.resCode = 0;
        result.resMessage = "成功";
        result.data = object;
        return result;
    }

    public static Result errorOf(int errorCode, String message) {
        Result result = new Result();
        result.resCode = errorCode;
        result.resMessage = message;
        return result;
    }
}