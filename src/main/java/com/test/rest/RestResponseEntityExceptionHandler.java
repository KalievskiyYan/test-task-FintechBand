package com.test.rest;

import com.test.exception.NoSuchPaymentRequestException;
import com.test.exception.RequestAlreadyExistException;
import com.test.rest.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

/**
 * Handle application errors and return appropriate error response
 */
@ControllerAdvice
@Slf4j
class RestResponseEntityExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = NoSuchPaymentRequestException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNoSuchPaymentRequestException(RuntimeException exception) {
        log.warn("No such payment request exception", exception);
        return new ErrorDTO(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = RequestAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleRequestAlreadyExist(RuntimeException exception) {
        log.warn("Duplicated request was found", exception);
        return new ErrorDTO(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleInvalidDataException(MethodArgumentNotValidException exception) {
        log.warn("Invalid request data", exception);
        return new ErrorDTO(Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleGenericException(Exception exception) {
        log.error("Unhandled error", exception);
        return new ErrorDTO("Something went wrong!");
    }

}