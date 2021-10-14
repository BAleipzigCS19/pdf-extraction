package de.baleipzig.pdfextraction.api.dto;

import java.util.List;
import java.util.Objects;

public class TemplateDTO {

    private String name;
    private String consumer;
    private List<FieldDTO> fields;

    public TemplateDTO() {
    }

    public TemplateDTO(String name, String consumer, List<FieldDTO> fields) {
        this.name = name;
        this.consumer = consumer;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public List<FieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<FieldDTO> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TemplateDTO other)) {
            return false;
        }
        return Objects.equals(name, other.name) && Objects.equals(consumer, other.consumer) && Objects.equals(fields, other.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, consumer, fields);
    }

    @Override
    public String toString() {
        return "Template{" +
                "name='" + name + '\'' +
                ", consumer='" + consumer + '\'' +
                ", fields=" + fields +
                '}';
    }
}
