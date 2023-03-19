package org.apache.airavata;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.apache.airavata.replicacatalog.catalog.service.ReplicaCatalogAPIServiceGrpc;
import org.apache.airavata.replicacatalog.catalog.stubs.*;
import org.apache.airavata.replicacatalog.resource.stubs.common.FileResource;
import org.apache.airavata.replicacatalog.resource.stubs.common.GenericResource;
import org.apache.airavata.replicacatalog.resource.stubs.common.GenericResourceCreateRequest;
import org.apache.airavata.replicacatalog.resource.stubs.common.SecretForStorage;
import org.apache.airavata.replicacatalog.resource.stubs.common.StorageCommon;
import org.apache.airavata.replicacatalog.resource.stubs.common.StorageCommonServiceGrpc;
import org.apache.airavata.replicacatalog.resource.stubs.common.StorageType;
import org.apache.airavata.replicacatalog.resource.stubs.common.StorageWrapper;
import org.apache.airavata.replicacatalog.resource.stubs.s3.S3Storage;
import org.apache.airavata.replicacatalog.secret.stubs.common.SecretCommonServiceGrpc;
import org.apache.airavata.replicacatalog.secret.stubs.common.SecretWrapper;
import org.apache.airavata.replicacatalog.secret.stubs.common.StorageSecret;
import org.apache.airavata.replicacatalog.secret.stubs.s3.S3Secret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicaCatalogAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(ReplicaCatalogAPIClient.class);

    private final ReplicaCatalogAPIServiceGrpc.ReplicaCatalogAPIServiceBlockingStub blockingApiStub;

    public ReplicaCatalogAPIClient(Channel channel) {
        blockingApiStub = ReplicaCatalogAPIServiceGrpc.newBlockingStub(channel);
    }

    public ReplicaCatalogAPIServiceGrpc.ReplicaCatalogAPIServiceBlockingStub getBlockingApiStub() {
        return blockingApiStub;
    }

    public StorageCommonServiceGrpc.StorageCommonServiceBlockingStub getBlockingStorageStub(Channel channel) {
        return StorageCommonServiceGrpc.newBlockingStub(channel);
    }

//    public GenericResourceServiceGrpc.GenericResourceServiceBlockingStub getBlockingResourceStub( Channel channel )
//    {
//        return GenericResourceServiceGrpc.newBlockingStub( channel );
//
//    }

    public SecretCommonServiceGrpc.SecretCommonServiceBlockingStub getBlockingSecretStub(Channel channel) {
        return SecretCommonServiceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:6565";

        /*
            --- Sample scenario 1---
            Airavata MFT copied a 100GB astrological data file to Amazon S3 bucket
            User can access that S3 bucket data file via Replica catalog details

            1) Register Data Product
            2) Register Replica Location
            3) Register Location storage details
            4) Register location storage credentials

         */
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        try {
            ReplicaCatalogAPIClient client = new ReplicaCatalogAPIClient(channel);

            DataProduct dataProduct = DataProduct.newBuilder().setDataProductType(DataProductType.FILE)
                    .setProductName("ASTRO").setProductUri("613").setProductDescription("Astro Data").build();
            DataProduct productResult = client.createDataProduct(dataProduct);

            DataReplicaLocation replicaLocation = DataReplicaLocation.newBuilder().setReplicaName("ASTRO S3")
                    .setReplicaDescription("S3 replica").setDataProductId(productResult.getProductUri())
                    .setCreationTime(System.currentTimeMillis()).build();
            DataReplicaLocation replicaResult = client.createReplicaLocation(replicaLocation);

            S3Storage storage = S3Storage.newBuilder().setName("ASTRO S3").setBucketName("arn:aws:s3:::mftjayan")
                    .setRegion("us-east-1").setEndpoint("https://s3.us-east-1.amazonaws.com").build();

            GenericResource resource = GenericResource.newBuilder()
                    .setStorage(StorageWrapper.newBuilder().setS3Storage(storage).build())
                    .setFile(FileResource.newBuilder().setResourcePath("/astro.zip").build()).build();
            GenericResource resourceResult = client.createStorage(channel, resource);

            S3Secret secret = S3Secret.newBuilder().setAccessKey("access").setSecretKey("secKey")
                    .setSessionToken("token").build();
            StorageSecret storageSecret = StorageSecret.newBuilder()
                    .setSecret(SecretWrapper.newBuilder().setS3Secret(secret).build())
                    .setStorageType(org.apache.airavata.replicacatalog.secret.stubs.common.StorageType.S3).build();

            StorageSecret secretResult = client.createSecret(channel, storageSecret);

            SecretForStorage secretForStorage = SecretForStorage.newBuilder()
                    .setStorageId(resourceResult.getStorage().getS3Storage().getStorageId())
                    .setSecretId(secretResult.getSecretId()).setStorageType(StorageType.S3).build();

            client.createCommonStorage(channel, secretForStorage);


            System.out.println(
                    MessageFormat.format("Created data product with id [{0}]", replicaResult.getDataReplicaId()));

            /*
            --- Sample scenario 2---
            Airavata MFT copied a 1000GB astrological data Folder which contains 100 data files to Amazon S3 bucket
            User can access that S3 bucket data folder via Replica catalog details

            1) Register Data Product
            2) Register Replica Location
            3) Register Location storage details
            4) Register location storage credentials

         */

        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }


    public DataProduct createDataProduct(DataProduct dataProduct) {
        DataProductCreateRequest request = DataProductCreateRequest.newBuilder().setDataProduct(dataProduct).build();
        DataProductCreateResponse response = blockingApiStub.registerDataProduct(request);
        return response.getDataProduct();
    }

    public DataReplicaLocation createReplicaLocation(DataReplicaLocation replicaLocation) {
        DataReplicaCreateRequest request =
                DataReplicaCreateRequest.newBuilder().setDataReplica(replicaLocation).build();
        DataReplicaCreateResponse response = blockingApiStub.registerReplicaLocation(request);
        return response.getDataReplica();
    }


    public SecretForStorage createCommonStorage(Channel channel, SecretForStorage secretForStorage) {

        SecretForStorage response = getBlockingStorageStub(channel).registerSecretForStorage(secretForStorage);
        return response;
    }

    public GenericResource createStorage(Channel channel, GenericResource resource) {
        StorageType storageType = null;
        switch (resource.getStorage().getStorageCase().getNumber()) {
            case StorageWrapper.S3STORAGE_FIELD_NUMBER:
                storageType = StorageType.S3;
                break;

            default:
                break;
        }
        GenericResourceCreateRequest request = GenericResourceCreateRequest.newBuilder().
                setResource(resource).setStorageType(storageType).build();
        GenericResource response = getBlockingStorageStub(channel).createGenericResource(request);
        return response;
    }

    public StorageSecret createSecret(Channel channel, StorageSecret resource) {
        StorageSecret response = getBlockingSecretStub(channel).registerSecret(resource);
        return response;
    }


}