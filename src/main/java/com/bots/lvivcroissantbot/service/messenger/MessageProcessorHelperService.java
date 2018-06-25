package com.bots.lvivcroissantbot.service.messenger;

import com.bots.lvivcroissantbot.dto.messanger.Messaging;

public interface MessageProcessorHelperService {
    public void helpCompleteCroissantSecondStep(Messaging messaging);

    public void helpCompleteOrderingList(Messaging messaging);

    public void helpCompletingOrderings(Messaging messaging);

    public void helpOrderingListFilling(Messaging messaging);

    public void helpParseRoleRequest(Messaging messaging);

    public void helpCourierRegistration(Messaging messaging);

    public void helpGetListOfOrdering(Messaging messaging);

    public void helpDeleteOrderings(Messaging messaging);
}
