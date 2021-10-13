package de.baleipzig.pdfextraction.api;

import java.time.LocalDate;
import java.util.Objects;

public class ExpirationDate extends ApiBase {

    private LocalDate date;

    public ExpirationDate(LocalDate expirationDate) {
        this.date = expirationDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpirationDate that = (ExpirationDate) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "ExpirationDate{" +
                "date=" + date +
                '}';
    }
}
