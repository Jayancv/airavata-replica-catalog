package org.apache.airavata.replicacatalog.catalogapi.service;

import io.grpc.stub.StreamObserver;

import org.apache.airavata.replicacatalog.catalog.service.ReplicaCatalogAPIServiceGrpc;
import org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaCreateRequest;
import org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaCreateResponse;
import org.apache.airavata.replicacatalog.catalogapi.mapper.DataProductMapper;
import org.apache.airavata.replicacatalog.catalogapi.model.DataProductEntity;
import org.apache.airavata.replicacatalog.catalogapi.repository.DataProductRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@GRpcService
public class DataCatalogAPIService extends ReplicaCatalogAPIServiceGrpc.ReplicaCatalogAPIServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(DataCatalogAPIService.class);

    @Autowired
    DataProductRepository dataProductRepository;

    @Autowired
    DataProductMapper dataProductMapper = new DataProductMapper();

    @Override
    public void registerReplicaLocation(DataReplicaCreateRequest request, StreamObserver<DataReplicaCreateResponse> responseObserver) {

        // TODO: SharingManager.resolveUser
        logger.info("Creating data product {}", request.getDataReplica());
        DataProductEntity dataProductEntity = new DataProductEntity();
        dataProductEntity.setExternalId(UUID.randomUUID().toString());
        dataProductMapper.mapModelToEntity(request.getDataReplica(), dataProductEntity);
        DataProductEntity savedDataProductEntity = dataProductRepository.save(dataProductEntity);

        // TODO: SharingManager.grantPermissionToUser(userInfo, dataProduct,
        // Permission.OWNER)

        DataReplicaCreateResponse.Builder responseBuilder = DataReplicaCreateResponse.newBuilder();
        dataProductMapper.mapEntityToModel(savedDataProductEntity, responseBuilder.getDataReplicaBuilder());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
