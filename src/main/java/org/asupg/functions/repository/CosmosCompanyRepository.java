package org.asupg.functions.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asupg.functions.model.CompanyDto;
import org.asupg.functions.model.CompanyLookupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class CosmosCompanyRepository {

    private static final Logger logger = LoggerFactory.getLogger(CosmosCompanyRepository.class);

    private final CosmosContainer cosmosContainer;
    private final ObjectMapper objectMapper;

    @Inject
    public CosmosCompanyRepository(
            @Named("companyClient") CosmosContainer cosmosContainer,
            ObjectMapper objectMapper
    ) {
        this.cosmosContainer = cosmosContainer;
        this.objectMapper = objectMapper;
    }

    public CompanyLookupResult getCompaniesToUpdate(Set<String> listOfCompanyInn) {
        List<CompanyDto> companiesToUpdate = new ArrayList<>();
        List<String> notFoundCompanies = new ArrayList<>();

        for (String inn : listOfCompanyInn) {
            try {
                CompanyDto company = cosmosContainer
                        .readItem(inn, new PartitionKey(inn), CompanyDto.class)
                        .getItem();

                companiesToUpdate.add(company);

            } catch (CosmosException e) {
                if (e.getStatusCode() == 404) {
                    notFoundCompanies.add(inn);
                } else {
                    throw e;
                }
            }
        }

        logger.info("Companies to update {}, companies not found: {}", companiesToUpdate.size(), notFoundCompanies.size());
        logger.info(companiesToUpdate.toString());
        logger.info(notFoundCompanies.toString());

        return new CompanyLookupResult(companiesToUpdate, notFoundCompanies);
    }

    public void bulkUpdateCompanies(List<CompanyDto> companiesToUpdate) {

        if (companiesToUpdate.isEmpty()) {
            logger.info("Company list is empty");
            return;
        }

        List<CosmosItemOperation> ops = companiesToUpdate.stream()
                .filter(c -> {
                    if (c.getEtag() == null || c.getEtag().isBlank()) {
                        logger.warn("Skipping company {} - missing ETag", c.getInn());
                        return false;
                    }
                    return true;
                })
                .map(c -> {
                            CosmosItemRequestOptions options = new CosmosItemRequestOptions();
                            options.setIfMatchETag(c.getEtag());

                            return CosmosBulkOperations.getReplaceItemOperation(
                                    c.getId(),
                                    c,
                                    new PartitionKey(c.getInn()),
                                    options
                            );
                        }
                )
                .toList();

        cosmosContainer.executeBulkOperations(ops);
    }


}
