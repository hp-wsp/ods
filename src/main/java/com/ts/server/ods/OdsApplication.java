package com.ts.server.ods;

import com.ts.server.ods.common.spring.MixPropertySourceFactory;
import com.ts.server.ods.security.kaptcha.KaptchaProperties;
import com.ts.server.ods.security.authenticate.AuthenticateProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource(value = "classpath:authenticate.yml", factory = MixPropertySourceFactory.class)
@EnableConfigurationProperties({OdsProperties.class, SmsProperties.class,
        AuthenticateProperties.class, KaptchaProperties.class})
public class OdsApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(OdsApplication.class);

    public static void main(String[] args){
        ApplicationContext context = new SpringApplicationBuilder(OdsApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);

        for(String name: context.getBeanDefinitionNames()){
            LOGGER.trace("Instance bean name={}", name);
        }
    }
}
