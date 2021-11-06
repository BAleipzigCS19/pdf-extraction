package de.baleipzig.pdfextraction.client.utils;

import com.sun.javafx.application.PlatformImpl;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@EnabledOnOs({OS.WINDOWS, OS.MAC})
class EventUtilsTest {

    @BeforeAll
    static void setUp() {
        PlatformImpl.startup(() -> {
        });
    }


    private static void checkTimings(final AtomicReference<Instant> original, final AtomicReference<Instant> chained) {
        Assertions.assertNotNull(original.get());
        Assertions.assertNotNull(chained.get());
        Assertions.assertTrue(original.get().compareTo(chained.get()) <= 0);
    }

    @Test
    @DisplayName("Test if the action runs actually after on a Button with Runnable")
    void testButtonRunnable() {
        final AtomicReference<Instant> original = new AtomicReference<>(null);
        final AtomicReference<Instant> chained = new AtomicReference<>(null);

        final Button button = new Button();
        button.setOnAction(ev -> original.set(Instant.now()));
        EventUtils.chainAfterOnAction(button, () -> chained.set(Instant.now()));
        button.fire();

        checkTimings(original, chained);
    }

    @Test
    @DisplayName("Test if the action runs actually after on a Button with Event Handle")
    void testButtonEVH() {
        final AtomicReference<Instant> original = new AtomicReference<>(null);
        final AtomicReference<Instant> chained = new AtomicReference<>(null);

        final Button button = new Button();
        button.setOnAction(ev -> original.set(Instant.now()));
        EventUtils.chainAfterOnAction(button, ev -> chained.set(Instant.now()));
        button.fire();

        checkTimings(original, chained);
    }

    @Test
    @DisplayName("Test if the action runs actually after on a Menu Item with Runnable")
    void testMenuItemRunnable() {
        final AtomicReference<Instant> original = new AtomicReference<>(null);
        final AtomicReference<Instant> chained = new AtomicReference<>(null);

        final MenuItem item = new MenuItem();
        item.setOnAction(ev -> original.set(Instant.now()));
        EventUtils.chainAfterOnAction(item, () -> chained.set(Instant.now()));
        item.fire();

        checkTimings(original, chained);
    }

    @Test
    @DisplayName("Test if the action runs actually after on a Menu Item with Event Handle")
    void testMenuItemEVH() {
        final AtomicReference<Instant> original = new AtomicReference<>(null);
        final AtomicReference<Instant> chained = new AtomicReference<>(null);

        final MenuItem item = new MenuItem();
        item.setOnAction((ev) -> original.set(Instant.now()));
        EventUtils.chainAfterOnAction(item, ev -> chained.set(Instant.now()));
        item.fire();

        checkTimings(original, chained);
    }

    @Test
    @DisplayName("Test if the Chain is working even with a null as previous onAction")
    void testNull() {

        final AtomicReference<Instant> ref = new AtomicReference<>(null);

        final Button button = new Button();
        button.setOnAction(null);
        EventUtils.chainAfterOnAction(button, () -> ref.set(Instant.now()));
        button.fire();

        Assertions.assertNotNull(ref.get());
    }

    @Test
    @DisplayName("Test if the Chain is working even with a null as previous onAction")
    void testDefault() {

        final AtomicReference<Instant> ref = new AtomicReference<>(null);

        final Button button = new Button();
        EventUtils.chainAfterOnAction(button, () -> ref.set(Instant.now()));
        button.fire();

        Assertions.assertNotNull(ref.get());
    }


}
