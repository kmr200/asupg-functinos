package org.asupg.functions.config;

import com.microsoft.azure.functions.spi.inject.FunctionInstanceInjector;
import org.asupg.functions.BlobTriggerJava;
import org.asupg.functions.HttpTriggerJava;
import org.asupg.functions.TimerTriggerJava;
import org.asupg.functions.component.FunctionComponent;
import org.asupg.functions.component.DaggerFunctionComponent;

public class InstanceInjector implements FunctionInstanceInjector {

    private static final FunctionComponent COMPONENT = DaggerFunctionComponent.create();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> functionClass) throws Exception {
        if (functionClass.equals(HttpTriggerJava.class)) {
            return (T) COMPONENT.getHttpTrigger();
        }
        if (functionClass.equals(TimerTriggerJava.class)) {
            return (T) COMPONENT.getTimerTrigger();
        }
        if (functionClass.equals(BlobTriggerJava.class)) {
            return (T) COMPONENT.getBlobTrigger();
        }
        throw new IllegalArgumentException("Unsupported function class " + functionClass);
    }
}
