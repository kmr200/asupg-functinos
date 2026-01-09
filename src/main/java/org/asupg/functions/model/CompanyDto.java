package org.asupg.functions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class CompanyDto {

    @JsonProperty("id")
    private String id;

    private String inn;

    private String name;

    private BigDecimal monthlyRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate subscriptionStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate billingStartDate;

    private BigDecimal totalPaid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastPaymentDate;

    private CompanyStatus status;

    private String email;

    private String phone;

    @JsonProperty("_etag")
    private String etag;

    public CompanyDto() {}

    public CompanyDto(
            String inn,
            String name,
            BigDecimal monthlyRate,
            LocalDate subscriptionStartDate,
            LocalDate billingStartDate,
            BigDecimal totalPaid,
            LocalDate lastPaymentDate,
            CompanyStatus status,
            String email,
            String phone
    ) {
        this.id = inn;
        this.inn = inn;
        this.name = name;
        this.monthlyRate = monthlyRate;
        this.subscriptionStartDate = subscriptionStartDate;
        this.billingStartDate = billingStartDate;
        this.totalPaid = totalPaid;
        this.lastPaymentDate = lastPaymentDate;
        this.status = status;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.inn = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.id = inn;
        this.inn = inn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public LocalDate getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public LocalDate getBillingStartDate() {
        return billingStartDate;
    }

    public void setBillingStartDate(LocalDate billingStartDate) {
        this.billingStartDate = billingStartDate;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDto that = (CompanyDto) o;
        return Objects.equals(inn, that.inn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inn);
    }

    @Override
    public String toString() {
        return "CompanyDto{" +
                ", inn='" + inn + '\'' +
                ", name='" + name + '\'' +
                ", monthlyRate=" + monthlyRate +
                ", subscriptionStartDate=" + subscriptionStartDate +
                ", billingStartDate=" + billingStartDate +
                ", totalPaid=" + totalPaid +
                ", lastPaymentDate=" + lastPaymentDate +
                ", status=" + status +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
