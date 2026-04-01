package com.studiodomino.jplatform.shared.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Collections;

@Configuration
public class WebConfig implements ServletContextInitializer, WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setInitParameter("IDSITE", "1");
        servletContext.setSessionTrackingModes(
                Collections.singleton(SessionTrackingMode.COOKIE)
        );
    }

    public ServletContextInitializer sessionConfig() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(false);
            sessionCookieConfig.setName("JPLATFORMSESSION");
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imageProfile/**")
                .addResourceLocations("file:" + uploadPath + "imageProfile/");

        registry.addResourceHandler("/cmss/cms-repository/images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}