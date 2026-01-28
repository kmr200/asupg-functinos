package org.asupg.workers.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "devices")
@CompoundIndexes({
        @CompoundIndex(def = "{ 'status': 1, 'freeUntil': 1, 'lastBilledMonth': 1, 'companyInn': 1 }")

})
public class DeviceDTO {

    @Id
    private String deviceId;

    private String deviceName;

    private String companyInn;

    private String deviceType;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal monthlyRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate activatedAt;

    @Field(targetType = FieldType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth freeUntil;

    @Field(targetType = FieldType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth lastBilledMonth;

    private DeviceStatus status;

}
