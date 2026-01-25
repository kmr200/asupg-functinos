package org.asupg.functions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "transactions")
@CompoundIndexes({
        @CompoundIndex(
                name = "inn_type_idx",
                def = "{ 'counterpartyInn': 1, 'transactionType': 1 }"
        )
})
public class TransactionDTO {

    @Id
    private String transactionId;

    private String counterpartyInn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String counterpartyName;

    private String accountNumber;

    private String mfo;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

    private String description;

    private TransactionType transactionType;

    private ReconciliationDTO reconciliation;

    @Version
    @JsonIgnore
    private Long version;

    //Constructor for generating monthly charge transaction
    public TransactionDTO(
        String transactionId,
        String counterpartyName,
        String counterpartyInn,
        BigDecimal amount
    ) {
        this.transactionId = transactionId;
        this.date = LocalDate.now(ZoneOffset.UTC);
        this.counterpartyName = counterpartyName;
        this.counterpartyInn = counterpartyInn;
        this.amount = amount;
        this.transactionType = TransactionType.MONTHLY_CHARGE;
    }

    public TransactionDTO(
            LocalDate date,
            String transactionId,
            String counterpartyName,
            String counterpartyInn,
            String accountNumber,
            String mfo,
            BigDecimal amount,
            String description,
            TransactionType transactionType
    ) {
        this.date = date;
        this.transactionId = transactionId;
        this.counterpartyName = counterpartyName;
        this.counterpartyInn = counterpartyInn;
        this.accountNumber = accountNumber;
        this.mfo = mfo;
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public enum TransactionType {
            BANK_PAYMENT("BANK_PAYMENT"),      // Payment from customer (external)
            MONTHLY_CHARGE("MONTHLY_CHARGE");
            private String value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDTO that = (TransactionDTO) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionId);
    }

}
