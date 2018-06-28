package com.bots.lvivcroissantbot.controller;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CroissantEntityRepository;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.EmailService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.bots.lvivcroissantbot.service.uni.CroissantService;
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
    private CroissantEntityRepository croissantRepository;
    @Autowired
    private CroissantService croissantService;

    @RequestMapping(value = "/req/{customerId}", method = RequestMethod.GET)
    public String getMyReq(Model model, @PathVariable String customerId) {
        model.addAttribute("customerId", customerId);
        return "WebView";
    }


    @RequestMapping(value = "/showMore/{orderId}")
    public String showMoreForOrderings(@PathVariable String orderId, Model model) {
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findById(Long.parseLong(orderId)).orElseThrow(ElementNoFoundException::new);
        List<String> orderings = new ArrayList<>();
        for (String cr : customerOrdering.getCroissants()) {
            try {
                CroissantEntity croissantEntity = croissantRepository.getOne(Long.parseLong(cr));
                orderings.add(croissantEntity.toString());
            } catch (Exception ex) {
                orderings.add(cr);
            }
        }
        model.addAttribute("ordering", customerOrdering);
        model.addAttribute("croissants", orderings);
        return "showMorePage";
    }

    @RequestMapping(value = "/fillings/{croissantId}")
    public String showOrdering(@PathVariable String croissantId, Model model) {
        CroissantEntity croissantEntity = croissantRepository.getOne(Long.parseLong(croissantId));

        model.addAttribute("fillings", croissantEntity.getCroissantsFillings());
        return "fillings";
    }

    @GetMapping("/payment/{userId}")
    public String getPayment(@PathVariable String userId, Model model) {
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByMUserOrderByIdDesc(MUserRepositoryService.findOnebyRId(Long.parseLong(userId)));
        model.addAttribute("userId", userId);
        model.addAttribute("price", customerOrdering.getPrice());
        return "payment";
    }

    @GetMapping("/reference")
    public String ref() {
        return "reference";
    }

    @GetMapping("/successTrans")
    public String finTrans() {
        return "successTransaction";
    }

    @GetMapping("/save_croissant")
    public String savingCroissant() {
        return "creatingCroissant";
    }

    @GetMapping("/update_croissant")
    public String updatingCroissant() {
        return "updatingCroissant";
    }

    @GetMapping("/update_croissant_form")
    public String updatingCroissantForm(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "updatingCroissantForm";
    }
}
