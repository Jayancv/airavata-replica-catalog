package org.apache.airavata.replicacatalog.catalogapi.mapper;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.airavata.replicacatalog.catalog.stubs.DataProduct;
import org.apache.airavata.replicacatalog.catalogapi.model.DataProductEntity;
import org.apache.airavata.replicacatalog.catalogapi.model.DataReplicaLocationEntity;
import org.apache.airavata.replicacatalog.catalogapi.repository.DataProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Map to/from
 * {@link DataReplicaLocationEntity}
 * <-> {@link org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaLocation}
 */
@Component
public class DataProductMapper {

    @Autowired
    DataProductRepository dataProductRepository;

    public void mapModelToEntity(DataProduct dataProduct, DataProductEntity dataProductEntity) {

        dataProductEntity.setProductName(dataProduct.getProductName());
        dataProductEntity.setProductDescription(dataProduct.getProductDescription());
        dataProductEntity.setDataProductType(dataProduct.getDataProductType());
        dataProductEntity.setParentProductUri(dataProduct.getParentProductUri());
        dataProductEntity.setCreationTime(new Timestamp(dataProduct.getCreationTime()));

        // TODO: handle parent data product not found
        DataProductEntity parentDataProductEntity = dataProductRepository.findByProductUri(dataProduct.getParentProductUri());
//        parentDataProductEntity.setParentDataProductEntity(parentDataReplicaLocationEntity);

//        if (dataProduct.getMetadataMap() != null || !dataProduct.getMetadataMap().isEmpty()) {
//            ObjectMapper mapper = new ObjectMapper();
//            try {
//                JsonNode metadata = mapper.read(dataProduct.getMetadata());
//                dataProductEntity.setMetadata(metadata);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    public void mapEntityToModel(DataProductEntity dataProductEntity, DataProduct.Builder dataProductBuilder) {

        dataProductBuilder
                .setProductUri(dataProductEntity.getProductUri())
                .setProductName(dataProductEntity.getProductName())
                .setDataProductType(dataProductEntity.getDataProductType())
                .setCreationTime(dataProductEntity.getCreationTime().getTime())
                .setLastModifiedTime(dataProductEntity.getLastModifiedTime().getTime()).build();

    }
}
