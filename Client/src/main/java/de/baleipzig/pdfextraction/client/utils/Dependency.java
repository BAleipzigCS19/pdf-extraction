package de.baleipzig.pdfextraction.client.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Dependency {

    private final String name;
    private final String version;
    private final String link;
    private final String license;

    @JsonCreator
    public Dependency(
            @JsonProperty("name") String name,
            @JsonProperty("version") String version,
            @JsonProperty("link") String link,
            @JsonProperty("license") String license) {
        this.name = name;
        this.version = version;
        this.link = link;
        this.license = license;
    }

    @Override
    public String toString() {
        return "dependency{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", link='" + link + '\'' +
                ", license='" + license + '\'' +
                '}';
    }

    // no setters are intendet here, bean Propertys should only be set through JSON parser

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getLink() {
        return link;
    }

    public String getLicense() {
        return license;
    }

}
