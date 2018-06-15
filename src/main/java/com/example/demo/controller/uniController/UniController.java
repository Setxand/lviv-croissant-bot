package com.example.demo.controller.uniController;

import com.example.demo.dto.uniRequestModel.CroissantDTO;
import com.example.demo.exceptions.ElementNoFoundException;
import com.example.demo.exceptions.FieldsNotValidException;
import com.example.demo.service.uniService.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UniController {
    @Autowired
    private CroissantService croissantService;
    @RequestMapping(value = "/croissants",method = RequestMethod.GET)
    public List<CroissantDTO>croissants(){
        return croissantService.getCroissants();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/croissants",method = RequestMethod.POST)
    public CroissantDTO postCroissants(@Valid @RequestBody CroissantDTO croissantDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            throw new FieldsNotValidException();
        return croissantService.createCroissant(croissantDTO);
    }

    @PutMapping(value = "/croissants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putCroissants(@Valid @RequestBody CroissantDTO croissantDTO, BindingResult bindingResult,@PathVariable Long id){
        if(bindingResult.hasErrors())
            throw new FieldsNotValidException();
        croissantService.putCroissant(croissantDTO, id);
    }

    @GetMapping(value = "/croissants/{id}")
    public CroissantDTO getById(@PathVariable Long id) {
        return croissantService.findById(id).orElseThrow(() -> new ElementNoFoundException());
    }

}
