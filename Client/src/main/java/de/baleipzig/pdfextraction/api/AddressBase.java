package de.baleipzig.pdfextraction.api;

public class AddressBase extends ApiBase{

    private String title;
    private String name;
    private String street;
    private String postcode;
    private String town;

    public AddressBase(String title, String name, String street, String postcode, String town) {
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
}
