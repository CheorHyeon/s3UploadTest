package com.ll.s3test.controller;

import static java.net.URLEncoder.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;

import org.apache.commons.codec.net.URLCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    @GetMapping("/multipart-files")
    public ResponseEntity<UrlResource> downloadImage(@RequestParam String originalFilename) throws
		UnsupportedEncodingException {
        UrlResource urlResource = new UrlResource(amazonS3Client.getUrl(bucket, originalFilename));

        // prefix로 사용되는 "yourFolderName-" 제외한 다운로드 자동 되도록 설정
        // attachment : 브라우저가 파일 다운로드 하도록 지시 / inline : 브라우저는 파일 직접 열려고 시도
        // folderName과 UUID 길이를 제외한 파일 이름 추출
        int folderNameLength = "yourFolderName/".length();
        int uuidLength = 36; // UUID 길이는 항상 36자
        String extractedFilename = originalFilename.substring(uuidLength + folderNameLength);

        String[] parts = originalFilename.split("\\.");
        String extension = parts.length > 1 ? parts[parts.length - 1] : ""; // 확장자 추출

        // URL 인코딩된 파일 이름 생성
        String encodedFilename = URLEncoder.encode(extractedFilename, StandardCharsets.UTF_8);

        // Content-Disposition 헤더에 인코딩된 파일 이름 사용
        String contentDisposition = "attachment; filename=\"" + encodedFilename +  "." + extension + "\"";

        // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .body(urlResource);
    }
}