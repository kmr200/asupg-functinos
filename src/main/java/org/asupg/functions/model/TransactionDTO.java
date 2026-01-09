package org.asupg.functions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

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

    private ReconciliationDTO reconciliation;

    @JsonProperty("_etag")
    private String etag;

    public TransactionDTO() {}

    public TransactionDTO(
            LocalDate date,
            String transactionId,
            String counterpartyName,
            String counterpartyInn,
            String accountNumber,
            String mfo,
            BigDecimal amount,
            String description,
            ReconciliationDTO reconciliation
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
        this.reconciliation = reconciliation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.transactionId = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.id = id;
        this.transactionId = id;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getCounterpartyInn() {
        return counterpartyInn;
    }

    public void setCounterpartyInn(String counterpartyInn) {
        this.counterpartyInn = counterpartyInn;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getMfo() {
        return mfo;
    }

    public void setMfo(String mfo) {
        this.mfo = mfo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReconciliationDTO getReconciliation() {
        return reconciliation;
    }

    public void setReconciliation(ReconciliationDTO reconciliation) {
        this.reconciliation = reconciliation;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
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

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", transactionId='" + transactionId + '\'' +
                ", counterpartyName='" + counterpartyName + '\'' +
                ", counterpartyInn='" + counterpartyInn + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", mfo='" + mfo + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", reconciliation=" + reconciliation +
                '}';
    }
}
