package com.example.demo.controllers.messangerControllers;

import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.models.messanger.Button;
import com.example.demo.services.messangerService.MessageSenderService;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.repositoryService.CustomerOrderingRepositoryService;
import com.example.demo.services.supportService.EmailService;
import com.example.demo.services.supportService.RecognizeService;
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

import static com.example.demo.enums.messengerEnums.Roles.ADMIN;
import static com.example.demo.enums.messengerEnums.speaking.ServerSideSpeaker.*;
import static com.example.demo.enums.messengerEnums.types.ButtonType.web_url;

@RestController
@RequestMapping("/reqDispatcher")
public class RequestHandler {
	@Autowired
	private RecognizeService recognizeService;
	@Autowired
	private MessageSenderService messageSenderService;
	@Autowired
	private UserRepositoryService userRepositoryService;
	@Autowired
	private CustomerOrderingRepositoryService customerOrderingRepositoryService;
	@Autowired
	private EmailService emailService;
	@Value("${server.url}")
	private String SERVER_URL;
	@Value("${stripe.secret.key}")
	private String STRIPE_API_KEY;

	@RequestMapping(value = "/charge", method = RequestMethod.POST)
	public ResponseEntity charge(@RequestParam String stripeToken, @RequestParam String price) {
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

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/successTrans")
	public void successTrans(@RequestParam String userId) {
		Long uId = Long.parseLong(userId);
		CustomerOrdering customerOrdering = customerOrderingRepositoryService.findTopByUser(userRepositoryService.findOnebyRId(uId));
		messageSenderService.sendSimpleMessage(recognizeService.recognize(ORDERING_WAS_DONE.name(), uId) + "\n" + customerOrdering.getCroissants() + "\nPrice: " + customerOrdering.getPrice() + recognizeService.recognize(CURRENCY.name(), uId), uId);
		Button button = new Button(web_url.name(), recognizeService.recognize(RATING_BUTTON.name(), uId));
		button.setMesExtentions(true);
		button.setUrl(SERVER_URL + "/req/" + uId);
		messageSenderService.sendButtons(new ArrayList<Button>(Arrays.asList(button)), recognizeService.recognize(RATE_US.name(), uId), uId);
		messageSenderService.sendUserActions(uId);
	}

	@RequestMapping(value = "/sendMail")
	public void sendMail(@RequestParam(name = "mark1") String mark, @RequestParam(name = "recipientId") String recipient) throws MessagingException, MalformedURLException {
		List<User> admins = userRepositoryService.getByRole(ADMIN);
		Long userId = Long.parseLong(recipient);
		User user = userRepositoryService.findOnebyRId(userId);
		emailService.sendMailForAdminAboutMark(user, mark);
		messageSenderService.sendSimpleMessage(recognizeService.recognize(THANK_FOR_RATE.name(), userId), userId);
	}


}
