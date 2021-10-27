package de.baleipzig.pdfextraction.client.controller;

import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.AlertUtils;
import de.baleipzig.pdfextraction.client.utils.ControllerUtils;
import de.baleipzig.pdfextraction.client.utils.Job;
import de.baleipzig.pdfextraction.client.view.Actions;
import de.baleipzig.pdfextraction.client.view.CreateTemplate;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class ImportController implements Initializable {

    @FXML
    private Button continueButton;

    @FXML
    private ComboBox<String> templateComboBox;

    @FXML
    private Button createTemplateButton;

    @Inject
    private TemplateConnector connector;

    @Inject
    private Job job;

    @FXML
    private void continueButtonOnAction() {
        final Optional<String> chosenValue = Optional.ofNullable(this.templateComboBox.getValue());
        if (chosenValue.isEmpty()) {
            //Intentionally not checking if something is set in the job
            AlertUtils.showErrorAlert("Bitte wählen sie erst eine Vorlage aus.");
            return;
        }

        if (this.job.getPathToFile() == null) {
            AlertUtils.showErrorAlert("Bitte wählen sie zuerst eine PDF Datei aus.");
            return;
        }

        chosenValue.ifPresent(this.job::setTemplateName);

        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(),
                new Actions());
    }

    @FXML
    private void createTemplateButtonOnAction() {

        ControllerUtils.switchScene((Stage) this.continueButton.getScene().getWindow(),
                new CreateTemplate());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.connector.getAllNames()
                .doOnError(err -> LoggerFactory.getLogger(ImportController.class)
                        .error("Exception while listening for response.", err))
                .doOnError(err -> Platform.runLater(() -> AlertUtils.showErrorAlert(err)))
                .subscribe(this::onRequestCompleted);

        readWordDocument("./KündigungKFZ.docx");
        updateDocument("KündigungKFZ.docx", "test.docx", "Test");
        readWordDocument("./test.docx");
        //readWordDocument("./KündigungKFZ.docx");
        //createWordDocument("./text.docx");
    }

    private void createWordDocument(final String filePath) {

        try (XWPFDocument document = new XWPFDocument()) {

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Build Your REST API with Spring");
            titleRun.setColor("009933");
            titleRun.setBold(true);
            titleRun.setFontFamily("Courier");
            titleRun.setFontSize(20);


            try (OutputStream out = Files.newOutputStream(Path.of(filePath))) {
                document.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readWordDocument(final String filePath) {

        try (XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(filePath));
             XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
             XWPFDocument document = new XWPFDocument()) {

            System.out.println(extractor.getText());
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(extractor.getText());

            try (OutputStream out = Files.newOutputStream(Path.of("test1.docx"));) {
                document.write(out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateDocument(String inputPath, String outputPath, String dateToSet) {

        try (InputStream in = Files.newInputStream(Paths.get(inputPath));
             XWPFDocument doc = new XWPFDocument(in)) {

            for (XWPFParagraph xwpfParagraph : doc.getParagraphs()) {
                for (XWPFRun xwpfRun : xwpfParagraph.getRuns()) {
                    String docText = xwpfRun.getText(0);
                    if (docText != null) {
                        docText = docText.replace("<Ablaufdatum>", dateToSet);
                        xwpfRun.setText(docText, 0);
                    }
                }
            }

            try (OutputStream out = Files.newOutputStream(Path.of(outputPath))) {
                doc.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onRequestCompleted(final String name) {
        this.templateComboBox.getItems().add(name);
    }
}
