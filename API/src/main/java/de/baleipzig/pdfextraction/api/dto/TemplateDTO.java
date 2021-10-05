package de.baleipzig.pdfextraction.api.dto;

import java.util.Objects;

public class TemplateDTO {
    private String name;
    private String content;

    public TemplateDTO() {
    }

    public TemplateDTO(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TemplateDTO other)) {
            return true;
        }

        return Objects.equals(name, other.name) && Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, content);
    }
}
