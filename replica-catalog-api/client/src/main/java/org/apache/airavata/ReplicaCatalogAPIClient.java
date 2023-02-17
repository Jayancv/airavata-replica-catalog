package org.apache.airavata;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.apache.airavata.replicacatalog.catalog.service.ReplicaCatalogAPIServiceGrpc;
import org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaCreateRequest;
import org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaCreateResponse;
import org.apache.airavata.replicacatalog.catalog.stubs.DataReplicaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicaCatalogAPIClient {
    private static final Logger logger = LoggerFactory.getLogger(ReplicaCatalogAPIClient.class);

    private final ReplicaCatalogAPIServiceGrpc.ReplicaCatalogAPIServiceBlockingStub blockingStub;

    public ReplicaCatalogAPIClient(Channel channel) {
        blockingStub = ReplicaCatalogAPIServiceGrpc.newBlockingStub(channel);
    }

    public DataReplicaLocation createReplicaCDataProduct(DataReplicaLocation dataProduct) {
        DataReplicaCreateRequest request = DataReplicaCreateRequest.newBuilder().setDataReplica(dataProduct).build();
        DataReplicaCreateResponse response = blockingStub.registerReplicaLocation(request);
        return response.getDataReplica();
    }

    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:6565";

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        try {
            ReplicaCatalogAPIClient client = new ReplicaCatalogAPIClient(channel);
            DataReplicaLocation parentDataProduct = DataReplicaLocation.newBuilder().setReplicaName("parent dp").build();
            DataReplicaLocation parentResult = client.createReplicaCDataProduct(parentDataProduct);

            DataReplicaLocation dataProduct = DataReplicaLocation.newBuilder().setReplicaName("testing").putMetadata("foo","bar")
                    .setParentDataProductId(parentResult.getParentDataProductId())
                    .build();
            DataReplicaLocation result = client.createReplicaCDataProduct(dataProduct);
            System.out.println(MessageFormat.format("Created data product with id [{0}]", result.getDataReplicaId()));

        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}