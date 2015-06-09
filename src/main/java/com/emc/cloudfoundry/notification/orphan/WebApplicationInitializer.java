package com.emc.cloudfoundry.notification.orphan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.common.MysqlServiceInfo;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

public class WebApplicationInitializer implements ApplicationContextInitializer<AnnotationConfigEmbeddedWebApplicationContext> {

    public static final String IN_MEMORY_PROFILE = "in-memory";
    private static final Log logger = LogFactory.getLog(WebApplicationInitializer.class);
    private static final Map<Class<? extends ServiceInfo>, String> serviceTypeToProfileName =
            new HashMap<Class<? extends ServiceInfo>, String>();
    private static final List<String> validLocalProfiles = Arrays.asList("mysql");
    static {
        serviceTypeToProfileName.put(MysqlServiceInfo.class, "mysql");
    }

    @Override
    public void initialize(AnnotationConfigEmbeddedWebApplicationContext applicationContext) {
    	System.out.println("Initializing...");
        Cloud cloud = getCloud();
    	System.out.println("Are we in cloud? " + cloud);

        ConfigurableEnvironment appEnvironment = applicationContext.getEnvironment();

		if (appEnvironment.getActiveProfiles() != null) {
			for (String activeProfile: appEnvironment.getActiveProfiles()) {
				System.out.println("activeProfile = " + activeProfile);
			}
		}
		
        List<String[]> persistenceProfiles = getCloudProfile(cloud);
        if (persistenceProfiles == null) {
            persistenceProfiles = getActiveProfile(appEnvironment);
        }
        if (persistenceProfiles == null) {
            persistenceProfiles = new ArrayList<String[]>();
            persistenceProfiles.add(new String[] { IN_MEMORY_PROFILE });
        }
        for (String[] profile : persistenceProfiles) {
            for (String persistenceProfile : profile) {
				System.out.println("adding to active profiles: '" + persistenceProfile + "'");
                appEnvironment.addActiveProfile(persistenceProfile);
            }
        }
        logger.info("Active profiles: " + StringUtils.arrayToCommaDelimitedString(appEnvironment.getActiveProfiles()));
    }

    public List<String[]> getCloudProfile(Cloud cloud) {
        if (cloud == null) {
            return null;
        }

        List<String> profiles = new ArrayList<String>();

        List<ServiceInfo> serviceInfos = cloud.getServiceInfos();

        if (serviceInfos!=null) {
			logger.info("Found serviceInfos: ");
			for (ServiceInfo serviceInfo : serviceInfos) {
		 		logger.info(serviceInfo.getClass() + ":" + serviceInfo.getId());
			}
		}

        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceTypeToProfileName.containsKey(serviceInfo.getClass())) {
                profiles.add(serviceTypeToProfileName.get(serviceInfo.getClass()));
            }
        }

        if (profiles.size() > 0) {
            return createProfileNames(profiles, "cloud");
        }

        return null;
    }

    private Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException ce) {
            return null;
        }
    }

    private List<String[]> getActiveProfile(ConfigurableEnvironment appEnvironment) {
        List<String> serviceProfiles = new ArrayList<String>();

        for (String profile : appEnvironment.getActiveProfiles()) {
            if (validLocalProfiles.contains(profile)) {
                serviceProfiles.add(profile);
            }
        }

        if (serviceProfiles.size() > 0) {
            logger.info("Profile found: '" + serviceProfiles.get(0) + "'");
            return createProfileNames(serviceProfiles, "local");
        }
        logger.warn("No profile was set.");
        return null;
    }

    private List<String[]> createProfileNames(List<String> baseNames, String suffix) {
        List<String[]> profileNames = new ArrayList<String[]>();
        for (String baseName : baseNames) {
            String[] profiles = {baseName, baseName + "-" + suffix};
            logger.info("Setting profile names: " + StringUtils.arrayToCommaDelimitedString(profiles));
            profileNames.add(profiles);

        }
        return profileNames;
    }
}
