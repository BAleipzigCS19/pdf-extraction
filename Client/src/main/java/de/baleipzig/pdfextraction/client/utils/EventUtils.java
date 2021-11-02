package de.baleipzig.pdfextraction.client.utils;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;

import java.util.Objects;

public final class EventUtils {

    private static final String ERR_MSG_ACTION = "The provided Action cannot be null";
    private static final String ERR_MSG_PROVIDER = "The provider cannot be null";

    /**
     * Chains the given Action after the one that is set in the Button
     *
     * @param onActionProvider The Button where to chain the onAction on
     * @param runAfter         Action to run after the one set on the Button (if it exists)
     */
    public static void chainAfterOnAction(final ButtonBase onActionProvider, final Runnable runAfter) {
        Objects.requireNonNull(runAfter, ERR_MSG_ACTION);
        Objects.requireNonNull(onActionProvider, ERR_MSG_PROVIDER);

        chainAfterOnAction(onActionProvider, ev -> runAfter.run());
    }

    /**
     * Chains the given Action after the one that is set in the Button
     *
     * @param onActionProvider The Button where to chain the onAction on
     * @param toChain          Action to run after the one set on the Button (if it exists)
     */
    public static void chainAfterOnAction(final ButtonBase onActionProvider, final EventHandler<ActionEvent> toChain) {
        Objects.requireNonNull(toChain, ERR_MSG_ACTION);
        Objects.requireNonNull(onActionProvider, ERR_MSG_PROVIDER);

        chainAfter(onActionProvider.onActionProperty(), toChain);
    }

    /**
     * Chains the given Action after the one that is set in the Menu Item
     *
     * @param onActionProvider The Menu Button where to chain the onAction on
     * @param runAfter         Action to run after the one set on the Menu Item (if it exists)
     */
    public static void chainAfterOnAction(final MenuItem onActionProvider, final Runnable runAfter) {
        Objects.requireNonNull(runAfter, ERR_MSG_ACTION);
        Objects.requireNonNull(onActionProvider, ERR_MSG_PROVIDER);

        chainAfter(onActionProvider.onActionProperty(), ev -> runAfter.run());
    }

    /**
     * Chains the given Action after the one that is set in the Button
     *
     * @param onActionProvider The Menu Item where to chain the onAction on
     * @param toChain          Action to run after the one set on the Menu Item (if it exists)
     */
    public static void chainAfterOnAction(final MenuItem onActionProvider, final EventHandler<ActionEvent> toChain) {
        Objects.requireNonNull(toChain, ERR_MSG_ACTION);
        Objects.requireNonNull(onActionProvider, ERR_MSG_PROVIDER);

        chainAfter(onActionProvider.onActionProperty(), toChain);
    }

    private static void chainAfter(final ObjectProperty<EventHandler<ActionEvent>> property, final EventHandler<ActionEvent> toChain) {
        final EventHandler<ActionEvent> base = property.get();
        property.set(base == null ?
                toChain :
                ev -> {
                    base.handle(ev);
                    toChain.handle(ev);
                });
    }

    private EventUtils() {
    }
}
