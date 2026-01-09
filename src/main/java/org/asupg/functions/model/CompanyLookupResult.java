package org.asupg.functions.model;

import java.util.List;

public record CompanyLookupResult(
        List<CompanyDto> companiesToUpdate,
        List<String> notFoundCompanies
        ) {
}
