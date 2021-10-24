package de.baleipzig.pdfextraction.client.utils;

import jakarta.inject.Singleton;

import java.nio.file.Path;
import java.util.Objects;

@Singleton
public class Job {

    private String templateName;
    private Path pathToFile;

    public Job() {
        //empty Constructor
    }

    public Path getPathToFile() {
        return pathToFile;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setPathToFile(Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Job other)) {
            return false;
        }
        return Objects.equals(templateName, other.templateName)
                && Objects.equals(pathToFile, other.pathToFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateName, pathToFile);
    }

    @Override
    public String toString() {
        return "Job{" +
                "templateName='" + templateName + '\'' +
                ", pathToFile=" + pathToFile +
                '}';
    }
}
