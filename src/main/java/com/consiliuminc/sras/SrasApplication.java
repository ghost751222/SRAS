package com.consiliuminc.sras;


import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
@EncryptablePropertySource(name = "EncryptedProperties", value = "classpath:persistence.properties")
public class SrasApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SrasApplication.class);


    public static void main(String[] args) throws Exception {
        System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification","true");
        SpringApplication app = new SpringApplication(SrasApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        //SpringApplication.run(SrasApplication.class, args);
    }
}

