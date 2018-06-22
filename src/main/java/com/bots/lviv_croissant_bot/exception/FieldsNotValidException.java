package com.bots.lviv_croissant_bot.exception;

import java.util.ResourceBundle;

import static com.bots.lviv_croissant_bot.constantEnum.Error.FIELDS_NOT_VALID;

public class FieldsNotValidException extends RuntimeException {
    public FieldsNotValidException() {
        super(ResourceBundle.getBundle("difMessages").getString(FIELDS_NOT_VALID.name()));
    }
}
