package org.example.lab1.model.minio;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetBucketPolicyArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import org.example.lab1.exceptions.NotFoundException;
import org.example.lab1.model.interfaces.FileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
public class MinioStorage implements FileStorage {
    private final String bucket;

    private static final String tmpPrefix = "tmp/";

    private final MinioClient minioClient;

    private final Integer partSize;

    private final String publicMinioUrl;

    private final String internalUrl;

    public MinioStorage(
            @Value("#{servletContext.getInitParameter('minioUrl') ?: ''}") String url,
            @Value("#{servletContext.getInitParameter('minioUser') ?: ''}") String user,
            @Value("#{servletContext.getInitParameter('minioPassword') ?: ''}") String password,
            @Value("#{servletContext.getInitParameter('minioBucket') ?: ''}") String bucket,
            @Value("#{servletContext.getInitParameter('minioPartSize') ?: '5242880'}") Integer partSize,
            @Value("#{servletContext.getInitParameter('publicMinioUrl') ?: ''}") String publicMinioUrl
    ) throws Exception {
        this.minioClient = MinioClient.builder().endpoint(url).credentials(user, password).build();
        this.bucket = bucket;
        this.partSize = partSize;
        this.publicMinioUrl = publicMinioUrl;
        this.internalUrl = url;
        ensurePublicBucket();
    }

    @Override
    public String getDownloadLink(String objectName, String originName) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(this.bucket)
                            .object(objectName)
                            .build()
            );
            String base = (this.publicMinioUrl != null && !this.publicMinioUrl.isBlank())
                    ? this.publicMinioUrl
                    : this.internalUrl;
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            return base + "/" + this.bucket + "/" + objectName;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new NotFoundException("File" + objectName + " not found in " + this.bucket + " bucket");
            }
            throw e;
        }
    }

    @Override
    public void uploadTmp(InputStream is, String objectName, String contentType) throws Exception {
        this.minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(this.bucket)
                        .object(MinioStorage.tmpPrefix + objectName)
                        .stream(is, -1, this.partSize)
                        .contentType(contentType)
                        .build()
        );
    }

    @Override
    public void saveTmp(String objectName) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .source(CopySource.builder()
                                .bucket(this.bucket)
                                .object(MinioStorage.tmpPrefix + objectName)
                                .build()
                        )
                        .build()
        );
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(this.bucket)
                        .object(MinioStorage.tmpPrefix + objectName)
                        .build()
        );
    }

    @Override
    public void deleteFile(String objectName) throws Exception {
        this.minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(this.bucket)
                        .object(objectName)
                        .build()
        );
    }

    @Override
    public void deleteTmp(String objectName) throws Exception {
        this.minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(this.bucket)
                        .object(MinioStorage.tmpPrefix + objectName)
                        .build()
        );
    }

    private void ensurePublicBucket() throws Exception{
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            String policy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Effect": "Allow",
                          "Principal": {"AWS": ["*"]},
                          "Action": ["s3:GetObject"],
                          "Resource": ["arn:aws:s3:::%s/*"]
                        }
                      ]
                    }
                    """.formatted(bucket);
            try {
                String current = minioClient.getBucketPolicy(GetBucketPolicyArgs.builder().bucket(bucket).build());
                if (!policy.equals(current)) {
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
                }
            } catch (Exception ignored) {
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
            }
    }
}
