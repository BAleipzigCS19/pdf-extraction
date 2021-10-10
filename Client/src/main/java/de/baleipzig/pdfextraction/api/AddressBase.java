package de.baleipzig.pdfextraction.api;

import java.util.Objects;

public class AddressBase extends ApiBase{

    private String title;
    private String name;
    private String street;
    private String postcode;
    private String town;

    protected AddressBase(String title, String name, String street, String postcode, String town) {
        this.title = title;
        this.name = name;
        this.street = street;
        this.postcode = postcode;
        this.town = town;
    }

    protected String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getStreet() {
        return street;
    }

    protected void setStreet(String street) {
        this.street = street;
    }

    protected String getPostcode() {
        return postcode;
    }

    protected void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    protected String getTown() {
        return town;
    }

    protected void setTown(String town) {
        this.town = town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressBase that = (AddressBase) o;
        return Objects.equals(title, that.title) && Objects.equals(name, that.name) && Objects.equals(street, that.street) && Objects.equals(postcode, that.postcode) && Objects.equals(town, that.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, name, street, postcode, town);
    }

    @Override
    public String toString() {
        return "AddressBase{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", postcode='" + postcode + '\'' +
                ", town='" + town + '\'' +
                '}';
    }
}
