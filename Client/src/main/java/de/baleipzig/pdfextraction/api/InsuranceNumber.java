package de.baleipzig.pdfextraction.api;

import java.util.Objects;

public class InsuranceNumber extends ApiBase {

    private String number;

    public InsuranceNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsuranceNumber that = (InsuranceNumber) o;
        return Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "InsuranceNumber{" +
                "number='" + number + '\'' +
                '}';
    }
}
