package com.translate.project.controller;



import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.translate.project.helper.AWSTranslateHelper;
import com.translate.project.service.S3UploadService;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
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
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<?> saveTodo(@RequestParam("originalLang") String originalLang,
                                             @RequestParam("translationLang") String translationLang,
                                             @RequestParam("file") MultipartFile file) {
//
        S3ObjectInputStream res= helper.getTranslatedFileUrl(file.getOriginalFilename(), file.getOriginalFilename(), translationLang);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
