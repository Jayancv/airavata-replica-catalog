//package org.apache.airavata.replicacatalog.sceret.service;
//
//import java.util.UUID;
//
//import io.grpc.stub.StreamObserver;
//import org.apache.airavata.replicacatalog.resource.mapper.ResourceStorageMapper;
//import org.apache.airavata.replicacatalog.resource.repository.GenericResourceRepository;
//import org.apache.airavata.replicacatalog.resource.stubs.common.SecretForStorage;
//import org.apache.airavata.replicacatalog.secret.stubs.common.SecretCommonServiceGrpc;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class StorageSecretAPIService extends SecretCommonServiceGrpc.SecretCommonServiceImplBase
//{
//
//
//    private static final Logger logger = LoggerFactory.getLogger( StorageSecretAPIService.class);
//
//    @Autowired
//    GenericResourceRepository genericResourceRepository;
//
//    @Autowired
//    ResourceStorageMapper resourceStorageMapper = new ResourceStorageMapper();
//
//    @Override
//    public void registerSecret( SecretForStorage request, StreamObserver<SecretForStorage> responseObserver) {
//
//        // TODO: SharingManager.resolveUser
//        logger.info("Creating data product {}", request.getStorageId());
//        GenericResourceEntity resourceEntity = new GenericResourceEntity();
//        resourceEntity.setResourceId( UUID.randomUUID().toString());
//        resourceStorageMapper.mapModelToEntity(request, resourceEntity);
//        GenericResourceEntity savedDataProductEntity = genericResourceRepository.save(resourceEntity);
//
//        // TODO: SharingManager.grantPermissionToUser(userInfo, dataProduct,
//        // Permission.OWNER)
//
//        SecretForStorage.Builder responseBuilder = SecretForStorage.newBuilder();
//        resourceStorageMapper.mapEntityToModel(savedDataProductEntity, responseBuilder);
//        responseObserver.onNext(responseBuilder.build());
//        responseObserver.onCompleted();
//    }
//}
