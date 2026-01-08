package org.asupg.functions.component;

import dagger.Component;
import org.asupg.functions.BlobTriggerJava;
import org.asupg.functions.HttpTriggerJava;
import org.asupg.functions.TimerTriggerJava;
import org.asupg.functions.config.DaggerModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = DaggerModule.class)
public interface FunctionComponent {

    BlobTriggerJava getBlobTrigger();
    HttpTriggerJava getHttpTrigger();
    TimerTriggerJava getTimerTrigger();

}
