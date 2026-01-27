package org.asupg.functions.repository;

import org.asupg.functions.model.CompanyDevicesAggregation;
import org.asupg.functions.model.DeviceDTO;
import org.asupg.functions.model.DeviceStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface DeviceRepository extends MongoRepository<DeviceDTO, String> {

    List<DeviceDTO> findByCompanyInnAndStatusAndLastBilledMonthBefore(String companyInn, DeviceStatus status, YearMonth lastBilledMonthBefore);

    @Aggregation(pipeline = {
            "{ $match:  { status:  ?0, freeUntil: { $lt:  ?2 }, $or: [ {lastBilledMonth: { $lt:  ?1 } }, { lastBilledMonth:  null } ] } }",
            "{ $group:  { _id:  '$companyInn', devices:  { $push:  '$$ROOT' } } }"
    })
    List<CompanyDevicesAggregation> findBillableDevicesGroupByCompanyInn(
            DeviceStatus status,
            YearMonth lastBilledMonthBefore,
            YearMonth freeUntilBefore
    );

}
