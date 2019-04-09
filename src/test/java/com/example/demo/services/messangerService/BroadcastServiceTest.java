package com.example.demo.services.messangerService;

import com.example.demo.DemoApplicationTests;
import com.example.demo.model.messanger.Message;
import com.example.demo.model.messanger.broadcast.BroadcastMessage;
import com.example.demo.model.messanger.broadcast.Data;
import com.example.demo.model.messanger.broadcast.DynamicText;
import com.example.demo.model.messanger.broadcast.EstimationReach;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class BroadcastServiceTest extends DemoApplicationTests{

    @Autowired
    private BroadcastService broadcastService;
    private static boolean isCustomLabelCreated;
    private List<Message>messages;
    private final String CUSTOM_LABEL_NAME = new String("customLabel");

    @Before
    public void befTest(){
        messages = new ArrayList<>();
        Message message = new Message();
        DynamicText dynamicText = new DynamicText();
        dynamicText.setText("Hello, {{first_name}}");
        dynamicText.setFallBackText("!!!");
        message.setDynamicText(dynamicText);
        messages.add(message);
        logger.debug("\"@Before\" successfully ended!");
    }

    @After
    public void afTest(){
        if(isCustomLabelCreated) {
            List<Data> customLabels = broadcastService.retrieveListOfLabels();
            assertNotNull(customLabels);
            for (Data data : customLabels) {
                if (data.getName().equals(CUSTOM_LABEL_NAME)) {
                    broadcastService.deleteLabel(Long.parseLong(data.getId()));
                }
            }
            logger.debug("\"@After\" successfully ended, and deleted custom label!");
            isCustomLabelCreated = false;
        }

    }


    @Test
    public void createBroadCastMessage() throws Exception {
        Long creativeId = broadcastService.createBroadCastMessage(messages);
        assertNotNull(creativeId);
    }

    @Test
    public void sendBroadCastMessage() throws Exception {
        BroadcastMessage broadcastMessage = new BroadcastMessage(broadcastService.createBroadCastMessage(messages));
        Long broadCastId = broadcastService.sendBroadCastMessage(broadcastMessage);
        assertNotNull(broadCastId);
    }

    @Test
    public void totalUsersNumber() throws Exception {
        BroadcastMessage broadcastMessage = new BroadcastMessage(broadcastService.createBroadCastMessage(messages));
        assertNotNull(broadcastMessage.getMessageCreativeId());
        Long broadcastId = broadcastService.sendBroadCastMessage(broadcastMessage);
        assertNotNull(broadcastId);
        List<Data> data = broadcastService.totalUsersNumber(broadcastId);
        assertNotNull(data);
        logger.info("\n\nAll data about broadcast message:\n\n"+data);
    }

    @Test
    public void createCustomLabel() throws Exception {
        Long customLabelId = broadcastService.createCustomLabel(CUSTOM_LABEL_NAME);
        assertNotNull(customLabelId);
        isCustomLabelCreated = true;
        logger.info("Custom label id: "+customLabelId);
    }

    @Test
    public void associateCustomLabel() throws Exception {
        Long customLabelId = broadcastService.createCustomLabel(CUSTOM_LABEL_NAME);
        assertNotNull(customLabelId);
        isCustomLabelCreated =true;
        broadcastService.associateCustomLabel(userId,customLabelId);
    }

    @Test
    public void retrieveAssociateLabels() throws Exception {
        List<Data> customLabels = broadcastService.retrieveListOfLabels();
        assertNotNull(customLabels);
        logger.info("\n\nList of labels: "+customLabels+"\n\n");
    }

    @Test
    public void retrieveLabelDetails() throws Exception {
        Long customLabelId = broadcastService.createCustomLabel(CUSTOM_LABEL_NAME);
        assertNotNull(customLabelId);
        isCustomLabelCreated = true;
        Data data = broadcastService.retrieveLabelDetails(customLabelId);
        assertNotNull(data);
        logger.info("\n\nLabel details:"+data+"\n\n");

    }

    @Test
    public void retrieveListOfLabels() throws Exception {
        List<Data> data = broadcastService.retrieveListOfLabels();
        assertNotNull(data);
        logger.info("\n\nList of labels: "+data+"\n\n");
    }

    @Test
    public void deleteCustomLabelFromUserId() throws Exception {
        Long customLabelId = broadcastService.createCustomLabel(CUSTOM_LABEL_NAME);
        assertNotNull(customLabelId);
        isCustomLabelCreated =true;
        broadcastService.associateCustomLabel(userId,customLabelId);
        broadcastService.deleteCustomLabelFromUserId(customLabelId,userId);

    }

    @Test
    public void deleteLabel() throws Exception {
        List<Data> customLabels = broadcastService.retrieveListOfLabels();
        assertNotNull(customLabels);
        for(Data data: customLabels){
            if(data.getName().equals(CUSTOM_LABEL_NAME)){
                broadcastService.deleteLabel(Long.parseLong(data.getId()));
            }
        }
    }

    @Test
    public void estimateTheReach() throws Exception {
        Long customLabelId = broadcastService.createCustomLabel(CUSTOM_LABEL_NAME);
        assertNotNull(customLabelId);
        isCustomLabelCreated = true;
        String estReachId = broadcastService.estimateTheReach(customLabelId);
        assertNotNull(estReachId);
        logger.info("\n\nEstimate reach id: "+estReachId+"\n\n");
    }

    @Test
    public void retrieveRichEstimate() throws Exception {
        Long customLabelId = broadcastService.createCustomLabel(CUSTOM_LABEL_NAME);
        assertNotNull(customLabelId);
        isCustomLabelCreated = true;
        String estReachId = broadcastService.estimateTheReach(customLabelId);
        assertNotNull(estReachId);
        EstimationReach estimationReach = broadcastService.retrieveRichEstimate(estReachId);
        assertNotNull(estimationReach);
        logger.info("\n\nRetrieved reach estimation info: "+estimationReach+"\n\n");

    }

}