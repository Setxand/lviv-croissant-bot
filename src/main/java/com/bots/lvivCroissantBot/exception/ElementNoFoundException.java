package com.bots.lvivCroissantBot.exception;

import java.util.ResourceBundle;

import static com.bots.lvivCroissantBot.constantEnum.Error.ELEMENT_NOT_FOUND;

public class ElementNoFoundException extends RuntimeException {

    public ElementNoFoundException() {
        super(ResourceBundle.getBundle("difMessages").getString(ELEMENT_NOT_FOUND.name()));
    }
}
