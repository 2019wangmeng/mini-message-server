package com.minimessage.controller;

import com.minimessage.entity.enums.ResponseCodeEnum;
import com.minimessage.entity.vo.ResponseVO;
import com.minimessage.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class AGlobalExceptionHandlerController extends ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);

    @ExceptionHandler(value = Exception.class)
    Object handlerException(Exception e, HttpServletRequest request) {
        logger.error("请求错误，请求地址{},错误信息:", request.getRequestURL(), e);
        ResponseVO responseVO = new ResponseVO();
        responseVO.setStatus(STATUS_ERROR);
        if (e instanceof NoHandlerFoundException) {
            responseVO.setCode(ResponseCodeEnum.CODE_404.getCode());
            responseVO.setInfo(ResponseCodeEnum.CODE_404.getMsg());
        } else if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            responseVO.setCode(be.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : be.getCode());
            responseVO.setInfo(be.getMessage());
        } else if (e instanceof BindException || e instanceof MethodArgumentTypeMismatchException) {
            responseVO.setCode(ResponseCodeEnum.CODE_600.getCode());
            responseVO.setInfo(ResponseCodeEnum.CODE_600.getMsg());
        } else if (e instanceof DuplicateKeyException) {
            responseVO.setCode(ResponseCodeEnum.CODE_601.getCode());
            responseVO.setInfo(ResponseCodeEnum.CODE_601.getMsg());
        } else if (e instanceof ConstraintViolationException || e instanceof BindException) {
            responseVO.setCode(ResponseCodeEnum.CODE_600.getCode());
            responseVO.setInfo(ResponseCodeEnum.CODE_600.getMsg());
        } else {
            responseVO.setCode(ResponseCodeEnum.CODE_500.getCode());
            responseVO.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        }
        return responseVO;
    }
}
