package com.translate.project.controller;



import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.translate.project.helper.AWSTranslateHelper;
import com.translate.project.service.S3UploadService;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "v1")
public class TranslateController {

    @Autowired
    private S3UploadService uploadService;

    @Autowired
    private AWSTranslateHelper helper;

    @PostMapping(
    path = "/translate",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Resource> saveTodo(@RequestParam("originalLang") String originalLang,
                                             @RequestParam("translationLang") String translationLang,
                                             @RequestParam("file") MultipartFile file) {

        S3ObjectInputStream res= helper.getTranslatedFileUrl(file.getOriginalFilename(), file.getOriginalFilename(), translationLang);

        InputStreamResource resource = new InputStreamResource(res);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }
}
