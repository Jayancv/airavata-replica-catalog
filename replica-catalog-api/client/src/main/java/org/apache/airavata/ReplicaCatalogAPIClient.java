package org.apache.airavata;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;


import org.apache.airavata.replicacatalog.api.service.ReplicaCatalogAPIServiceGrpc;
import org.apache.airavata.replicacatalog.api.stubs.DataProduct;
import org.apache.airavata.replicacatalog.api.stubs.DataProductCreateRequest;
import org.apache.airavata.replicacatalog.api.stubs.DataProductCreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicaCatalogAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(ReplicaCatalogAPIClient.class);

    private final ReplicaCatalogAPIServiceGrpc.ReplicaCatalogAPIServiceBlockingStub blockingStub;

    public ReplicaCatalogAPIClient(Channel channel) {
        blockingStub = ReplicaCatalogAPIServiceGrpc.newBlockingStub(channel);
    }

    public DataProduct createDataProduct(DataProduct dataProduct) {
        DataProductCreateRequest request = DataProductCreateRequest.newBuilder().setDataProduct(dataProduct).build();
        DataProductCreateResponse response = blockingStub.createReplicaCDataProduct(request);
        return response.getDataProduct();
    }

    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:6565";

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        try {
            ReplicaCatalogAPIClient client = new ReplicaCatalogAPIClient(channel);
            DataProduct parentDataProduct = DataProduct.newBuilder().setName("parent dp").build();
            DataProduct parentResult = client.createDataProduct(parentDataProduct);

            DataProduct dataProduct = DataProduct.newBuilder().setName("testing").setMetadata("{\"foo\": \"bar\"}")
                    .setParentDataProductId(parentResult.getDataProductId())
                    .build();
            DataProduct result = client.createDataProduct(dataProduct);
            System.out.println(MessageFormat.format("Created data product with id [{0}]", result.getDataProductId()));

        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}