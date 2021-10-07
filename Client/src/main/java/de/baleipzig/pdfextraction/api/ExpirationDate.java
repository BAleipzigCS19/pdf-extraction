package de.baleipzig.pdfextraction.api;

import java.time.LocalDate;

public class ExpirationDate extends ApiBase{

    private LocalDate expirationDate;

    public ExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
