package com.translate.project.helper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.InputDataConfig;
import com.amazonaws.services.translate.model.OutputDataConfig;
import com.amazonaws.services.translate.model.StartTextTranslationJobRequest;
import com.amazonaws.services.translate.model.StartTextTranslationJobResult;
import com.translate.project.constants.LanguageCodes;
import com.translate.project.service.S3UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class AWSTranslateHelper {

    private Logger logger = LoggerFactory.getLogger(AWSTranslateHelper.class);

    @Value("cloud.aws.s3.input.uri")
    private String s3UriInput;

    @Value("cloud.aws.s3.output.uri")
    private String s3UriOutput;

    @Value("cloud.aws.translate.arn")
    private String translateRoleArn;

    @Autowired
    private AmazonTranslate amazonTranslateClient;

    public String translateDocument(String originalLang, String translationLang){

        try {
            logger.info("Building translation job");
            StartTextTranslationJobRequest request = generateJobRequest(originalLang, translationLang);
            logger.info("Translation Job submitted");
            StartTextTranslationJobResult result = amazonTranslateClient.startTextTranslationJob(request);
            logger.info("Translation Job status={}", result.getJobStatus());
            return result.getJobStatus();
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException: " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }
    }

    private StartTextTranslationJobRequest generateJobRequest(String originalLang, String translationLang) {

        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        StartTextTranslationJobRequest request = new StartTextTranslationJobRequest();
        InputDataConfig iConfig = new InputDataConfig();
        iConfig.setS3Uri(s3UriInput);
        iConfig.setContentType("docx");
        OutputDataConfig oConfig = new OutputDataConfig();
        oConfig.setS3Uri(s3UriOutput);
        request.setInputDataConfig(iConfig);
        request.setOutputDataConfig(oConfig);
        request.setSourceLanguageCode(LanguageCodes.valueOf(originalLang.toUpperCase()).getLangCode());
        request.setTargetLanguageCodes(Collections.singleton(LanguageCodes.valueOf(translationLang.toUpperCase()).getLangCode()));
        request.setDataAccessRoleArn(translateRoleArn);
        request.setJobName("TranslationJob");
        request.setClientToken(token);
        return  request;
    }


}
