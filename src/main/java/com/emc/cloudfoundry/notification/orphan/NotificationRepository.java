package com.emc.cloudfoundry.notification.orphan;

import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, NotificationPk> {

}
