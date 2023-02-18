/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.airavata.replicacatalog.resource.service;


import org.apache.airavata.replicacatalog.resource.model.GenericResourceEntity;
import org.apache.airavata.replicacatalog.resource.model.ResolveStorageEntity;
import org.apache.airavata.replicacatalog.resource.model.S3StorageEntity;
import org.apache.airavata.replicacatalog.resource.model.StorageSecretEntity;
import org.apache.airavata.replicacatalog.resource.repository.GenericResourceRepository;
import org.apache.airavata.replicacatalog.resource.repository.ResolveStorageRepository;
import org.apache.airavata.replicacatalog.resource.repository.S3StorageRepository;
import org.apache.airavata.replicacatalog.resource.repository.StorageSecretRepository;

import org.apache.airavata.replicacatalog.resource.stubs.common.*;
import org.apache.airavata.replicacatalog.resource.stubs.common.Error;
import org.apache.airavata.replicacatalog.resource.stubs.s3.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("SQLResourceBackend")
public class SQLResourceBackend implements ResourceBackend {

    private static final Logger logger = LoggerFactory.getLogger(SQLResourceBackend.class);
    private DozerBeanMapper mapper = new DozerBeanMapper();

    @Autowired
    private GenericResourceRepository resourceRepository;

    @Autowired
    private S3StorageRepository s3StorageRepository;

    @Autowired
    private StorageSecretRepository resourceSecretRepository;

    @Autowired
    private ResolveStorageRepository resolveStorageRepository;


    @Override
    public void init() {
        logger.info("Initializing database resource backend");
    }

    @Override
    public void destroy() {
        logger.info("Destroying database resource backend");
    }



    @Override
    public SecretForStorage getSecretForStorage(SecretForStorageGetRequest request) throws Exception {
        Optional<StorageSecretEntity> resourceSecEtyOp = resourceSecretRepository.findByStorageId(request.getStorageId());
        SecretForStorage.Builder resultBuilder = SecretForStorage.newBuilder();
        if (resourceSecEtyOp.isPresent()) {
            StorageSecretEntity storageSecretEntity = resourceSecEtyOp.get();
            resultBuilder.setSecretId(storageSecretEntity.getSecretId());
            resultBuilder.setStorageId(storageSecretEntity.getStorageId());
        } else {
            resultBuilder.setError(Error.NOT_FOUND);
        }
        return resultBuilder.build();
    }

    @Override
    public SecretForStorage registerSecretForStorage(SecretForStorage request) throws Exception {
        StorageSecretEntity ety = new StorageSecretEntity();
        ety.setSecretId(request.getSecretId());
        ety.setStorageId(request.getStorageId());
        ety.setType(request.getStorageType().name());
        resourceSecretRepository.save(ety);
        return request;
    }

    @Override
    public boolean deleteSecretForStorage(SecretForStorageDeleteRequest request) throws Exception {
        resourceSecretRepository.deleteByStorageId(request.getStorageId());
        return true;
    }

    @Override
    public StorageListResponse searchStorages(StorageSearchRequest request) throws Exception {
        StorageListResponse.Builder resp = StorageListResponse.newBuilder();
        switch (request.getSearchQueryCase()) {
            case STORAGE_ID:
                Optional<ResolveStorageEntity> storageOp = resolveStorageRepository.getByStorageId(request.getStorageId());
                if (storageOp.isPresent()) {
                    StorageListEntry.Builder entry = StorageListEntry.newBuilder();
                    entry.setStorageId(storageOp.get().getStorageId());
                    entry.setStorageName(storageOp.get().getStorageName());
                    entry.setStorageType(StorageType.valueOf(storageOp.get().getStorageType().name()));
                    resp.addStorageList(entry);
                }
                break;
            case STORAGE_NAME:
                List<ResolveStorageEntity> storages = resolveStorageRepository.getByStorageName(request.getStorageName());
                storages.forEach(st -> {
                    StorageListEntry.Builder entry = StorageListEntry.newBuilder();
                    entry.setStorageId(st.getStorageId());
                    entry.setStorageName(st.getStorageName());
                    entry.setStorageType(StorageType.valueOf(st.getStorageType().name()));
                    resp.addStorageList(entry);
                });
                break;
            case STORAGE_TYPE:
                storages = resolveStorageRepository.getByStorageType(ResolveStorageEntity.StorageType.valueOf(request.getStorageType().name()));
                storages.forEach(st -> {
                    StorageListEntry.Builder entry = StorageListEntry.newBuilder();
                    entry.setStorageId(st.getStorageId());
                    entry.setStorageName(st.getStorageName());
                    entry.setStorageType(StorageType.valueOf(st.getStorageType().name()));
                    resp.addStorageList(entry);
                });
                break;
        }
        return resp.build();
    }

    @Override
    public StorageListResponse listStorage(StorageListRequest request) throws Exception {
        Iterable<ResolveStorageEntity> all = resolveStorageRepository.findAll();
        StorageListResponse.Builder builder = StorageListResponse.newBuilder();
        all.forEach(r -> {
            StorageListEntry.Builder entry = StorageListEntry.newBuilder();
            entry.setStorageId(r.getStorageId());
            entry.setStorageType(StorageType.valueOf(r.getStorageType().name()));
            entry.setStorageName(r.getStorageName());
            builder.addStorageList(entry);
        });
        return builder.build();
    }


    @Override
    public S3StorageListResponse listS3Storage(S3StorageListRequest request) throws Exception {
        S3StorageListResponse.Builder respBuilder = S3StorageListResponse.newBuilder();
        List<S3StorageEntity> all = s3StorageRepository.findAll(PageRequest.of(request.getOffset(), request.getLimit()));
        all.forEach(ety -> respBuilder.addStorages(mapper.map(ety, S3Storage.newBuilder().getClass())));
        return respBuilder.build();
    }

    @Override
    public Optional<S3Storage> getS3Storage(S3StorageGetRequest request) throws Exception {
        Optional<S3StorageEntity> entity = s3StorageRepository.findById(request.getStorageId());
        return entity.map(e -> mapper.map(e, S3Storage.newBuilder().getClass()).build());
    }

    @Override
    public S3Storage createS3Storage(S3StorageCreateRequest request) throws Exception {
        S3StorageEntity savedEntity = s3StorageRepository.save(mapper.map(request, S3StorageEntity.class));

        ResolveStorageEntity storageTypeEty = new ResolveStorageEntity();
        storageTypeEty.setStorageId(savedEntity.getStorageId());
        storageTypeEty.setStorageType(ResolveStorageEntity.StorageType.S3);
        storageTypeEty.setStorageName(savedEntity.getName());
        resolveStorageRepository.save(storageTypeEty);

        return mapper.map(savedEntity, S3Storage.newBuilder().getClass()).build();
    }

    @Override
    public boolean updateS3Storage(S3StorageUpdateRequest request) throws Exception {
        s3StorageRepository.save(mapper.map(request, S3StorageEntity.class));
        return true;
    }

    @Override
    public boolean deleteS3Storage(S3StorageDeleteRequest request) throws Exception {
        s3StorageRepository.deleteById(request.getStorageId());
        resourceRepository.deleteByStorageIdAndStorageType(request.getStorageId(), GenericResourceEntity.StorageType.S3);
        return true;
    }


    @Override
    public StorageTypeResolveResponse resolveStorageType(StorageTypeResolveRequest request) throws Exception {
        Optional<ResolveStorageEntity> resolveStorageOp = resolveStorageRepository.getByStorageId(request.getStorageId());
        StorageTypeResolveResponse.Builder responseBuilder = StorageTypeResolveResponse.newBuilder();

        if (resolveStorageOp.isPresent()) {
            ResolveStorageEntity resolveStorageEntity = resolveStorageOp.get();
            responseBuilder.setStorageId(resolveStorageEntity.getStorageId());
            responseBuilder.setStorageType(StorageType.valueOf(resolveStorageEntity.getStorageType().name()));
            responseBuilder.setStorageName(resolveStorageEntity.getStorageName());
        } else {
            responseBuilder.setError(Error.NOT_FOUND);
        }
        return responseBuilder.build();
    }
}
