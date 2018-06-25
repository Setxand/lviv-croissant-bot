package com.bots.lvivcroissantbot.exception;

import java.util.ResourceBundle;

import static com.bots.lvivcroissantbot.constantenum.Error.FIELDS_NOT_VALID;

public class FieldsNotValidException extends RuntimeException {
    public FieldsNotValidException() {
        super(ResourceBundle.getBundle("difMessages").getString(FIELDS_NOT_VALID.name()));
    }
}
