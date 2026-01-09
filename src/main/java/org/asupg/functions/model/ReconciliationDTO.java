package org.asupg.functions.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReconciliationDTO {

    private ReconciliationStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime processedAt;

    private String failureReason;

    private boolean manual;

    private String updatedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

    public ReconciliationDTO() {}

    public ReconciliationDTO(ReconciliationStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
        manual = false;
    }

    public ReconciliationDTO(ReconciliationStatus status, LocalDateTime processedAt, String failureReason, boolean manual, String updatedBy, LocalDateTime updatedAt) {
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

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationDTO that = (ReconciliationDTO) o;
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
