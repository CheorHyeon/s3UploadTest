package com.ll.s3test.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3 amazonS3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @PostMapping("/multipart-files/single")
    public String uploadMultipleFile(@RequestPart MultipartFile file) throws IOException {
        // 객체의 메타 data 설정 - 해두면 클라이언트에서 다운 받을 때 처리 용이
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 파일의 형식에 맞는 MIME 타입을 설정, size 설정 하는것이 좋음
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        String folderName = "yourFolderName/";
        String transFolder = folderName + UUID.randomUUID() + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucket, // 버킷
                transFolder, // 파일명, 폴더 구분할 수 있다.
                file.getInputStream(),
                objectMetadata // 객체의 메타data 설정 클래스
        );

        amazonS3Client.putObject(putObjectRequest);
        return amazonS3Client.getUrl(bucket, transFolder).toString();
    }

    @PostMapping("/multipart-files")
    public LinkedHashMap<String, String> uploadMultipleFiles(@RequestPart("uploadFiles") List<MultipartFile> multipartFiles) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (MultipartFile file : multipartFiles) {
            // 객체의 메타 data 설정 - 해두면 클라이언트에서 다운 받을 때 처리 용이
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 파일의 형식에 맞는 MIME 타입을 설정, size 설정 하는것이 좋음
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            String folderName = "yourFolderName/";
            String transFolder = folderName + UUID.randomUUID() + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket, // 버킷
                    transFolder, // 파일명, 폴더 구분할 수 있다.
                    file.getInputStream(),
                    objectMetadata // 객체의 메타data 설정 클래스
            );

            amazonS3Client.putObject(putObjectRequest);
            String url = amazonS3Client.getUrl(bucket, transFolder).toString();
            map.put(file.getOriginalFilename(), url);
        }
        return map;
    }

    @DeleteMapping("/multipart-files")
    public String uploadMultipleFiles(@RequestParam String deleteFileName){
        try {
            amazonS3Client.deleteObject(bucket, deleteFileName);
            return "삭제 성공";
        } catch (AmazonServiceException e) {
            return "삭제 실패: " + e.getMessage();
        }
    }
}