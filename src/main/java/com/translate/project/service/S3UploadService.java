package com.translate.project.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

@Service
public class S3UploadService {
    private Logger logger = LoggerFactory.getLogger(S3UploadService.class);

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    private final String OUT_FOL = bucketName + "/outputFolder";

    /**
     * Upload file into AWS S3
     *
     * @param keyName
     * @param file
     * @return String
     */
    public String uploadFile(String keyName, MultipartFile file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType("docx");
            keyName = "inputFolder/" + keyName;
            PutObjectResult t = amazonS3Client.putObject(bucketName, keyName, file.getInputStream(), metadata);
            URL fileUrl = amazonS3Client.getUrl(bucketName, keyName);
            return fileUrl.toString();
        } catch (IOException ioe) {
            logger.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException: " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }
        return "File not uploaded: " + keyName;
    }

    public S3ObjectInputStream getTranslatedFileUrl(String url, String fileName) {
        try {
            String[] str = url.split("/");
            AccessControlList acl = amazonS3Client.getObjectAcl(bucketName, str[3]+"/en."+fileName);
            if(!acl.getGrantsAsList().get(0).getPermission().equals("Read")) {
                amazonS3Client.setObjectAcl(bucketName, str[3]+"/en."+fileName, CannedAccessControlList.PublicRead);
            }

            S3Object object = amazonS3Client.getObject(new GetObjectRequest(bucketName, str[3]+"/en."+fileName));
            S3ObjectInputStream stream = object.getObjectContent();
//            getFIle(object.getObjectContent().getHttpRequest());
            return stream;
        } catch (AmazonServiceException serviceException) {
            logger.info("AmazonServiceException: " + serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            logger.info("AmazonClientException Message: " + clientException.getMessage());
            throw clientException;
        }


    }

    public void getFIle(HttpRequestBase req) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(req);

//        HttpEntity entity = response.getEntity();
//        File file = new File("file");

    }

}
