package com.example.demo.controller.messangerController;


import com.example.demo.dto.messanger.Event;
import com.example.demo.service.supportService.VerifyService;
import com.example.demo.service.messangerService.EventParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/WebHook")
class MessengerWebHookController {



    @Autowired
    private EventParserService eventParserService;

    @Autowired
    private VerifyService verifyService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verify(@RequestParam(name = "hub.verify_token") String verifyToken,
                                         @RequestParam(name = "hub.challenge") String challenge){
        if(verifyService.verify(verifyToken)){
            return new ResponseEntity<>(challenge,new HttpHeaders(), HttpStatus.OK);
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> message(@RequestBody Event event){

        if(event.getObject().equals("page")){


            if(eventParserService.parseEvent(event)) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            else
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();




    }




}