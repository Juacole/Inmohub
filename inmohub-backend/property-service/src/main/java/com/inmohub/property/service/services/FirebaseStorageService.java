package com.inmohub.property.service.services;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Value("${firebase.storage.bucket-name}")
    private String bucketName;

    @Value("${firebase.storage.download-url-format}")
    private String urlFormat;

    public String uploadPhoto(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        bucket.create(fileName, file.getBytes(), file.getContentType());

        return String.format(urlFormat, bucketName, fileName);
    }
}