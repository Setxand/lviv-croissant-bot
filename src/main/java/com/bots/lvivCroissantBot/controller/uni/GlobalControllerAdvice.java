package com.bots.lvivCroissantBot.controller.uni;

import com.bots.lvivCroissantBot.constantEnum.Error;
import com.bots.lvivCroissantBot.dto.JsonResponse;
import com.bots.lvivCroissantBot.exception.ElementNoFoundException;
import com.bots.lvivCroissantBot.exception.FieldsNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("com.bots.lvivCroissantBot.controller.uni")
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
