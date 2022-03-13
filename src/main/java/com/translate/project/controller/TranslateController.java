package com.translate.project.controller;



import com.translate.project.helper.AWSTranslateHelper;
import com.translate.project.service.S3UploadService;
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
    produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> saveTodo(@RequestParam("originalLang") String originalLang,
                                           @RequestParam("translationLang") String translationLang,
                                           @RequestParam("file") MultipartFile file) {
//        return new ResponseEntity<>(uploadService.uploadFile(file.getOriginalFilename(), file), HttpStatus.OK);
            String uploadResult = uploadService.uploadFile(file.getOriginalFilename(), file);
            if(uploadResult.equals("SUCCESS")) {
                String response = helper.translateDocument(originalLang, translationLang);
//                if (response.equals("COMPLETED ") || response.equals("COMPLETED_WITH_ERROR ")) {
//
//                }

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        return ResponseEntity.ok().build();
    }
}
