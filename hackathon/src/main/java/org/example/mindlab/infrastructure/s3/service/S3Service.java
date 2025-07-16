package org.example.mindlab.infrastructure.s3.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PreDestroy;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final ExecutorService executorService;

    public S3Service(S3Client s3Client, @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @PreDestroy
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // ===== UPLOAD 메서드들 =====

    /**
     * 단일 파일 업로드
     */
    public String upload(MultipartFile file, Long postId) {
        try {
            String key = generateFileKey(postId, file.getOriginalFilename());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("파일 업로드 성공: {}", key);
            return key;

        } catch (Exception e) {
            log.error("파일 업로드 실패, postId: {}, fileName: {}", postId, file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 여러 파일 업로드
     */
    public List<String> uploadMultiple(List<MultipartFile> files, Long postId) {
        List<String> uploadedKeys = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String key = upload(file, postId);
                uploadedKeys.add(key);
            }
        }

        return uploadedKeys;
    }

    /**
     * 멀티파트 업로드 (대용량 파일용)
     */
    public String uploadMultipart(MultipartFile file, Long postId) {
        try {
            String key = generateFileKey(postId, file.getOriginalFilename());

            // 파일 크기가 작으면 일반 업로드
            if (file.getSize() < 100 * 1024 * 1024) { // 100MB 미만
                return upload(file, postId);
            }

            // 멀티파트 업로드 시작
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            String uploadId = createResponse.uploadId();

            // 파트 업로드
            List<CompletedPart> completedParts = uploadParts(file, key, uploadId);

            // 멀티파트 업로드 완료
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder()
                    .parts(completedParts)
                    .build())
                .build();

            s3Client.completeMultipartUpload(completeRequest);

            log.info("멀티파트 업로드 성공: {}", key);
            return key;

        } catch (Exception e) {
            log.error("멀티파트 업로드 실패, postId: {}, fileName: {}", postId, file.getOriginalFilename(), e);
            throw new RuntimeException("멀티파트 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private List<CompletedPart> uploadParts(MultipartFile file, String key, String uploadId) throws IOException {
        List<CompletedPart> completedParts = new ArrayList<>();

        int partSize = 10 * 1024 * 1024; // 10MB per part
        byte[] buffer = new byte[partSize];

        try (InputStream inputStream = file.getInputStream()) {
            int partNumber = 1;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                byte[] partData = new byte[bytesRead];
                System.arraycopy(buffer, 0, partData, 0, bytesRead);

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength((long) bytesRead)
                    .build();

                UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                    uploadPartRequest,
                    RequestBody.fromBytes(partData)
                );

                CompletedPart completedPart = CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(uploadPartResponse.eTag())
                    .build();

                completedParts.add(completedPart);
                partNumber++;
            }
        }

        return completedParts;
    }

    // ===== DOWNLOAD 메서드들 =====

    /**
     * 멀티파트 다운로드로 MultipartFile 반환
     */
    public MultipartFile load(Long postId) {
        try {
            String prefix = "posts/" + postId + "/";

            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            if (listResponse.contents().isEmpty()) {
                throw new RuntimeException("파일을 찾을 수 없습니다.");
            }

            // 첫 번째 파일 선택
            S3Object s3Object = listResponse.contents().get(0);
            return downloadMultipartAsMultipartFile(s3Object.key(), s3Object.size());

        } catch (S3Exception e) {
            log.error("파일 조회 실패, postId: {}", postId, e);
            throw new RuntimeException("파일 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 파일 다운로드
     */
    public MultipartFile load(Long postId, String fileName) {
        try {
            String key = "posts/" + postId + "/" + fileName;

            // 파일 존재 여부 확인 및 크기 조회
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            Long fileSize = headObjectResponse.contentLength();

            return downloadMultipartAsMultipartFile(key, fileSize);

        } catch (NoSuchKeyException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
        } catch (S3Exception e) {
            log.error("파일 조회 실패, postId: {}, fileName: {}", postId, fileName, e);
            throw new RuntimeException("파일 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 목록 조회 (URL 반환)
     */
    public List<String> getFileUrls(Long postId) {
        try {
            String prefix = "posts/" + postId + "/";

            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            return listResponse.contents().stream()
                .map(S3Object::key)
                .map(this::getFileUrl)
                .toList();

        } catch (S3Exception e) {
            log.error("파일 목록 조회 실패, postId: {}", postId, e);
            throw new RuntimeException("파일 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    private MultipartFile downloadMultipartAsMultipartFile(String key, Long fileSize) {
        try {
            String fileName = key.substring(key.lastIndexOf('/') + 1);

            // 파일 크기가 작으면 일반 다운로드
            if (fileSize < 10 * 1024 * 1024) { // 10MB 미만
                return downloadSingleAsMultipartFile(key, fileName);
            }

            // 멀티파트 다운로드
            int partSize = 5 * 1024 * 1024; // 5MB per part
            int numParts = (int) Math.ceil((double) fileSize / partSize);

            List<Future<byte[]>> futures = new ArrayList<>();

            for (int i = 0; i < numParts; i++) {
                final int partNumber = i;
                long startByte = (long) partNumber * partSize;
                long endByte = Math.min(startByte + partSize - 1, fileSize - 1);

                Future<byte[]> future = executorService.submit(() ->
                    downloadPart(key, startByte, endByte));
                futures.add(future);
            }

            // 모든 파트 다운로드 완료 대기 및 결합
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (Future<byte[]> future : futures) {
                byte[] partData = future.get();
                outputStream.write(partData);
            }

            // Content-Type 조회
            String contentType = getContentType(key);

            // 커스텀 MultipartFile 구현체 반환
            return new S3MultipartFile(fileName, outputStream.toByteArray(), contentType);

        } catch (Exception e) {
            log.error("멀티파트 다운로드 실패, key: {}", key, e);
            throw new RuntimeException("멀티파트 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    private MultipartFile downloadSingleAsMultipartFile(String key, String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            byte[] content = s3Object.readAllBytes();

            return new S3MultipartFile(fileName, content, s3Object.response().contentType());

        } catch (Exception e) {
            log.error("단일 다운로드 실패, key: {}", key, e);
            throw new RuntimeException("단일 다운로드 실패", e);
        }
    }

    private byte[] downloadPart(String key, long startByte, long endByte) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .range("bytes=" + startByte + "-" + endByte)
                .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            return s3Object.readAllBytes();

        } catch (Exception e) {
            log.error("파트 다운로드 실패, key: {}, range: {}-{}", key, startByte, endByte, e);
            throw new RuntimeException("파트 다운로드 실패", e);
        }
    }

    // ===== DELETE 메서드들 =====

    /**
     * 파일 삭제
     */
    public void delete(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("파일 삭제 성공: {}", key);

        } catch (Exception e) {
            log.error("파일 삭제 실패, key: {}", key, e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 게시물의 모든 파일 삭제
     */
    public void deleteAllFiles(Long postId) {
        try {
            String prefix = "posts/" + postId + "/";

            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            if (!listResponse.contents().isEmpty()) {
                List<ObjectIdentifier> objectsToDelete = listResponse.contents().stream()
                    .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
                    .toList();

                DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(objectsToDelete).build())
                    .build();

                s3Client.deleteObjects(deleteObjectsRequest);
                log.info("게시물 파일 전체 삭제 성공, postId: {}", postId);
            }

        } catch (Exception e) {
            log.error("게시물 파일 전체 삭제 실패, postId: {}", postId, e);
            throw new RuntimeException("게시물 파일 전체 삭제 중 오류가 발생했습니다.", e);
        }
    }

    // ===== UTILITY 메서드들 =====

    private String generateFileKey(Long postId, String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);

        return String.format("posts/%d/%s_%s%s", postId, timestamp, uuid, extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    private String getFileUrl(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }

    private String getContentType(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            return headObjectResponse.contentType();

        } catch (Exception e) {
            return "application/octet-stream";
        }
    }

    // ===== 커스텀 MultipartFile 구현체 =====

    public static class S3MultipartFile implements MultipartFile {

        private final String originalFilename;
        private final byte[] content;
        private final String contentType;

        public S3MultipartFile(String originalFilename, byte[] content, String contentType) {
            this.originalFilename = originalFilename;
            this.content = content;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(content);
            }
        }
    }
}