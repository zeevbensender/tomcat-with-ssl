package com.jtestim.tomcatssl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

/**
 * Created by bensende on 05/09/2018.
 */
@Configuration
@ComponentScan
@PropertySources({@PropertySource(value="file:application.properties",ignoreResourceNotFound = true)})
public class AppConfig {
    @Autowired
    private Environment env;

    public String getPort() {
        return env.getProperty("server.port");
    }

    public String getKeyStore() {
        return env.getProperty("server.ssl.key-store");
    }

    public String getKeyStorePassword() {
        return env.getProperty("server.ssl.key-store-password");

    }

    public String getKeyPassword() {
        return env.getProperty("server.ssl.key-password");
    }

    public String getKeyStoreType() {
        return env.getProperty("server.ssl.key-store-type");
    }



}

