package com.example.demo.controller.uniController;

import com.example.demo.dto.uniRequestModel.CroissantDTO;
import com.example.demo.service.uniService.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class UniController {
    @Autowired
    private CroissantService croissantService;
    @RequestMapping(value = "/croissants",method = RequestMethod.GET)
    public List<CroissantDTO>croissants(){
        return croissantService.getCroissants();
    }

    @RequestMapping(value = "/croissants",method = RequestMethod.POST)
    public CroissantDTO postCroissants(@RequestBody CroissantDTO croissantDTO){
        return croissantService.createCroissant(croissantDTO);
    }

    @PutMapping(value = "/croissants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity putCroissants(@RequestBody CroissantDTO croissantDTO, @PathVariable Long id){
        croissantService.putCroissant(croissantDTO,id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/croissants/{id}")
    public CroissantDTO getById(@PathVariable("id") Long id) throws SQLException {
        Optional<CroissantDTO>croissantDTO = croissantService.findById(id);

        return croissantService.findById(id).get();

    }

}
