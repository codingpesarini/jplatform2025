package com.studiodomino.jplatform.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jplatform")
@Data
public class JplatformProperties {

    /**
     * Configurazione Site
     */
    private Site site = new Site();

    @Data
    public static class Site {
        /**
         * ID del site default da caricare se non specificato
         */
        private String defaultId = "1";
    }

    // Altre configurazioni future possono andare qui
    // Es: jplatform.email.*, jplatform.upload.*, etc.
}