package org.apache.airavata.replicacatalog.api.service;

import io.grpc.stub.StreamObserver;

import org.apache.airavata.replicacatalog.api.mapper.DataProductMapper;
import org.apache.airavata.replicacatalog.api.model.DataProductEntity;
import org.apache.airavata.replicacatalog.api.repository.DataProductRepository;
import org.apache.airavata.replicacatalog.api.stubs.DataProductCreateRequest;
import org.apache.airavata.replicacatalog.api.stubs.DataProductCreateResponse;
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
    public void createReplicaCDataProduct(DataProductCreateRequest request, StreamObserver<DataProductCreateResponse> responseObserver) {

        // TODO: SharingManager.resolveUser
        logger.info("Creating data product {}", request.getDataProduct());
        DataProductEntity dataProductEntity = new DataProductEntity();
        dataProductEntity.setExternalId(UUID.randomUUID().toString());
        dataProductMapper.mapModelToEntity(request.getDataProduct(), dataProductEntity);
        DataProductEntity savedDataProductEntity = dataProductRepository.save(dataProductEntity);

        // TODO: SharingManager.grantPermissionToUser(userInfo, dataProduct,
        // Permission.OWNER)

        DataProductCreateResponse.Builder responseBuilder = DataProductCreateResponse.newBuilder();
        dataProductMapper.mapEntityToModel(savedDataProductEntity, responseBuilder.getDataProductBuilder());
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
