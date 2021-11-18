package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.api.ResultConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import de.baleipzig.pdfextraction.client.view.TemplateOverview;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Locale;

public class MenuBarController extends Controller {

    @FXML
    public MenuItem chooseFile;

    @FXML
    public MenuItem createTemplate;

    @FXML
    public MenuBar menuBar;

    @Inject
    private PDFRenderer renderer;

    @Inject
    private Job job;

    @Inject
    private ResultConnector resultConnector;

    @FXML
    private void onCreateTemplate() {

        switchScene((Stage) this.menuBar.getScene().getWindow(), new CreateTemplate());
    }

    @FXML
    public void onShowTemplates() {

        switchScene((Stage) this.menuBar.getScene().getWindow(), new TemplateOverview());
    }

    @FXML
    public void onChooseFile() {
        // Filechooser
        final Stage current = (Stage) this.menuBar.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(List.of(new FileChooser.ExtensionFilter("PDFs", "*.pdf")));
        final File selectedFile = fileChooser.showOpenDialog(current);

        if (selectedFile == null) {
            //User canceled the dialog
            return;
        }

        final Path pdfPath = selectedFile.toPath();
        job.setPathToFile(pdfPath);
        this.renderer.setPdfPath(pdfPath);
    }

    /**
     * changes the Language Locale and reloads the Scene with the new Ressource Bundle
     *
     * @param locale
     */
    public void onChangeLanguage(Locale locale) {
        Locale.setDefault(locale);
        reloadScene((Stage) menuBar.getScene().getWindow());
    }

    public void onChangeLanguageGerman() {
        onChangeLanguage(Locale.GERMAN);
    }

    public void onChangeLanguageEnglish() {
        onChangeLanguage(Locale.ENGLISH);
    }



    @FXML
    public void onSaveResultTemplate() {
        final Stage current = (Stage) this.menuBar.getScene().getWindow();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().setAll(List.of(new FileChooser.ExtensionFilter("Open Document Template Dokumente", "*.odt")));
        final File selectedFile = fileChooser.showOpenDialog(current);

        if (selectedFile == null) {
            //User canceled the dialog
            return;
        }

        final TextInputDialog dialog = new TextInputDialog(null);
        dialog.setHeaderText("Bitte geben sie einem Namen ein, unter welchem diese Vorlage gespeichert werden soll.");
        final Optional<String> optAnswer = dialog.showAndWait();
        if (!optAnswer.map(StringUtils::hasText).orElse(false)) {
            //User canceled the dialog
            return;
        }

        this.resultConnector.saveResultTemplate(optAnswer.orElseThrow(), selectedFile.toPath())
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .doOnSuccess(v -> Platform.runLater(() -> AlertUtils.showAlert(Alert.AlertType.INFORMATION, null, null, "Erfolgreich gespeichert")))
                .subscribe();
    }
}
