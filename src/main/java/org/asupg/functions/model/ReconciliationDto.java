package org.asupg.functions.model;

import java.time.Instant;
import java.util.Objects;

public class ReconciliationDto {

    private ReconciliationStatus status;
    private Instant processedAt;
    private String failureReason;
    private boolean manual;
    private String updatedBy;
    private Instant updatedAt;

    public ReconciliationDto() {}

    public ReconciliationDto(ReconciliationStatus status) {
        this.status = status;
        this.processedAt = Instant.now();
        manual = false;
    }

    public ReconciliationDto(ReconciliationStatus status, Instant processedAt, String failureReason, boolean manual, String updatedBy, Instant updatedAt) {
        this.status = status;
        this.processedAt = processedAt;
        this.failureReason = failureReason;
        this.manual = manual;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public ReconciliationStatus getStatus() {
        return status;
    }

    public void setStatus(ReconciliationStatus status) {
        this.status = status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationDto that = (ReconciliationDto) o;
        return manual == that.manual && status == that.status && Objects.equals(processedAt, that.processedAt) && Objects.equals(failureReason, that.failureReason) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, processedAt, failureReason, manual, updatedBy, updatedAt);
    }

    @Override
    public String toString() {
        return "ReconciliationDto{" +
                "status=" + status +
                ", processedAt=" + processedAt +
                ", failureReason='" + failureReason + '\'' +
                ", manual=" + manual +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
