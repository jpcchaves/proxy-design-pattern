package br.com.designpattern.proxy.config;

import br.com.designpattern.proxy.service.ConfigurationDummyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Primary
@Component
public class ConfigurationProxy extends ConfigurationDummyService {

    private final AtomicReference<ConfigurationDummyService> delegate;

    public ConfigurationProxy(ConfigurationDummyService delegate) {
        log.info("[ConfigurationProxy] Iniciando com delegate: {}", delegate.getServiceId());
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

    public synchronized void updateDelegate() {
        log.info("[ConfigurationProxy] Atualizando proxy...");
        ConfigurationDummyService newDelegate = new ConfigurationDummyService();
        this.delegate.set(newDelegate);
        log.info("[ConfigurationProxy] Delegate atualizado para: {}", newDelegate.getServiceId());
    }

    public synchronized String currentDelegateId() {
        return delegate.get().getServiceId();
    }
}
