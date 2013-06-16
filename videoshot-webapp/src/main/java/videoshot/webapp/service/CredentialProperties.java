package videoshot.webapp.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class CredentialProperties implements InitializingBean {

    private static final String RESOURCE_FILE = "/credential.properties";

    private Properties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream(RESOURCE_FILE));
    }

    public String getDeveloperKey() {
        return properties.getProperty("developerKey");
    }

    public String getOAuthToken() {
        return properties.getProperty("oAuthToken");
    }

    public String getOAuthTokenSecret() {
        return properties.getProperty("oAuthTokenSecret");
    }

    public String getOAuthConsumerKey() {
        return properties.getProperty("oAuthConsumerKey");
    }

    public String getOAuthConsumerSecret() {
        return properties.getProperty("oAuthConsumerSecret");
    }
}
