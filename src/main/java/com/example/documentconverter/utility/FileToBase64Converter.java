package com.example.documentconverter.utility;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class FileToBase64Converter {

    /**
     * Main reusable method
     */
    public String convertToBase64(File file) {

        try {

            validateFile(file);

            String fileName =
                    file.getName().toLowerCase();

            byte[] pdfBytes;

            /*
             * If already PDF
             */
            if (isPdf(fileName)) {

                pdfBytes =
                        Files.readAllBytes(file.toPath());

            }
            /*
             * If image
             */
            else if (isImage(fileName)) {

                pdfBytes =
                        convertImageToPdf(file);

            }
            /*
             * Unsupported file
             */
            else {

                throw new RuntimeException(
                        "Unsupported file type"
                );
            }

            /*
             * Convert PDF bytes to Base64
             */
            return Base64.getEncoder()
                    .encodeToString(pdfBytes);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Conversion failed",
                    ex
            );
        }
    }

    /**
     * Check PDF
     */
    private boolean isPdf(String fileName) {

        return fileName.endsWith(".pdf");
    }

    /**
     * Check image
     */
    private boolean isImage(String fileName) {

        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png");
    }

    /**
     * Image -> PDF
     */
    private byte[] convertImageToPdf(File imageFile) {

        try (
                PDDocument document =
                        new PDDocument();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            /*
             * Load image
             */
            PDImageXObject image =
                    PDImageXObject.createFromFile(
                            imageFile.getAbsolutePath(),
                            document
                    );

            /*
             * Original image size
             */
            float imageWidth = image.getWidth();
            float imageHeight = image.getHeight();

            /*
             * Create A4 page
             */
            PDPage page =
                    new PDPage(PDRectangle.A4);

            document.addPage(page);

            float pageWidth =
                    page.getMediaBox().getWidth();

            float pageHeight =
                    page.getMediaBox().getHeight();

            /*
             * Margin
             */
            float margin = 20;

            /*
             * Available drawing area
             */
            float maxWidth =
                    pageWidth - (2 * margin);

            float maxHeight =
                    pageHeight - (2 * margin);

            /*
             * Maintain aspect ratio
             */
            float widthScale =
                    maxWidth / imageWidth;

            float heightScale =
                    maxHeight / imageHeight;

            /*
             * Choose smaller scale
             */
            float scale =
                    Math.min(widthScale, heightScale);

            /*
             * Final image size
             */
            float finalWidth =
                    imageWidth * scale;

            float finalHeight =
                    imageHeight * scale;

            /*
             * Center image on page
             */
            float x =
                    (pageWidth - finalWidth) / 2;

            float y =
                    (pageHeight - finalHeight) / 2;

            /*
             * Draw image
             */
            try (PDPageContentStream contentStream =
                         new PDPageContentStream(
                                 document,
                                 page
                         )) {

                contentStream.drawImage(
                        image,
                        x,
                        y,
                        finalWidth,
                        finalHeight
                );
            }

            /*
             * Save PDF
             */
            document.save(outputStream);

            return outputStream.toByteArray();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Image to PDF conversion failed",
                    ex
            );
        }
    }

    /**
     * Validation
     */
    private void validateFile(File file) {

        if (file == null) {
            throw new RuntimeException(
                    "File is null"
            );
        }

        if (!file.exists()) {
            throw new RuntimeException(
                    "File does not exist"
            );
        }

        if (file.length() == 0) {
            throw new RuntimeException(
                    "File is empty"
            );
        }
    }
}