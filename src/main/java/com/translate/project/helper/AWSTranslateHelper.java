package com.translate.project.helper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.*;
import com.translate.project.constants.LanguageCodes;
import com.translate.project.service.S3UploadService;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AWSTranslateHelper {

    private Logger logger = LoggerFactory.getLogger(AWSTranslateHelper.class);

//    private final String JOB_NAME = "TranslationJob";

    @Value("${cloud.aws.s3.input.uri}")
    private String s3UriInput;

    @Value("${cloud.aws.s3.output.uri}")
    private String s3UriOutput;

    @Value("${cloud.aws.translate.arn}")
    private String translateRoleArn;

    @Autowired
    private S3UploadService s3UploadService;

    @Autowired
    private AmazonTranslate amazonTranslateClient;

    public String translateDocument(String originalLang, String translationLang, String fileName){

        try {
            logger.info("Building translation job");
            StartTextTranslationJobRequest request = generateJobRequest(originalLang, translationLang, fileName);
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

    private StartTextTranslationJobRequest generateJobRequest(String originalLang, String translationLang, String fileName) {

        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        StartTextTranslationJobRequest request = new StartTextTranslationJobRequest();
        InputDataConfig iConfig = new InputDataConfig();
        iConfig.setS3Uri(s3UriInput);
        iConfig.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        OutputDataConfig oConfig = new OutputDataConfig();
        oConfig.setS3Uri(s3UriOutput);
        request.setInputDataConfig(iConfig);
        request.setOutputDataConfig(oConfig);
        request.setSourceLanguageCode(LanguageCodes.valueOf(originalLang.toUpperCase()).getLangCode());
        request.setTargetLanguageCodes(Collections.singleton(LanguageCodes.valueOf(translationLang.toUpperCase()).getLangCode()));
        request.setDataAccessRoleArn(translateRoleArn);
        request.setJobName(fileName);
        request.setClientToken(token);
        return  request;
    }

    public S3ObjectInputStream getTranslatedFileUrl(String jobName, String fileName, String translatedLanguage) {
        String result = getTranslationJobStatus(jobName);
//        fileName = LanguageCodes.valueOf(translatedLanguage.toUpperCase()).getLangCode() + "." + fileName;
        S3ObjectInputStream object = s3UploadService.getTranslatedFileUrl(result, fileName);
        return object;
//        return result;

    }

    private String getTranslationJobStatus(String jobName) {

        ListTextTranslationJobsRequest req = new ListTextTranslationJobsRequest();
        TextTranslationJobFilter filter = new TextTranslationJobFilter();
        filter.setJobName(jobName);
        req.setFilter(filter);

        ListTextTranslationJobsResult res = amazonTranslateClient.listTextTranslationJobs(req);
        List<TextTranslationJobProperties> list = res.getTextTranslationJobPropertiesList();
        String jobResult =  list.get(0).getJobStatus().equals("COMPLETED") ? list.get(0).getOutputDataConfig().getS3Uri() : null;

        return jobResult;

    }




}
