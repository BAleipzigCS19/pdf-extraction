package de.baleipzig.pdfextraction.api.fields;

import java.util.Objects;

public class AddressBase implements ApiBase {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
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
