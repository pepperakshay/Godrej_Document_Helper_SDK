package com.example.documentconverter.service;

import com.example.documentconverter.utility.FileToBase64Converter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.io.File;

@Component
public class DemoRunner implements CommandLineRunner {

    private final FileToBase64Converter converter;

    public DemoRunner(FileToBase64Converter converter) {
        this.converter = converter;
    }

    @Override
    public void run(String... args) {

        try {

            /*
             * Input file path
             */
            File file =
                    new File("C:/sampleFilesGodrej/Sample.pdf");

            /*
             * Convert file
             */
            String base64 =
                    converter.convertToBase64(file);

            System.out.println(
                    "Conversion Successful"
            );

            byte[] pdfBytes =
                    Base64.getDecoder().decode(base64);

            Path outputPath =
                    Path.of("C:/sampleFilesGodrej/converted.pdf");

            /*
             * Create folder if not exists
             */
            Files.createDirectories(
                    outputPath.getParent()
            );

            /*
             * Write PDF
             */
            Files.write(outputPath, pdfBytes);

            System.out.println(
                    "PDF recreated successfully"
            );

            System.out.println("PDF created successfully");

            /*
             * Print first 300 chars
             */
            System.out.println(
                    base64.substring(0, 300)
            );

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}