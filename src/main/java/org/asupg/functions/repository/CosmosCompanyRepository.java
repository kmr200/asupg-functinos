package org.asupg.functions.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.model.CompanyLookupResult;
import org.asupg.functions.model.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
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
        List<CompanyDTO> companiesToUpdate = new ArrayList<>();
        Set<String> notFoundCompanies = new HashSet<>();

        for (String inn : listOfCompanyInn) {
            try {
                CompanyDTO company = cosmosContainer
                        .readItem(inn, new PartitionKey(inn), CompanyDTO.class)
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

        return new CompanyLookupResult(companiesToUpdate, notFoundCompanies);
    }

    public List<CompanyDTO> bulkUpdateCompanies(List<CompanyDTO> companiesToUpdate) {

        if (companiesToUpdate.isEmpty()) {
            logger.info("Company list is empty");
            return List.of();
        }

        List<CompanyDTO> failedToUpdate = new ArrayList<>();
        List<CosmosItemOperation> ops = new ArrayList<>();

        for (CompanyDTO c : companiesToUpdate) {
            if (c.getEtag() == null || c.getEtag().isBlank()) {
                logger.warn("Skipping company {} - missing ETag", c.getInn());
                failedToUpdate.add(c);
                continue;
            }

            CosmosBulkItemRequestOptions options = new CosmosBulkItemRequestOptions();
            options.setIfMatchETag(c.getEtag());

            ops.add(
                    CosmosBulkOperations.getReplaceItemOperation(
                            c.getId(),
                            c,
                            new PartitionKey(c.getInn()),
                            options,
                            c
                    )
            );
        }

        Iterable<CosmosBulkOperationResponse<Object>> responses =
                cosmosContainer.executeBulkOperations(ops);

        for (CosmosBulkOperationResponse<Object> response : responses) {

            CompanyDTO company = response.getOperation().getContext();

            // failure = response unsuccessful OR exception present
            if (response.getException() != null ||
                    response.getResponse() == null ||
                    !response.getResponse().isSuccessStatusCode()) {

                failedToUpdate.add(company);

                if (response.getException() != null) {
                    logger.error(
                            "Error while saving company {}",
                            company.getId(),
                            response.getException()
                    );
                } else {
                    logger.error(
                            "Error while saving company {}, status: {}",
                            company.getId(),
                            response.getResponse().getStatusCode()
                    );
                }
            }
        }

        if (!failedToUpdate.isEmpty()) {
            logger.warn("Failed to update {} companies", failedToUpdate.size());
        }

        return failedToUpdate;
    }

}
