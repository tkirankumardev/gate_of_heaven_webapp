package com.tkk.service;

import com.tkk.entity.Gallery;
import com.tkk.repo.GalleryRepo;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;


@Service
public class GalleryService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final GalleryRepo galleryRepo;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public GalleryService(S3Presigner s3Presigner, S3Client s3Client, GalleryRepo galleryRepo) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
        this.galleryRepo = galleryRepo;
    }

    /**
     * @param category required for grading
     * @param metaData another requirement for better grading
     * @param date optional, default to current data if not defined
     */

    public void uploadFile(String category, String metaData, LocalDate date, MultipartFile file) throws IOException {

        try{
            UUID objectName = UUID.randomUUID();

            String key = category + "/" + metaData+ "/" + objectName + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            RequestBody body = RequestBody.fromInputStream(
                    file.getInputStream(),
                    file.getSize()
            );

            PutObjectResponse response = s3Client.putObject(request, body);

            LocalDate dateValue = (date != null) ? date : LocalDate.now();

            Gallery gallery = Gallery.builder()
                    .category(category)
                    .metaData(metaData)
                    .objectId(objectName.toString())
                    .objectExtension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                    .date(dateValue)
                    .build();

            if(response.sdkHttpResponse().isSuccessful()){
                galleryRepo.save(gallery);
            }
            else {
                System.out.println("something went wrong! Object Not saved..");
            }
        }catch (S3Exception e){
            System.out.println("aws S3 rejected the request" + e.awsErrorDetails().errorMessage());
        }catch (SdkException e){
            throw new FileUploadException("S3 client/network error occurred", e);
        }catch (IOException e){
            throw new FileUploadException("File error while uploading", e);
        }catch (Exception e){
            throw new FileUploadException("Unexcepted error during file operations");
        }


    }

    public List<Gallery> getCategoryObjects(String category, int limit){
        Pageable pageable = PageRequest.of(0, limit);
        List<Gallery> list = galleryRepo.getRandom(category, pageable);

        for(Gallery g : list){
            // 1. Construct the key using the clean database values
            String key = g.getCategory() + "/" + g.getMetaData() + "/" + g.getObjectId() + "." + g.getObjectExtension();

            // Debug line: Check your console to make sure this prints clean "events/metadata/uuid.ext"
            System.out.println("Generated S3 Key: " + key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

            // 2. CRITICAL: Save to a separate field, NOT objectId!
            g.setPresignedUrl(presignedRequest.url().toString());
        }

        return list;
    }


    /**
     * @return Map<category, Map<metaData, List<ImagesObject>>
     * @param category required category as parameter
    * */
    public Map<String, Map<String, List<Gallery>>> getObjectsBy(String category){

        List<String> metaDataList = galleryRepo.getMetaData(category);

        Map<String, List<Gallery>> objectsList = new HashMap<>();

        for(String m : metaDataList){
            List<Gallery> galleryList = galleryRepo.findGalleryByCategoryAndMetaData(category, m);
            for(Gallery g : galleryList){
                // 1. Construct the key using the clean database values
                String key = g.getCategory() + "/" + g.getMetaData() + "/" + g.getObjectId() + "." + g.getObjectExtension();

                // Debug line: Check your console to make sure this prints clean "events/metadata/uuid.ext"
                System.out.println("Generated S3 Key: " + key);

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

                GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getObjectRequest)
                        .build();

                PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

                // 2. CRITICAL: Save to a separate field, NOT objectId!
                g.setPresignedUrl(presignedRequest.url().toString());
            }
            objectsList.put(m, galleryList);
        }

        Map<String, Map<String, List<Gallery>>> result = new HashMap<>();
        result.put(category, objectsList);
        return result;
    }
}