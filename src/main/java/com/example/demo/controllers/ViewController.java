package com.example.demo.controllers;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.services.lvivCroissantRepositoryService.CroissantRepositoryService;
import com.example.demo.services.lvivCroissantRepositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.EmailService;
import com.example.demo.services.supportService.RecognizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;


@Controller
public class ViewController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepositoryService userRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CustomerOrderingRepositoryService customerOrderingRepositoryService;
    @Autowired
    private CroissantRepositoryService croissantRepositoryService;
    @RequestMapping(value = "/req/{customerId}",method = RequestMethod.GET)
    public String  getMyReq(Model model,@PathVariable String customerId){
        model.addAttribute("customerId",customerId);
        return "WebView";
    }



    @RequestMapping(value = "/showMore/{orderId}")
    public String showMoreForOrderings(@PathVariable String orderId,Model model){
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findOne(Long.parseLong(orderId));
        List<String> orderings = new ArrayList<>();
        for(String cr: customerOrdering.getCroissants()){
            try {
                Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(cr));
                orderings.add(croissant.toString());
            }
            catch (Exception ex) {
                orderings.add(cr);
            }
        }
        model.addAttribute("ordering",customerOrdering);
        model.addAttribute("croissants",orderings);
        return "showMorePage";
    }

    @RequestMapping(value = "/fillings/{croissantId}")
    public String showOrdering(@PathVariable String croissantId,Model model){
        Croissant croissant = croissantRepositoryService.findOne(Long.parseLong(croissantId));

        model.addAttribute("fillings",croissant.getCroissantsFillings());
        return "fillings";
    }

    @GetMapping("/payment/{userId}")
    public String getPayment(@PathVariable String userId, Model model){
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByUser(userRepositoryService.findOnebyRId(Long.parseLong(userId)));
        model.addAttribute("userId",userId);
        model.addAttribute("price",customerOrdering.getPrice());
        return "payment";
    }

    @GetMapping("/reference")
    public String ref(){
        return "reference";
    }

    @GetMapping("/successTrans")
    public String finTrans(){
        return "successTransaction";
    }


}
