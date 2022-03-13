package com.translate.project.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AWSTranslateConfig {

    @Value("${cloud.aws.credentials.accessKey}")
    private String awsId;

    @Value("${cloud.aws.credentials.secretKey}")
    private String awsKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("cloud.aws.translate.arn")
    private String translateRoleArn;

    @Bean
    @Primary
    public AmazonTranslate translateClient() {

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsId, awsKey);
        AmazonTranslate amazonTranslateClient = AmazonTranslateClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        return amazonTranslateClient;
    }

    private BasicSessionCredentials getCredentials() {
        String roleSessionName = "translate-sesion-1.0";
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsId, awsKey);

        // Creating the STS client is part of your trusted code. It has
        // the security credentials you use to obtain temporary security credentials.
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region))
                .build();

        // Obtain credentials for the IAM role. Note that you cannot assume the role of an AWS root account;
        // Amazon S3 will deny access. You must use credentials for an IAM user or an IAM role.
        AssumeRoleRequest roleRequest = new AssumeRoleRequest()
                .withRoleArn(translateRoleArn)
                .withRoleSessionName(roleSessionName);
        AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);
        Credentials sessionCredentials = roleResponse.getCredentials();

        // Create a BasicSessionCredentials object that contains the credentials you just retrieved.
        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(
                sessionCredentials.getAccessKeyId(),
                sessionCredentials.getSecretAccessKey(),
                sessionCredentials.getSessionToken());

        return awsCredentials;
    }
}
