package com.studiodomino.jplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Espone la cartella fisica delle immagini in modo che sia accessibile via browser
        registry.addResourceHandler("/cmss/cms-repository/**")
                .addResourceLocations("file:src/main/resources/static/cmss/cms-repository/");
    }
}