package de.baleipzig.pdfextraction.api;

public class InsuranceNumber extends ApiBase{

    private String insuranceNumber;

    public InsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
}
