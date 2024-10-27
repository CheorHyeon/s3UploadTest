package com.ll.s3test.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

	private final AmazonS3 amazonS3Client;

	@PostMapping("/multipart-files")
	public void uploadMultipleFile(@RequestPart MultipartFile file) throws IOException {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());

		PutObjectRequest putObjectRequest = new PutObjectRequest(
			"bucketName",
			"objectKey",
			file.getInputStream(),
			objectMetadata
		);

		amazonS3Client.putObject(putObjectRequest);
	}

	@PostMapping("/multipart-files")
	public String uploadMultipleFiles(@RequestPart("uploadFiles") List<MultipartFile> multipartFiles,
		@RequestParam String type) throws IOException {
		for (MultipartFile file : multipartFiles) {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType(file.getContentType());
			objectMetadata.setContentLength(file.getSize());

			PutObjectRequest putObjectRequest = new PutObjectRequest(
				"bucketName",
				"objectKey", // 여기에 파일 이름이나 고유한 키를 설정하세요.
				file.getInputStream(),
				objectMetadata
			);

			amazonS3Client.putObject(putObjectRequest);
		}
		return "업로드 완료";
	}
}