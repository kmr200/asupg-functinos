package org.asupg.functions.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.InputStream;

@Singleton
public class BlobStorageService {

    private static final Logger logger = LoggerFactory.getLogger(BlobStorageService.class);

    private final BlobContainerClient blobContainerClient;

    @Inject
    public BlobStorageService(
            @Named("BLOB_STORAGE_CONN_STR") String blobStorageConnStr,
            @Named("BLOB_CONTAINER_NAME") String blobContainerName
    ) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(blobStorageConnStr)
                .buildClient();

        this.blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainerName);

        if (!this.blobContainerClient.exists()) {
            this.blobContainerClient.create();
        }
    }

    public void upload(
            String blobName,
            InputStream data,
            long length
    ) {
        BlobClient blobClient = this.blobContainerClient.getBlobClient(blobName);
        blobClient.upload(data, length, true);
        logger.info("Uploaded blob: {}", blobName);
    }

}
