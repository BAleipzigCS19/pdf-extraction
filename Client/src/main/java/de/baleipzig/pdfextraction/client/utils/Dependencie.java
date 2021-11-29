package de.baleipzig.pdfextraction.client.utils;

public class Dependencie {

    private String name;
    private String version;
    private String link;
    private String license;

    public Dependencie () {

    }

    public Dependencie(String name, String version, String link, String license) {
        this.name = name;
        this.version = version;
        this.link = link;
        this.license = license;
    }

    @Override
    public String toString() {
        return "dependencie{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", link='" + link + '\'' +
                ", license='" + license + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
