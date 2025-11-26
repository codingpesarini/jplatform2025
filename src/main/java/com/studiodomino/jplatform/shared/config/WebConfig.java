package com.studiodomino.jplatform.shared.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Collections;

@Configuration
public class WebConfig implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        // Parametro globale IDSITE
        servletContext.setInitParameter("IDSITE", "1");

        // DISABILITA session ID nell'URL
        servletContext.setSessionTrackingModes(
                Collections.singleton(SessionTrackingMode.COOKIE)
        );
    }

    @Bean
    public ServletContextInitializer sessionConfig() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(false); // true in produzione con HTTPS
            sessionCookieConfig.setName("JPLATFORMSESSION");
        };
    }
}