package de.baleipzig.pdfextraction.api.fields;

import java.time.LocalDate;
import java.util.Objects;

public class ExpirationDate implements ApiBase{

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
