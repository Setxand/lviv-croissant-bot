package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.controller.Test;
import com.bots.lvivcroissantbot.dto.messanger.Message;
import com.bots.lvivcroissantbot.dto.messanger.broadcast.*;
import com.bots.lvivcroissantbot.service.messenger.BroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BroadcastServiceImpl implements BroadcastService {
    @Value("${messenger.broadcast.message.creatives}")
    private String BROADCAST_MESSAGE_CREATIVES;

    @Value("${messenger.broadcast.message}")
    private String BROADCAST_MESSAGE;

    @Value("${messenger.page.access.token}")
    private String PAGE_ACCESS_TOKEN;

    @Value("${messenger.broadcast.domain}")
    private String DOMAIN_BROADCAST;

    @Value("${messenger.broadcast.message.sent}")
    private String BROADCAST_MESSAGE_SENT;

    @Value("${messenger.broadcast.label}")
    private String BROADCAST_LABEL;


    @Value("${messenger.broadcast.customLabel}")
    private String CUSTOM_LABEL;

    @Value(("${messenger.broadcast.estimation}"))
    private String ESTIMATION_URL;
    @Value("${messenger.token}")
    private String TOKEN_ARG;


    private   final static Logger logger = LoggerFactory.getLogger(BroadcastServiceImpl.class);

    @Override
    public Long createBroadCastMessage(List<Message> messages) {
        String url = BROADCAST_MESSAGE_CREATIVES+PAGE_ACCESS_TOKEN;
        BroadcastRequest broadcastRequest = new BroadcastRequest();
        broadcastRequest.setMessages(messages);

        ResponseEntity<BroadcastRequest> response = new RestTemplate()
                .postForEntity(url, broadcastRequest,BroadcastRequest.class);
        logger.debug("BroadcastService message successfully has been created...");
        return response.getBody().getMessageCreativeId();
    }



    @Override
    public Long sendBroadCastMessage(BroadcastMessage broadcastMessage) {
        String url = BROADCAST_MESSAGE+PAGE_ACCESS_TOKEN;

        ResponseEntity<BroadcastRequest> response = new RestTemplate()
                .postForEntity(url,broadcastMessage,BroadcastRequest.class);
        logger.debug("B. message successfully has been sent...");

        return response.getBody().getBroadcastId();
    }

    @Override
    public List<Data> totalUsersNumber(Long broadcastId) {
        String url = DOMAIN_BROADCAST+broadcastId+BROADCAST_MESSAGE_SENT+PAGE_ACCESS_TOKEN;

        ResponseEntity<BroadcastRequest> response = new RestTemplate().getForEntity(url,BroadcastRequest.class);
        logger.debug("Request 'totalUsersNumber' successfully has been sent");
        return response.getBody().getData();
    }

    @Override
    public Long createCustomLabel(String labelName) {
        String url = BROADCAST_LABEL+PAGE_ACCESS_TOKEN;
        CustomLabel customLabel = new CustomLabel(labelName);
        ResponseEntity<CustomLabel> response = new RestTemplate()
                .postForEntity(url,customLabel,CustomLabel.class);
        logger.debug("Custom label successfully has been created...");
        return response.getBody().getId();
    }

    @Override
    public void associateCustomLabel(Long userId, Long customLabelId) {
        String url = DOMAIN_BROADCAST+customLabelId+"/label?access_token="+PAGE_ACCESS_TOKEN;
        CustomLabel customLabel = new CustomLabel();
        customLabel.setUserId(userId);
        new RestTemplate().postForObject(url,customLabel,Void.class);

        logger.debug("associate request successfully has been sent");
    }


@Autowired
Test test;
    @Override
    public void deleteCustomLabelFromUserId(Long customLabelId, Long userId) {
        String url = DOMAIN_BROADCAST+customLabelId+"/label?access_token="+PAGE_ACCESS_TOKEN;
        CustomLabel customLabel = new CustomLabel();
        customLabel.setId(userId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<CustomLabel>httpEntity  = new HttpEntity<>(customLabel,httpHeaders);
        new RestTemplate().exchange(url, HttpMethod.DELETE,httpEntity,Void.class);
        logger.debug("Custom label from MUser id was separated...");
    }

    @Override
    public List<Data> retrieveAssociateLabels(Long userId) {
        String url = DOMAIN_BROADCAST+userId+"/custom_labels?access_token="+PAGE_ACCESS_TOKEN;
        ResponseEntity<BroadcastRequest> response = new RestTemplate().getForEntity(url,BroadcastRequest.class);
        logger.debug("Associate labels was retrieved...");
        return response.getBody().getData();
    }

    @Override
    public Data retrieveLabelDetails(Long customLabelId) {
        String url = DOMAIN_BROADCAST+customLabelId+"?fields=name&access_token="+PAGE_ACCESS_TOKEN;
        ResponseEntity<Data> response = new RestTemplate().getForEntity(url,Data.class);
        logger.debug("the details of current label was retrieved...");
        return response.getBody();
    }

    @Override
    public List<Data> retrieveListOfLabels() {
        String url = DOMAIN_BROADCAST+"me/custom_labels?fields=name&access_token="+PAGE_ACCESS_TOKEN;
        ResponseEntity<BroadcastRequest> response = new RestTemplate().getForEntity(url,BroadcastRequest.class);
        logger.debug("List of labels was retrieved...");
        return response.getBody().getData();
    }

    @Override
    public void deleteLabel(Long customLabelId) {
        String url = DOMAIN_BROADCAST+customLabelId+TOKEN_ARG+PAGE_ACCESS_TOKEN;
        String labelName = retrieveLabelDetails(customLabelId).getName();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(url);

        logger.debug("Label :"+labelName+" was deleted...");
    }

    @Override
    public String estimateTheReach(Long customLabelId) {
        String url = ESTIMATION_URL+PAGE_ACCESS_TOKEN;
        BroadcastRequest broadcastRequest = new BroadcastRequest();
        broadcastRequest.setCustomLabelId(customLabelId);
        ResponseEntity<BroadcastRequest> response = new RestTemplate().postForEntity(url,broadcastRequest,BroadcastRequest.class);
        logger.debug("Estimation id successfully has been received");
        return response.getBody().getReachEstimationId();
    }

    @Override
    public EstimationReach retrieveRichEstimate(String estimationId) {
        String url = DOMAIN_BROADCAST+estimationId+TOKEN_ARG+PAGE_ACCESS_TOKEN;
        ResponseEntity<EstimationReach>response = new RestTemplate().getForEntity(url,EstimationReach.class);
        logger.debug("func 'retrieveRichEstimate' finished!");
        return response.getBody();
    }


}
