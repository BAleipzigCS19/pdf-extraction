package de.baleipzig.pdfextraction.utils;

@FunctionalInterface
public interface CheckedSupplier<T> {

    T get() throws Throwable;
}
