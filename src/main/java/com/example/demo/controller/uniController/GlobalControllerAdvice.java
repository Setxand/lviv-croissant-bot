package com.example.demo.controller.uniController;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@ControllerAdvice("com.example.demo.controller.uniController")
public class GlobalControllerAdvice {


    @ExceptionHandler({SQLException.class, NoSuchElementException.class})
    public ModelAndView error(Exception ex){
        return new ModelAndView("error","exception",ex);
    }
}
