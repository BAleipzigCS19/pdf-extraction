package de.baleipzig.pdfextraction.client.view;

import de.baleipzig.pdfextraction.client.controller.CreateTemplateController;
import de.baleipzig.pdfextraction.client.controller.PdfPreviewController;

public class CreateTemplate implements FXView {
    private final CreateTemplateController template = new CreateTemplateController();
    private final PdfPreviewController pdf = new PdfPreviewController();
}
