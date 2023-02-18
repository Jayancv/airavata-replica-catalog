package org.apache.airavata.replicacatalog.resource.service;

import io.grpc.stub.StreamObserver;
import org.apache.airavata.replicacatalog.resource.mapper.ResourceStorageMapper;
import org.apache.airavata.replicacatalog.resource.model.GenericResourceEntity;
import org.apache.airavata.replicacatalog.resource.repository.GenericResourceRepository;
import org.apache.airavata.replicacatalog.resource.stubs.common.SecretForStorage;
import org.apache.airavata.replicacatalog.resource.stubs.common.StorageCommonServiceGrpc;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@GRpcService
public class ResourceAPIService extends StorageCommonServiceGrpc.StorageCommonServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(ResourceAPIService.class);

    @Autowired
    GenericResourceRepository genericResourceRepository;

    @Autowired
    ResourceStorageMapper resourceStorageMapper = new ResourceStorageMapper();

    @Override
    public void registerSecretForStorage(SecretForStorage request, StreamObserver<SecretForStorage> responseObserver) {

        // TODO: SharingManager.resolveUser
        logger.info("Creating data product {}", request.getStorageId());
        GenericResourceEntity resourceEntity = new GenericResourceEntity();
        resourceEntity.setResourceId(UUID.randomUUID().toString());
        resourceStorageMapper.mapModelToEntity(request, resourceEntity);
        GenericResourceEntity savedDataProductEntity = genericResourceRepository.save(resourceEntity);

        // TODO: SharingManager.grantPermissionToUser(userInfo, dataProduct,
        // Permission.OWNER)

        SecretForStorage.Builder responseBuilder = SecretForStorage.newBuilder();
        resourceStorageMapper.mapEntityToModel(savedDataProductEntity, responseBuilder);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
