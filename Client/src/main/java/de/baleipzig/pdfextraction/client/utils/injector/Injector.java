package de.baleipzig.pdfextraction.client.utils.injector;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Injector {
    private static final Map<Class<?>, Object> singletonMap = new ConcurrentHashMap<>();
    private static final Reflections reflections = new Reflections(new ConfigurationBuilder()
            .forPackage("de.baleipzig")
            .setScanners(Scanners.SubTypes));

    /**
     * Creates an Instance of the given Class and injects and fields marked with {@link Inject} on this field
     *
     * @param toInstantiate Class of the Object to Instantiate
     * @return An Instance of the given Class with injected Fields
     */
    public static <T> T createInstance(Class<T> toInstantiate) {
        if (toInstantiate.isInterface()) {
            return getImplementingClass(toInstantiate);
        }

        final boolean isSingleton = toInstantiate.getAnnotation(Singleton.class) != null;

        return isSingleton ? getSingleton(toInstantiate) : getInstance(toInstantiate);
    }

    @SuppressWarnings("unchecked")//These Casts are safe
    private static <T> T getImplementingClass(Class<T> interfaceToInstantiate) {
        final Set<Class<?>> classes = reflections.get(Scanners.SubTypes.of(interfaceToInstantiate).asClass());
        if (classes.isEmpty()) {
            throw new IllegalArgumentException("Class \"%s\" has no implementation".formatted(interfaceToInstantiate.getName()));
        }

        final Optional<Class<?>> classWithHighestOrder = classes.stream()
                .max(Comparator.comparingInt(c -> Optional.ofNullable(c.getAnnotation(ImplementationOrder.class)).map(ImplementationOrder::order).orElse(-1)));
        return (T) createInstance(classWithHighestOrder.orElseThrow());
    }

    @SuppressWarnings("unchecked")//These Casts are safe
    public static <T> T getSingleton(Class<T> toInstantiate) {
        T instance = (T) singletonMap.get(toInstantiate);
        if (instance == null) {
            instance = getInstance(toInstantiate);
            singletonMap.put(toInstantiate, instance);
        }
        return instance;
    }

    @SuppressWarnings("unchecked")//These Casts are safe
    private static <T> T getInstance(Class<T> toInstantiate) {
        final Constructor<T>[] constructors = (Constructor<T>[]) toInstantiate.getDeclaredConstructors();
        final Optional<Constructor<T>> emptyConstructor = Arrays.stream(constructors)
                .filter(c -> c.getParameterCount() == 0)
                .findFirst();

        final T instance;
        if (emptyConstructor.isPresent()) {
            instance = createInstance(emptyConstructor.orElseThrow());
        } else {
            final Optional<Constructor<T>> first = Arrays.stream(constructors)
                    .filter(c -> Arrays.stream(c.getParameterTypes())
                            .map(Class::getName)
                            .noneMatch(s -> s.startsWith("java")))//Standard Classes can't be injected (yet)
                    .min(Comparator.comparingInt(Constructor::getParameterCount));

            final Constructor<T> constructor = first.orElseThrow();
            final Object[] parameter = Arrays.stream(constructor.getParameterTypes())
                    .map(Injector::createInstance)
                    .toArray();

            instance = createInstance(constructor, parameter);
        }

        if (instance == null) {
            throw new IllegalStateException("Could not instantiate Class %s".formatted(toInstantiate.getName()));
        }

        injectFields(instance);

        return instance;
    }

    private static void injectFields(Object instance) {
        for (final Field f : getInjectableFields(instance)) {
            Object toSet = Injector.createInstance(f.getType());
            try {
                doInjection(instance, toSet, f);
            } catch (IllegalAccessException e) {
                LoggerFactory.getLogger(Inject.class)
                        .atError()
                        .setCause(e)
                        .addArgument(f.getType()::getName)
                        .log("Exception while creating instance of class {}");
            }
        }
    }

    @SuppressWarnings("java:S3011")//Dependency injection needs to toggle Accessibility
    private static <T> T createInstance(Constructor<T> constructor, Object... parameter) {
        final boolean wasAccessible = constructor.canAccess(null);
        try {
            if (!wasAccessible) {
                constructor.setAccessible(true);
            }

            return constructor.newInstance(parameter);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LoggerFactory.getLogger(Inject.class)
                    .atError()
                    .setCause(e)
                    .addArgument(constructor.getDeclaringClass()::getName)
                    .log("Exception while creating instance of class {}");
        } finally {
            if (!wasAccessible) {
                constructor.setAccessible(false);
            }
        }
        return null;
    }

    @SuppressWarnings("java:S3011")//Dependency injection needs to toggle Accessibility
    private static void doInjection(Object instance, Object toSet, Field field)
            throws IllegalAccessException {
        final boolean wasAccessible = field.canAccess(instance);
        try {
            if (!wasAccessible) {
                field.setAccessible(true);
            }

            field.set(instance, toSet);
        } finally {
            if (!wasAccessible) {
                field.setAccessible(false);
            }
        }
    }

    private static Collection<Field> getInjectableFields(final Object toCheck) {
        return Arrays.stream(toCheck.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Inject.class) != null)
                .toList();
    }

    private Injector() {
    }
}
