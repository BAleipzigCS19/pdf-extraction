package de.baleipzig.pdfextraction.client.utils.views;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static java.util.ResourceBundle.getBundle;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2015 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 *
 * @author adam-bien.com
 */
public abstract class FXMLView extends StackPane {

    public static final String DEFAULT_ENDING = "View";
    protected static final ExecutorService PARENT_CREATION_POOL = getExecutorService();
    protected final Function<String, Object> injectionContext;
    protected ObjectProperty<Object> presenterProperty;
    protected FXMLLoader fxmlLoader;
    protected String bundleName;
    protected ResourceBundle bundle;
    protected URL resource;

    /**
     * Constructs the view lazily (fxml is not loaded) with empty injection
     * context.
     */
    protected FXMLView() {
        this(f -> null);
    }

    /**
     * @param injectionContext the function is used as an injection source.
     *                         Values matching for the keys are going to be used for injection into the
     *                         corresponding presenter.
     */
    protected FXMLView(Function<String, Object> injectionContext) {
        this.injectionContext = injectionContext;
        this.init(getFXMLName());
    }

    static String stripEnding(String clazz) {
        if (!clazz.endsWith(DEFAULT_ENDING)) {
            return clazz;
        }
        int viewIndex = clazz.lastIndexOf(DEFAULT_ENDING);
        return clazz.substring(0, viewIndex);
    }

    public static ResourceBundle getResourceBundle(String name) {
        try {
            return getBundle(name);
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    static ExecutorService getExecutorService() {
        return Executors.newCachedThreadPool(r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            String name = thread.getName();
            thread.setName("afterburner.fx-" + name);
            thread.setDaemon(true);
            return thread;
        });
    }

    private void init(final String conventionalName) {
        this.presenterProperty = new SimpleObjectProperty<>();
        this.resource = getClass().getResource(conventionalName);
        this.bundleName = getBundleName();
        this.bundle = getResourceBundle(bundleName);
    }

    FXMLLoader loadSynchronously(final URL resource, ResourceBundle bundle, final String conventionalName) throws IllegalStateException {
        final FXMLLoader loader = new FXMLLoader(resource, bundle);
        PresenterFactory factory = discover();
        Callback<Class<?>, Object> controllerFactory = (Class<?> p) -> factory.instantiatePresenter(p, this.injectionContext);
        loader.setControllerFactory(controllerFactory);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load " + conventionalName, ex);
        }
        return loader;
    }

    PresenterFactory discover() {
        Iterable<PresenterFactory> discoveredFactories = PresenterFactory.discover();
        List<PresenterFactory> factories = StreamSupport.stream(discoveredFactories.spliterator(), false)
                .toList();
        if (factories.isEmpty()) {
            return Injector::instantiatePresenter;
        }

        if (factories.size() == 1) {
            return factories.get(0);
        }

        factories.forEach(f -> LoggerFactory.getLogger(FXMLView.class).error("More than 1 PresenterFactory found {}", f));
        throw new IllegalStateException("More than one PresenterFactories discovered");
    }

    void initializeFXMLLoader() {
        if (this.fxmlLoader == null) {
            this.fxmlLoader = this.loadSynchronously(resource, bundle, bundleName);
            this.presenterProperty.set(this.fxmlLoader.getController());
        }
    }

    /**
     * Initializes the view by loading the FXML (if not happened yet) and
     * returns the top Node (parent) specified in
     *
     * @return the node loaded by FXMLLoader
     */
    public Parent getView() {
        this.initializeFXMLLoader();
        Parent parent = fxmlLoader.getRoot();
        addCSSIfAvailable(parent);
        return parent;
    }

    /**
     * Initializes the view synchronously and invokes and passes the created
     * parent Node to the consumer within the FX UI thread.
     *
     * @param consumer - an object interested in received the Parent as callback
     */
    public void getView(Consumer<Parent> consumer) {
        Supplier<Parent> supplier = this::getView;
        supplyAsync(supplier, Platform::runLater).
                thenAccept(consumer).
                exceptionally(this::exceptionReporter);
    }

    /**
     * Creates the view asynchronously using an internal thread pool and passes
     * the parent node within the UI Thread.
     *
     * @param consumer - an object interested in received the Parent as callback
     */
    public void getViewAsync(Consumer<Parent> consumer) {
        Supplier<Parent> supplier = this::getView;
        CompletableFuture.supplyAsync(supplier, PARENT_CREATION_POOL).
                thenAcceptAsync(consumer, Platform::runLater).
                exceptionally(this::exceptionReporter);

    }

    /**
     * Scene Builder creates for each FXML document a root container. This
     * method omits the root container (e.g. AnchorPane) and gives you the
     * access to its first child.
     *
     * @return the first child of the AnchorPane
     */
    public Node getViewWithoutRootContainer() {
        final ObservableList<Node> children = getView().getChildrenUnmodifiable();
        if (children.isEmpty()) {
            return null;
        }
        return children.listIterator().next();
    }

    void addCSSIfAvailable(Parent parent) {
        URL uri = getClass().getResource(getStyleSheetName());
        if (uri == null) {
            return;
        }
        String uriToCss = uri.toExternalForm();
        parent.getStylesheets().add(uriToCss);
    }

    String getStyleSheetName() {
        return getResourceCamelOrLowerCase(false, ".css");
    }

    /**
     * @return the name of the fxml file derived from the FXML view. e.g. The
     * name for the AirhacksView is going to be airhacks.fxml.
     */
    final String getFXMLName() {
        return getResourceCamelOrLowerCase(true, ".fxml");
    }

    String getResourceCamelOrLowerCase(boolean mandatory, String ending) {
        String name = getConventionalName(true, ending);
        URL found = getClass().getResource(name);
        if (found != null) {
            return name;
        }
        LoggerFactory.getLogger(FXMLView.class)
                .atDebug()
                .addArgument(name)
                .log("File {} not found, attempting with camel case");
        name = getConventionalName(false, ending);
        found = getClass().getResource(name);
        if (mandatory && found == null) {
            LoggerFactory.getLogger(FXMLView.class)
                    .atError()
                    .addArgument(name)
                    .log("Cannot load file {}. Stopping initialization phase...");

            throw new IllegalStateException(name);
        }
        return name;
    }

    /**
     * In case the view was not initialized yet, the conventional fxml
     * (airhacks.fxml for the AirhacksView and AirhacksPresenter) are loaded and
     * the specified presenter / controller is going to be constructed and
     * returned.
     *
     * @return the corresponding controller / presenter (usually for a
     * AirhacksView the AirhacksPresenter)
     */
    public Object getPresenter() {
        this.initializeFXMLLoader();
        return this.presenterProperty.get();
    }

    /**
     * Does not initialize the view. Only registers the Consumer and waits until
     * the the view is going to be created / the method FXMLView#getView or
     * FXMLView#getViewAsync invoked.
     *
     * @param presenterConsumer listener for the presenter construction
     */
    public void getPresenter(Consumer<Object> presenterConsumer) {
        this.presenterProperty.addListener((ObservableValue<?> o, Object oldValue, Object newValue) -> presenterConsumer.accept(newValue));
    }

    /**
     * @param lowercase indicates whether the simple class name should be
     *                  converted to lowercase of left unchanged
     * @param ending    the suffix to append
     * @return the conventional name with stripped ending
     */
    protected String getConventionalName(boolean lowercase, String ending) {
        return getConventionalName(lowercase) + ending;
    }

    /**
     * @param lowercase indicates whether the simple class name should be
     * @return the name of the view without the "View" prefix.
     */
    protected String getConventionalName(boolean lowercase) {
        final String clazzWithEnding = this.getClass().getSimpleName();
        String clazz = stripEnding(clazzWithEnding);
        if (lowercase) {
            clazz = clazz.toLowerCase();
        }
        return clazz;
    }

    String getBundleName() {
        String conventionalName = getConventionalName(true);
        return this.getClass().getPackage().getName() + "." + conventionalName;
    }

    /**
     * @return an existing resource bundle, or null
     */
    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }

    /**
     * @param t exception to report
     * @return nothing
     */
    public Void exceptionReporter(Throwable t) {
        LoggerFactory.getLogger(FXMLView.class)
                .atError()
                .setCause(t)
                .log("Exception occurred.");
        return null;
    }

}
