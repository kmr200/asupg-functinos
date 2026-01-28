package org.asupg.workers.model;

import java.util.List;
import java.util.Set;

public record CompanyLookupResult(
        List<CompanyDTO> companiesToUpdate,
        Set<String> notFoundCompanies
        ) {
}
