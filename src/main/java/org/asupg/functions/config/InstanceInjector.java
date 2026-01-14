package org.asupg.functions.config;

import com.microsoft.azure.functions.spi.inject.FunctionInstanceInjector;
import org.asupg.functions.*;
import org.asupg.functions.component.FunctionComponent;
import org.asupg.functions.component.DaggerFunctionComponent;

public class InstanceInjector implements FunctionInstanceInjector {

    private static final FunctionComponent COMPONENT = DaggerFunctionComponent.create();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> functionClass) throws Exception {
        if (functionClass.equals(ReportHttpTrigger.class)) {
            return (T) COMPONENT.getHttpTrigger();
        }
        if (functionClass.equals(ReportTimerTrigger.class)) {
            return (T) COMPONENT.getTimerTrigger();
        }
        if (functionClass.equals(ParserBlobTrigger.class)) {
            return (T) COMPONENT.getBlobTrigger();
        }
        if (functionClass.equals(MonthlyChargeTimerTrigger.class)) {
            return (T) COMPONENT.getMonthlyChargeTrigger();
        }
        if (functionClass.equals(MonthlyChargeHttpTrigger.class)) {
            return (T) COMPONENT.getMonthlyChargeHttpTrigger();
        }
        throw new IllegalArgumentException("Unsupported function class " + functionClass);
    }
}
