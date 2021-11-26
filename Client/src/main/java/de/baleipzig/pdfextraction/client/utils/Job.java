package de.baleipzig.pdfextraction.client.utils;

import jakarta.inject.Singleton;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.util.Objects;

@Singleton
public class Job {

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private String templateName;
    private Path pathToFile;

    public Path getPathToFile() {
        return pathToFile;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setPathToFile(Path pathToFile) {

        Path oldValue = this.pathToFile;
        this.pathToFile = pathToFile;
        // Import Controller listens on this Property
        changes.firePropertyChange("pathToFile", oldValue, pathToFile);
    }

    public void setTemplateName(String templateName) {

        String oldValue = this.templateName;
        this.templateName = templateName;
        // Import Controller listens on this Property
        changes.firePropertyChange("templateName", oldValue, templateName);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
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
