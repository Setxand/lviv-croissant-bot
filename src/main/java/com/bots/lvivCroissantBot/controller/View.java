package com.bots.lvivCroissantBot.controller;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CustomerOrdering;
import com.bots.lvivCroissantBot.exception.ElementNoFoundException;
import com.bots.lvivCroissantBot.repository.CustomerOrderingRepository;
import com.bots.lvivCroissantBot.service.messenger.MessageSenderService;
import com.bots.lvivCroissantBot.service.peopleRegister.MUserRepositoryService;
import com.bots.lvivCroissantBot.service.support.EmailService;
import com.bots.lvivCroissantBot.service.support.RecognizeService;
import com.bots.lvivCroissantBot.service.uni.CroissantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Controller
public class View {

    @Autowired
    private EmailService emailService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private CroissantService croissantRepositoryService;
    @Autowired
    private CroissantService croissantService;
    @RequestMapping(value = "/req/{customerId}",method = RequestMethod.GET)
    public String  getMyReq(Model model,@PathVariable String customerId){
        model.addAttribute("customerId",customerId);
        return "WebView";
    }



    @RequestMapping(value = "/showMore/{orderId}")
    public String showMoreForOrderings(@PathVariable String orderId,Model model){
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(Long.parseLong(orderId)).orElseThrow(ElementNoFoundException::new);
        List<String> orderings = new ArrayList<>();
        for(String cr: customerOrdering.getCroissants()){
            try {
                CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(cr));
                orderings.add(croissantEntity.toString());
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
        CroissantEntity croissantEntity = croissantRepositoryService.findOne(Long.parseLong(croissantId));

        model.addAttribute("fillings", croissantEntity.getCroissantsFillings());
        return "fillings";
    }

    @GetMapping("/payment/{userId}")
    public String getPayment(@PathVariable String userId, Model model){
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByMUserOrderByIdDesc(MUserRepositoryService.findOnebyRId(Long.parseLong(userId)));
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

    @GetMapping("/save_croissant")
    public String savingCroissant(){
        return "creatingCroissant";
    }
    @GetMapping("/update_croissant")
    public String updatingCroissant(){
        return "updatingCroissant";
    }
    @GetMapping("/update_croissant_form")
    public String updatingCroissantForm(@RequestParam String id, Model model){
        model.addAttribute("id",id);
        return "updatingCroissantForm";
    }
}
