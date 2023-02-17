package org.apache.airavata.replicacatalog.catalogapi.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaLocation;
import org.apache.airavata.replicacatalog.catalogapi.model.DataProductEntity;
import org.apache.airavata.replicacatalog.catalogapi.repository.DataProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Map to/from
 * {@link DataProductEntity}
 * <-> {@link org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaLocation}
 */
@Component
public class DataProductMapper {

    @Autowired
    DataProductRepository dataProductRepository;

    public void mapModelToEntity(DataReplicaLocation dataProduct, DataProductEntity dataProductEntity) {

        dataProductEntity.setName(dataProduct.getReplicaName());


            // TODO: handle parent data product not found
            DataProductEntity parentDataProductEntity = dataProductRepository
                    .findByExternalId(dataProduct.getParentDataProductId());
            dataProductEntity.setParentDataProductEntity(parentDataProductEntity);

        if (dataProduct.getMetadataMap() != null || !dataProduct.getMetadataMap().isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
//            try {
//                JsonNode metadata = mapper.read(dataProduct.getMetadata());
//                dataProductEntity.setMetadata(metadata);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    public void mapEntityToModel(DataProductEntity dataProductEntity, DataReplicaLocation.Builder dataProductBuilder) {

        dataProductBuilder
                .setDataReplicaId(dataProductEntity.getExternalId())
                .setReplicaName(dataProductEntity.getName());
        if (dataProductEntity.getParentDataProductEntity() != null) {
            dataProductBuilder.setParentDataProductId(dataProductEntity.getParentDataProductEntity().getExternalId());
        }
    }
}
