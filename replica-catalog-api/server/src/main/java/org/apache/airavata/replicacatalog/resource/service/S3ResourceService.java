package org.apache.airavata.replicacatalog.resource.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.apache.airavata.replicacatalog.resource.service.s3.S3StorageServiceGrpc;
import org.apache.airavata.replicacatalog.resource.stubs.s3.storage.S3Storage;
import org.apache.airavata.replicacatalog.resource.stubs.s3.storage.S3StorageCreateRequest;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@GRpcService
public class S3ResourceService extends S3StorageServiceGrpc.S3StorageServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageCreateRequest.class);

    @Autowired
    @Qualifier("SQLResourceBackend")
    private ResourceBackend backend;



    @Override
    public void createS3Storage(S3StorageCreateRequest request, StreamObserver<S3Storage> responseObserver) {
        try {
            responseObserver.onNext(this.backend.createS3Storage(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Failed in creating the S3 storage", e);

            responseObserver.onError(Status.INTERNAL.withCause(e)
                    .withDescription("Failed in creating the S3 storage")
                    .asRuntimeException());
        }
    }

}

