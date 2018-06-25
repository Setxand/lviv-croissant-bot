package com.bots.lvivcroissantbot.controller.messanger;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.dto.messanger.Button;
import com.bots.lvivcroissantbot.entity.register.User;
import com.bots.lvivcroissantbot.repository.CustomerOrderingRepository;
import com.bots.lvivcroissantbot.repository.UserRepository;
import com.bots.lvivcroissantbot.service.messenger.MessageSenderService;
import com.bots.lvivcroissantbot.service.peopleregister.MUserRepositoryService;
import com.bots.lvivcroissantbot.service.support.EmailService;
import com.bots.lvivcroissantbot.service.support.RecognizeService;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.bots.lvivcroissantbot.constantenum.messenger.Role.ADMIN;
import static com.bots.lvivcroissantbot.constantenum.messenger.speaking.ServerSideSpeaker.*;
import static com.bots.lvivcroissantbot.constantenum.messenger.type.ButtonType.web_url;

@RestController
@RequestMapping("/reqDispatcher")
public class RequestHandler {
    @Autowired
    private RecognizeService recognizeService;
    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private MUserRepositoryService MUserRepositoryService;
    @Autowired
    private CustomerOrderingRepository customerOrderingRepositoryService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Value("${server.url}")
    private String SERVER_URL;
    @Value("${stripe.secret.key}")
    private String STRIPE_API_KEY;

    @RequestMapping(value = "/charge", method = RequestMethod.POST)
    public ResponseEntity charge(@RequestParam String stripeToken, @RequestParam String price){
        Stripe.apiKey = STRIPE_API_KEY;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", Integer.parseInt(price));
        params.put("currency", "usd");
        params.put("description", "Example charge");
        params.put("source", stripeToken);

        try {
            Charge charge = Charge.create(params);

        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }

        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/successTrans")
    public void successTrans(@RequestParam String userId){
        Long uId = Long.parseLong(userId);
        CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByMUserOrderByIdDesc(MUserRepositoryService.findOnebyRId(uId));
        messageSenderService.sendSimpleMessage(recognizeService.recognize(ORDERING_WAS_DONE.name(), uId) + "\n" + customerOrdering.getCroissants() + "\nPrice: " + customerOrdering.getPrice() + recognizeService.recognize(CURRENCY.name(), uId), uId);
        Button button = new Button(web_url.name(), recognizeService.recognize(RATING_BUTTON.name(), uId));
        button.setMesExtentions(true);
        button.setUrl(SERVER_URL + "/req/" + uId);
        messageSenderService.sendButtons(new ArrayList<Button>(Arrays.asList(button)), recognizeService.recognize(RATE_US.name(), uId), uId);
        messageSenderService.sendUserActions(uId);
    }

    @RequestMapping(value = "/sendMail")
    public void sendMail(@RequestParam(name = "mark1") String mark,@RequestParam(name = "recipientId")String recipient) throws MessagingException, MalformedURLException {
        List<MUser> admins = userRepository.findAllByRole(ADMIN).stream().map(User::getMUser).collect(Collectors.toList());
        Long userId = Long.parseLong(recipient);
        MUser MUser = MUserRepositoryService.findOnebyRId(userId);
        emailService.sendMailForAdminAboutMark(MUser,mark);
        messageSenderService.sendSimpleMessage(recognizeService.recognize(THANK_FOR_RATE.name(),userId),userId);
    }


}
