package de.baleipzig.pdfextraction.client.utils.injector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If multiple Implementation of an Interface exist, this Interface provides a Way to prioritize one above the other
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ImplementationOrder {

    /**
     * Higher Order means an implementing class is chosen when an Interface is to be instantiated
     *
     * @return The configured Prioritization
     */
    int order() default 0;
}
