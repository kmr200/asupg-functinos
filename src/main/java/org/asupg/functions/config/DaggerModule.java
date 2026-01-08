package org.asupg.functions.config;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Named("cosmosContainerName")
    public String provideDatabase() {
        return System.getenv("COSMOS_CONTAINER_NAME");
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
                .buildClient();
    }

    @Provides
    @Singleton
    public CosmosContainer provideCosmosContainer(
            CosmosClient cosmosClient,
            @Named("cosmosDatabaseName") String cosmosDatabaseName,
            @Named("cosmosContainerName")  String cosmosContainerName
    ) {
        return cosmosClient.getDatabase(cosmosDatabaseName).getContainer(cosmosContainerName);
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    private static String getEnv(String key) {
        String value = System.getProperty(key, System.getenv(key));
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing env variable: " + key);
        }
        return value;
    }

}
