package org.apache.airavata.replicacatalog.catalogapi.repository;

import org.apache.airavata.replicacatalog.catalogapi.model.DataProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface DataProductRepository extends JpaRepository<DataProductEntity, Long> {

    DataProductEntity findByExternalId(String externalId);

    @Transactional
    void deleteByExternalId(String externalId);

}
