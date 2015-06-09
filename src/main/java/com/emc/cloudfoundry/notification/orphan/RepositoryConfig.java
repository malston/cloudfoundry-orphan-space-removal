package com.emc.cloudfoundry.notification.orphan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {NotificationRepository.class})
public class RepositoryConfig {
}

