package org.asupg.functions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TransactionDTO {

    @JsonProperty("id")
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String transactionId;

    private String counterpartyName;

    private String counterpartyInn;

    private String accountNumber;

    private String mfo;

    private BigDecimal amount;

    private String description;

    private TransactionType transactionType;

    private ReconciliationDTO reconciliation;

    @JsonProperty("_etag")
    @ToString.Exclude
    private String etag;

    //Constructor for generating monthly charge transaction
    public TransactionDTO(
        String transactionId,
        String counterpartyName,
        String counterpartyInn,
        BigDecimal amount
    ) {
        this.id = transactionId;
        this.transactionId = transactionId;
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
        this.id = transactionId;
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

    public void setId(String id) {
        this.id = id;
        this.transactionId = id;
    }

    public void setTransactionId(String transactionId) {
        this.id = id;
        this.transactionId = id;
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
