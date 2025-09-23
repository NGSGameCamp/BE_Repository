package com.imfine.ngs.media.service;

import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {
    private final FirebaseApp firebaseApp;
    private final String bucketName = "ngsgamecamp.firebasestorage.app";

    public String uploadFile(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 파일 업로드
        Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());

        // Firebase 다운로드 토큰 생성
        Map<String, String> metadata = new HashMap<>();
        metadata.put("firebaseStorageDownloadTokens", UUID.randomUUID().toString());

        blob.toBuilder().setMetadata(metadata).build().update();

        // Firebase 다운로드 URL 반환
        String token = metadata.get("firebaseStorageDownloadTokens");
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                bucketName, fileName.replace("/", "%2F"), token);
    }
}
