package org.asupg.functions.config;

import com.azure.cosmos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dagger.Module;
import dagger.Provides;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class DaggerModule {

    @Provides
    @Singleton
    @Named("bankHost")
    String provideBankClientHost() {
        return getEnv("BANK_CLIENT_HOST");
    }

    @Provides
    @Singleton
    @Named("bankLogin")
    String provideBankClientLogin() {
        return getEnv("BANK_CLIENT_LOGIN");
    }

    @Provides
    @Singleton
    @Named("bankPassword")
    String provideBankClientPassword() {
        return getEnv("BANK_CLIENT_PASSWORD");
    }

    @Provides
    @Singleton
    @Named("bankAccount")
    String provideBankAccount() {
        return getEnv("BANK_ACCOUNT");
    }

    @Provides
    @Singleton
    @Named("BLOB_STORAGE_CONN_STR")
    String provideBlobStorageConnStr() {
        return getEnv("AzureWebJobsStorage");
    }

    @Provides
    @Singleton
    @Named("BLOB_CONTAINER_NAME")
    String provideBlobContainerName() {
        return getEnv("BLOB_CONTAINER_NAME");
    }

    @Provides
    @Singleton
    CloseableHttpClient provideHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .disableAutomaticRetries()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    @Provides
    @Singleton
    CookieStore provideCookieStore() {
        return new BasicCookieStore();
    }

    @Provides
    @Singleton
    @Named("cosmosEndpoint")
    public String provideEndpoint() {
        return System.getenv("COSMOS_ENDPOINT");
    }

    @Provides
    @Singleton
    @Named("cosmosKey")
    public String provideKey() {
        return System.getenv("COSMOS_KEY");
    }

    @Provides
    @Singleton
    @Named("cosmosDatabaseName")
    public String provideDatabaseName() {
        return System.getenv("COSMOS_DATABASE_NAME");
    }

    @Provides
    @Singleton
    @Named("cosmosTransactionsContainerName")
    public String provideTransactionsContainerName() {
        return System.getenv("COSMOS_TRANSACTIONS_CONTAINER_NAME");
    }

    @Provides
    @Singleton
    @Named("cosmosCompaniesContainerName")
    public String provideCompaniesContainerName() {
        return System.getenv("COSMOS_COMPANIES_CONTAINER_NAME");
    }

    @Provides
    @Singleton
    public CosmosClient provideCosmosClient(
            @Named("cosmosEndpoint") String cosmosEndpoint,
            @Named("cosmosKey") String cosmosKey
    ) {
        return new CosmosClientBuilder()
                .endpoint(cosmosEndpoint)
                .key(cosmosKey)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .contentResponseOnWriteEnabled(false)
                .gatewayMode()
                .buildClient();
    }

    @Provides
    @Singleton
    public CosmosDatabase provideCosmosDatabase(
            CosmosClient cosmosClient,
            @Named("cosmosDatabaseName") String cosmosDatabaseName
    ) {
        return cosmosClient.getDatabase(cosmosDatabaseName);
    }


    @Provides
    @Singleton
    @Named("transactionClient")
    public CosmosContainer provideCosmosTransactionClient (
            CosmosDatabase cosmosDatabase,
            @Named("cosmosTransactionsContainerName") String cosmosContainerName
    ) {
        return cosmosDatabase.getContainer(cosmosContainerName);
    }

    @Provides
    @Singleton
    @Named("companyClient")
    public CosmosContainer provideCosmosCompanyClient (
            CosmosDatabase cosmosDatabase,
            @Named("cosmosCompaniesContainerName") String cosmosContainerName
    ) {
        return cosmosDatabase.getContainer(cosmosContainerName);
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    private static String getEnv(String key) {
        String value = System.getProperty(key, System.getenv(key));
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing env variable: " + key);
        }
        return value;
    }

}
