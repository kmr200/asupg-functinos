package org.asupg.functions.model;

import java.util.List;

public record CompanyLookupResult(
        List<CompanyDTO> companiesToUpdate,
        List<String> notFoundCompanies
        ) {
}
