package com.example.demo.exceptions;

import org.springframework.beans.factory.annotation.Value;

import java.util.ResourceBundle;

import static com.example.demo.constantEnum.Error.ELEMENT_NOT_FOUND;

public class ElementNoFoundException extends RuntimeException {

    public ElementNoFoundException() {
        super(ResourceBundle.getBundle("difMessages").getString(ELEMENT_NOT_FOUND.name()));
    }
}
