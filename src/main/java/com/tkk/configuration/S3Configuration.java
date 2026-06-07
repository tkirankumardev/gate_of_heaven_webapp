package com.tkk.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Configuration {

    @Value("${aws.region}")
    private String region;

    @Bean(name = "S3bean")
    @Profile("dev")
    public S3Client S3ClientDev(@Value("${aws.access.key}") String accessKey,
                                @Value("${aws.secret.key}") String secretKey) {

        System.out.println("Starting S3 Client in LOCAL mode using static keys.");
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey,secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    @Bean(name = "S3bean")
    @Profile("prod")
    public S3Client S3ClientProd(){
        System.out.println("Starting S3 Client in PROD mode using IAM Roles.");
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }


    /**
     * @return returns a bean for S3Persigner
     * @value used of generating url for certain time
     *
     * */
    @Bean
    @Profile("dev")
    public S3Presigner s3PresignerDev(@Value("${aws.access.key}") String accessKey,
                                   @Value("${aws.secret.key}") String secretKey){
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey,secretKey);
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    @Bean
    @Profile("prod")
    public S3Presigner s3PresignerProd(){
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}
