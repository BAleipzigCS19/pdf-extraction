package de.baleipzig.pdfextraction.client.view;

import de.baleipzig.pdfextraction.client.utils.interfaces.ControllerProvider;
import de.baleipzig.pdfextraction.client.utils.interfaces.FXMLProvider;
import de.baleipzig.pdfextraction.client.utils.interfaces.InjectableProvider;
import de.baleipzig.pdfextraction.client.utils.interfaces.ResourceBundleProvider;

public interface FXView extends ResourceBundleProvider, FXMLProvider, ControllerProvider, InjectableProvider {

}
