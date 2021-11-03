package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.utils.PDFRenderer;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class MenuBarController extends Controller {

    @FXML
    public MenuItem chooseFile;

    @FXML
    public MenuItem createTemplate;

    @FXML
    public MenuBar menuBar;

    @FXML
    public MenuItem english;

    @FXML
    public MenuItem deutsch;

    @Inject
    private PDFRenderer renderer;

    @Inject
    private Job job;

    @FXML
    private void onCreateTemplate() {

        switchScene((Stage) this.menuBar.getScene().getWindow(), new CreateTemplate());
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


}
