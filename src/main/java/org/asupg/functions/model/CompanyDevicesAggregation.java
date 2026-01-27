package org.asupg.functions.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompanyDevicesAggregation {

    @Id
    private String companyInn;

    private List<DeviceDTO> devices;

}
