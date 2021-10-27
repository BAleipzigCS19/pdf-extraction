package de.baleipzig.pdfextraction.backend.entities;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "TEMPLATES", schema = "PUBLIC",
        uniqueConstraints = {
                @UniqueConstraint(name = "unq_name", columnNames = {"name"})
        }
)
public class ExtractionTemplate extends AbstractPersistable<Integer> {

    private String name;

    private String consumer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Field> fields;

    public ExtractionTemplate() {
        //Empty Constructor for Serialization
    }

    public ExtractionTemplate(String name, String content, List<Field> fields) {
        this.name = name;
        this.consumer = content;
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

    public void setConsumer(String content) {
        this.consumer = content;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ExtractionTemplate other)) {
            return true;
        }
        return Objects.equals(name, other.name) && Objects.equals(consumer, other.consumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, consumer);
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
