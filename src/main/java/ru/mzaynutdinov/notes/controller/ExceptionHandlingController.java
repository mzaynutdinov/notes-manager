package ru.mzaynutdinov.notes.controller;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mzaynutdinov.notes.dto.ResponseDto;
import ru.mzaynutdinov.notes.utils.ApiException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionHandlingController {
    private final static Logger logger = Logger.getLogger(ExceptionHandlingController.class);

    /**
     * Перехватывает исключение типа <code>ApiException</code> и возвращает <code>ResponseDto</code>
     * с заполенным полем <code>error</code>
     */
    @ResponseBody
    @ExceptionHandler(ApiException.class)
    public ResponseDto handleError(HttpServletRequest req, HttpServletResponse resp, ApiException ex) {
        logger.error("Request: " + req.getRequestURL() + " raised " + ex.getClass().getSimpleName(), ex);
        resp.setStatus(ex.getType().httpStatus);
        return ResponseDto.error(ex.getType().code, ex.getType().name());
    }

    /**
     * Перехватывает все оставшиеся исключения и возвращает <code>ResponseDto</code>
     * с заполенным полем <code>error</code>. Исключение помечается как INTERNAL_ERROR с кодом 9000.
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseDto handleError(HttpServletRequest req, HttpServletResponse resp, Exception ex) {
        logger.error("Request: " + req.getRequestURL() + " raised " + ex.getClass().getSimpleName(), ex);
        resp.setStatus(500);
        return ResponseDto.error(9000, "INTERNAL_ERROR");
    }
}
