package de.baleipzig.pdfextraction.backend.entities;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table(name = "TEMPLATES", schema = "PUBLIC",
        uniqueConstraints = {
                @UniqueConstraint(name = "unq_name", columnNames = {"name"})
        }
)
public class Template extends AbstractPersistable<Integer> {


    private String name;

    private String content;

    public Template() {
        //Empty Constructor for Serialization
    }

    public Template(String name, String content) {
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
        if (!(o instanceof Template other)) {
            return true;
        }
        return Objects.equals(name, other.name) && Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, content);
    }
}
