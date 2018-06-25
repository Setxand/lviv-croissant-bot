package com.bots.lvivcroissantbot.service.messenger;

import com.bots.lvivcroissantbot.dto.messanger.Message;
import com.bots.lvivcroissantbot.dto.messanger.broadcast.BroadcastMessage;
import com.bots.lvivcroissantbot.dto.messanger.broadcast.Data;
import com.bots.lvivcroissantbot.dto.messanger.broadcast.EstimationReach;

import java.util.List;

public interface BroadcastService {
    public Long createBroadCastMessage(List<Message> messages);

    public Long sendBroadCastMessage(BroadcastMessage broadcastMessage);

    public List<Data> totalUsersNumber(Long broadcastId);

    public Long createCustomLabel(String labelName);

    public void associateCustomLabel(Long userId, Long customLabelId);

    public List<Data> retrieveAssociateLabels(Long userId);

    public Data retrieveLabelDetails(Long customLabelId);

    public List<Data> retrieveListOfLabels();

    public void deleteCustomLabelFromUserId(Long customLabelId, Long userId);

    public void deleteLabel(Long customLabelId);

    public String estimateTheReach(Long customLabelId);

    public EstimationReach retrieveRichEstimate(String estimationId);
}
