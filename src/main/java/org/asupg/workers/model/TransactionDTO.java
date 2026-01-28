package org.asupg.workers.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
import java.time.YearMonth;
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
        ),
        @CompoundIndex(
                name = "monthly_charge_unique_idx",
                def = "{ 'counterpartyInn': 1, 'deviceId': 1, 'billingMonth': 1, 'transactionType': 1 }",
                unique = true
        )
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    @Id
    private String transactionId;

    private String counterpartyInn;

    private String deviceId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Field(targetType = FieldType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    //Which month company was charged for
    private YearMonth billingMonth;

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

    public TransactionDTO(
            LocalDate date,
            YearMonth billingMonth,
            String transactionId,
            String counterpartyName,
            String counterpartyInn,
            String deviceId,
            String accountNumber,
            String mfo,
            BigDecimal amount,
            String description,
            TransactionType transactionType
    ) {
        this.date = date;
        this.billingMonth = billingMonth;
        this.transactionId = transactionId;
        this.counterpartyName = counterpartyName;
        this.counterpartyInn = counterpartyInn;
        this.deviceId = deviceId;
        this.accountNumber = accountNumber;
        this.mfo = mfo;
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
    }

    public static TransactionDTO monthlyDeviceCharge(
            String transactionId,
            String companyInn,
            String deviceId,
            BigDecimal monthlyRate,
            YearMonth now
    ) {
        return new TransactionDTO(
                LocalDate.now(ZoneOffset.UTC),
                now,
                transactionId,
                null,
                companyInn,
                deviceId,
                null,
                null,
                monthlyRate.abs().negate(),
                "Charge for device: " + deviceId + " for month: " + now.toString(),
                TransactionType.MONTHLY_CHARGE
        );
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
