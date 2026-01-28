package org.asupg.workers.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "companies")
@CompoundIndexes({
        @CompoundIndex(name = "balance_idx", def = "{'currentBalance': 1, '_id': 1}"),
        @CompoundIndex(name = "status_idx", def = "{'status': 1}"),
        @CompoundIndex(name = "name_idx", def = "{'name': 1}"),
        @CompoundIndex(def = "{'status': 1, 'currentBalance': 1, '_id': 1}")
})
public class CompanyDTO {

    @Id
    private String inn;

    private String name;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal currentBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime balanceUpdatedAt;

    private CompanyStatus status;

    private String email;

    private String phone;

    @Version
    @JsonIgnore
    private Long version;

    public CompanyDTO(
            String inn,
            String name,
            CompanyStatus status,
            String email,
            String phone
    ) {
        this.inn = inn;
        this.name = name;
        this.status = status;
        this.email = email;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDTO that = (CompanyDTO) o;
        return Objects.equals(inn, that.inn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inn);
    }

}
