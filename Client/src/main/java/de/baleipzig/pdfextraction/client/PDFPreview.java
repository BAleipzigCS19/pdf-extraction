package de.baleipzig.pdfextraction.client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;


public class PDFPreview {


        public static void createPreview(VBox previewWrapper){

                // TODO: pdfname als übergabeparameter für die funktion festlegen, so dass man in einer anderen klasse die pdf auswählen kann die man in der vorschau haben will
                String pdfname = "Projektarbeit_Wilhelm_Kuhring.pdf";
                final String pdfPath = "D:\\Intelij_Projects\\PdfBox_Examples\\" + pdfname;
                String imagePath = null;

                try {
                        imagePath = PDFPreview.convertPDFtoImage(pdfPath);
                        loadImageToImportView(imagePath, previewWrapper);

                } catch (IOException e) {
                        LoggerFactory.getLogger(PDFPreview.class)
                        .atError()
                                .addArgument(pdfPath)
                                .setCause(e)
                                .log("Exception occurred while converting the pdf {} into an image");
                }

        }

        private static String convertPDFtoImage(final String pdfPath) throws IOException{

                PDDocument document = PDDocument.load(new File(pdfPath));
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                //TODO: for schleife für mehrere Seiten / deswegen ist der Pfad auch aufgesplittet
                int pageIndex = 0;
                final String imageDirectory = "src/main/resources/images";
                final String imageName = "/image-" + pageIndex + ".png";
                final String imagePath = imageDirectory + imageName;

                cleanImageDirectory(imageDirectory);

                BufferedImage bim = pdfRenderer.renderImage(pageIndex);
                ImageIO.write(bim, "PNG", new File(imagePath));

                document.close();

                // hier könnte man dann eine Liste mit den erzeugten Images zurückgeben
                return imagePath;
        }

        private static void loadImageToImportView(final String imagepath, VBox PreviewWrapper) throws FileNotFoundException {

                FileInputStream inputStream = new FileInputStream(imagepath);
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);

                imageView.setFitHeight(400);
                imageView.setFitWidth(300);

                PreviewWrapper.getChildren().add(imageView);
        }

        private static void cleanImageDirectory(final String imageDirectory){

                final File[] files = new File(imageDirectory).listFiles();
                if(files != null) {
                        for (File f : files) {
                                f.delete();
                        }
                }
        }

}
