package org.asupg.functions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompanyDTO {

    @JsonProperty("id")
    private String id;

    private String inn;

    private String name;

    private BigDecimal monthlyRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate subscriptionStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate billingStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastBilledMonth;

    private BigDecimal currentBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime balanceUpdatedAt;

    private CompanyStatus status;

    private String email;

    private String phone;

    @JsonProperty("_etag")
    @ToString.Exclude
    private String etag;

    public void setId(String id) {
        this.id = id;
        this.inn = id;
    }

    public void setInn(String inn) {
        this.id = inn;
        this.inn = inn;
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
