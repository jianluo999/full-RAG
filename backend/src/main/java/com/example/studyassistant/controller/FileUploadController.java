package com.example.studyassistant.controller;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            if (!document.isEncrypted()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                return new ResponseEntity<>(text, HttpStatus.OK);
            }
            return new ResponseEntity<>("The document is encrypted.", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            // Log the exception
            return new ResponseEntity<>("Failed to parse PDF file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 