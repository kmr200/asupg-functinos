package org.asupg.functions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.functions.model.CompanyDTO;
import org.asupg.functions.model.CompanyDevicesAggregation;
import org.asupg.functions.model.DeviceDTO;
import org.asupg.functions.model.DeviceStatus;
import org.asupg.functions.repository.DeviceRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    public List<DeviceDTO> bulkUpdateDevices(List<DeviceDTO> devicesToUpdate) {

        if (devicesToUpdate.isEmpty()) {
            log.info("Company list is empty");
            return List.of();
        }

        List<DeviceDTO> failed = new ArrayList<>();

        for (DeviceDTO device : devicesToUpdate) {
            try {
                deviceRepository.save(device);
            } catch (OptimisticLockingFailureException e) {
                log.warn("Optimistic lock failed for device {}", device.getDeviceId());
                failed.add(device);
            } catch (Exception e) {
                log.error("Error while updating device {}", device.getDeviceId(), e);
                failed.add(device);
            }
        }

        return failed;
    }

    public void markDevicesBilled(List<DeviceDTO> devices, YearMonth billingMonth) {
        devices.forEach(device -> device.setLastBilledMonth(billingMonth));
        deviceRepository.saveAll(devices);
    }
}
