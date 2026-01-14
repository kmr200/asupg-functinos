package org.asupg.functions.component;

import dagger.Component;
import org.asupg.functions.*;
import org.asupg.functions.config.DaggerModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = DaggerModule.class)
public interface FunctionComponent {

    ParserBlobTrigger getBlobTrigger();
    ReportHttpTrigger getHttpTrigger();
    ReportTimerTrigger getTimerTrigger();
    MonthlyChargeTimerTrigger getMonthlyChargeTrigger();
    MonthlyChargeHttpTrigger  getMonthlyChargeHttpTrigger();

}
