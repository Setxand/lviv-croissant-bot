package com.example.demo.controller.uniController;

import com.example.demo.constantEnum.Error;
import com.example.demo.dto.JsonResponse;
import com.example.demo.exceptions.ElementNoFoundException;
import com.example.demo.exceptions.FieldsNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("com.example.demo.controller.uniController")
public class GlobalControllerAdvice {


    @ExceptionHandler(ElementNoFoundException.class)
    public @ResponseBody JsonResponse error(ElementNoFoundException ex) {
        return new JsonResponse(Error.ELEMENT_NOT_FOUND.name(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(FieldsNotValidException.class)
    public @ResponseBody JsonResponse error(FieldsNotValidException ex) {
        return new JsonResponse(Error.FIELDS_NOT_VALID.name(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }



}
