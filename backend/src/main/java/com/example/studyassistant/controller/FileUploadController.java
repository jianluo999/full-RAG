package com.example.studyassistant.controller;

import com.example.studyassistant.entity.Document;
import com.example.studyassistant.repository.DocumentRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            Document newDocument = new Document(file.getOriginalFilename(), text);
            documentRepository.save(newDocument);

            return new ResponseEntity<>("File uploaded and saved successfully!", HttpStatus.OK);

        } catch (IOException e) {
            // Log the exception
            return new ResponseEntity<>("Failed to parse or save PDF file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 