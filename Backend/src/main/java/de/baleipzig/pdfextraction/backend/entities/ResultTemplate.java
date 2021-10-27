package de.baleipzig.pdfextraction.backend.entities;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "DOCS", schema = "PUBLIC",
        uniqueConstraints = {
                @UniqueConstraint(name = "unq_name", columnNames = {"name"})
        }
)
public class ResultTemplate extends AbstractPersistable<Integer> {
    private String name;
    private byte[] content;

    public ResultTemplate() {
    }

    public ResultTemplate(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResultTemplate other)) {
            return false;
        }
        return Objects.equals(name, other.name);//File-Content is not checked
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), name);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "ResultTemplate{" +
                "name='" + name + '\'' +
                '}';
    }
}
