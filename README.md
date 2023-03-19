# Apache Airavata Replica Catalog

## Getting started
 
### To run the test

#### prerequisite
1) PostgreSQL database



Run the API server

```
mvn install
cd replica-catalog-api/server
mvn spring-boot:run
```

Run the API client

```
mvn install
cd replica-catalog-api/client
mvn exec:java -Dexec.mainClass=org.apache.airavata.ReplicaCatalogAPIClient
```
