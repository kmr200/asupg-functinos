package org.asupg.workers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.workers.model.CompanyDevicesAggregation;
import org.asupg.workers.model.DeviceDTO;
import org.asupg.workers.model.DeviceStatus;
import org.asupg.workers.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;


    public Map<String, List<DeviceDTO>> getBillableDevicesAggregatedByCompanyInn(YearMonth billingMonth) {

        List<CompanyDevicesAggregation> aggregation = deviceRepository.findBillableDevicesGroupByCompanyInn(
                DeviceStatus.ACTIVE,
                billingMonth,
                billingMonth
        );

        return aggregation.stream()
                .collect(Collectors.toMap(
                        CompanyDevicesAggregation::getCompanyInn,
                        CompanyDevicesAggregation::getDevices
                ));
    }

}
