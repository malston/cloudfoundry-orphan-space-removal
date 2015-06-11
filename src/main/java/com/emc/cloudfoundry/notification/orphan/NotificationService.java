package com.emc.cloudfoundry.notification.orphan;

import java.util.List;

public interface NotificationService {

	void sendNotification(String orgGuid, String userGuid, String from, List<String> to, String messageBody);

}
