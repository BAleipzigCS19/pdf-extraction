package de.baleipzig.pdfextraction.api.dto;

import de.baleipzig.pdfextraction.fieldtype.FieldType;

import java.util.Objects;

public class Field {
    private FieldType type;
    private int page;
    private double xPos;
    private double yPos;
    private double percWidth;
    private double percHeight;

    public Field() {
    }

    public Field(FieldType type, int page, double xPos, double yPos, double percWidth, double percHeight) {
        this.type = type;
        this.page = page;
        this.xPos = xPos;
        this.yPos = yPos;
        this.percWidth = percWidth;
        this.percHeight = percHeight;
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

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public double getPercWidth() {
        return percWidth;
    }

    public void setPercWidth(double percWidth) {
        this.percWidth = percWidth;
    }

    public double getPercHeight() {
        return percHeight;
    }

    public void setPercHeight(double percHeight) {
        this.percHeight = percHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field other)) {
            return false;
        }
        return page == other.page
                && Double.compare(other.xPos, xPos) == 0
                && Double.compare(other.yPos, yPos) == 0
                && Double.compare(other.percWidth, percWidth) == 0
                && Double.compare(other.percHeight, percHeight) == 0
                && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, page, xPos, yPos, percWidth, percHeight);
    }

    @Override
    public String toString() {
        return "Field{" +
                "type=" + type +
                ", page=" + page +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", percWidth=" + percWidth +
                ", percHeight=" + percHeight +
                '}';
    }
}
