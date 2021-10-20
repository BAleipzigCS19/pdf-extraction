package de.baleipzig.pdfextraction.client.view;

import de.baleipzig.pdfextraction.client.controller.ImportController;
import de.baleipzig.pdfextraction.client.controller.PdfPreviewController;

public class Imports implements FXView {
    private final PdfPreviewController previewController = new PdfPreviewController();
    private final ImportController importController = new ImportController();

}
