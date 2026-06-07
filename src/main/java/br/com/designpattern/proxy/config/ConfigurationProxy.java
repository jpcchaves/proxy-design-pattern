package br.com.designpattern.proxy.config;

import br.com.designpattern.proxy.service.ConfigurationDummyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Primary
@Component
public class ConfigurationProxy extends ConfigurationDummyService {

    private final AtomicReference<ConfigurationDummyService> delegate;
    private final GenericApplicationContext applicationContext;

    public ConfigurationProxy(ConfigurationDummyService delegate, GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.setServiceId(delegate.getServiceId());
        this.delegate = new AtomicReference<>(delegate);
    }

    @Override
    public void logDelegateId() {
        log.info("[ConfigurationProxy] Delegando logDelegateId()");
        delegate.get().logDelegateId();
    }

    @Override
    public String simulateBehaviour() {
        return delegate.get().simulateBehaviour();
    }

    public synchronized String updateDelegate() {
        log.info("[ConfigurationProxy] Atualizando proxy...");

        ConfigurationDummyService newDelegate = new ConfigurationDummyService();

        applicationContext.registerBean("configurationDummyService",
                ConfigurationDummyService.class,
                () -> newDelegate,
                (bd) -> bd.setPrimary(true)
        );

        this.delegate.set(newDelegate);
        log.info("[ConfigurationProxy] Delegate atualizado para: {}", newDelegate.getServiceId());
        return newDelegate.getServiceId();
    }

    public synchronized String currentDelegateId() {
        log.info("[ConfigurationProxy] ID do delegate atual {}", delegate.get().getServiceId());
        return delegate.get().getServiceId();
    }
}
