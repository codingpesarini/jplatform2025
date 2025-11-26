package com.studiodomino.jplatform.shared.config;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Breadcrumb implements Serializable {

    private List<BreadcrumbItem> items = new ArrayList<>();

    public void add(String label, String url) {
        items.add(new BreadcrumbItem(label, url));
    }

    public void clear() {
        items.clear();
    }

    @Data
    public static class BreadcrumbItem implements Serializable {
        private String label;
        private String url;

        public BreadcrumbItem(String label, String url) {
            this.label = label;
            this.url = url;
        }
    }
}