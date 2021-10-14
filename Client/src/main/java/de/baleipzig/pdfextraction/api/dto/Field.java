package de.baleipzig.pdfextraction.api.dto;

import de.baleipzig.pdfextraction.fieldtype.FieldType;

import java.util.Objects;

public class Field {
    private FieldType type;
    private int page;
    private double xPosPercentage;
    private double yPosPercentage;
    private double widthPercentage;
    private double heightPercentage;

    public Field() {
    }

    public Field(FieldType type, int page, double percXPos, double yPos, double percWidth, double percHeight) {
        this.type = type;
        this.page = page;
        this.xPosPercentage = percXPos;
        this.yPosPercentage = yPos;
        this.widthPercentage = percWidth;
        this.heightPercentage = percHeight;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public double getxPosPercentage() {
        return xPosPercentage;
    }

    public void setxPosPercentage(double xPosPercentage) {
        this.xPosPercentage = xPosPercentage;
    }

    public double getyPosPercentage() {
        return yPosPercentage;
    }

    public void setyPosPercentage(double yPosPercentage) {
        this.yPosPercentage = yPosPercentage;
    }

    public double getWidthPercentage() {
        return widthPercentage;
    }

    public void setWidthPercentage(double widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public double getHeightPercentage() {
        return heightPercentage;
    }

    public void setHeightPercentage(double heightPercentage) {
        this.heightPercentage = heightPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field other)) {
            return false;
        }
        return page == other.page
                && Double.compare(other.xPosPercentage, xPosPercentage) == 0
                && Double.compare(other.yPosPercentage, yPosPercentage) == 0
                && Double.compare(other.widthPercentage, widthPercentage) == 0
                && Double.compare(other.heightPercentage, heightPercentage) == 0
                && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, page, xPosPercentage, yPosPercentage, widthPercentage, heightPercentage);
    }

    @Override
    public String toString() {
        return "Field{" +
                "type=" + type +
                ", page=" + page +
                ", xPosPercentage=" + xPosPercentage +
                ", yPosPercentage=" + yPosPercentage +
                ", widthPercentage=" + widthPercentage +
                ", heightPercentage=" + heightPercentage +
                '}';
    }
}
