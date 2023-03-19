package org.apache.airavata.replicacatalog.resource.service;

import io.grpc.stub.StreamObserver;
import org.apache.airavata.replicacatalog.resource.mapper.ResourceStorageMapper;
import org.apache.airavata.replicacatalog.resource.model.GenericResourceEntity;
import org.apache.airavata.replicacatalog.resource.model.StorageSecretEntity;
import org.apache.airavata.replicacatalog.resource.repository.GenericResourceRepository;
import org.apache.airavata.replicacatalog.resource.repository.StorageSecretRepository;
import org.apache.airavata.replicacatalog.resource.stubs.common.GenericResource;
import org.apache.airavata.replicacatalog.resource.stubs.common.GenericResourceCreateRequest;
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
    StorageSecretRepository storageSecretRepository;

    @Autowired
    ResourceStorageMapper resourceStorageMapper = new ResourceStorageMapper();

    @Override
    public void createGenericResource( GenericResourceCreateRequest request, StreamObserver<GenericResource> responseObserver  )
    {
        logger.info( "Creating Storage {}", request.getStorageId() );
        GenericResourceEntity resourceEntity = new GenericResourceEntity();
        resourceEntity.setResourceId( UUID.randomUUID().toString() );
        resourceStorageMapper.mapGenericStorageModelToEntity( request.getResource(), resourceEntity );
        GenericResourceEntity savedDataProductEntity = genericResourceRepository.save( resourceEntity );

        // TODO: SharingManager.grantPermissionToUser(userInfo, dataProduct,
        // Permission.OWNER)

        GenericResource.Builder responseBuilder = GenericResource.newBuilder();
        resourceStorageMapper.mapGenericStorageEntityToModel( savedDataProductEntity, responseBuilder );
        responseObserver.onNext( responseBuilder.build() );
        responseObserver.onCompleted();
    }

    @Override
    public void registerSecretForStorage(SecretForStorage request, StreamObserver<SecretForStorage> responseObserver) {

        // TODO: SharingManager.resolveUser
        logger.info("Creating data product {}", request.getStorageId());
        StorageSecretEntity resourceEntity = new StorageSecretEntity();
        resourceStorageMapper.mapStorageSecretModelToEntity(request, resourceEntity);
        StorageSecretEntity savedDataProductEntity = storageSecretRepository.save(resourceEntity);

        // TODO: SharingManager.grantPermissionToUser(userInfo, dataProduct,
        // Permission.OWNER)

        SecretForStorage.Builder responseBuilder = SecretForStorage.newBuilder();
        resourceStorageMapper.mapStorageSecretEntityToModel(savedDataProductEntity, responseBuilder);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
